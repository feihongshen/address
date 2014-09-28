package cn.explink.service;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.explink.dao.AddressDao;
import cn.explink.dao.AddressPermissionDao;
import cn.explink.dao.AliasDao;
import cn.explink.dao.DeliveryStationDao;
import cn.explink.dao.DeliveryStationRuleDao;
import cn.explink.dao.OrderDao;
import cn.explink.dao.VendorDao;
import cn.explink.dao.VendorsAgingDao;
import cn.explink.domain.Address;
import cn.explink.domain.AddressPermission;
import cn.explink.domain.Alias;
import cn.explink.domain.Deliverer;
import cn.explink.domain.DelivererRule;
import cn.explink.domain.DeliveryStation;
import cn.explink.domain.DeliveryStationRule;
import cn.explink.domain.Order;
import cn.explink.domain.VendorsAging;
import cn.explink.domain.enums.AddressStatusEnum;
import cn.explink.domain.enums.DeliveryStationRuleTypeEnum;
import cn.explink.exception.ExplinkRuntimeException;
import cn.explink.modle.AjaxJson;
import cn.explink.schedule.Constants;
import cn.explink.tree.ZTreeNode;
import cn.explink.util.StringUtil;
import cn.explink.web.vo.SingleAddressMappingResult;
import cn.explink.ws.vo.AddressMappingResultEnum;
import cn.explink.ws.vo.AddressVo;
import cn.explink.ws.vo.BeanVo;
import cn.explink.ws.vo.DelivererVo;
import cn.explink.ws.vo.DeliveryStationVo;
import cn.explink.ws.vo.OrderAddressMappingResult;
import cn.explink.ws.vo.OrderVo;

@Service
public class AddressService extends CommonServiceImpl<Address, Long> {

	public AddressService() {
		super(Address.class);
	}

	public static final int MIN_ADDRESS_LENGTH = 2;

	private static Logger logger = LoggerFactory.getLogger(AddressService.class);

	@Autowired
	private AddressDao addressDao;

	@Autowired
	private AliasDao aliasDao;

	@Autowired
	private DeliveryStationDao  deliveryStationDao ;
	
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

	@Autowired
	private VendorDao vendorDao;
	
	@Autowired
	private VendorsAgingDao vendorAgingService;
	
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
	public Address createAndBindAddress(Address address, Address parentAddress, Long customerId) {
		if (address.getParentId() == null) {
			throw new RuntimeException("parentId can't be null.");
		}
		if (parentAddress == null) {
			parentAddress = addressDao.get(address.getParentId());
		}
		if (StringUtil.length(address.getName()) < MIN_ADDRESS_LENGTH) {
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
		return address;
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
		
		if (StringUtil.length(alias.getName()) <  MIN_ADDRESS_LENGTH) {
			throw new ExplinkRuntimeException("关键字长度不能小于2");
		}
		
		aliasDao.save(alias);
		scheduledTaskService.createScheduledTask(Constants.TASK_TYPE_SUB_UPDATE_INDEX, Constants.REFERENCE_TYPE_ALIAS_ID, String.valueOf(alias.getId()));
	}

	public List<Alias> getAliasByIdList(List<Long> aliasIdList) {
		return aliasDao.getAliasByIdList(aliasIdList);
	}

	public List<Address> getChildAddress(Long customerId, Long addressId,Long deliveryStationId) {
		if (addressId == null) {
			addressId = cn.explink.Constants.ADDRESS_ID_CHINA;
		}
		List<Address> addressList = addressDao.getChildAddress(customerId, addressId,deliveryStationId);
		return addressList;
	}
	public void deleteAddress(Long addressId, Long customerId) {
       Address a = this.addressDao.get(addressId);
       String pathLike = "";
       if(StringUtil.isEmpty(a.getPath())){
    	   pathLike = "%";
       }else{
    	   pathLike = a.getPath()+"-"+a.getId()+"-%";
       }
       List<Address> list = addressDao.getChildAllAddress(customerId,a.getPath()+"-"+a.getId(),pathLike);
       List<Long> ids = new ArrayList<Long>();
       ids.add(a.getId());
       if(list!=null&&!list.isEmpty()){
    	  for(Address ad :list){
    		  ids.add(ad.getId());
    	  }
       }
       batchUnbindAddress(ids,customerId);
	}

	/**
	 * 批量删除地址
	 * 
	 * @param addressIdList
	 */
	public void batchUnbindAddress(List<Long> addressIdList, Long customerId) {
		addressPermissionDao.batchUnbindAddress(addressIdList, customerId);
		//批量删除别名
		aliasDao.deleteAliasByIds(addressIdList, customerId);
		//批量删除站点关联关系
		deliveryStationRuleDao.deleteRuleByIds(addressIdList, customerId);
		
		
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
	 * 绑定地址到给定的客户的站点
	 * @param address
	 * @param customerId
	 * @return true：已绑定，false：新绑定
	 */
	public boolean bindAddressWithStation(Address address, Long stationId) {
		DeliveryStationRule defaultRule = addressDao.getDefaultStation(address.getId());
		if(defaultRule!=null){
			 throw new ExplinkRuntimeException("该关键字已绑定默认站点"+defaultRule.getDeliveryStation().getName());
		}
		DeliveryStationRule dsr = addressDao.getStationRuleByAddressAndStation(address.getId(), stationId);
		if (dsr == null) {
			dsr = new DeliveryStationRule();
			dsr.setAddress(address);
			DeliveryStation  ds = new DeliveryStation ();
			ds.setId(stationId);
			dsr.setDeliveryStation(ds);
			dsr.setCreationTime(new Date());
			dsr.setRule("");
			dsr.setRuleType(DeliveryStationRuleTypeEnum.fallback.getValue());
			dsr.setRuleExpression("");
			deliveryStationRuleDao.save(dsr);
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
	public Map<String, Object> txNoneMatch(Long customerId, List<OrderVo> orderList) {
		Map<String, Object> attributes=new HashMap<String, Object>();
		List<BeanVo> suList=new ArrayList<BeanVo>();
		List<BeanVo> unList=new ArrayList<BeanVo>();
		List<BeanVo> dList=new ArrayList<BeanVo>();
		List<BeanVo> kList=new ArrayList<BeanVo>();
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
				if(singleResult.getDeliveryStationList().isEmpty()){
					b.setVal("未匹配");
					kList.add(b);
				}else{
					b.setVal(singleResult.getDeliveryStationList().get(0).getName());
					suList.add(b);
				}
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
				b.setVal("未匹配");
				unList.add(b);
				break;
			}
			result.add(singleResult);
		}
		int pper=(int)((suList.size()+dList.size()+0.0)/orderList.size()*100);
		attributes.put("susum", suList.size());
		attributes.put("unsum", unList.size()+kList.size());
		attributes.put("dsum", dList.size());
		attributes.put("pper", pper);
		attributes.put("dList", dList);
		attributes.put("unList", unList);
		attributes.put("suList", suList);
		attributes.put("kList", kList);
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
			result.setRelatedAddressList(addressList);

			// 找到地址对应的站点规则/站点
			List<DeliveryStationRule> deliveryStationRuleList = deliverStationRuleService.search(addressList, orderVo);
			sb = new StringBuilder();
			int count = 0;
			if(deliveryStationRuleList!=null){
				for (DeliveryStationRule rule : deliveryStationRuleList) {
					DeliveryStation deliveryStation = rule.getDeliveryStation();
					deliveryStationList.add(deliveryStation);
					if (count > 0) {
						sb.append(",");
					}
					sb.append(deliveryStation.getId());
					count++;
				}
			}
			result.setDeliveryStationList(deliveryStationList);
			order.setDeliveryStationIds(sb.toString());

			// 找到地址对应的配送员规则/配送员
			List<DelivererRule> delivererRuleList = delivererRuleService.search(addressList, orderVo);
			sb = new StringBuilder();
			count = 0;
			if(delivererRuleList!=null){
				for (DelivererRule rule : delivererRuleList) {
					Deliverer deliverer = rule.getDeliverer();
					delivererList.add(deliverer);

					if (count > 0) {
						sb.append(",");
					}
					sb.append(deliverer.getId());
					count++;
				}
			}
			result.setDelivererList(delivererList);
			order.setDelivererIds(sb.toString());

			// 找到地址对应的供货商时效
			if (orderVo.getVendorId() != null) {
				List<Integer> timeLimitList = new ArrayList<Integer>();
				for (Address address : addressList) {
					List<VendorsAging> vendorAgingList = vendorAgingService.getVendorAgingByExternalId(address.getId(), orderVo.getVendorId(), orderVo.getCustomerId());
					if (vendorAgingList != null && vendorAgingList.size() > 0) {
						timeLimitList.add(Integer.parseInt(vendorAgingList.get(0).getAging()));
					}
				}
				result.setTimeLimitList(timeLimitList);
			}
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

	public List<ZTreeNode> getZAddress(Long customerId,String name,Integer isBind) {
		String sql="SELECT DSR.ADDRESS_ID FROM DELIVERY_STATION_RULES DSR LEFT JOIN DELIVERY_STATIONS DS ON DSR.DELIVERY_STATION_ID=DS.ID  WHERE DS.CUSTOMER_ID="+customerId;
		Query query =getSession().createSQLQuery(sql);
		List<Integer> list=query.list();
		StringBuffer sb=null;
		if(null!=list&&list.size()>0&&Integer.valueOf(1).equals(isBind)){
			sb=new StringBuffer();
			for (Integer aid : list) {
				sb.append(aid+",");
			}
		}
		
		return addressDao.getZTree(customerId,name,sb);
	}

	public List<ZTreeNode> getAsyncAddress(Long customerId, Long parentId,String ids) {
		return addressDao.getAsyncAddress(customerId,parentId,ids);
	}

	public List<Address> addAddressWithStation(Long parentId, String addresses, Long stationId,Long customerId) {
		Address parent = addressDao.get(parentId);
		List<Address> list = new ArrayList<Address>();
		for(String addressLine : addresses.split("\n")){
			if(addressLine.trim().length()==0){
				continue;
			}
			Address a = new Address();
			a.setParentId(parentId);
			a.setName(addressLine);
			Address l = addressDao.getAddressByNameAndPid(addressLine, parentId);
			if(l!=null ){//已存在则绑定
				a = l;
				bindAddress(l,  customerId);
				bindAddressWithStation(l, stationId);
			}else{
				createAndBindAddress(a, parent, customerId);
				bindAddressWithStation(a, stationId);
			}
			list.add(a);
		}
		return list;
	}

	public List<Address> addAddress(Long parentId, String addresses,Long customerId) {
		Address parent = addressDao.get(parentId);
		List<Address> list = new ArrayList<Address>();
		for(String addressLine : addresses.split("\n")){
			if(addressLine.trim().length()==0){
				continue;
			}
			Address a = new Address();
			a.setParentId(parentId);
			a.setName(addressLine);
			Address l = addressDao.getAddressByNameAndPid(addressLine, parentId);
			if(l!=null ){//已存在则绑定
				bindAddress(l,  customerId);
				a=l;
			}else{
				createAndBindAddress(a, parent, customerId);
			}
			list.add(a);
		}
		return list;
	}

	public AjaxJson addAlias(Long addressId, String alias,Long customerId) {
		AjaxJson aj = new AjaxJson();
		Alias  a = aliasDao.getAliasByAddressIdAndAlias(addressId,alias,customerId);
		Address address = addressDao.get(addressId);
		try{
			if(a==null){
				aj.setSuccess(true);
				a = new Alias();
				a.setAddressId(addressId);
				a.setCustomerId(customerId);
				a.setName(alias);
				a.setOldName(address.getName());
				createAlias(a);
			}else{
				aj.setSuccess(false);
				aj.setMsg("已存在别名："+alias);
			}
		}catch(Exception e){
			aj.setSuccess(false);
			aj.setMsg(e.getMessage());
		}
		aj.setObj(a);
		return aj;
	}

	public List<Alias> getAliasByAddressId(Long addressId, Long customerId) {
		return aliasDao.getAliasByAddressIdAndCustomerId(addressId,customerId);
	}

	public void deleteAlias(Long id) {
		Alias a = aliasDao.get(id);
		aliasDao.delete(a);
		scheduledTaskService.createScheduledTask(Constants.TASK_TYPE_SUB_UPDATE_INDEX, Constants.REFERENCE_TYPE_ALIAS_ID, String.valueOf(a.getAddressId()));
	}
	
	public List<ZTreeNode> getStationAddressTree(Long customerId, Long parentId) {
		List<ZTreeNode> list=getAsyncAddress(customerId, parentId,null);
		appendStation(customerId, list);
		
		return list;
	}

	public void appendStation(Long customerId, List<ZTreeNode> list) {
		StringBuffer ids=new StringBuffer();
		for (ZTreeNode zTreeNode : list) {
			ids.append(zTreeNode.getId()+",");
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
			for (ZTreeNode zTreeNode : list) {
				if(null!=view.get(zTreeNode.getId())){
					zTreeNode.setName(zTreeNode.getName()+" -- "+view.get(zTreeNode.getId()));
				}
			}
		}
	}

	public List<ZTreeNode> getAdressByStation(Long customerId, String stationId) {
		List<ZTreeNode> address=deliverStationRuleService.getAdressByStation(customerId,stationId);
		Set<String> set=new HashSet<String>();
		if(null!=address&&address.size()>0){
			StringBuffer aIds=new StringBuffer();
			for (ZTreeNode a : address) {
				aIds.append(a.getId()+"-"+a.getT()+"-");
			}
			String[] ids=aIds.toString().split("-");
			for (String id : ids) {
				set.add(id);
			}
			set.remove("");
			aIds.setLength(0);
			for (String string : set) {
				aIds.append(string+",");
			}
			aIds.setLength(aIds.length()-1);
			return addressDao.getZTreeNodeByIdListAndCustomerId(aIds.toString(),customerId);
		}
			
		else{
			return null;
		}
	}

	public Map getAdressPromtInfo(Long customerId) {
		Map<String, BigInteger> map=new HashMap<String, BigInteger>();
		String keysql=" select count(1)  from ADDRESS_PERMISSIONS p left join ADDRESS a on a.id=p.ADDRESS_ID where a.ADDRESS_LEVEL>3 and p.CUSTOMER_ID="+customerId;
		BigInteger keys=(BigInteger) getSession().createSQLQuery(keysql).uniqueResult();
		String bindSql=" select count(DISTINCT  r.ADDRESS_ID) from DELIVERY_STATION_RULES r left join DELIVERY_STATIONS d on r.DELIVERY_STATION_ID=d.ID where d.STATUS=1 and d.CUSTOMER_ID="+customerId;
		BigInteger binds=(BigInteger) getSession().createSQLQuery(bindSql).uniqueResult();
		map.put("keys", keys);
		map.put("binds", binds);
		return map;
	}

	public List<cn.explink.domain.Address> getAddressByNames(
			Set<String> addressNames, Long customerId) {
		return addressDao.getAddressByNames(addressNames,  customerId);
	}

	public List<cn.explink.domain.Address> getAdministrationAddress(
			Set<String> adminNames, Long customerId) {
		return addressDao.getAdministrationAddress(adminNames,  customerId);
	}

	public List<Address> getAllBands(Long customerId) {
		return addressDao.getAllBands(  customerId);
	}

	public List<String> findCannotRemoveIds(List<Long> addressIdList,Long customerId) {
		String hql="select a.path from Address as a, AddressPermission p where a.id = p.addressId and p.customerId = :customerId and a.parentId in :addressIdList and a.id not in:addressIdList";
		Query query = getSession().createQuery(hql);
		query.setParameterList("addressIdList", addressIdList);
		query.setLong("customerId", customerId);
		return query.list();
	}



}
