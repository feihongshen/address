package cn.explink.dao;

import java.util.List;

import org.hibernate.Query;
import org.springframework.stereotype.Repository;

import cn.explink.dao.support.BasicHibernateDaoSupport;
import cn.explink.domain.Alias;

@Repository
public class AliasDao extends BasicHibernateDaoSupport<Alias, Long> {

	public AliasDao() {
		super(Alias.class);
	}

	public List<Alias> getAliasByAddressId(Long addressId) {
		String hql = "from Alias where addressId = :addressId";
		Query query = getSession().createQuery(hql);
		query.setLong("addressId", addressId);
		return query.list();
	}

	public List<Alias> getAliasByIdList(List<Long> idList) {
		String hql = "from Alias where id in :idList";
		Query query = getSession().createQuery(hql);
		query.setParameterList("idList", idList);
		return query.list();
	}

}
