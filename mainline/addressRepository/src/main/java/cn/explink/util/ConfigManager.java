package cn.explink.util;

import cn.explink.service.SystemConfigService;


public class ConfigManager {
	private SystemConfigService systemConfigService;
	private static class CacheHolder{
		static ConfigManager instance = new ConfigManager();
	}
	private ConfigManager(){
		 
	}
	public Object get(String key){
		Object obj =this.getSystemConfigService().getConfig(key);
		return obj ;
	}
	static public ConfigManager getInstance(){
		return CacheHolder.instance;
	}
	 
		public SystemConfigService getSystemConfigService() {
			if(systemConfigService==null){
				systemConfigService=ApplicationContextUtil.getBean("systemConfigServiceImpl");
			}
			return systemConfigService;
		}

		public void setSystemConfigService(SystemConfigService systemConfigService) {
			this.systemConfigService = systemConfigService;
		}
}
