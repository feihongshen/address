package cn.explink.ws.service;

import java.util.List;

import javax.jws.WebService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.explink.domain.Address;
import cn.explink.domain.ClientApplication;
import cn.explink.domain.DelivererRule;
import cn.explink.service.AddressService;
import cn.explink.service.DelivererRuleService;
import cn.explink.util.AddressUtil;
import cn.explink.util.ApplicationContextUtil;
import cn.explink.ws.vo.AddressQueryResult;
import cn.explink.ws.vo.AddressVo;
import cn.explink.ws.vo.ApplicationVo;
import cn.explink.ws.vo.DelivererRuleVo;
import cn.explink.ws.vo.ResultCodeEnum;

@WebService(endpointInterface = "cn.explink.ws.service.AddressQueryService")
public class AddressQueryServiceImpl extends BaseWebserviceImpl implements AddressQueryService {

	private static Logger logger = LoggerFactory.getLogger(AddressQueryServiceImpl.class);
	
	@Override
	public AddressQueryResult getAddress(ApplicationVo applicationVo, Long addressId, Long deliveryStationId) {
		logger.info("getAddressByParent for parentAddressId : {}", addressId);
		AddressQueryResult result = new AddressQueryResult();
		ClientApplication clientApplication = null;
		try {
			clientApplication = validateApplication(applicationVo);
		} catch (Exception e) {
			result.setResultCode(ResultCodeEnum.failure);
			result.setMessage(e.getMessage());
			return result;
		}
		AddressService addressService = ApplicationContextUtil.getBean("addressService");
		DelivererRuleService delivererRuleService = ApplicationContextUtil.getBean("delivererRuleService");
		try {
			List<Address> addressList = addressService.getChildAddress(clientApplication.getCustomerId(), addressId);
			List<AddressVo> addressVoList = AddressUtil.cloneToAddressVoList(addressList);
			result.setAddressVoList(addressVoList);
			
			List<DelivererRule> delivererRuleList = delivererRuleService.getDelivererRuleList(clientApplication.getCustomerId(), addressId);
			List<DelivererRuleVo> delivererRuleVoList = AddressUtil.cloneToDelivererRuleList(delivererRuleList);
			result.setDelivererRuleVoList(delivererRuleVoList);
			result.setResultCode(ResultCodeEnum.success);
		} catch (Exception e) {
			logger.error("mappingAddress failed for customerId = {}", clientApplication.getCustomerId(), e);
			result.setResultCode(ResultCodeEnum.failure);
			result.setMessage(e.getMessage());
		}
		return result;
	}

}