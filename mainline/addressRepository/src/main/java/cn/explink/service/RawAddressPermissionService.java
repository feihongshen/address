package cn.explink.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.explink.dao.RawAddressPermissionDao;
import cn.explink.domain.RawAddressPermission;

@Service
public class RawAddressPermissionService extends CommonServiceImpl<RawAddressPermission, Long> {
	@Autowired
	private RawAddressPermissionDao rawAddressPermissionDao;

	public RawAddressPermissionService() {
		super(RawAddressPermission.class);
	}

	public int batchUnbindAddress(List<Long> rawAddressIdList, Long customerId) {
		return this.rawAddressPermissionDao.batchUnbindAddress(rawAddressIdList, customerId);
	}

}
