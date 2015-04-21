package cn.explink.dao;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.Query;
import org.springframework.stereotype.Repository;

import cn.explink.dao.support.BasicHibernateDaoSupport;
import cn.explink.domain.RawAddress;
import cn.explink.spliter.vo.RawAddressStationPair;

import com.sun.xml.bind.v2.model.core.ID;

@Repository
public class RawAddressDao extends BasicHibernateDaoSupport<RawAddress, ID> {

	public RawAddressDao() {
		super(RawAddress.class);
	}

	@SuppressWarnings("unchecked")
	public List<RawAddress> getRawAddressByNames(Collection<String> addressNames) {
		StringBuilder hql = new StringBuilder("from RawAddress a");
		hql.append(" where a.name in :addressNames");
		Query query = this.getSession().createQuery(hql.toString());
		query.setParameterList("addressNames", addressNames);
		return query.list();
	}

	@SuppressWarnings("unchecked")
	public List<RawAddress> getAdministrationRawAddress(Set<String> addressNames, Long customerId) {
		StringBuilder hql = new StringBuilder("select a from RawAddress  a, RawAddressPermission p ");
		hql.append(" where a.addressLevel in (1,2,3)");
		hql.append(" and a.id = p.rawAddressId");
		hql.append(" and p.customerId = :customerId");
		Query query = this.getSession().createQuery(hql.toString());
		query.setLong("customerId", customerId);
		return query.list();
	}

	@SuppressWarnings("unchecked")
	public List<RawAddress> getAllBands(Long customerId) {
		StringBuilder hql = new StringBuilder("select a  from RawAddress  a, RawAddressPermission p ");
		hql.append(" where a.addressLevel >3 ");
		hql.append(" and a.id = p.rawAddressId");
		hql.append(" and p.customerId = :customerId");
		Query query = this.getSession().createQuery(hql.toString());
		query.setLong("customerId", customerId);
		return query.list();
	}

	public List<RawAddressStationPair> getPageAddressList(Long customerId, Long rawAddressId) {
		StringBuffer sql = new StringBuffer("select r.raw_address_id , r.raw_delivery_station_id from raw_address a,raw_address_permissions p,raw_delivery_stations d, raw_delivery_station_rules r ");
		sql.append(this.getWhereSql(customerId, rawAddressId));
		Query query = this.getSession().createSQLQuery(sql.toString());
		@SuppressWarnings("unchecked")
		List<Object> data = query.list();

		return this.getAddrStatPairList(data);
	}

	// public int getRawAddressCount(Long customerId) {
	// StringBuffer sql = new
	// StringBuffer("select count(r.raw_address_id) from raw_address a,raw_address_permissions p,raw_delivery_stations d, raw_delivery_station_rules r ");
	// sql.append(this.getWhereSql(customerId));
	// Query query = this.getSession().createSQLQuery(sql.toString());
	// return ((Number) query.uniqueResult()).intValue();
	// }

	private StringBuffer getWhereSql(Long customerId, Long rawAddressId) {
		StringBuffer sql = new StringBuffer();
		sql.append(" where a.id=r.raw_address_id and a.id=p.raw_address_id and p.customer_id=d.customer_id and d.id=r.raw_delivery_station_id and d.customer_id=").append(customerId)
				.append(" and a.id=").append(rawAddressId);
		return sql;
	}

	private List<RawAddressStationPair> getAddrStatPairList(List<Object> data) {
		List<RawAddressStationPair> pairList = new ArrayList<RawAddressStationPair>();
		for (Object tmp : data) {
			pairList.add(this.createAddrStatPair(tmp));
		}
		return pairList;
	}

	private RawAddressStationPair createAddrStatPair(Object tmp) {
		Object[] parts = (Object[]) tmp;
		RawAddressStationPair pair = new RawAddressStationPair();
		pair.setRawAddressId((Integer) parts[0]);
		pair.setRawStationId((Integer) parts[1]);

		return pair;
	}

	public List<RawAddress> getFullPathAddrList(List<RawAddressStationPair> pairList) {
		Set<Long> addrIdSet = this.getAddressIdSet(pairList);
		List<RawAddress> addrList = this.getAddressList(addrIdSet);
		List<RawAddress> pathAddrList = this.getPathAddrList(addrList);

		addrList.addAll(pathAddrList);

		return addrList;
	}

	private List<RawAddress> getPathAddrList(List<RawAddress> addrList) {
		Set<Long> pathAddrIdSet = new HashSet<Long>();
		for (RawAddress addr : addrList) {
			this.fillAddrPathIdSet(pathAddrIdSet, addr);
		}
		return this.getAddressList(pathAddrIdSet);
	}

	private void fillAddrPathIdSet(Set<Long> pathIdSet, RawAddress addr) {
		String path = addr.getPath();
		if ((path == null) || path.isEmpty()) {
			return;
		}
		String[] parts = path.split("-");
		for (String part : parts) {
			pathIdSet.add(Long.valueOf(part));
		}
	}

	@SuppressWarnings("unchecked")
	private List<RawAddress> getAddressList(Set<Long> addrIdSet) {
		if ((null == addrIdSet) || (addrIdSet.size() == 0)) {
			return new ArrayList<RawAddress>();
		}
		String hql = "from RawAddress a where a.id in(:ids)";
		Query query = this.getSession().createQuery(hql);
		query.setParameterList("ids", addrIdSet);

		return query.list();
	}

	private Set<Long> getAddressIdSet(List<RawAddressStationPair> pairList) {
		Set<Long> addrIdSet = new HashSet<Long>();
		for (RawAddressStationPair tmp : pairList) {
			addrIdSet.add(tmp.getRawAddressId());
		}
		return addrIdSet;
	}

}
