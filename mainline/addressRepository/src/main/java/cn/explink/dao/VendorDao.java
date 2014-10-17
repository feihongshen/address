package cn.explink.dao;

import java.util.List;

import org.hibernate.Query;
import org.springframework.stereotype.Repository;

import cn.explink.dao.support.BasicHibernateDaoSupport;
import cn.explink.domain.Vendor;
import cn.explink.modle.ComboBox;


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

	public List<ComboBox> getComBoxDeliveryStation(Long customerId) {
		String hql = "select new cn.explink.modle.ComboBox(ds.id,ds.name) from Vendor ds where customer.id = :customerId ";
		Query query = getSession().createQuery(hql);
		query.setLong("customerId", customerId);
		return query.list();
	}

	public void deleteAgingByIds(List<Long> addressIdList, Long customerId) {
		if(addressIdList!=null&&addressIdList.size()>0&&customerId!=null){
			String sql = "delete from VENDORS_AGING where ADDRESS_ID in:list and CUSTOMER_ID=:customerId";
			Query query = getSession().createSQLQuery(sql)
					.setParameterList("list", addressIdList)
					.setLong("customerId", customerId);
			query.executeUpdate();
		}
	}
	
}
