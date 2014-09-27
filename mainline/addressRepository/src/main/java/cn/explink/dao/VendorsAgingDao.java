package cn.explink.dao;

import java.util.List;

import org.hibernate.Query;
import org.springframework.stereotype.Repository;

import cn.explink.dao.support.BasicHibernateDaoSupport;
import cn.explink.domain.VendorsAging;

@Repository
public class VendorsAgingDao extends BasicHibernateDaoSupport<VendorsAging, Long> {

	public VendorsAgingDao() {
		super(VendorsAging.class);
	}

	@SuppressWarnings("unchecked")
	public List<VendorsAging> getVendorAging(Long addressId, Long vendorId, Long customerId) {
		Query query = getSession().createQuery("from VendorsAging where address.id = :addressId and customer.id = :custmerId and vendor.id = :vendorId");
		query.setLong("custmerId", customerId);
		query.setLong("addressId", addressId);
		query.setLong("vendorId", vendorId);
		return query.list();
	}
}
