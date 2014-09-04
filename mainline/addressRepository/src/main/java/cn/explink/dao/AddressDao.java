package cn.explink.dao;

import java.util.Collection;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.springframework.stereotype.Repository;

import cn.explink.dao.support.BasicHibernateDaoSupport;
import cn.explink.domain.Address;


@Repository
public class AddressDao extends BasicHibernateDaoSupport<Address, Long> {

	public AddressDao() {
		super(Address.class);
	}
	
	public List<Address> getAllAddresses() {
		String hql = "from Address";
		Query query = getSession().createQuery(hql);
		return query.list();
	}

	public List<Address> getBaseAddress(int start, int end) {
		String sql = "from Address where addressLevel <= 3";
		Query query = getSession().createQuery(sql);
		query.setFirstResult(start);
		query.setMaxResults(end);
		return query.list();
	}

	public void baseAddressIndexed() {
		String hql = "update Address set indexed = 1 where addressLevel <= 3";
		Query query = getSession().createQuery(hql);
		query.executeUpdate();
	}

	public List<Address> getAddressByIdList(List<Long> idList) {
		String hql = "from Address where id in :idList";
		Query query = getSession().createQuery(hql);
		query.setParameterList("idList", idList);
		return query.list();
	}

	public List<Address> getChildAddress(Long customerId, Long parentId) {
		StringBuilder hql = new StringBuilder("select a.* from Address a, AddressPermission p");
		hql.append(" where a.parentId = :parentId");
		hql.append(" and a.addressId = p.addressId");
		hql.append(" and p.customerId = :customerId");
		Query query = getSession().createQuery(hql.toString());
		query.setLong("parentId", parentId);
		query.setLong("customerId", customerId);
		return query.list();
	}

	public List<Address> getAddressByNames(Collection<String> addressNames) {
		StringBuilder hql = new StringBuilder("from Address a");
		hql.append(" where a.name in :addressNames");
		Query query = getSession().createQuery(hql.toString());
		query.setParameterList("addressNames", addressNames);
		return query.list();
	}

	public Address getAddressByName(String name) {
		StringBuilder hql = new StringBuilder("from Address where name = :name");
		Query query = getSession().createQuery(hql.toString());
		query.setString("name", name);
		return (Address) query.uniqueResult();
	}

	public List<Address> getAddressByIdListAndCustomerId(List<Long> addressIdList, Long customerId) {
		StringBuilder hql = new StringBuilder("select a from Address a, AddressPermission p");
		hql.append(" where a.id = p.addressId");
		hql.append(" and a.id in :addressIdList");
		hql.append(" and p.customerId = :customerId");
		Query query = getSession().createQuery(hql.toString());
		query.setParameterList("addressIdList", addressIdList);
		query.setLong("customerId", customerId);
		return query.list();
	}
	
}
