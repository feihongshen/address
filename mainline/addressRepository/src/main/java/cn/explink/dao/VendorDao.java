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

	public Vendor getVendor(Long externalVendorId, Long customerId) {
		String hql = "from Vendor where externalVendorId = :externalVendorId and customer.id = :customerId";
		Query query = getSession().createQuery(hql);
		query.setLong("externalVendorId", externalVendorId);
		query.setLong("customerId", customerId);
		return (Vendor) query.uniqueResult();
	}
	
}
