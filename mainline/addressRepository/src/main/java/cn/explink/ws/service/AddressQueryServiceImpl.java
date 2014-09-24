package cn.explink.ws.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jws.WebService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import cn.explink.domain.Address;
import cn.explink.domain.ClientApplication;
import cn.explink.domain.DelivererRule;
import cn.explink.service.AddressService;
import cn.explink.service.DelivererRuleService;
import cn.explink.service.DelivererService;
import cn.explink.service.DeliveryStationRuleService;
import cn.explink.tree.ZTreeNode;
import cn.explink.util.AddressUtil;
import cn.explink.util.ApplicationContextUtil;
import cn.explink.ws.vo.AddressQueryResult;
import cn.explink.ws.vo.AddressVo;
import cn.explink.ws.vo.ApplicationVo;
import cn.explink.ws.vo.BeanVo;
import cn.explink.ws.vo.DelivererRuleVo;
import cn.explink.ws.vo.ResultCodeEnum;

@WebService(endpointInterface = "cn.explink.ws.service.AddressQueryService")
public class AddressQueryServiceImpl extends BaseWebserviceImpl implements AddressQueryService {

	private static Logger logger = LoggerFactory.getLogger(AddressQueryServiceImpl.class);
	 
	private DeliveryStationRuleService deliverStationRuleService;
	
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
		DelivererService delivererService = ApplicationContextUtil.getBean("delivererService");
		try {
			List<Address> addressList = addressService.getChildAddress(clientApplication.getCustomerId(), addressId);
			fillStation(addressList,clientApplication.getCustomerId());
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

	private void fillStation(List<Address> addressList,Long customerId) {
		StringBuffer ids=new StringBuffer();
		if(deliverStationRuleService==null){
			deliverStationRuleService = ApplicationContextUtil.getBean("deliverStationRuleService");
		}
		if(addressList!=null&&!addressList.isEmpty()){
			for (Address a : addressList) {
				ids.append(a.getId()+",");
			}
		}
		if(ids.length()>1){
			String inIds=ids.toString().substring(0,ids.length()-1);
			List<BeanVo> dlist=deliverStationRuleService.getStationAddressTree(customerId,inIds);
			Map<String,String> view=new HashMap<String,String>();
			if(null!=dlist&&dlist.size()>0){
				for (BeanVo b : dlist) {
					String key=b.getKey();
					if(view.get(key)!=null){
						view.put(key, b.getVal()+" | "+view.get(key));
					}else{
						view.put(key,  b.getVal());
					}
				}
			}
			if(view.size()>0)
			for (Address a : addressList) {
				if(null!=view.get(a.getId())){
					a.setName(a.getName()+" -- "+view.get(a.getId()));
				}
			}
		}
	}

	public DeliveryStationRuleService getDeliverStationRuleService() {
		return deliverStationRuleService;
	}

	public void setDeliverStationRuleService(
			DeliveryStationRuleService deliverStationRuleService) {
		this.deliverStationRuleService = deliverStationRuleService;
	}

}