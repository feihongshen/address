package cn.explink.dao;

import org.hibernate.Query;
import org.springframework.stereotype.Repository;

import cn.explink.dao.support.BasicHibernateDaoSupport;
import cn.explink.domain.DeliveryStation;

@Repository
public class DeliveryStationDao extends BasicHibernateDaoSupport<DeliveryStation, Long> {

	public DeliveryStationDao() {
		super(DeliveryStation.class);
	}

	public DeliveryStation getDeliveryStation(Long customerId, Long externalId) {
		String hql = "from DeliveryStation where customer.id = :customerId and externalId = :externalId";
		Query query = getSession().createQuery(hql);
		query.setLong("customerId", customerId);
		query.setLong("externalId", externalId);
		return (DeliveryStation) query.uniqueResult();
	}

}
