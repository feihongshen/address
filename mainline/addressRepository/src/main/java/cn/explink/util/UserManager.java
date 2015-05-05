package cn.explink.util;

import org.springframework.security.core.userdetails.UserDetailsService;

public class UserManager {
	private UserDetailsService userDetailsService;

	private static class CacheHolder {
		static UserManager instance = new UserManager();
	}

	private UserManager() {

	}

	public Object getByUsername(String username) {
		Object obj = this.getUserDetailsService().loadUserByUsername(username);
		return obj;
	}

	static public UserManager getInstance() {
		return CacheHolder.instance;
	}

	public UserDetailsService getUserDetailsService() {
		if (this.userDetailsService == null) {
			this.userDetailsService = ApplicationContextUtil.getBean("userService");
		}
		return this.userDetailsService;
	}

	public void setUserDetailsService(UserDetailsService userDetailsService) {
		this.userDetailsService = userDetailsService;
	}

}
