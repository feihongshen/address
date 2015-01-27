package cn.explink.dao;

import java.util.List;

import org.hibernate.Query;
import org.springframework.stereotype.Repository;

import cn.explink.domain.DeliveryStationRule;
import cn.explink.domain.enums.DelivererRuleTypeEnum;
import cn.explink.service.CommonServiceImpl;

@Repository
public class DeliveryStationRuleDao extends CommonServiceImpl<DeliveryStationRule, Long> {

	public DeliveryStationRuleDao() {
		super(DeliveryStationRule.class);
	}

	public void deleteRuleByIds(List<Long> addressIdList, Long customerId) {
		String sql = "SELECT R.ID FROM DELIVERY_STATION_RULES  R ,DELIVERY_STATIONS S  WHERE R.ADDRESS_ID IN :idList AND S.CUSTOMER_ID =:customerId AND S.ID=R.DELIVERY_STATION_ID";
		List<Integer> list = this.getSession().createSQLQuery(sql).setParameterList("idList", addressIdList).setLong("customerId", customerId).list();
		for (Integer l : list) {
			this.delete(Long.parseLong(l + ""));
		}
	}

	public List<Long> getAddressIds(Long parentId, Long customerId) {
		StringBuilder hql = new StringBuilder("select a.id from DeliveryStationRule d, Address a, AddressPermission p");
		hql.append(" where a.id = p.addressId");
		hql.append(" and d.address.id=a.id ");
		hql.append(" and p.customerId = :customerId");
		hql.append(" and a.parentId = :parentId");
		Query query = this.getSession().createQuery(hql.toString());
		query.setLong("customerId", customerId);
		query.setLong("parentId", parentId);
		return query.list();
	}

	public List getByAddressAndStation(Long addressId, Long stationId, Long customerId) {
		String sql = "SELECT r.id FROM DELIVERY_STATION_RULES r left join DELIVERY_STATIONS" + " s on s.ID=r.DELIVERY_STATION_ID WHERE r.RULE_TYPE=:ruleType AND r.RULE='' "
				+ " AND s.CUSTOMER_ID=:customerId " + " AND r.ADDRESS_ID=:addressId";
		Query query = this.getSession().createSQLQuery(sql);
		query.setLong("addressId", addressId);
		query.setLong("customerId", customerId);
		query.setInteger("ruleType", DelivererRuleTypeEnum.fallback.getValue());
		return query.list();
	}

	public int removeAddressRule(Long addressId, Long stationId) {
		Query query = this.getSession().createSQLQuery("UPDATE DELIVERY_STATION_RULES SET  DELIVERY_STATION_ID = :stationId WHERE ADDRESS_ID=:addressId");
		query.setLong("addressId", addressId);
		query.setLong("stationId", stationId);
		return query.executeUpdate();
	}

	public int removeAddressRule(Long addressId, Long oldStationId, Long stationId) {
		Query query = this.getSession().createSQLQuery("UPDATE DELIVERY_STATION_RULES SET  DELIVERY_STATION_ID = :stationId WHERE ADDRESS_ID=:addressId" + " and DELIVERY_STATION_ID=:oldStationId");
		query.setLong("addressId", addressId);
		query.setLong("stationId", stationId);
		query.setLong("oldStationId", oldStationId);
		return query.executeUpdate();
	}

	public void updateAddressStation(Long addressId, Long stationId) {
		Query query = this.getSession().createSQLQuery("UPDATE DELIVERY_STATION_RULES SET  DELIVERY_STATION_ID = :stationId WHERE ADDRESS_ID=:addressId");
		query.setParameter("stationId", stationId);
		query.setParameter("addressId", addressId);

		query.executeUpdate();
	}
}
