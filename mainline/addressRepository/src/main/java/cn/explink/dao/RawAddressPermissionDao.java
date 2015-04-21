package cn.explink.dao;

import java.util.List;

import org.hibernate.Query;
import org.springframework.stereotype.Repository;

import cn.explink.dao.support.BasicHibernateDaoSupport;
import cn.explink.domain.RawAddressPermission;

@Repository
public class RawAddressPermissionDao extends BasicHibernateDaoSupport<RawAddressPermission, Long> {

	public RawAddressPermissionDao() {
		super(RawAddressPermission.class);
	}

	public int batchUnbindAddress(List<Long> rawAddressIdList, Long customerId) {
		String hql = " delete from RawAddressPermission where rawAddressId in :rawAddressIdList and customerId = :customerId";
		Query query = this.getSession().createQuery(hql);
		query.setParameterList("rawAddressIdList", rawAddressIdList);
		query.setLong("customerId", customerId);
		return query.executeUpdate();
	}

}
