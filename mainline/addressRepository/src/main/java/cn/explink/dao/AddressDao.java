package cn.explink.dao;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.springframework.stereotype.Repository;

import cn.explink.dao.support.BasicHibernateDaoSupport;
import cn.explink.domain.Address;
import cn.explink.domain.AddressPermission;
import cn.explink.domain.DeliveryStationRule;
import cn.explink.tree.ZTreeNode;


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
		StringBuilder hql = new StringBuilder("select new cn.explink.domain.Address(a.id, a.name, a.addressLevel, a.parentId, a.path) from Address a, AddressPermission p");
		hql.append(" where a.parentId = :parentId");
		hql.append(" and a.id = p.addressId");
		hql.append(" and p.customerId = :customerId");
		Query query = getSession().createQuery(hql.toString());
//		Criteria criteria = getSession().createCriteria(Address.class);
		query.setLong("parentId", parentId);
		query.setLong("customerId", customerId);
		return query.list();
	}
	
	public List<ZTreeNode> getAsyncAddress(Long customerId, Long parentId) {
		StringBuilder hql = new StringBuilder("select new cn.explink.tree.ZTreeNode( a.name,a.id,a.parentId ,a.addressLevel) from Address a, AddressPermission p");
		if(parentId==null){
			hql.append(" where a.parentId is null");
		}else{
			hql.append(" where a.parentId = "+parentId);
		}
		hql.append(" and a.id = p.addressId");
		hql.append(" and p.customerId = :customerId");
		Query query = getSession().createQuery(hql.toString());
		query.setLong("customerId", customerId);
		return query.list();
	}
	
	public List<ZTreeNode> getZTree(Long customerId,String name,StringBuffer sb) {
		StringBuilder hql = new StringBuilder("select new cn.explink.tree.ZTreeNode( a.name,a.id,a.parentId,a.addressLevel )from Address a, AddressPermission p ");
		hql.append(" where 1 = 1");
		if(null!=sb){
			String ids=sb.substring(0, sb.length()-1);
			hql.append(" and a.id not in("+ids+")");
		}
		if(StringUtils.isNotEmpty(name)){
			hql.append(" and a.id = p.addressId");
			hql.append(" and p.customerId = :customerId");
			hql.append(" and a.name like :name");
			Query query = getSession().createQuery(hql.toString());
			query.setLong("customerId", customerId);
			query.setString("name", "%"+name+"%");
			return query.list();
		}else{
			hql.append(" and a.id = p.addressId");
			hql.append(" and p.customerId = :customerId");
			Query query = getSession().createQuery(hql.toString());
			query.setLong("customerId", customerId);
			return query.list();
		}
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
    /**
     * 通过地址名称和父ID获取地址（结果唯一）
     * @param name
     * @param parentId
     * @return
     */
	public Address getAddressByNameAndPid(String name, Long parentId) {
		Query query =getSession().createQuery("from Address where name=:name and parentId=:parentId ")
				.setString("name", name).setLong("parentId", parentId);
		return (Address) query.uniqueResult();	
	}

	public DeliveryStationRule getStationRuleByAddressAndStation(Long addressId,
			Long stationId) {
		String hql = "from DeliveryStationRule where address.id = :addressId and deliveryStation.id = :stationId";
		Query query = getSession().createQuery(hql);
		query.setLong("addressId", addressId);
		query.setLong("stationId", stationId);
		return (DeliveryStationRule) query.uniqueResult();
	}

	public List<Address> getChildAllAddress(Long customerId,String path,String pathLike) {
		StringBuilder hql = new StringBuilder("select new cn.explink.domain.Address(a.id, a.name, a.addressLevel, a.parentId, a.path) from Address a ,AddressPermission p");
		hql.append(" where ( a.path like :pathlike or a.path =:path )");
		hql.append(" and a.id = p.addressId");
		hql.append(" and p.customerId = :customerId");
		Query query = getSession().createQuery(hql.toString()) ;
		query.setString("pathlike",  pathLike);
		query.setString("path",  path );
		query.setLong("customerId", customerId);
		return query.list(); 
	}

	public List<ZTreeNode> getZTreeNodeByIdListAndCustomerId(Set<Long> set,
			Long customerId) {
		StringBuilder hql = new StringBuilder("select new cn.explink.tree.ZTreeNode( a.name,a.id,a.parentId,a.addressLevel ) from Address a, AddressPermission p");
		hql.append(" where a.id = p.addressId");
		hql.append(" and a.id in :addressIdList");
		hql.append(" and p.customerId = :customerId");
		Query query = getSession().createQuery(hql.toString());
		query.setParameterList("addressIdList", set);
		query.setLong("customerId", customerId);
		return query.list();
	}
    /**
     * 更新地址索引标识
     * @param id
     */
	public void updateAddressIndex(Long id) {
		String hql = "update Address set indexed = 1 where id =:id";
		Query query = getSession().createQuery(hql).setLong("id", id);
		query.executeUpdate();
	}



	
}
