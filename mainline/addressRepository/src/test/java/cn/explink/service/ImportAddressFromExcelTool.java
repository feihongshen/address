package cn.explink.service;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import cn.explink.dao.AddressDao;
import cn.explink.dao.AddressPermissionDao;
import cn.explink.dao.UserDao;
import cn.explink.domain.Address;
import cn.explink.test.support.BaseTestCase;

/**
 * 导入地址信息（注：第一列最高）
 *
 * @author songkaojun
 * @since EAP1.0
 */
public class ImportAddressFromExcelTool extends BaseTestCase {

	@Autowired
	private AddressImportService addressImportService;

	@Autowired
	private UserDao userDao;

	@Autowired
	private AddressDao addressDao;

	@Autowired
	private AddressPermissionDao addressPermissionDao;

	@Test
	public void testImportAddress() {
		try {
			this.readExcel();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void readExcel() throws FileNotFoundException, IOException {
		File file = new File("d://data.xls");

		String[][] result = this.getData(file);

		int rowLength = result.length;

		List<Address> addressList = new ArrayList<Address>();
		for (int i = 0; i < rowLength; i++) {

			for (int j = 0; j < result[i].length; j++) {
				if ((j % 2) != 0) {
					Address addressByName = this.addressDao.getAddressByName(result[i][j - 1]);

					if (null == addressByName) {
						continue;
					}
					Long id = addressByName.getId();
					String path = addressByName.getPath();

					List<String> addressNameList = new ArrayList<String>();
					addressNameList.add(result[i][j]);
					List<Address> addressList2 = this.addressDao.getAddressByNames(addressNameList);
					if ((null == addressList2) || (addressList2.size() == 0)) {
						Address newAddress = new Address();
						newAddress.setParentId(id);
						newAddress.setAddressLevel(3);
						newAddress.setName(result[i][j]);
						newAddress.setStatus(1);
						newAddress.setPath(path + "-" + id);

						addressList.add(newAddress);
					} else {
						// AddressPermission addressPermission =
						// this.addressPermissionDao.getPermissionByAddressAndCustomer(address.getId(),
						// Long.valueOf(6));
						// if (null == addressPermission) {
						// addressPermission = new AddressPermission();
						// addressPermission.setAddressId(addressId);
						// }

					}
				}
			}
		}
		// for (Address address2 : addressList) {
		// Address addressByName =
		// this.addressDao.getAddressByName(address2.getName());
		// Long id = addressByName.getId();
		//
		// AddressPermission ap = new AddressPermission();
		// ap.setCustomerId(Long.valueOf(6));
		// ap.setAddressId(id);
		//
		// this.addressPermissionDao.save(ap);
		// // AddressPermission addressPermission =
		// //
		// this.addressPermissionDao.getPermissionByAddressAndCustomer(address2.getId(),
		// // Long.valueOf(6));
		// }

		this.addressDao.insert(addressList);

	}

	/**
	 *
	 * 读取Excel的内容，第一维数组存储的是一行中格列的值，二维数组存储的是多少个行
	 *
	 * @param file
	 *            读取数据的源Excel
	 *
	 *
	 * @return 读出的Excel中数据的内容
	 *
	 * @throws FileNotFoundException
	 *
	 * @throws IOException
	 */

	public String[][] getData(File file) throws FileNotFoundException, IOException {
		List<String[]> result = new ArrayList<String[]>();
		int rowSize = 0;
		BufferedInputStream in = new BufferedInputStream(new FileInputStream(file));
		// 打开HSSFWorkbook
		POIFSFileSystem fs = new POIFSFileSystem(in);
		HSSFWorkbook wb = new HSSFWorkbook(fs);
		HSSFCell cell = null;
		for (int sheetIndex = 0; sheetIndex < wb.getNumberOfSheets(); sheetIndex++) {
			HSSFSheet st = wb.getSheetAt(sheetIndex);
			for (int rowIndex = 0; rowIndex <= st.getLastRowNum(); rowIndex++) {
				HSSFRow row = st.getRow(rowIndex);
				if (row == null) {
					continue;
				}
				int tempRowSize = row.getLastCellNum() + 1;
				if (tempRowSize > rowSize) {
					rowSize = tempRowSize;
				}
				String[] values = new String[rowSize];
				Arrays.fill(values, "");
				boolean hasValue = false;
				for (short columnIndex = 0; columnIndex <= row.getLastCellNum(); columnIndex++) {
					String value = "";
					cell = row.getCell(columnIndex);
					if (cell != null) {
						// 注意：一定要设成这个，否则可能会出现乱码
						// cell.setEncoding(HSSFCell.ENCODING_UTF_16);
						switch (cell.getCellType()) {
						case Cell.CELL_TYPE_STRING:
							value = cell.getStringCellValue();
							break;
						case Cell.CELL_TYPE_NUMERIC:
							if (DateUtil.isCellDateFormatted(cell)) {
								Date date = cell.getDateCellValue();
								if (date != null) {
									value = new SimpleDateFormat("yyyy-MM-dd").format(date);
								} else {
									value = "";
								}

							} else {

								value = new DecimalFormat("0").format(cell

								.getNumericCellValue());

							}

							break;

						case Cell.CELL_TYPE_FORMULA:

							// 导入时如果为公式生成的数据则无值

							if (!cell.getStringCellValue().equals("")) {

								value = cell.getStringCellValue();

							} else {

								value = cell.getNumericCellValue() + "";

							}

							break;

						case Cell.CELL_TYPE_BLANK:

							break;

						case Cell.CELL_TYPE_ERROR:

							value = "";

							break;

						case Cell.CELL_TYPE_BOOLEAN:

							value = (cell.getBooleanCellValue() == true ? "Y"

							: "N");

							break;

						default:

							value = "";

						}

					}

					if ((columnIndex == 0) && value.trim().equals("")) {

						break;

					}

					values[columnIndex] = this.rightTrim(value);

					hasValue = true;

				}

				if (hasValue) {

					result.add(values);

				}

			}

		}

		in.close();

		String[][] returnArray = new String[result.size()][rowSize];

		for (int i = 0; i < returnArray.length; i++) {

			returnArray[i] = result.get(i);

		}

		return returnArray;

	}

	/**
	 *
	 * 去掉字符串右边的空格
	 *
	 * @param str
	 *            要处理的字符串
	 *
	 * @return 处理后的字符串
	 */

	private String rightTrim(String str) {

		if (str == null) {

			return "";

		}

		int length = str.length();

		for (int i = length - 1; i >= 0; i--) {

			if (str.charAt(i) != 0x20) {

				break;

			}

			length--;

		}

		return str.substring(0, length);

	}

}
