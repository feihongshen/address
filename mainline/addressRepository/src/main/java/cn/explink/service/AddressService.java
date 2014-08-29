package cn.explink.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
import cn.explink.domain.Order;
import cn.explink.domain.enums.AddressStatusEnum;
import cn.explink.schedule.Constants;
import cn.explink.ws.vo.AddressMappingResultEnum;
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

	public List<Address> getChildAddressTree(Long parentId, Long customerId) {
		if (parentId == null) {
			parentId = cn.explink.Constants.ADDRESS_ID_CHINA;
		}
		List<Address> addressList = addressDao.getChildAddress(parentId, customerId);
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
			SingleAddressMappingResult singleResult = search(orderVo);
			result.add(singleResult);
		}
		return result;
	}

	private SingleAddressMappingResult search(OrderVo orderVo) {
		SingleAddressMappingResult result = new SingleAddressMappingResult();

		try {
			List<Address> addressList = luceneService.search(orderVo.getAddressLine(), orderVo.getCustomerId());
			deliverStationRuleService.search(addressList, orderVo);
			
		} catch (Exception e) {
			logger.error("search address failed due to {}", e.getMessage(), e);
			result.setResult(AddressMappingResultEnum.exceptionResult);
			result.setMessage(e.getMessage());
		}

		Order order = new Order();
		BeanUtils.copyProperties(orderVo, order);
		order.setExternalOrderId(orderVo.getOrderId());
		order.setCreationDate(new Date());
//		order.setDelivererIds(delivererIds);
		orderDao.save(order);
		return null;
	}
}
