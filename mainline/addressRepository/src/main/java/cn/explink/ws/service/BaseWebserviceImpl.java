package cn.explink.ws.service;

import cn.explink.domain.ClientApplication;
import cn.explink.service.ApplicationService;
import cn.explink.util.ApplicationContextUtil;
import cn.explink.ws.vo.ApplicationVo;

public class BaseWebserviceImpl {

	protected ClientApplication validateApplication(ApplicationVo applicationVo) {
		ApplicationService applicationService = ApplicationContextUtil.getBean("applicationService");
		ClientApplication clientApplication = applicationService.validateClientApplication(applicationVo);
		return clientApplication;
	}
}
