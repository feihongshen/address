package cn.explink.dao;

import org.hibernate.Query;
import org.springframework.stereotype.Repository;

import cn.explink.dao.support.BasicHibernateDaoSupport;
import cn.explink.domain.Deliverer;

@Repository
public class DelivererDao extends BasicHibernateDaoSupport<Deliverer, Long> {

	public DelivererDao() {
		super(Deliverer.class);
	}

	public Deliverer getDeliverer(Long externalVendorId, Long customerId) {
		String hql = "from Deliverer where externalVendorId = :externalVendorId and customer.id = :customerId";
		Query query = getSession().createQuery(hql);
		query.setLong("externalVendorId", externalVendorId);
		query.setLong("customerId", customerId);
		return (Deliverer) query.uniqueResult();
	}

}
