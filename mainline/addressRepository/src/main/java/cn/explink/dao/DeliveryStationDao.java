package cn.explink.dao;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.Query;
import org.springframework.stereotype.Repository;

import cn.explink.dao.support.BasicHibernateDaoSupport;
import cn.explink.domain.Address;
import cn.explink.domain.DeliveryStation;
import cn.explink.domain.Vendor;
import cn.explink.modle.ComboBox;
import cn.explink.quick.AddressStationPair;

@Repository
public class DeliveryStationDao extends BasicHibernateDaoSupport<DeliveryStation, Long> {

	public DeliveryStationDao() {
		super(DeliveryStation.class);
	}

	public DeliveryStation getDeliveryStation(Long customerId, Long externalId) {
		String hql = "from DeliveryStation where customer.id = :customerId and externalId = :externalId";
		Query query = this.getSession().createQuery(hql);
		query.setLong("customerId", customerId);
		query.setLong("externalId", externalId);
		return (DeliveryStation) query.uniqueResult();
	}

	public List<Address> getAddress(Long id) {
		String hql = "from Address a where a.id in( select address.id from  DeliveryStationRule where deliveryStation.id=:id )";
		Query query = this.getSession().createQuery(hql);
		query.setLong("id", id);
		return query.list();
	}

	@SuppressWarnings("unchecked")
	public List<Address> getAddressByIds(Set<Long> addIds) {
		if (addIds.isEmpty()) {
			return new ArrayList<Address>();
		}
		String hql = "from Address a where a.id in :addIds ";
		Query query = this.getSession().createQuery(hql);
		query.setParameterList("addIds", addIds);
		return query.list();
	}

	public List<DeliveryStation> listAll(Long customerId) {
		if (customerId == null) {
			return new ArrayList<DeliveryStation>();
		}
		String hql = "from DeliveryStation where customer.id=:customerId and status=1";
		Query query = this.getSession().createQuery(hql);
		query.setLong("customerId", customerId);
		return query.list();
	}

	public List<ComboBox> getComBoxDeliveryStation(Long customerId) {
		String hql = "select new cn.explink.modle.ComboBox(ds.id,ds.name) from DeliveryStation ds where customer.id = :customerId order by ds.name ";
		Query query = this.getSession().createQuery(hql);
		query.setLong("customerId", customerId);
		return query.list();
	}

	public DeliveryStation getByNameAndCustomerId(String deliveryStationName, Long customerId) {
		String hql = "from DeliveryStation where customer.id = :customerId and name=:name ";
		Query query = this.getSession().createQuery(hql);
		query.setLong("customerId", customerId);
		query.setString("name", deliveryStationName);
		return (DeliveryStation) query.uniqueResult();
	}

	public List<Vendor> listAllVendor(Long customerId) {
		String hql = "from Vendor where customer.id = :customerId and status=1";
		Query query = this.getSession().createQuery(hql);
		query.setLong("customerId", customerId);
		return query.list();
	}

	/**
	 * 关键词维护工具(广州通路)
	 *
	 * @param delStatIdSet
	 * @return
	 */
	public List<DeliveryStation> getDeliverStation(Set<Long> delStatIdSet) {
		String hql = "from DeliveryStation where  id in (:ids)";
		Query query = this.getSession().createQuery(hql);
		query.setParameterList("ids", delStatIdSet);

		return query.list();
	}

	public List<DeliveryStation> getAllDeliverStation() {
		String hql = "from DeliveryStation";
		Query query = this.getSession().createQuery(hql);

		return query.list();
	}

	public List<DeliveryStation> getDeliverStation(List<AddressStationPair> pairList) {
		Set<Long> delStatIdSet = this.getDeliverStationIdSet(pairList);

		return this.getDeliverStation(delStatIdSet);
	}

	private Set<Long> getDeliverStationIdSet(List<AddressStationPair> pairList) {
		Set<Long> delStatIdSet = new HashSet<Long>();
		for (AddressStationPair pair : pairList) {
			delStatIdSet.add(pair.getAddressId());
		}
		return delStatIdSet;
	}
}
