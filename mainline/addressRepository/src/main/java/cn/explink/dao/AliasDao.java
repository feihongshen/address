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
		if(idList!=null&&!idList.isEmpty()){
			String hql = "from Alias where id in :idList";
			Query query = getSession().createQuery(hql);
			query.setParameterList("idList", idList);
			return query.list();
		}else{
			return null;
		}
	}

	public Alias getAliasByAddressIdAndAlias(Long addressId, String alias,Long customerId) {
		String hql = "from Alias where addressId = :addressId and name=:name and customerId=:customerId";
		Query query = getSession().createQuery(hql);
		query.setLong("addressId", addressId);
		query.setString("name", alias);
		query.setLong("customerId", customerId);
		return (Alias) query.uniqueResult();
	}

	public List<Alias> getAliasByAddressIdAndCustomerId(Long addressId,
			Long customerId) {
		String hql = "from Alias where addressId = :addressId and customerId=:customerId";
		Query query = getSession().createQuery(hql);
		query.setLong("addressId", addressId);
		query.setLong("customerId", customerId);
		return query.list();
	}

	public void deleteAliasByIds(List<Long> addressIdList, Long customerId) {
		String hql = " delete from  Alias where addressId in :addressIdList and customerId = :customerId";
		Query query = getSession().createQuery(hql);
		query.setParameterList("addressIdList", addressIdList);
		query.setLong("customerId", customerId);
		query.executeUpdate();
	}

}
