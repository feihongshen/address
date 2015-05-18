package cn.explink.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

import cn.explink.domain.SystemConfig;
import cn.explink.util.ApplicationContextUtil;
import cn.explink.util.ConfigManager;

import com.mchange.v2.c3p0.ComboPooledDataSource;

public class DBBackupAndRestoreService {
	static String dbName = "";
	static String username = "";
	static String password = "";
	static String host = "";
	static String port = "";
	static String dbToolsPath = "";
	static String databasePath = "";
	static String tablesPath = "";
	static String filePath = ""; // 备份文件的路径

	static {
		ComboPooledDataSource dataSource = (ComboPooledDataSource) ApplicationContextUtil.getBean("dataSource");
		String jdbcUrl = dataSource.getJdbcUrl();
		DBBackupAndRestoreService.host = jdbcUrl.substring(jdbcUrl.indexOf("://") + 3, jdbcUrl.lastIndexOf(":"));
		DBBackupAndRestoreService.port = jdbcUrl.substring(jdbcUrl.lastIndexOf(":") + 1, jdbcUrl.lastIndexOf("/"));
		DBBackupAndRestoreService.dbName = jdbcUrl.substring(jdbcUrl.lastIndexOf("/") + 1, jdbcUrl.lastIndexOf("?"));
		DBBackupAndRestoreService.username = dataSource.getUser();
		DBBackupAndRestoreService.password = dataSource.getPassword();

		DBBackupAndRestoreService.dbToolsPath = ((SystemConfig) ConfigManager.getInstance().get("MySQLPath")).getValue();
		DBBackupAndRestoreService.databasePath = ((SystemConfig) ConfigManager.getInstance().get("databasePath")).getValue();
		DBBackupAndRestoreService.tablesPath = ((SystemConfig) ConfigManager.getInstance().get("tablesPath")).getValue();
	}

	/**
	 * 生成用于备份数据库的shell命令
	 *
	 * @param targetName
	 *            要备份的对象名：只能为表名和数据库名称
	 * @return 实际执行的shell命令
	 */
	public static String getBackupShellString(String targetName) {
		String basepath = Thread.currentThread().getContextClassLoader().getResource("").toString();
		String database_tools_path = DBBackupAndRestoreService.dbToolsPath;
		// 备份的文件
		String backFilePath = "";
		// 若要备份整个数据库
		if (targetName.equals(DBBackupAndRestoreService.dbName)) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
			// 要备份的文件
			backFilePath = DBBackupAndRestoreService.databasePath + targetName + "_" + sdf.format(new Date()) + ".sql";
			targetName = "";
		} else {
			backFilePath = DBBackupAndRestoreService.tablesPath + targetName + ".sql";
		}
		// 判断要备份的文件/文件夹是否已存在
		File backFileFolder_db = new File(DBBackupAndRestoreService.databasePath);
		File backFileFolder_tab = new File(DBBackupAndRestoreService.tablesPath);

		if (!backFileFolder_db.exists()) {
			backFileFolder_db.mkdirs();
		}
		if (!backFileFolder_tab.exists()) {
			backFileFolder_tab.mkdirs();
		}

		String OSType = System.getProperty("os.name");
		String shellStr = "";
		shellStr = database_tools_path + "mysqldump -h " + DBBackupAndRestoreService.host + " -P " + DBBackupAndRestoreService.port + " -u " + DBBackupAndRestoreService.username + " -p" // ★第二个-p后面不能有空格，否则将被认为是数据库的名称
				+ DBBackupAndRestoreService.password + " --result-file=" + backFilePath + " --default-character-set=gbk " + DBBackupAndRestoreService.dbName + " " + targetName;
		System.out.print("##############" + shellStr);
		return shellStr;
	}

	/**
	 * 备份数据库
	 *
	 * @param targetName
	 *            要备份的对象名：只能为表名和数据库名称
	 * @return 成功:TRUE 失败:FALSE
	 *
	 */
	public static boolean backup(String targetName) {
		Runtime runt = Runtime.getRuntime();
		Process proc;
		try {
			proc = runt.exec(DBBackupAndRestoreService.getBackupShellString(targetName));
			int tag = proc.waitFor();// 等待进程终止
			if (tag == 0) {
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}

	public static void restoreDB(String targetName) {
		// 要读取的备份文件的路径
		String fPath = "";
		try {
			if (targetName.equals(DBBackupAndRestoreService.dbName)) {
				// 这里用于测试，先指定一个数据库文件，实际应用中应根据选择来确定文件名称
				fPath = DBBackupAndRestoreService.databasePath + "hr_20110415161235.sql";
			} else {
				fPath = DBBackupAndRestoreService.tablesPath + targetName + ".sql";
			}
			System.out.println("fPath:" + fPath);
			Runtime rt = Runtime.getRuntime();
			// 调用 mysql 的 cmd:
			Process child = rt.exec(DBBackupAndRestoreService.dbToolsPath + "mysql -uroot -pshutao " + DBBackupAndRestoreService.dbName);

			// int tag = child.waitFor();

			OutputStream out = child.getOutputStream();// 控制台的输入信息作为输出流
			String inStr;
			StringBuffer sb = new StringBuffer("");
			String outStr;
			// 下面的InputStreamReader和OutputStreamWriter的第二个参数为数据的编码格式，
			// 注意要跟备份的格式一样，否则会有异常：java.io.IOException: 管道已结束。
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(fPath), "gbk"));
			while ((inStr = br.readLine()) != null) {
				sb.append(inStr + "/r/n");
			}
			outStr = sb.toString();
			OutputStreamWriter writer = new OutputStreamWriter(out, "gbk");
			writer.write(outStr);
			writer.flush();
			// 别忘记关闭输入输出流
			out.close();
			br.close();
			writer.close();
			System.out.println("/* Load OK! */");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 根据路径生成恢复数据库的Shell字符串
	 *
	 * @param targetName
	 *            要还原的对象名：只能为表名和数据库名称
	 * @return 恢复数据时实际执行的shell
	 */
	public static String getRestoreShellString(String targetName) {
		String database_tools_path = DBBackupAndRestoreService.dbToolsPath;
		String backFile = "";// 已备份的文件
		if (targetName.indexOf(DBBackupAndRestoreService.dbName) == -1) {// 还原表
			backFile = DBBackupAndRestoreService.tablesPath + targetName + ".sql";
		} else {// 还原库
			backFile = DBBackupAndRestoreService.databasePath + targetName;
		}
		String shellStr = "";
		shellStr = database_tools_path + "mysql -h" + DBBackupAndRestoreService.host + " -P" + DBBackupAndRestoreService.port + " -u" + DBBackupAndRestoreService.username + " -p"
				+ DBBackupAndRestoreService.password + " --default-character-set=gbk " + DBBackupAndRestoreService.dbName + " < " + backFile;

		return shellStr;
	}

	/**
	 * 恢复数据库
	 *
	 * @param targetName
	 *            要备份的对象名：只能为表名和数据库名称
	 * @return 成功:TRUE 失败:FALSE
	 */
	public static boolean restore(String targetName) {
		try {
			Runtime runt = Runtime.getRuntime();
			Process proc;
			String cmdtext = DBBackupAndRestoreService.getRestoreShellString(targetName);
			if (System.getProperty("os.name").indexOf("Windows") != -1) {
				String[] cmd = { "cmd", "/c", cmdtext };
				proc = runt.exec(cmd);
			} else {
				String[] cmd = { "sh", "-c", cmdtext };
				proc = runt.exec(cmd);
			}
			System.out.println("cmdtext: " + cmdtext);
			int tag = proc.waitFor();// 等待进程终止
			System.out.println("进程返回值为tag:" + tag);
			if (tag == 0) {
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

}
