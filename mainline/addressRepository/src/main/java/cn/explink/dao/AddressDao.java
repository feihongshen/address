package cn.explink.dao;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Query;
import org.springframework.stereotype.Repository;

import cn.explink.dao.support.BasicHibernateDaoSupport;
import cn.explink.dao.support.DataInfo;
import cn.explink.domain.Address;
import cn.explink.domain.DeliveryStationRule;
import cn.explink.quick.AddressStationPair;
import cn.explink.tree.ZTreeNode;

@Repository
public class AddressDao extends BasicHibernateDaoSupport<Address, Long> {

	public AddressDao() {
		super(Address.class);
	}

	public List<Address> getAllAddresses() {
		String hql = "from Address";
		Query query = this.getSession().createQuery(hql);
		return query.list();
	}

	public List<Address> getBaseAddress(int start, int end) {
		// String sql = "from Address where addressLevel <= 3";
		String sql = "from Address where status = 1";
		Query query = this.getSession().createQuery(sql);
		query.setFirstResult(start);
		query.setMaxResults(end);
		return query.list();
	}

	public void baseAddressIndexed() {
		String hql = "update Address set indexed = 1 where addressLevel <= 3";
		Query query = this.getSession().createQuery(hql);
		query.executeUpdate();
	}

	public List<Address> getAddressByIdList(List<Long> idList) {
		if ((idList != null) && !idList.isEmpty()) {
			String hql = "from Address where id in :idList";
			Query query = this.getSession().createQuery(hql);
			query.setParameterList("idList", idList);
			return query.list();
		} else {
			return new ArrayList<Address>();
		}
	}

	/**
	 *
	 * @Title: getAddressByNameList
	 * @description 根据关键词名字的集合，找出address对象的集合
	 * @author 刘武强
	 * @date  2015年11月26日下午7:37:21
	 * @param  @param NameList
	 * @param  @return
	 * @return  List<Address>
	 * @throws
	 */
	public List<Address> getAddressByNameList(List<String> NameList) {
		if ((NameList != null) && !NameList.isEmpty()) {
			String hql = "from Address where name in :NameList";
			Query query = this.getSession().createQuery(hql);
			query.setParameterList("idList", NameList);
			return query.list();
		} else {
			return new ArrayList<Address>();
		}
	}

	public List<Address> getAddressByIdSet(Set<Long> idSet) {
		return this.getAddressByIdList(new ArrayList<Long>(idSet));
	}

	public List<Address> getChildAddress(Long customerId, Long parentId, Long deliveryStationId) {
		Address parent = this.get(parentId);
		if ((parent != null) && (parent.getAddressLevel() > 2)) {
			String bindSql = " select concat(a.path,'-',CAST(a.ID AS CHAR)) from DELIVERY_STATION_RULES r ," + " DELIVERY_STATIONS d ," + " ADDRESS a where a.ID=r.ADDRESS_ID " + " and r.DELIVERY_STATION_ID=d.ID " + " and d.STATUS=1" + " and d.CUSTOMER_ID=:customerId and d.EXTERNAL_ID=:deliveryStationId";
			List<Object> bindsList = this.getSession().createSQLQuery(bindSql).setLong("customerId", customerId).setLong("deliveryStationId", deliveryStationId).list();
			Set<String> binds = new HashSet<String>();
			if ((bindsList != null) && !bindsList.isEmpty()) {
				for (Object a : bindsList) {
					String[] ps = (a + "").split("-");
					for (int j = 0; j < ps.length; j++) {
						binds.add(ps[j]);
					}
				}
			}
			if ((binds != null) && !binds.isEmpty()) {
				String ids = "";
				for (String i : binds) {
					ids += i + ",";
				}
				ids = ids.substring(0, ids.length() - 1);
				StringBuilder hql = new StringBuilder("select a from Address a, AddressPermission p");
				hql.append(" where a.parentId = :parentId");
				hql.append(" and a.id = p.addressId");
				hql.append(" and a.id in ( " + ids + ")");
				hql.append(" and p.customerId = :customerId");
				Query query = this.getSession().createQuery(hql.toString());
				query.setLong("parentId", parentId);
				query.setLong("customerId", customerId);
				return query.list();
			} else {
				return new ArrayList<Address>();
			}
		} else {
			StringBuilder hql = new StringBuilder("select a from Address a, AddressPermission p");
			hql.append(" where a.parentId = :parentId");
			hql.append(" and p.customerId = :customerId");
			hql.append(" and p.addressId = a.id");
			Query query = this.getSession().createQuery(hql.toString());
			query.setLong("parentId", parentId);
			query.setLong("customerId", customerId);
			return query.list();
		}
	}

	// zTree 异步方法不能使用这个方法，这个事用来load data for initalize tree
	public List<ZTreeNode> getAsyncAddress(Long customerId, Long parentId, String ids) {
		StringBuilder hql = new StringBuilder("select new cn.explink.tree.ZTreeNode( a.name,a.id,a.parentId ,a.addressLevel) from Address a, AddressPermission p");
		if (parentId == null) {
			if (StringUtils.isNotBlank(ids)) {
				hql.append(" where a.id in (" + ids + ")");
			} else {
				hql.append(" where a.parentId < 2");
			}
		} else {
			hql.append(" where a.parentId = " + parentId);
		}
		hql.append(" and a.id = p.addressId");
		hql.append(" and p.customerId = :customerId");
		Query query = this.getSession().createQuery(hql.toString());
		query.setLong("customerId", customerId);
		return query.list();
	}

	public List<ZTreeNode> getZTree(Long customerId, String name, StringBuffer sb) {
		StringBuilder hql = new StringBuilder("select new cn.explink.tree.ZTreeNode( a.name,a.id,a.parentId,a.addressLevel )from Address a, AddressPermission p ");
		hql.append(" where 1 = 1");
		if (null != sb) {
			String ids = sb.substring(0, sb.length() - 1);
			hql.append(" and a.id not in(" + ids + ")");
		}
		if (StringUtils.isNotEmpty(name)) {
			hql.append(" and a.id = p.addressId");
			hql.append(" and p.customerId = :customerId");
			hql.append(" and a.name like :name");
			Query query = this.getSession().createQuery(hql.toString());
			query.setLong("customerId", customerId);
			query.setString("name", "%" + name + "%");
			return query.list();
		} else {
			hql.append(" and a.id = p.addressId");
			hql.append(" and p.customerId = :customerId");
			Query query = this.getSession().createQuery(hql.toString());
			query.setLong("customerId", customerId);
			return query.list();
		}
	}

	public List<Address> getAddressByNames(Collection<String> addressNames) {
		StringBuilder hql = new StringBuilder("from Address a");
		hql.append(" where a.name in :addressNames");
		Query query = this.getSession().createQuery(hql.toString());
		query.setParameterList("addressNames", addressNames);
		return query.list();
	}

	public List<Address> getAddressByNames(Collection<String> addressNames, Long customerId) {
		if ((addressNames != null) && !addressNames.isEmpty()) {
			StringBuilder hql = new StringBuilder("select a from Address  a, AddressPermission p ");
			hql.append(" where a.name in :addressNames");
			hql.append(" and a.id = p.addressId");
			hql.append(" and p.customerId = :customerId");
			Query query = this.getSession().createQuery(hql.toString());
			query.setLong("customerId", customerId);
			query.setParameterList("addressNames", addressNames);
			return query.list();
		} else {
			return null;
		}

	}

	public Address getAddressByName(String name) {
		StringBuilder hql = new StringBuilder("from Address where name = :name");
		Query query = this.getSession().createQuery(hql.toString());
		query.setString("name", name);
		return (Address) query.uniqueResult();
	}

	public List<Address> getAddressByIdListAndCustomerId(List<Long> addressIdList, Long customerId) {
		StringBuilder hql = new StringBuilder("select a from Address a, AddressPermission p");
		hql.append(" where a.id = p.addressId");
		hql.append(" and a.id in :addressIdList");
		hql.append(" and p.customerId = :customerId");
		Query query = this.getSession().createQuery(hql.toString());
		query.setParameterList("addressIdList", addressIdList);
		query.setLong("customerId", customerId);
		return query.list();
	}

	/**
	 * 快捷工具接口(广州通路).
	 *
	 * @param page
	 * @param pageSize
	 * @return
	 */
	public List<AddressStationPair> getPageAddressList(int page, int pageSize) {
		String sql = "select address_id , delivery_station_id from delivery_station_rules";
		Query query = this.getSession().createSQLQuery(sql);
		query.setFirstResult((page - 1) * pageSize);
		query.setMaxResults(pageSize);
		@SuppressWarnings("unchecked")
		List<Object> data = query.list();

		return this.getAddrStatPairList(data);
	}

	public List<Address> getFullPathAddrList(List<AddressStationPair> pairList) {
		Set<Long> addrIdSet = this.getAddressIdSet(pairList);
		List<Address> addrList = null;
		if (addrIdSet.size() == 0) {
			addrList = new ArrayList<Address>();
		} else {
			addrList = this.getAddressList(addrIdSet);
		}
		List<Address> pathAddrList = this.getPathAddrList(addrList);

		addrList.addAll(pathAddrList);

		return addrList;
	}

	private List<Address> getPathAddrList(List<Address> addrList) {
		Set<Long> pathAddrIdSet = new HashSet<Long>();
		for (Address addr : addrList) {
			this.fillAddrPathIdSet(pathAddrIdSet, addr);
		}
		if (pathAddrIdSet.size() == 0) {
			return new ArrayList<Address>();
		} else {
			return this.getAddressList(pathAddrIdSet);
		}
	}

	private void fillAddrPathIdSet(Set<Long> pathIdSet, Address addr) {
		String path = addr.getPath();
		if ((path == null) || path.isEmpty()) {
			return;
		}
		String[] parts = path.split("-");
		for (String part : parts) {
			pathIdSet.add(Long.valueOf(part));
		}
	}

	public List<Address> getAddressList(Set<Long> addrIdSet) {
		String hql = "from Address a where a.id in(:ids)";
		Query query = this.getSession().createQuery(hql);
		query.setParameterList("ids", addrIdSet);

		return query.list();
	}

	private Set<Long> getAddressIdSet(List<AddressStationPair> pairList) {
		Set<Long> addrIdSet = new HashSet<Long>();
		for (AddressStationPair tmp : pairList) {
			addrIdSet.add(tmp.getAddressId());
		}
		return addrIdSet;
	}

	private List<AddressStationPair> getAddrStatPairList(List<Object> data) {
		List<AddressStationPair> pairList = new ArrayList<AddressStationPair>();
		for (Object tmp : data) {
			pairList.add(this.createAddrStatPair(tmp));
		}
		return pairList;
	}

	private AddressStationPair createAddrStatPair(Object tmp) {
		Object[] parts = (Object[]) tmp;
		AddressStationPair pair = new AddressStationPair();
		pair.setAddressId((Integer) parts[0]);
		pair.setStationId((Integer) parts[1]);

		return pair;
	}

	/**
	 * 通过地址名称和父ID获取地址（结果唯一）
	 *
	 * @param name
	 * @param parentId
	 * @return
	 */
	public Address getAddressByNameAndPid(String name, Long parentId) {
		Query query = this.getSession().createQuery("from Address  where name=:name and  parentId=:parentId  ").setString("name", name).setLong("parentId", parentId);
		List<Address> list = query.list();
		if ((list != null) && (list.size() > 0)) {
			return list.get(0);
		}
		return null;
	}

	public DeliveryStationRule getStationRuleByAddressAndStation(Long addressId, Long stationId) {
		String hql = "from DeliveryStationRule where address.id = :addressId and deliveryStation.id = :stationId";
		Query query = this.getSession().createQuery(hql);
		query.setLong("addressId", addressId);
		query.setLong("stationId", stationId);
		return (DeliveryStationRule) query.uniqueResult();
	}

	public List<Address> getChildAllAddress(Long customerId, String path, String pathLike) {
		StringBuilder hql = new StringBuilder("select new cn.explink.domain.Address(a.id, a.name, a.addressLevel, a.parentId, a.path) from Address a ,AddressPermission p");
		hql.append(" where ( a.path like :pathlike or a.path =:path )");
		hql.append(" and a.id = p.addressId");
		hql.append(" and p.customerId = :customerId");
		Query query = this.getSession().createQuery(hql.toString());
		query.setString("pathlike", pathLike);
		query.setString("path", path);
		query.setLong("customerId", customerId);
		return query.list();
	}

	public List<ZTreeNode> getZTreeNodeByIdListAndCustomerId(Set<Long> set, Long customerId) {
		StringBuilder hql = new StringBuilder("select new cn.explink.tree.ZTreeNode( a.name,a.id,a.parentId,a.addressLevel ) from Address a, AddressPermission p");
		hql.append(" where a.id = p.addressId");
		hql.append(" and a.id in :addressIdList");
		hql.append(" and p.customerId = :customerId");
		Query query = this.getSession().createQuery(hql.toString());
		query.setParameterList("addressIdList", set);
		query.setLong("customerId", customerId);
		return query.list();
	}

	public List<ZTreeNode> getZTreeNodeByIdListAndCustomerId(String ids, Long customerId) {
		StringBuilder hql = new StringBuilder("select new cn.explink.tree.ZTreeNode( a.name,a.id,a.parentId,a.addressLevel ) from Address a, AddressPermission p");
		hql.append(" where a.id = p.addressId");
		hql.append(" and a.id in (" + ids + ")");
		hql.append(" and p.customerId = :customerId");
		Query query = this.getSession().createQuery(hql.toString());
		query.setLong("customerId", customerId);
		return query.list();
	}

	/**
	 * 更新地址索引标识
	 *
	 * @param id
	 */
	public void updateAddressIndex(Long id) {
		String hql = "update Address set indexed = 1 where id =:id";
		Query query = this.getSession().createQuery(hql).setLong("id", id);
		query.executeUpdate();
	}

	public List<Address> getAdministrationAddress(Set<String> addressNames, Long customerId) {
		StringBuilder hql = new StringBuilder("select a from Address  a, AddressPermission p ");
		hql.append(" where a.addressLevel in (1,2,3)");
		hql.append(" and a.id = p.addressId");
		hql.append(" and p.customerId = :customerId");
		Query query = this.getSession().createQuery(hql.toString());
		query.setLong("customerId", customerId);
		return query.list();
	}

	public List<Address> getAllBands(Long customerId) {
		StringBuilder hql = new StringBuilder("select a  from Address  a, AddressPermission p ");
		hql.append(" where a.addressLevel >3 ");
		hql.append(" and a.id = p.addressId");
		hql.append(" and p.customerId = :customerId");
		Query query = this.getSession().createQuery(hql.toString());
		query.setLong("customerId", customerId);
		return query.list();
	}

	public DeliveryStationRule getDefaultStation(Long addressId) {
		String hql = "from DeliveryStationRule" + " where address.id = :addressId" + " and ruleType=1 ";
		Query query = this.getSession().createQuery(hql);
		query.setLong("addressId", addressId);
		List<DeliveryStationRule> list = query.list();
		if ((list != null) && !list.isEmpty()) {
			return list.get(0);
		} else {
			return null;
		}
	}

	public List<ZTreeNode> getAsyncAddressPage(Long customerId, Long parentId, String ids, Integer page, Integer pageSize) {
		StringBuilder hql = new StringBuilder("select new cn.explink.tree.ZTreeNode( a.name,a.id,a.parentId ,a.addressLevel) from Address a, AddressPermission p");
		if (parentId == null) {
			if (StringUtils.isNotBlank(ids)) {
				hql.append(" where a.id in (" + ids + ")");
			} else {
				hql.append(" where a.parentId < 2");
			}
		} else {
			hql.append(" where a.parentId = " + parentId);
		}
		hql.append(" and a.id = p.addressId");
		hql.append(" and p.customerId = :customerId");

		StringBuilder hqlCount = new StringBuilder("select count(a.id) from Address a, AddressPermission p");
		if (parentId == null) {
			if (StringUtils.isNotBlank(ids)) {
				hqlCount.append(" where a.id in (" + ids + ")");
			} else {
				hqlCount.append(" where a.parentId < 2");
			}
		} else {
			hqlCount.append(" where a.parentId = " + parentId);
		}
		hqlCount.append(" and a.id = p.addressId");
		hqlCount.append(" and p.customerId = :customerId");
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("customerId", customerId);
		DataInfo di = this.findByHql(hql.toString(), hqlCount.toString(), page, pageSize, param);
		List<ZTreeNode> l = (List<ZTreeNode>) di.getResult();
		for (int i = 0; i < l.size(); i++) {
			if (i == 0) {
				l.get(i).setMaxPage(di.getPageCount());
				l.get(i).setPage(di.getPage());
			} else {
				l.get(i).setPageSize(pageSize);
				l.get(i).setPage(1);
			}
		}
		return l;
	}
}
