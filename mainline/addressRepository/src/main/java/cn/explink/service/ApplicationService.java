package cn.explink.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.explink.dao.ClientApplicationDao;
import cn.explink.domain.ClientApplication;
import cn.explink.exception.ExplinkRuntimeException;
import cn.explink.ws.vo.ApplicationVo;

@Service
public class ApplicationService {

	@Autowired
	private ClientApplicationDao clientApplicationDao;

	public ClientApplication validateClientApplication(ApplicationVo applicationVo) {
		if (applicationVo == null) {
			throw new ExplinkRuntimeException("applicationVo is null");
		}
		ClientApplication application = clientApplicationDao.get(applicationVo.getId());
		if (application == null) {
			throw new ExplinkRuntimeException("application id is wrong");
		}
		if (!application.getPassword().equals(applicationVo.getPassword())) {
			throw new ExplinkRuntimeException("password is wrong");
		}
		if (!application.getCustomerId().equals(applicationVo.getCustomerId())) {
			throw new ExplinkRuntimeException("customerId is wrong");
		}
		return application;
	}

}
