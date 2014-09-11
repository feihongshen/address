package cn.explink.util;

import java.util.ResourceBundle;

/**
 * 
 * 读取文件路径配置文件
 * 
 */
public class ResourceBundleUtil {

	private static ResourceBundle addressResourceBundle = ResourceBundle.getBundle("address");

	// lucene索引目录
	public static final String LUCENE_INDEX_PATH = addressResourceBundle.getString("luceneIndexPath");

	// lucene词典目录
	public static final String LUCENE_DICT_PATH = addressResourceBundle.getString("luceneDictPath");

}
