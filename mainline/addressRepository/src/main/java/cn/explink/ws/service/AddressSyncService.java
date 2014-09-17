package cn.explink.ws.service;

import java.util.List;

import javax.jws.WebService;

import cn.explink.ws.vo.ApplicationVo;
import cn.explink.ws.vo.AddressSyncServiceResult;
import cn.explink.ws.vo.DelivererRuleVo;
import cn.explink.ws.vo.DelivererVo;
import cn.explink.ws.vo.DeliveryStationVo;
import cn.explink.ws.vo.VendorVo;

@WebService
public interface AddressSyncService {

	AddressSyncServiceResult createDeliveryStation(ApplicationVo applicationVo, DeliveryStationVo deliveryStationVo);

	AddressSyncServiceResult updateDeliveryStation(ApplicationVo applicationVo, DeliveryStationVo deliveryStationVo);
	
	AddressSyncServiceResult deleteDeliveryStation(ApplicationVo applicationVo, DeliveryStationVo deliveryStationVo);
	
	AddressSyncServiceResult createVendor(ApplicationVo applicationVo, VendorVo vendorVo);
	
	AddressSyncServiceResult updateVendor(ApplicationVo applicationVo, VendorVo vendorVo);
	
	AddressSyncServiceResult deleteVendor(ApplicationVo applicationVo, VendorVo vendorVo);
	
	AddressSyncServiceResult createDeliverer(ApplicationVo applicationVo, DelivererVo deliverer);
	
	AddressSyncServiceResult updateDeliverer(ApplicationVo applicationVo, DelivererVo deliverer);
	
	AddressSyncServiceResult deleteDeliverer(ApplicationVo applicationVo, DelivererVo deliverer);
	
	/**
	 * 创建配送员规则
	 * @param applicationVo
	 * @param delivererRuleVoList
	 * @return
	 */
	AddressSyncServiceResult createDelivererRule(ApplicationVo applicationVo, List<DelivererRuleVo> delivererRuleVoList);
	
	/**
	 * 删除配送员规则
	 * @param applicationVo
	 * @param delivererRuleVoList
	 * @return
	 */
	AddressSyncServiceResult deleteDelivererRule(ApplicationVo applicationVo, Long ruleId);
	
}
