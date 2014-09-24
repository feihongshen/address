package cn.explink.dao;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Query;
import org.springframework.stereotype.Repository;

import cn.explink.dao.support.BasicHibernateDaoSupport;
import cn.explink.domain.Deliverer;
import cn.explink.domain.DeliveryStation;

@Repository
public class DelivererDao extends BasicHibernateDaoSupport<Deliverer, Long> {

	public DelivererDao() {
		super(Deliverer.class);
	}

	public Deliverer getDeliverer(Long customerId, Long externalId) {
		String hql = "from Deliverer where externalId = :externalId and customer.id = :customerId";
		Query query = getSession().createQuery(hql);
		query.setLong("externalId", externalId);
		query.setLong("customerId", customerId);
		return (Deliverer) query.uniqueResult();
	}

	public Deliverer getByNameAndCustomerId(String delivererName,
			Long customerId) {
		String hql = "from Deliverer where name = :name and customer.id = :customerId";
		Query query = getSession().createQuery(hql);
		query.setString("name", delivererName);
		query.setLong("customerId", customerId);
		return (Deliverer) query.uniqueResult();
	}

	public List<Deliverer> listAll(Long customerId) {
		if(customerId==null){
			return new ArrayList<Deliverer>();
		}
		String hql = "from Deliverer where customer.id=:customerId and status=1";
		Query query = getSession().createQuery(hql);
		query.setLong("customerId", customerId);
		return query.list();
	}

}
