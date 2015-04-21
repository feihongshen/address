package cn.explink.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.explink.dao.RawDeliveryStationRuleDao;
import cn.explink.domain.RawDeliveryStationRule;

@Service
public class RawDeliveryStationRuleService extends CommonServiceImpl<RawDeliveryStationRule, Long> {

	public RawDeliveryStationRuleService() {
		super(RawDeliveryStationRule.class);
	}

	@Autowired
	private RawDeliveryStationRuleDao rawDeliveryStationRuleDao;

	public int batchUnbindRawAddressStationRule(List<Long> rawAddressIdList) {
		return this.rawDeliveryStationRuleDao.batchUnbindRawAddressStationRule(rawAddressIdList);
	}
}
