package cn.explink.dao;

import java.util.List;

import org.hibernate.Query;
import org.springframework.stereotype.Repository;

import cn.explink.dao.support.BasicHibernateDaoSupport;
import cn.explink.domain.AddressPermission;

@Repository
public class AddressPermissionDao extends BasicHibernateDaoSupport<AddressPermission, Long> {

	public AddressPermissionDao() {
		super(AddressPermission.class);
	}

	public AddressPermission getPermissionByAddressAndCustomer(Long addressId, Long customerId) {
		String hql = "from AddressPermission where addressId = :addressId and customerId = :customerId";
		Query query = getSession().createQuery(hql);
		query.setLong("addressId", addressId);
		query.setLong("customerId", customerId);
		return (AddressPermission) query.uniqueResult();
	}

	public int batchUnbindAddress(List<Long> addressIdList, Long customerId) {
		String hql = " delete from AddressPermission where addressId in :addressIdList and customerId = :customerId";
		Query query = getSession().createQuery(hql);
		query.setParameterList("addressIdList", addressIdList);
		query.setLong("customerId", customerId);
		return query.executeUpdate();
	}

}
