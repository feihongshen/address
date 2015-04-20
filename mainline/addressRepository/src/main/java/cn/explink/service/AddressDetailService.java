package cn.explink.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.explink.dao.AddressDetailDao;
import cn.explink.domain.AddressDetail;

/**
 *
 * @author songkaojun 2015年4月20日
 */
@Service
public class AddressDetailService extends CommonServiceImpl<AddressDetail, Long> {
	public AddressDetailService() {
		super(AddressDetail.class);
	}

	@Autowired
	private AddressDetailDao addressDetailDao;

	public void synAddressDetail(List<AddressDetail> addressDetailList) {
		this.addressDetailDao.synAddressDetail(addressDetailList);
	}

	public List<AddressDetail> fuzzyQueryByPage(String keyword, String stationName, Long customerId, int page, int pageSize) {
		return this.addressDetailDao.fuzzyQueryByPage(keyword, stationName, customerId, page, pageSize);
	}

	public int getAddressDetailCount(String keyword, String stationName, Long customerId) {
		return this.addressDetailDao.getAddressDetailCount(keyword, stationName, customerId);
	}

	public void deleteByIdList(List<Long> addressDetailIdList) {
		this.addressDetailDao.deleteByIdList(addressDetailIdList);
	}
}
