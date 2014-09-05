package cn.explink.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.explink.dao.AddressDao;
import cn.explink.dao.AddressPermissionDao;
import cn.explink.dao.AliasDao;
import cn.explink.dao.OrderDao;
import cn.explink.domain.Address;
import cn.explink.domain.AddressPermission;
import cn.explink.domain.Alias;
import cn.explink.domain.Deliverer;
import cn.explink.domain.DelivererRule;
import cn.explink.domain.DeliveryStation;
import cn.explink.domain.DeliveryStationRule;
import cn.explink.domain.Order;
import cn.explink.domain.enums.AddressStatusEnum;
import cn.explink.schedule.Constants;
import cn.explink.ws.vo.AddressMappingResultEnum;
import cn.explink.ws.vo.BeanVo;
import cn.explink.ws.vo.OrderVo;
import cn.explink.ws.vo.SingleAddressMappingResult;

@Service
public class AddressService {

	private static Logger logger = LoggerFactory.getLogger(AddressService.class);

	@Autowired
	private AddressDao addressDao;

	@Autowired
	private AliasDao aliasDao;

	@Autowired
	private AddressPermissionDao addressPermissionDao;

	@Autowired
	private ScheduledTaskService scheduledTaskService;

	@Autowired
	private LuceneService luceneService;

	@Autowired
	private OrderDao orderDao;

	@Autowired
	private DeliveryStationRuleService deliverStationRuleService;

	@Autowired
	private DelivererRuleService delivererRuleService;
	
	public void listAddress() {
		List<Address> addressList = addressDao.getAllAddresses();
		System.out.println(addressList);
	}

	/**
	 * 创建或更新地址
	 * 
	 * @param address
	 * @param parentAddress
	 */
	public void createAndBindAddress(Address address, Address parentAddress, Long customerId) {
		if (address.getParentId() == null) {
			throw new RuntimeException("parentId can't be null.");
		}
		if (parentAddress == null) {
			parentAddress = addressDao.get(address.getParentId());
		}
		address.setAddressLevel(parentAddress.getAddressLevel() + 1);
		address.setPath(parentAddress.getPath() + "-" + parentAddress.getId());
		address.setIndexed(false);
		address.setCreationTime(new Date());
		address.setStatus(AddressStatusEnum.valid.getValue());
		addressDao.save(address);

		if (customerId != null) {
			bindAddress(address, customerId);
		}
		scheduledTaskService.createScheduledTask(Constants.TASK_TYPE_SUB_UPDATE_INDEX, Constants.REFERENCE_TYPE_ADDRESS_ID, String.valueOf(address.getId()));
	}

	/**
	 * 创建别名
	 * @param alias
	 */
	public void createAlias(Alias alias) {
		Address address = addressDao.get(alias.getAddressId());
		if (address == null) {
			throw new RuntimeException("can't create alias for an unexist address " + alias.getAddressId());
		}
		aliasDao.save(alias);
		scheduledTaskService.createScheduledTask(Constants.TASK_TYPE_SUB_UPDATE_INDEX, Constants.REFERENCE_TYPE_ALIAS_ID, String.valueOf(alias.getId()));
	}

	public List<Alias> getAliasByIdList(List<Long> aliasIdList) {
		return aliasDao.getAliasByIdList(aliasIdList);
	}

	public List<Address> getChildAddress(Long customerId, Long addressId) {
		if (addressId == null) {
			addressId = cn.explink.Constants.ADDRESS_ID_CHINA;
		}
		List<Address> addressList = addressDao.getChildAddress(customerId, addressId);
		return addressList;
	}

	public void deleteAddress(Long addressId) {

	}

	/**
	 * 批量删除地址
	 * 
	 * @param addressIdList
	 */
	public void batchUnbindAddress(List<Long> addressIdList, Long customerId) {
		addressPermissionDao.batchUnbindAddress(addressIdList, customerId);
	}

	public boolean bindAddress(Address address, Long customerId) {
		AddressPermission permission = addressPermissionDao.getPermissionByAddressAndCustomer(address.getId(), customerId);
		if (permission == null) {
			permission = new AddressPermission();
			permission.setAddressId(address.getId());
			permission.setCustomerId(customerId);
			addressPermissionDao.save(permission);
			return true;
		}
		return false;
	}

	/**
	 * 搜索接口
	 * 
	 * @param customerId
	 * @param orderList
	 * @return
	 */
	public List<SingleAddressMappingResult> search(Long customerId, List<OrderVo> orderList) {
		List<SingleAddressMappingResult> result = new ArrayList<SingleAddressMappingResult>();
		for (OrderVo orderVo : orderList) {
			orderVo.setCustomerId(customerId);
			SingleAddressMappingResult singleResult = search(orderVo,true);
			result.add(singleResult);
		}
		return result;
	}
	/**
	 * 匹配接口不做存储
	 * 
	 * @param customerId
	 * @param orderList
	 * @return
	 */
	public Map<String, Object> match(Long customerId, List<OrderVo> orderList) {
		Map<String, Object> attributes=new HashMap<String, Object>();
		List<BeanVo> suList=new ArrayList<BeanVo>();
		List<BeanVo> unList=new ArrayList<BeanVo>();
		List<BeanVo> dList=new ArrayList<BeanVo>();
		List<SingleAddressMappingResult> result = new ArrayList<SingleAddressMappingResult>();
		for (OrderVo orderVo : orderList) {
			orderVo.setCustomerId(customerId);
			SingleAddressMappingResult singleResult = search(orderVo,false);
			BeanVo b=new BeanVo();
			b.setKey(orderVo.getAddressLine());
			switch (singleResult.getResult()) {
			case  zeroResult:
				b.setVal("未匹配");
				unList.add(b);
				break;
			case  singleResult:
				b.setVal(singleResult.getDeliveryStationList().get(0).getName());
				suList.add(b);
				break;
			case  multipleResult:
				List<DeliveryStation> dlist=singleResult.getDeliveryStationList();
				StringBuffer names=new StringBuffer();
				for (DeliveryStation deliveryStation : dlist) {
					names.append(deliveryStation.getName()+",");
				}
				b.setVal(names.toString());
				dList.add(b);
				break;
			default:
				unList.add(b);
				break;
			}
			result.add(singleResult);
		}
		int pper=(suList.size()+dList.size())/orderList.size()*100;
		attributes.put("susum", suList.size());
		attributes.put("unsum", unList.size());
		attributes.put("dsum", dList.size());
		attributes.put("pper", pper);
		attributes.put("dList", dList);
		attributes.put("unList", unList);
		attributes.put("suList", suList);
		return attributes;
	}

	private SingleAddressMappingResult search(OrderVo orderVo,boolean saveable) {
		// 查询订单记录
		Order order = new Order();
		BeanUtils.copyProperties(orderVo, order);
		order.setExternalOrderId(orderVo.getOrderId());
		order.setCreationDate(new Date());
		StringBuilder sb = null;
		
		// 查询结果
		SingleAddressMappingResult result = new SingleAddressMappingResult();
		// 匹配的站点list
		List<DeliveryStation> deliveryStationList = new ArrayList<DeliveryStation>();
		// 匹配的配送员list
		List<Deliverer> delivererList = new ArrayList<Deliverer>();
		try {
			// 找到地址
			List<Address> addressList = luceneService.search(orderVo.getAddressLine(), orderVo.getCustomerId());
			if (addressList == null || addressList.size() == 0) {
				result.setResult(AddressMappingResultEnum.zeroResult);
			} else if (addressList.size() == 1) {
				result.setResult(AddressMappingResultEnum.singleResult);
			} else {
				result.setResult(AddressMappingResultEnum.multipleResult);
			}
			
			// 找到地址对应的站点规则/站点
			List<DeliveryStationRule> deliveryStationRuleList = deliverStationRuleService.search(addressList, orderVo);
			sb = new StringBuilder();
			int count = 0;
			for (DeliveryStationRule rule : deliveryStationRuleList) {
				DeliveryStation deliveryStation = rule.getDeliveryStation();
				deliveryStationList.add(deliveryStation);
				if (count > 0) {
					sb.append(",");
				}
				sb.append(deliveryStation.getId());
				count++;
			}
			result.setDeliveryStationList(deliveryStationList);
			order.setDeliveryStationIds(sb.toString());
			
			// 找到地址对应的配送员规则/配送员
			List<DelivererRule> delivererRuleList = delivererRuleService.search(addressList, orderVo);
			sb = new StringBuilder();
			count = 0;
			for (DelivererRule rule : delivererRuleList) {
				Deliverer deliverer = rule.getDeliverer();
				delivererList.add(deliverer);
				
				if (count > 0) {
					sb.append(",");
				}
				sb.append(deliverer.getId());
				count++;
			}
			result.setDelivererList(delivererList);
			order.setDelivererIds(sb.toString());
			
			// TODO 找到地址对应的供货商时效
			result.setTimeLimit(null);
		} catch (Exception e) {
			logger.error("search address failed due to {}", e.getMessage(), e);
			result.setResult(AddressMappingResultEnum.exceptionResult);
			result.setMessage(e.getMessage());
		}
		if(saveable){
			orderDao.save(order);
		}
		return result;
	}

}
