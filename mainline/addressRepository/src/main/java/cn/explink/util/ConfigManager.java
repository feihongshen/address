package cn.explink.util;

import cn.explink.service.SystemConfigService;

public class ConfigManager {
	private SystemConfigService systemConfigService;

	private static class CacheHolder {
		static ConfigManager instance = new ConfigManager();
	}

	private ConfigManager() {

	}

	public Object get(String name) {
		Object obj = this.getSystemConfigService().getConfig(name);
		return obj;
	}

	public Object getByNameAndCustomerId(String name, Long customerId) {
		Object obj = this.getSystemConfigService().getConfigByNameAndCustomerId(name, customerId);
		return obj;
	}

	static public ConfigManager getInstance() {
		return CacheHolder.instance;
	}

	public SystemConfigService getSystemConfigService() {
		if (this.systemConfigService == null) {
			this.systemConfigService = ApplicationContextUtil.getBean("systemConfigServiceImpl");
		}
		return this.systemConfigService;
	}

	public void setSystemConfigService(SystemConfigService systemConfigService) {
		this.systemConfigService = systemConfigService;
	}
}
