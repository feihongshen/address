package cn.explink.service;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.explink.dao.AddressImportResultDao;
import cn.explink.dao.UserDao;
import cn.explink.domain.AddressImportResult;

@Service
public class AddressImportResultService extends CommonServiceImpl<AddressImportResult,Long> {

	public AddressImportResultService() {
		super(AddressImportResult.class);
	}

	private static Logger logger = LoggerFactory.getLogger(AddressImportResultService.class);


	@Autowired
	private AddressImportResultDao addressImportResultDao;
	@Autowired
	private UserDao userDao;

	public void setRlation(List<AddressImportResult> list) {
		Map<Long,String> userView=userDao.getview();
		
		for (AddressImportResult addressImportResult : list) {
			addressImportResult.setUserName(userView.get(addressImportResult.getUserId()));
		}
		
	}
	


}
