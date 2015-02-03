package cn.explink.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.hibernate.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.explink.dao.AddressImportResultDao;
import cn.explink.dao.UserDao;
import cn.explink.domain.AddressImportDetail;
import cn.explink.domain.AddressImportResult;

@Service
public class AddressImportResultService extends CommonServiceImpl<AddressImportResult, Long> {

	public AddressImportResultService() {
		super(AddressImportResult.class);
	}

	private static Logger logger = LoggerFactory.getLogger(AddressImportResultService.class);

	@Autowired
	private AddressImportResultDao addressImportResultDao;
	@Autowired
	private UserDao userDao;

	@Override
	public Object save(Object entity) {
		return this.addressImportResultDao.save((AddressImportResult) entity);
	}

	public List<List<String>> getImportDetailByResultId(Long resultId) {
		List<List<String>> resultList = new ArrayList<List<String>>();
		String hql = "select d from AddressImportDetail d , AddressImportResult r where d.addressImportResult=r.id and r.id=:resultId and d.status=1 order by d.message desc";
		Query query = this.getSession().createQuery(hql);
		query.setLong("resultId", resultId);
		@SuppressWarnings("unchecked")
		List<AddressImportDetail> addressImportDetailList = query.list();

		for (AddressImportDetail addressImportDetail : addressImportDetailList) {
			List<String> fieldList = new ArrayList<String>();
			fieldList.add(addressImportDetail.getMessage());
			fieldList.add(addressImportDetail.getProvince());
			fieldList.add(addressImportDetail.getCity());
			fieldList.add(addressImportDetail.getDistrict());
			fieldList.add(addressImportDetail.getAddress1());
			fieldList.add(addressImportDetail.getAddress2());
			fieldList.add(addressImportDetail.getAddress3());
			fieldList.add(addressImportDetail.getDeliveryStationName());

			resultList.add(fieldList);
		}
		return resultList;
	}

	public XSSFWorkbook createAddressImportResultFile(List<String> headerNameList, List<List<String>> importResultList) {
		XSSFWorkbook wookbook = new XSSFWorkbook();
		XSSFSheet sheet = wookbook.createSheet();
		XSSFRow row = sheet.createRow(0);
		int columnIndex = 0;
		for (String headerName : headerNameList) {
			XSSFCell cell = row.createCell(columnIndex);
			cell.setCellValue(headerName);
			columnIndex++;
		}
		for (int i = 0; i < importResultList.size(); i++) {
			columnIndex = 0;
			row = sheet.createRow(i + 1);
			for (int j = 0; j < importResultList.get(i).size(); j++) {
				XSSFCell cell = row.createCell(columnIndex);
				cell.setCellValue(importResultList.get(i).get(j));
				columnIndex++;
			}
		}

		return wookbook;
	}
}
