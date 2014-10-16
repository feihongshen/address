package cn.explink.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.explink.dao.SystemConfigDao;
import cn.explink.domain.SystemConfig;

 
@Service
public class SystemConfigServiceImpl implements SystemConfigService {
	
	@Autowired
	private SystemConfigDao systemConfigDao;
	
	@Override
	public SystemConfig getConfig(String code) {
		return systemConfigDao.getSystemConfigByName(code);
	}
	
}
