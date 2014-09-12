package cn.explink.dao;

import org.springframework.stereotype.Repository;

import cn.explink.domain.DeliveryStationRule;
import cn.explink.service.CommonServiceImpl;

@Repository
public class DeliveryStationRuleDao extends CommonServiceImpl<DeliveryStationRule, Long> {

	public DeliveryStationRuleDao() {
		super(DeliveryStationRule.class);
	}

}
