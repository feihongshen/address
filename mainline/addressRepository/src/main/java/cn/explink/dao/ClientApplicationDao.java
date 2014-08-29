package cn.explink.dao;

import org.springframework.stereotype.Repository;

import cn.explink.dao.support.BasicHibernateDaoSupport;
import cn.explink.domain.ClientApplication;

@Repository
public class ClientApplicationDao extends BasicHibernateDaoSupport<ClientApplication, Long> {

	public ClientApplicationDao() {
		super(ClientApplication.class);
	}

}
