package cn.explink.util;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class ApplicationContextUtil implements ApplicationContextAware {
	
	private static ApplicationContext applicationContext;

	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
		synchronized (ApplicationContextUtil.class) {
			ApplicationContextUtil.applicationContext = applicationContext;
			ApplicationContextUtil.class.notifyAll();
		}
	}

	public static ApplicationContext getApplicationContext() {
		synchronized (ApplicationContextUtil.class) {
			while (applicationContext == null) {
				try {
					ApplicationContextUtil.class.wait(60000);
				} catch (InterruptedException ex) {
				}
			}
			return applicationContext;
		}
	}

	@SuppressWarnings("unchecked")
	public static <T> T getBean(String name) {
		return (T) getApplicationContext().getBean(name);
	}

}