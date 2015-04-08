package cn.explink.service;

import java.io.IOException;
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
import cn.explink.spliter.AddressSplitter;
import cn.explink.spliter.vo.AddressDetail;
import cn.explink.spliter.vo.AddressStation;
import cn.explink.tree.ZTreeNode;
import cn.explink.util.AddressUtil;
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
	private DeliveryStationDao deliveryStationDao;

	@Autowired
	private AddressPermissionDao addressPermissionDao;

	@Autowired
	private ScheduledTaskService scheduledTaskService;

	@Autowired
	private LuceneService luceneService;

	@Autowired
	private VendorService vendorService;

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

	@Autowired
	private GisService gisService;

	@Autowired
	private RawAddressService rawAddressService;
	@Autowired
	private RawDeliveryStationService rawDeliveryStationService;

	public void listAddress() {
		List<Address> addressList = this.addressDao.getAllAddresses();
		System.out.println(addressList);
	}

	public List<ZTreeNode> getAllAddress(Long customerId) {
		return this.addressDao.getZTree(customerId, null, null);
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
			parentAddress = this.addressDao.get(address.getParentId());
		}
		if (StringUtil.length(address.getName()) < AddressService.MIN_ADDRESS_LENGTH) {
			throw new ExplinkRuntimeException("关键字长度不能小于2");
		}
		address.setAddressLevel(parentAddress.getAddressLevel() + 1);
		address.setPath(parentAddress.getPath() + "-" + parentAddress.getId());
		address.setIndexed(false);
		address.setCreationTime(new Date());
		address.setStatus(AddressStatusEnum.valid.getValue());
		this.addressDao.save(address);

		if (customerId != null) {
			this.bindAddress(address, customerId);
		}

		// this.scheduledTaskService.createScheduledTask(Constants.TASK_TYPE_SUB_UPDATE_INDEX,
		// Constants.REFERENCE_TYPE_ADDRESS_ID,
		// String.valueOf(address.getId()));

		// modified by songkaojun 2015-01-10 不创建调度任务，立即执行更新索引操作
		try {
			this.updateIndexRightNow(address.getId(), null);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return address;
	}

	private void updateIndexRightNow(Long addressId, Long aliasId) throws IOException {
		List<Long> addressIdList = new ArrayList<Long>();
		List<Long> aliasIdList = new ArrayList<Long>();
		if (null != addressId) {
			addressIdList.add(addressId);
		}
		if (null != aliasId) {
			aliasIdList.add(aliasId);
		}
		this.luceneService.updateIndex(addressIdList, aliasIdList);
	}

	/**
	 * 创建别名
	 *
	 * @param alias
	 */
	public void createAlias(Alias alias) {
		Address address = this.addressDao.get(alias.getAddressId());
		if (address == null) {
			throw new ExplinkRuntimeException("can't create alias for an unexist address " + alias.getAddressId());
		}

		if (StringUtil.length(alias.getName()) < AddressService.MIN_ADDRESS_LENGTH) {
			throw new ExplinkRuntimeException("关键字长度不能小于2");
		}

		this.aliasDao.save(alias);
		// this.scheduledTaskService.createScheduledTask(Constants.TASK_TYPE_SUB_UPDATE_INDEX,
		// Constants.REFERENCE_TYPE_ALIAS_ID, String.valueOf(alias.getId()));
		// modified by songkaojun 2015-01-10 不创建调度任务，立即执行更新索引操作
		try {
			this.updateIndexRightNow(null, alias.getId());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public List<Alias> getAliasByIdList(List<Long> aliasIdList) {
		return this.aliasDao.getAliasByIdList(aliasIdList);
	}

	public List<Address> getChildAddress(Long customerId, Long addressId, Long deliveryStationId) {
		if (addressId == null) {
			addressId = cn.explink.Constants.ADDRESS_ID_CHINA;
		}
		List<Address> addressList = this.addressDao.getChildAddress(customerId, addressId, deliveryStationId);
		return addressList;
	}

	public void deleteAddress(Long addressId, Long customerId) {
		Address a = this.addressDao.get(addressId);
		String pathLike = "";
		if (StringUtil.isEmpty(a.getPath())) {
			pathLike = "%";
		} else {
			pathLike = a.getPath() + "-" + a.getId() + "-%";
		}
		List<Address> list = this.addressDao.getChildAllAddress(customerId, a.getPath() + "-" + a.getId(), pathLike);
		List<Long> ids = new ArrayList<Long>();
		ids.add(a.getId());
		if ((list != null) && !list.isEmpty()) {
			for (Address ad : list) {
				ids.add(ad.getId());
			}
		}
		this.batchUnbindAddress(ids, customerId);
	}

	/**
	 * 批量删除地址
	 *
	 * @param addressIdList
	 */
	public void batchUnbindAddress(List<Long> addressIdList, Long customerId) {
		this.addressPermissionDao.batchUnbindAddress(addressIdList, customerId);
		// 批量删除别名
		this.aliasDao.deleteAliasByIds(addressIdList, customerId);
		// 批量删除站点关联关系
		this.deliveryStationRuleDao.deleteRuleByIds(addressIdList, customerId);

		this.delivererRuleService.deleteRuleByIds(addressIdList, customerId);

		this.vendorService.deleteAgingByIds(addressIdList, customerId);

	}

	/**
	 * 绑定地址到给定的客户
	 *
	 * @param address
	 * @param customerId
	 * @return true：已绑定，false：新绑定
	 */
	public boolean bindAddress(Address address, Long customerId) {
		AddressPermission permission = this.addressPermissionDao.getPermissionByAddressAndCustomer(address.getId(), customerId);
		if (permission == null) {
			permission = new AddressPermission();
			permission.setAddressId(address.getId());
			permission.setCustomerId(customerId);
			this.addressPermissionDao.save(permission);
			return true;
		}
		return false;
	}

	/**
	 * 绑定地址到给定的客户的站点
	 *
	 * @param address
	 * @param customerId
	 * @return true：已绑定，false：新绑定
	 */
	public boolean bindAddressWithStation(Address address, Long stationId) {
		DeliveryStationRule defaultRule = this.addressDao.getDefaultStation(address.getId());
		if (defaultRule != null) {
			throw new ExplinkRuntimeException("该关键字已绑定默认站点" + defaultRule.getDeliveryStation().getName());
		}
		DeliveryStationRule dsr = this.addressDao.getStationRuleByAddressAndStation(address.getId(), stationId);
		if (dsr == null) {
			dsr = new DeliveryStationRule();
			dsr.setAddress(address);
			DeliveryStation ds = new DeliveryStation();
			ds.setId(stationId);
			dsr.setDeliveryStation(ds);
			dsr.setCreationTime(new Date());
			dsr.setRule("");
			dsr.setRuleType(DeliveryStationRuleTypeEnum.fallback.getValue());
			dsr.setRuleExpression("");
			this.deliveryStationRuleDao.save(dsr);
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
			SingleAddressMappingResult singleResult = this.search(orderVo, true);

			// *******GIS********
			switch (singleResult.getResult()) {
			case zeroResult:
			case multipleResult:
			case exceptionResult:
				List<DeliveryStation> deliveryStationList = this.searchByGis(orderVo);

				if ((deliveryStationList != null) && (1 == deliveryStationList.size())) {
					singleResult.setResult(AddressMappingResultEnum.singleResult);

					try {
						this.splitAndImportRawAddress(orderVo.getCustomerId(), orderVo.getAddressLine(), deliveryStationList.get(0).getName());
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				singleResult.setRelatedAddressList(new ArrayList<Address>());
				singleResult.setDeliveryStationList(deliveryStationList);
			}
			// *******GIS********

			OrderAddressMappingResult orderResult = new OrderAddressMappingResult();

			List<AddressVo> addressList = new ArrayList<AddressVo>();

			orderResult.setAddressList(addressList);
			for (Address address : singleResult.getRelatedAddressList()) {
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

			if (deliveryStationList.size() == 0) {
				orderResult.setResult(AddressMappingResultEnum.zeroResult);
			}
			if (deliveryStationList.size() == 1) {
				orderResult.setResult(AddressMappingResultEnum.singleResult);
			}
			if (deliveryStationList.size() > 1) {
				orderResult.setResult(AddressMappingResultEnum.multipleResult);
			}

			orderResult.setTimeLimitList(singleResult.getTimeLimitList());
			result.put(orderVo.getOrderId(), orderResult);
		}
		return result;
	}

	private List<DeliveryStation> searchByGis(OrderVo orderVo) {
		return this.gisService.search(orderVo.getAddressLine(), orderVo.getCustomerId());
	}

	private void splitAndImportRawAddress(Long customerId, String addressLine, String stationName) {
		List<AddressStation> addressStationList = new ArrayList<AddressStation>();
		AddressStation addressStation = new AddressStation(addressLine, stationName);
		addressStationList.add(addressStation);

		AddressSplitter addressSplitter = new AddressSplitter(addressStationList);
		List<AddressDetail> addressDetailList = addressSplitter.split();
		if (addressDetailList.size() == 0) {
			return;
		}
		List<String> deliveryStationNameList = new ArrayList<String>();
		for (AddressDetail addressDetail : addressDetailList) {
			deliveryStationNameList.add(addressDetail.getDeliveryStationName());
		}
		this.rawDeliveryStationService.createDeliveryStation(customerId, deliveryStationNameList);
		this.rawAddressService.importAddress(customerId, addressDetailList);
	}

	/**
	 * 匹配接口不做存储
	 *
	 * @param customerId
	 * @param orderList
	 * @return
	 */
	public Map<String, Object> txNoneMatch(Long customerId, List<OrderVo> orderList) {
		Map<String, Object> attributes = new HashMap<String, Object>();
		List<BeanVo> suList = new ArrayList<BeanVo>();
		List<BeanVo> unList = new ArrayList<BeanVo>();
		List<BeanVo> dList = new ArrayList<BeanVo>();
		List<BeanVo> kList = new ArrayList<BeanVo>();
		List<SingleAddressMappingResult> result = new ArrayList<SingleAddressMappingResult>();
		for (OrderVo orderVo : orderList) {
			orderVo.setCustomerId(customerId);
			SingleAddressMappingResult singleResult = this.search(orderVo, false);

			// *******GIS********
			switch (singleResult.getResult()) {
			case zeroResult:
			case multipleResult:
			case exceptionResult:
				List<DeliveryStation> deliveryStationList = this.searchByGis(orderVo);

				if ((deliveryStationList == null) || (0 == deliveryStationList.size())) {
					singleResult.setResult(AddressMappingResultEnum.zeroResult);
				} else if ((deliveryStationList != null) && (1 == deliveryStationList.size())) {
					singleResult.setResult(AddressMappingResultEnum.singleResult);

					try {
						this.splitAndImportRawAddress(orderVo.getCustomerId(), orderVo.getAddressLine(), deliveryStationList.get(0).getName());
					} catch (Exception e) {
						e.printStackTrace();
					}
				} else {
					singleResult.setResult(AddressMappingResultEnum.multipleResult);
				}
				singleResult.setDeliveryStationList(deliveryStationList);
			}
			// *******GIS********

			BeanVo b = new BeanVo();
			b.setKey(orderVo.getAddressLine());
			switch (singleResult.getResult()) {
			case zeroResult:
				b.setVal("未匹配");
				unList.add(b);
				break;
			case singleResult:
				if (singleResult.getDeliveryStationList().isEmpty()) {
					b.setVal("未匹配");
					kList.add(b);
				} else {
					b.setVal(singleResult.getDeliveryStationList().get(0).getName());
					suList.add(b);
				}
				break;
			case multipleResult:
				List<DeliveryStation> dlist = singleResult.getDeliveryStationList();
				StringBuffer names = new StringBuffer();
				for (DeliveryStation deliveryStation : dlist) {
					names.append(deliveryStation.getName() + ",");
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
		int pper = (int) (((suList.size() + dList.size() + 0.0) / orderList.size()) * 100);
		attributes.put("susum", suList.size());
		attributes.put("ksum", kList.size());
		attributes.put("unsum", unList.size());
		attributes.put("dsum", dList.size());
		attributes.put("pper", pper);
		attributes.put("dList", dList);
		attributes.put("unList", unList);
		attributes.put("suList", suList);
		attributes.put("kList", kList);
		return attributes;
	}

	private SingleAddressMappingResult search(OrderVo orderVO, boolean saveable) {
		// 查询订单记录
		Order order = this.cloneOrder(orderVO);
		// 查询结果
		SingleAddressMappingResult result = new SingleAddressMappingResult();
		try {
			// 找到地址
			List<Address> addrList = this.luceneService.search(orderVO.getAddressLine(), orderVO.getCustomerId());
			// 执行站点匹配.
			this.matchDeliveryStation(result, addrList, order, orderVO);
			// 执行小件员匹配.
			this.matchDeliver(result, addrList, order, orderVO);
			// 执行供应商匹配.
			this.matchVender(result, addrList, order, orderVO);

			if ((addrList == null) || (addrList.size() == 0) || (result.getDeliveryStationList().size() == 0)) {
				result.setResult(AddressMappingResultEnum.zeroResult);
			} else if ((addrList.size() == 1) || (result.getDeliveryStationList().size() == 1)) {
				result.setResult(AddressMappingResultEnum.singleResult);
			} else {
				result.setResult(AddressMappingResultEnum.multipleResult);
			}
			result.setRelatedAddressList(addrList);

		} catch (Exception e) {
			AddressService.logger.error("search address failed due to {}", e.getMessage(), e);
			result.setResult(AddressMappingResultEnum.exceptionResult);
			result.setMessage(e.getMessage());
		}
		if (saveable) {
			// this.orderDao.save(order);
		}
		return result;
	}

	private Order cloneOrder(OrderVo orderVO) {
		Order order = new Order();
		BeanUtils.copyProperties(orderVO, order);
		order.setExternalOrderId(orderVO.getOrderId());
		order.setCreationDate(new Date());

		return order;
	}

	private void matchDeliveryStation(SingleAddressMappingResult result, List<Address> addrList, Order order, OrderVo orderVo) {
		Set<DeliveryStation> delStatSet = new HashSet<DeliveryStation>();
		List<DeliveryStationRule> delRuleList = this.deliverStationRuleService.search(addrList, orderVo);
		Set<Long> idSet = new HashSet<Long>();
		for (DeliveryStationRule rule : delRuleList) {
			DeliveryStation station = rule.getDeliveryStation();
			delStatSet.add(station);
			idSet.add(station.getId());
		}
		result.setDeliveryStationList(new ArrayList<DeliveryStation>(delStatSet));
		order.setDeliveryStationIds(AddressUtil.getInPara(idSet));
	}

	private void matchDeliver(SingleAddressMappingResult result, List<Address> addrList, Order order, OrderVo orderVo) {
		List<DelivererRule> delivererRuleList = this.delivererRuleService.search(addrList, orderVo);
		Set<Deliverer> delSet = new HashSet<Deliverer>();
		Set<Long> delIdSet = new HashSet<Long>();
		for (DelivererRule rule : delivererRuleList) {
			Deliverer deliverer = rule.getDeliverer();
			delSet.add(deliverer);
			delIdSet.add(deliverer.getId());
		}
		result.setDelivererList(new ArrayList<Deliverer>(delSet));
		order.setDelivererIds(AddressUtil.getInPara(delIdSet));
	}

	private void matchVender(SingleAddressMappingResult result, List<Address> addrList, Order order, OrderVo orderVo) {
		if (orderVo.getVendorId() != null) {
			List<Integer> timeLimitList = new ArrayList<Integer>();
			for (Address address : addrList) {
				List<VendorsAging> vendorAgingList = this.vendorAgingService.getVendorAgingByExternalId(address.getId(), orderVo.getVendorId(), orderVo.getCustomerId());
				if ((vendorAgingList != null) && (vendorAgingList.size() > 0)) {
					timeLimitList.add(Integer.parseInt(vendorAgingList.get(0).getAging()));
				}
			}
			result.setTimeLimitList(timeLimitList);
		}
	}

	public List<ZTreeNode> getZAddress(Long customerId, String name, Integer isBind) {
		String sql = "SELECT DSR.ADDRESS_ID FROM DELIVERY_STATION_RULES DSR LEFT JOIN DELIVERY_STATIONS DS ON DSR.DELIVERY_STATION_ID=DS.ID  WHERE DS.CUSTOMER_ID=" + customerId;
		Query query = this.getSession().createSQLQuery(sql);
		List<Integer> list = query.list();
		StringBuffer sb = null;
		if ((null != list) && (list.size() > 0) && Integer.valueOf(1).equals(isBind)) {
			sb = new StringBuffer();
			for (Integer aid : list) {
				sb.append(aid + ",");
			}
		}

		return this.addressDao.getZTree(customerId, name, sb);
	}

	public List<ZTreeNode> getAsyncAddress(Long customerId, Long parentId, String ids) {
		return this.addressDao.getAsyncAddress(customerId, parentId, ids);
	}

	public List<Address> addAddressWithStation(Long parentId, String addresses, Long stationId, Long customerId) {
		Address parent = this.addressDao.get(parentId);
		List<Address> list = new ArrayList<Address>();
		for (String addressLine : addresses.split("\n")) {
			if (addressLine.trim().length() == 0) {
				continue;
			}
			Address a = new Address();
			a.setParentId(parentId);
			a.setName(addressLine);
			Address l = this.addressDao.getAddressByNameAndPid(addressLine, parentId);
			if (l != null) {// 已存在则绑定
				a = l;
				this.bindAddress(l, customerId);
				this.bindAddressWithStation(l, stationId);
			} else {
				this.createAndBindAddress(a, parent, customerId);
				this.bindAddressWithStation(a, stationId);
			}
			list.add(a);
		}
		return list;
	}

	public List<Address> addAddress(Long parentId, String addresses, Long customerId) {
		Address parent = this.addressDao.get(parentId);
		List<Address> list = new ArrayList<Address>();
		for (String addressLine : addresses.split("\n")) {
			if (addressLine.trim().length() == 0) {
				continue;
			}
			Address a = new Address();
			a.setParentId(parentId);
			a.setName(addressLine);
			Address l = this.addressDao.getAddressByNameAndPid(addressLine, parentId);
			if (l != null) {// 已存在则绑定
				this.bindAddress(l, customerId);
				a = l;
			} else {
				this.createAndBindAddress(a, parent, customerId);
			}
			list.add(a);
		}
		return list;
	}

	public AjaxJson addAlias(Long addressId, String alias, Long customerId) {
		AjaxJson aj = new AjaxJson();
		Alias a = this.aliasDao.getAliasByAddressIdAndAlias(addressId, alias, customerId);
		Address address = this.addressDao.get(addressId);
		try {
			if (a == null) {
				aj.setSuccess(true);
				a = new Alias();
				a.setAddressId(addressId);
				a.setCustomerId(customerId);
				a.setName(alias);
				a.setOldName(address.getName());
				this.createAlias(a);
			} else {
				aj.setSuccess(false);
				aj.setMsg("已存在别名：" + alias);
			}
		} catch (Exception e) {
			aj.setSuccess(false);
			aj.setMsg(e.getMessage());
		}
		aj.setObj(a);
		return aj;
	}

	public List<Alias> getAliasByAddressId(Long addressId, Long customerId) {
		return this.aliasDao.getAliasByAddressIdAndCustomerId(addressId, customerId);
	}

	public void deleteAlias(Long id) {
		Alias a = this.aliasDao.get(id);
		this.aliasDao.delete(a);
		// this.scheduledTaskService.createScheduledTask(Constants.TASK_TYPE_SUB_UPDATE_INDEX,
		// Constants.REFERENCE_TYPE_ALIAS_ID, String.valueOf(a.getAddressId()));
		// modified by songkaojun 2015-01-10 不创建调度任务，立即执行更新索引操作
		try {
			this.updateIndexRightNow(null, a.getId());
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public List<ZTreeNode> getStationAddressTree(Long customerId, Long parentId) {
		List<ZTreeNode> list = this.getAsyncAddress(customerId, parentId, null);
		this.appendStation(customerId, list);
		return list;
	}

	public void appendStation(Long customerId, List<ZTreeNode> list) {
		if (null == list) {
			return;
		}
		StringBuffer ids = new StringBuffer();
		for (ZTreeNode zTreeNode : list) {
			ids.append(zTreeNode.getId() + ",");
		}
		if (ids.length() > 1) {
			String inIds = ids.toString().substring(0, ids.length() - 1);
			List<BeanVo> dlist = this.deliverStationRuleService.getStationAddressTree(customerId, inIds);
			Map<String, String> view = new HashMap<String, String>();
			if ((null != dlist) && (dlist.size() > 0)) {
				for (BeanVo b : dlist) {
					String key = b.getKey();
					if (view.get(key) != null) {
						view.put(key, b.getVal() + " | " + view.get(key));
					} else {
						view.put(key, b.getVal());
					}
				}
			}
			if (view.size() > 0) {
				for (ZTreeNode zTreeNode : list) {
					if (null != view.get(zTreeNode.getId())) {
						zTreeNode.setName(zTreeNode.getName() + " -- " + view.get(zTreeNode.getId()));
					}
				}
			}
		}
	}

	public List<ZTreeNode> getAdressByStation(Long customerId, String stationId) {
		List<ZTreeNode> address = this.deliverStationRuleService.getAdressByStation(customerId, stationId);
		Set<String> set = new HashSet<String>();
		if ((null != address) && (address.size() > 0)) {
			StringBuffer aIds = new StringBuffer();
			for (ZTreeNode a : address) {
				aIds.append(a.getId() + "-" + a.getT() + "-");
			}
			String[] ids = aIds.toString().split("-");
			for (String id : ids) {
				set.add(id);
			}
			set.remove("");
			aIds.setLength(0);
			for (String string : set) {
				aIds.append(string + ",");
			}
			aIds.setLength(aIds.length() - 1);

			return this.addressDao.getZTreeNodeByIdListAndCustomerId(aIds.toString(), customerId);
		}

		else {
			return null;
		}
	}

	public Map getAdressPromtInfo(Long customerId) {
		Map<String, BigInteger> map = new HashMap<String, BigInteger>();
		String keysql = " select count(1)  from ADDRESS_PERMISSIONS p inner join ADDRESS a on a.id=p.ADDRESS_ID where a.ADDRESS_LEVEL>3 and p.CUSTOMER_ID=" + customerId;
		BigInteger keys = (BigInteger) this.getSession().createSQLQuery(keysql).uniqueResult();
		String bindSql = " select count(DISTINCT  r.ADDRESS_ID) from DELIVERY_STATION_RULES r inner join DELIVERY_STATIONS d on r.DELIVERY_STATION_ID=d.ID where d.STATUS=1 and d.CUSTOMER_ID="
				+ customerId;
		BigInteger binds = (BigInteger) this.getSession().createSQLQuery(bindSql).uniqueResult();
		map.put("keys", keys);
		map.put("binds", binds);
		return map;
	}

	public List<cn.explink.domain.Address> getAddressByNames(Set<String> addressNames, Long customerId) {
		return this.addressDao.getAddressByNames(addressNames, customerId);
	}

	public List<cn.explink.domain.Address> getAdministrationAddress(Set<String> adminNames, Long customerId) {
		return this.addressDao.getAdministrationAddress(adminNames, customerId);
	}

	public List<Address> getAllBands(Long customerId) {
		return this.addressDao.getAllBands(customerId);
	}

	public List<String> findCannotRemoveIds(List<Long> addressIdList, Long customerId) {
		String hql = "select a.path from Address as a, AddressPermission p where a.id = p.addressId and p.customerId = :customerId and a.parentId in :addressIdList and a.id not in:addressIdList";
		Query query = this.getSession().createQuery(hql);
		query.setParameterList("addressIdList", addressIdList);
		query.setLong("customerId", customerId);
		return query.list();
	}

	public List<cn.explink.domain.Address> getAddressByNames(Set<String> addressNames) {
		return this.addressDao.getAddressByNames(addressNames);
	}

	public List<ZTreeNode> getStationAddressTreePage(Long customerId, Long parentId, Integer page, Integer pageSize) {
		List<ZTreeNode> list = this.getAsyncAddressPage(customerId, parentId, null, page, pageSize);
		this.appendStation(customerId, list);
		return list;
	}

	public List<ZTreeNode> getAsyncAddressPage(Long customerId, Long parentId, String ids, Integer page, Integer pageSize) {
		return this.addressDao.getAsyncAddressPage(customerId, parentId, ids, page, pageSize);
	}
}
