package cn.explink.dao;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.Query;
import org.springframework.stereotype.Repository;

import cn.explink.dao.support.BasicHibernateDaoSupport;
import cn.explink.domain.RawDeliveryStation;
import cn.explink.spliter.vo.RawAddressStationPair;

@Repository
public class RawDeliveryStationDao extends BasicHibernateDaoSupport<RawDeliveryStation, Long> {

	public RawDeliveryStationDao() {
		super(RawDeliveryStation.class);
	}

	@SuppressWarnings("unchecked")
	public List<RawDeliveryStation> listAll(Long customerId) {
		if (customerId == null) {
			return new ArrayList<RawDeliveryStation>();
		}
		String hql = "from RawDeliveryStation where customer.id=:customerId and status=1";
		Query query = this.getSession().createQuery(hql);
		query.setLong("customerId", customerId);
		return query.list();
	}

	public RawDeliveryStation getDeliveryStation(Long customerId, String name) {
		String hql = "from RawDeliveryStation where customer.id = :customerId and name = :name";
		Query query = this.getSession().createQuery(hql);
		query.setLong("customerId", customerId);
		query.setString("name", name);
		return (RawDeliveryStation) query.uniqueResult();
	}

	public List<RawDeliveryStation> getDeliverStation(List<RawAddressStationPair> pairList) {
		Set<Long> delStatIdSet = this.getDeliverStationIdSet(pairList);

		return this.getDeliverStation(delStatIdSet);
	}

	private Set<Long> getDeliverStationIdSet(List<RawAddressStationPair> pairList) {
		Set<Long> delStatIdSet = new HashSet<Long>();
		for (RawAddressStationPair pair : pairList) {
			delStatIdSet.add(pair.getRawStationId());
		}
		return delStatIdSet;
	}

	@SuppressWarnings("unchecked")
	private List<RawDeliveryStation> getDeliverStation(Set<Long> delStatIdSet) {
		if ((null == delStatIdSet) || (delStatIdSet.size() == 0)) {
			return new ArrayList<RawDeliveryStation>();
		}

		String hql = "from RawDeliveryStation where  id in (:ids)";
		Query query = this.getSession().createQuery(hql);
		query.setParameterList("ids", delStatIdSet);

		return query.list();
	}

}
