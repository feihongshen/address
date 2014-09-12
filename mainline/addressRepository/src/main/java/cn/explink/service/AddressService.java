package cn.explink.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Query;
import org.parboiled.matchervisitors.GetStarterCharVisitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cn.explink.dao.AddressDao;
import cn.explink.dao.AddressPermissionDao;
import cn.explink.dao.AliasDao;
import cn.explink.dao.DeliveryStationRuleDao;
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
import cn.explink.exception.ExplinkRuntimeException;
import cn.explink.schedule.Constants;
import cn.explink.tree.ZTreeNode;
import cn.explink.util.StringUtil;
import cn.explink.ws.vo.AddressMappingResultEnum;
import cn.explink.ws.vo.AddressVo;
import cn.explink.ws.vo.BeanVo;
import cn.explink.ws.vo.DelivererVo;
import cn.explink.ws.vo.DeliveryStationVo;
import cn.explink.ws.vo.OrderAddressMappingResult;
import cn.explink.ws.vo.OrderVo;
import cn.explink.web.vo.SingleAddressMappingResult;

@Service
public class AddressService extends CommonServiceImpl<Address, Long> {

	public AddressService() {
		super(Address.class);
	}

	private static final int MIN_ADDRESS_LENGTH = 2;

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
	@Autowired
	private DeliveryStationRuleDao deliveryStationRuleDao;
	
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
		if (StringUtil.length(address.getName()) <= MIN_ADDRESS_LENGTH) {
			throw new ExplinkRuntimeException("关键字长度不能小于2");
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
			throw new ExplinkRuntimeException("can't create alias for an unexist address " + alias.getAddressId());
		}
		
		if (StringUtil.length(alias.getName()) <= MIN_ADDRESS_LENGTH) {
			throw new ExplinkRuntimeException("关键字长度不能小于2");
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

	/**
	 * 绑定地址到给定的客户
	 * @param address
	 * @param customerId
	 * @return true：已绑定，false：新绑定
	 */
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
	public Map<String, OrderAddressMappingResult> search(Long customerId, List<OrderVo> orderList) {
		Map<String, OrderAddressMappingResult> result = new HashMap<String, OrderAddressMappingResult>();
		for (OrderVo orderVo : orderList) {
			orderVo.setCustomerId(customerId);
			SingleAddressMappingResult singleResult = search(orderVo,true);
			OrderAddressMappingResult orderResult = new OrderAddressMappingResult();
			
			List<AddressVo> addressList = new ArrayList<AddressVo>();
			orderResult.setAddressList(addressList);
			for (Address address : singleResult.getRelatedAddressList() ) {
				AddressVo addressVo = new AddressVo();
				BeanUtils.copyProperties(address, addressVo);
				addressList.add(addressVo);
			}
			
			List<DeliveryStationVo> deliveryStationList = new ArrayList<DeliveryStationVo>();
			orderResult.setDeliveryStationList(deliveryStationList);
			for (DeliveryStation ds : singleResult.getDeliveryStationList()) {
				DeliveryStationVo dsVo = new DeliveryStationVo();
				BeanUtils.copyProperties(ds, dsVo);
				deliveryStationList.add(dsVo);
			}
			
			List<DelivererVo> delivererList = new ArrayList<DelivererVo>();
			orderResult.setDelivererList(delivererList);
			for (Deliverer deliverer : singleResult.getDelivererList()) {
				DelivererVo delivererVo = new DelivererVo();
				BeanUtils.copyProperties(deliverer, delivererVo);
				delivererList.add(delivererVo);
			}
			
			orderResult.setTimeLimitList(singleResult.getTimeLimitList());
			result.put(orderVo.getOrderId(), orderResult);
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

	private SingleAddressMappingResult search(OrderVo orderVo, boolean saveable) {
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
			result.setTimeLimitList(null);
		} catch (Exception e) {
			logger.error("search address failed due to {}", e.getMessage(), e);
			result.setResult(AddressMappingResultEnum.exceptionResult);
			result.setMessage(e.getMessage());
		}
		if (saveable) {
			orderDao.save(order);
		}
		return result;
	}

	public List<ZTreeNode> getZAddress(Long customerId,String name,boolean isBind) {
		String sql="select dsr.ADDRESS_ID from delivery_station_rules dsr left join delivery_stations ds on dsr.DELIVERY_STATION_ID=ds.id  where ds.CUSTOMER_ID="+customerId;
		Query query =getSession().createSQLQuery(sql);
		List<Integer> list=query.list();
		StringBuffer sb=null;
		if(null!=list&&list.size()>0){
			sb=new StringBuffer();
			
			for (Integer aid : list) {
				sb.append(aid+",");
			}
		}
		
		return addressDao.getZTree(customerId,name,sb);
	}

	public List<ZTreeNode> getAsyncAddress(Long customerId, Long parentId) {
		return addressDao.getAsyncAddress(customerId,parentId);
	}

}
