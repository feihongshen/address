
package cn.explink.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        ClientApplication application = this.clientApplicationDao.get(applicationVo.getId());
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

    public ClientApplication getClientApplicationById(long id) {

        ClientApplication application = this.clientApplicationDao.get(id);
        if (application == null) {
            throw new ExplinkRuntimeException("通过id=" + id + "查找应用客户失败");
        }

        return application;
    }

    public ClientApplication validateClientApplication(Long customId, String pwd) {
        if ((customId == null) || (customId.intValue() == 0)) {
            throw new ExplinkRuntimeException("customId  is null");
        }
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("customId", customId);
        map.put("pwd", pwd);
        List<ClientApplication> applications = this.clientApplicationDao.findListByHql("from  ClientApplication"
                + " where status=1 and customerId=:customId  and password=:pwd ", map);
        if ((applications == null) || (applications.size() == 0)) {
            throw new ExplinkRuntimeException("通过 customId=" + customId + ",password=" + pwd + " 查询失败!");
        }
        Object obj = applications.get(0);

        ClientApplication application = (ClientApplication) obj;

        if (application == null) {
            throw new ExplinkRuntimeException("application id is wrong");
        }

        return application;
    }
}
