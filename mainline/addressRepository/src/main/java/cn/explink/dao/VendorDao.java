package cn.explink.dao;

import org.hibernate.Query;
import org.springframework.stereotype.Repository;

import cn.explink.dao.support.BasicHibernateDaoSupport;
import cn.explink.domain.Vendor;


@Repository
public class VendorDao extends BasicHibernateDaoSupport<Vendor, Long> {

	public VendorDao() {
		super(Vendor.class);
	}

	public Vendor getVendor(Long externalId, Long customerId) {
		String hql = "from Vendor where externalId = :externalId and customer.id = :customerId";
		Query query = getSession().createQuery(hql);
		query.setLong("externalId", externalId);
		query.setLong("customerId", customerId);
		return (Vendor) query.uniqueResult();
	}
	
}
