package cn.explink.dao;

import java.util.List;

import org.hibernate.Query;
import org.springframework.stereotype.Repository;

import cn.explink.dao.support.BasicHibernateDaoSupport;
import cn.explink.domain.RawDeliveryStationRule;
import cn.explink.domain.enums.DelivererRuleTypeEnum;

@Repository
public class RawDeliveryStationRuleDao extends BasicHibernateDaoSupport<RawDeliveryStationRule, Long> {

	public RawDeliveryStationRuleDao() {
		super(RawDeliveryStationRule.class);
	}

	@SuppressWarnings("unchecked")
	public List<Long> getByAddressAndStation(Long rawAddressId, Long rawStationId, Long customerId) {
		String sql = "SELECT r.id FROM RAW_DELIVERY_STATION_RULES r left join RAW_DELIVERY_STATIONS" + " s on s.ID=r.RAW_DELIVERY_STATION_ID WHERE r.RULE_TYPE=:ruleType AND r.RULE='' "
				+ " AND s.CUSTOMER_ID=:customerId " + " AND r.RAW_ADDRESS_ID=:rawAddressId";
		Query query = this.getSession().createSQLQuery(sql);
		query.setLong("rawAddressId", rawAddressId);
		query.setLong("customerId", customerId);
		query.setInteger("ruleType", DelivererRuleTypeEnum.fallback.getValue());
		return query.list();
	}

}
