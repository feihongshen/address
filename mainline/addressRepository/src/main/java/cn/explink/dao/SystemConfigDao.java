package cn.explink.dao;

import org.hibernate.Query;
import org.springframework.stereotype.Repository;

import cn.explink.dao.support.BasicHibernateDaoSupport;
import cn.explink.domain.SystemConfig;

@Repository
public class SystemConfigDao extends BasicHibernateDaoSupport<SystemConfig, Long> {

	public SystemConfigDao() {
		super(SystemConfig.class);
	}

	public SystemConfig getSystemConfigByName(String name) {
		String hql = "from SystemConfig where name = :name";
		Query query = getSession().createQuery(hql);
		query.setString("name", name);
		return (SystemConfig) query.uniqueResult();
	}

}
