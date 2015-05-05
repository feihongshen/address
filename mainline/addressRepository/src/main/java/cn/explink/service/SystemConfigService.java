package cn.explink.service;

import cn.explink.domain.SystemConfig;

public interface SystemConfigService {
	public SystemConfig getConfig(String name);

	public SystemConfig getConfigByNameAndCustomerId(String name, Long customerId);
}
