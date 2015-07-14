package cn.explink.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.stereotype.Service;

import cn.explink.dao.RawAddressDao;
import cn.explink.dao.RawAddressPermissionDao;
import cn.explink.dao.RawDeliveryStationDao;
import cn.explink.dao.RawDeliveryStationRuleDao;
import cn.explink.domain.AddressDetail;
import cn.explink.domain.RawAddress;
import cn.explink.domain.RawAddressPermission;
import cn.explink.domain.RawDeliveryStation;
import cn.explink.domain.RawDeliveryStationRule;
import cn.explink.domain.enums.AddressStatusEnum;
import cn.explink.domain.enums.DelivererRuleTypeEnum;
import cn.explink.exception.ExplinkRuntimeException;
import cn.explink.spliter.vo.FullRawAddressStationPair;
import cn.explink.spliter.vo.RawAddressQuickVO;
import cn.explink.spliter.vo.RawAddressStationPair;
import cn.explink.util.StringUtil;

@Service
public class RawAddressService extends CommonServiceImpl<RawAddress, Long> {

	public RawAddressService() {
		super(RawAddress.class);
	}

	private static Logger logger = LoggerFactory.getLogger(RawAddressService.class);

	@Autowired
	private SecurityContextHolderStrategy securityContextHolderStrategy;

	@Autowired
	private RawAddressDao rawAddressDao;

	@Autowired
	private RawAddressPermissionDao rawAddressPermissionDao;

	@Autowired
	private RawDeliveryStationDao rawDeliveryStationDao;

	@Autowired
	private RawDeliveryStationRuleDao rawDeliveryStationRuleDao;

	private List<FullRawAddressStationPair> getFullRawAddressStationPair(Long customerId, Long rawAddressId) {
		List<RawAddressStationPair> pairList = this.rawAddressDao.getPageAddressList(customerId, rawAddressId);
		List<RawAddress> fullPathAddrList = this.rawAddressDao.getFullPathAddrList(pairList);
		List<RawDeliveryStation> delStatList = this.rawDeliveryStationDao.getDeliverStation(pairList);

		return this.getFullPairList(pairList, fullPathAddrList, delStatList);
	}

	// public int getRawAddressCount(Long customerId) {
	// return this.rawAddressDao.getRawAddressCount(customerId);
	// }

	private List<FullRawAddressStationPair> getFullPairList(List<RawAddressStationPair> pairList, List<RawAddress> addrList, List<RawDeliveryStation> delStatList) {
		List<FullRawAddressStationPair> fullPairList = new ArrayList<FullRawAddressStationPair>();
		Map<Long, RawAddress> addrMap = this.getAddressMap(addrList);
		Map<Long, RawDeliveryStation> delStatMap = this.getDeliveryStationMap(delStatList);
		for (RawAddressStationPair pair : pairList) {
			fullPairList.add(this.createFullAddrStationPair(addrMap, delStatMap, pair));
		}
		return fullPairList;
	}

	private FullRawAddressStationPair createFullAddrStationPair(Map<Long, RawAddress> addrMap, Map<Long, RawDeliveryStation> delStatMap, RawAddressStationPair pair) {
		FullRawAddressStationPair fullPair = new FullRawAddressStationPair();
		List<RawAddress> fullPathAddrList = this.getFullPathAddrList(pair, addrMap);
		List<RawAddressQuickVO> rawAddressQuickVOList = new ArrayList<RawAddressQuickVO>();
		for (RawAddress address : fullPathAddrList) {
			RawAddressQuickVO rawAddressQuickVO = new RawAddressQuickVO(address.getId(), address.getName());
			rawAddressQuickVOList.add(rawAddressQuickVO);
		}
		fullPair.setAddrList(rawAddressQuickVOList);
		fullPair.setRawDeliveryStation(this.getRawDeliverStation(pair, delStatMap));

		return fullPair;
	}

	private RawDeliveryStation getRawDeliverStation(RawAddressStationPair pair, Map<Long, RawDeliveryStation> delStatMap) {
		return delStatMap.get(pair.getRawStationId());
	}

	private List<RawAddress> getFullPathAddrList(RawAddressStationPair pair, Map<Long, RawAddress> addrMap) {
		List<RawAddress> rawAddressList = new ArrayList<RawAddress>();
		RawAddress rawAddress = addrMap.get(pair.getRawAddressId());
		rawAddressList.add(rawAddress);
		String path = rawAddress.getPath();
		if ((path == null) || path.isEmpty()) {
			return rawAddressList;
		}
		String[] parts = path.split("-");
		for (String part : parts) {
			rawAddressList.add(addrMap.get(Long.valueOf(part)));
		}
		this.sortAddrList(rawAddressList);

		return rawAddressList;
	}

	private void sortAddrList(List<RawAddress> addrList) {
		Collections.sort(addrList, this.getAddressCompartor());
	}

	private RawAddressComparator rawAddressCompartor = new RawAddressComparator();

	private RawAddressComparator getAddressCompartor() {
		return this.rawAddressCompartor;
	}

	private class RawAddressComparator implements Comparator<RawAddress> {

		@Override
		public int compare(RawAddress o1, RawAddress o2) {
			if ((o1.getPath() == null) || o1.getPath().isEmpty()) {
				return -1;
			}
			if ((o2.getPath() == null) || o2.getPath().isEmpty()) {
				return 1;
			}
			return o1.getPath().length() - o2.getPath().length();
		}
	}

	private Map<Long, RawDeliveryStation> getDeliveryStationMap(List<RawDeliveryStation> delStatList) {
		Map<Long, RawDeliveryStation> delStatMap = new HashMap<Long, RawDeliveryStation>();
		for (RawDeliveryStation delStat : delStatList) {
			delStatMap.put(delStat.getId(), delStat);
		}
		return delStatMap;
	}

	private FullRawAddressStationPair createFullRawAddressStationPair(Map<Long, RawAddress> addrMap, Map<Long, RawDeliveryStation> delStatMap, RawAddressStationPair pair) {
		FullRawAddressStationPair fullPair = new FullRawAddressStationPair();
		List<RawAddress> fullPathAddrList = this.getFullPathAddrList(pair, addrMap);
		List<RawAddressQuickVO> rawAddressQuickVOList = new ArrayList<RawAddressQuickVO>();
		for (RawAddress rawAddress : fullPathAddrList) {
			RawAddressQuickVO rawAddressQuickVO = new RawAddressQuickVO(rawAddress.getId(), rawAddress.getName());
			rawAddressQuickVOList.add(rawAddressQuickVO);
		}
		fullPair.setAddrList(rawAddressQuickVOList);
		fullPair.setRawDeliveryStation(this.getRawDeliverStation(pair, delStatMap));

		return fullPair;
	}

	private Map<Long, RawAddress> getAddressMap(List<RawAddress> addrList) {
		Map<Long, RawAddress> addrMap = new HashMap<Long, RawAddress>();
		for (RawAddress addr : addrList) {
			addrMap.put(addr.getId(), addr);
		}
		return addrMap;
	}

	public List<AddressDetail> importAddress(Long customerId, List<AddressDetail> detailList) {
		Map<String, RawAddress> map = new HashMap<String, RawAddress>();// 省市区地址MAP(Key:省-市-区)
		Map<String, RawAddress> addressMap = new HashMap<String, RawAddress>();// 关键字MAP(Key:父ID-名称)
		Map<String, RawDeliveryStation> stationMap = new HashMap<String, RawDeliveryStation>(); // 站点MAP(Key:客户ID-名称)
		Map<Long, RawAddress> bindMap = new HashMap<Long, RawAddress>();// 客户已经包含的地址列表
		Set<String> addressNames = new HashSet<String>();
		Set<String> adminNames = new HashSet<String>();

		for (AddressDetail detail : detailList) {
			this.addNonNullValue(adminNames, detail.getProvince());
			this.addNonNullValue(adminNames, detail.getCity());
			this.addNonNullValue(adminNames, detail.getDistrict());
			this.addNonNullValue(addressNames, detail.getAddressName1());
			this.addNonNullValue(addressNames, detail.getAddressName2());
			this.addNonNullValue(addressNames, detail.getAddressName3());
		}

		List<RawAddress> rawAddressList = new ArrayList<RawAddress>();
		// 查找关键词并构造addressMap
		if (addressNames.size() > 0) {
			rawAddressList = this.rawAddressDao.getRawAddressByNames(addressNames);
		}
		if ((rawAddressList != null) && !rawAddressList.isEmpty()) {
			for (RawAddress a : rawAddressList) {
				addressMap.put(a.getParentId() + "-" + a.getName(), a);
			}
		}
		// 查找所有行政关键词并构造map
		List<RawAddress> adminRawAddressList = this.rawAddressDao.getAdministrationRawAddress(adminNames, customerId);
		if ((adminRawAddressList != null) && !adminRawAddressList.isEmpty()) {
			Map<String, String> m = new HashMap<String, String>();
			for (RawAddress a : adminRawAddressList) {
				m.put(a.getId() + "", a.getName());
			}
			for (RawAddress a : adminRawAddressList) {
				if (Integer.valueOf(3).equals(a.getAddressLevel())) {
					String path = a.getPath();
					String[] ids = path.split("-");
					map.put(m.get(ids[1]) + "-" + m.get(ids[2]) + "-" + a.getName(), a);
				}
			}
		}
		// 构造所有站点Map
		List<RawDeliveryStation> rawStationList = this.rawDeliveryStationDao.listAll(customerId);
		if ((rawStationList != null) && !rawStationList.isEmpty()) {
			for (RawDeliveryStation rawStation : rawStationList) {
				stationMap.put(customerId + "-" + rawStation.getName(), rawStation);
			}
		}

		// 构造该客户的绑定地址
		List<RawAddress> bandList = this.rawAddressDao.getAllBands(customerId);
		if (bandList != null) {
			for (RawAddress a : bandList) {
				bindMap.put(a.getId(), a);
			}
		}

		List<AddressDetail> successImportList = new ArrayList<AddressDetail>();
		for (AddressDetail detail : detailList) {
			RawAddress rawAddress = this.txNewImportDetail(map, detail, addressMap, stationMap, bindMap, customerId);
			if (null == rawAddress) {
				continue;
			}
			List<FullRawAddressStationPair> fullRawAddressStationPair = this.getFullRawAddressStationPair(customerId, rawAddress.getId());

			successImportList.addAll(this.convertToAddressDetail(fullRawAddressStationPair, customerId));
		}
		return successImportList;
	}

	private List<AddressDetail> convertToAddressDetail(List<FullRawAddressStationPair> fullRawAddressStationPairList, Long customerId) {
		List<AddressDetail> addressDetailList = new ArrayList<AddressDetail>();
		for (FullRawAddressStationPair fullRawASPair : fullRawAddressStationPairList) {
			AddressDetail addressDetail = new AddressDetail();
			List<RawAddressQuickVO> rawAddressList = fullRawASPair.getAddrList();

			addressDetail.setProvince(rawAddressList.get(1).getName());
			addressDetail.setCity(rawAddressList.get(2).getName());
			addressDetail.setDistrict(rawAddressList.get(3).getName());
			if ((rawAddressList.size() > 4) && (null != rawAddressList.get(4))) {
				addressDetail.setAddressId1(rawAddressList.get(4).getId());
				addressDetail.setAddressName1(rawAddressList.get(4).getName());
			}
			if ((rawAddressList.size() > 5) && (null != rawAddressList.get(5))) {
				addressDetail.setAddressId2(rawAddressList.get(5).getId());
				addressDetail.setAddressName2(rawAddressList.get(5).getName());
			}
			if ((rawAddressList.size() > 6) && (null != rawAddressList.get(6))) {
				addressDetail.setAddressId3(rawAddressList.get(6).getId());
				addressDetail.setAddressName3(rawAddressList.get(6).getName());
			}
			addressDetail.setDeliveryStationName(fullRawASPair.getRawDeliveryStation().getName());
			addressDetail.setCustomerId(customerId);

			addressDetailList.add(addressDetail);
		}
		return addressDetailList;
	}

	private RawAddress txNewImportDetail(Map<String, RawAddress> map, AddressDetail detail, Map<String, RawAddress> addressMap, Map<String, RawDeliveryStation> stationMap,
			Map<Long, RawAddress> bindMap, Long customerId) {
		if (this.validateDetail(detail)) {
			RawAddress rawAddress = map.get(detail.getProvince() + "-" + detail.getCity() + "-" + detail.getDistrict());
			if (rawAddress == null) {
				RawAddressService.logger.info("省/市/区地址不存在");
				return null;
			} else {
				RawAddress bindAddress = null;// 需要绑定站点或者配送员的地址（动态变化，取最后一级地址）
				RawAddress a1 = null;
				RawAddress a2 = null;
				RawAddress a3 = null;
				boolean isSaved = false;// 一次导入只能保存一个关键字
				// 处理第一关键字
				a1 = addressMap.get(rawAddress.getId() + "-" + detail.getAddressName1());
				if (a1 == null) {// 为空则创建并绑定
					a1 = this.createAndBind(rawAddress, detail.getAddressName1(), customerId);
					isSaved = true;
				} else {
					if (bindMap.get(a1.getId()) == null) {
						this.bindAddress(a1, customerId);
						isSaved = true;
					}
				}
				bindAddress = a1;

				// 处理第二关键字
				if (StringUtils.isNotBlank(detail.getAddressName2())) {
					if (isSaved) {
						RawAddressService.logger.info("父节点不存在");
						return null;
					}
					a2 = addressMap.get(a1.getId() + "-" + detail.getAddressName2());
					if (a2 == null) {// 为空则创建并绑定
						a2 = this.createAndBind(a1, detail.getAddressName2(), customerId);
						isSaved = true;
					} else {// 是否已经绑定
						if (bindMap.get(a2.getId()) == null) {
							this.bindAddress(a2, customerId);
							isSaved = true;
						}
					}
					bindAddress = a2;
				}

				// 处理第三个关键字
				if (StringUtils.isNotBlank(detail.getAddressName3())) {
					if (isSaved) {
						RawAddressService.logger.info("父节点不存在");
						return null;
					}
					a3 = addressMap.get(a2.getId() + "-" + detail.getAddressName3());
					if (a3 == null) {// 为空则创建并绑定
						a3 = this.createAndBind(a2, detail.getAddressName3(), customerId);
						isSaved = true;
					} else {
						if (bindMap.get(a3.getId()) == null) {
							this.bindAddress(a3, customerId);
							isSaved = true;
						}
					}
					bindAddress = a3;
				}

				if (StringUtils.isNotBlank(detail.getDeliveryStationName())) {
					RawDeliveryStation ds = stationMap.get(customerId + "-" + detail.getDeliveryStationName());
					if (ds == null) {
						RawAddressService.logger.info("配送站点不存在");
						return null;
					} else {
						RawDeliveryStationRule rawDeliveryStationRule = new RawDeliveryStationRule();
						rawDeliveryStationRule.setRawAddress(bindAddress);
						rawDeliveryStationRule.setCreationTime(new Date());
						rawDeliveryStationRule.setRawDeliveryStation(ds);
						rawDeliveryStationRule.setRule("");
						rawDeliveryStationRule.setRuleExpression("");
						rawDeliveryStationRule.setRuleType(DelivererRuleTypeEnum.fallback.getValue());
						isSaved = this.addRule(rawDeliveryStationRule, customerId);
					}
				}

				if (!isSaved) {
					RawAddressService.logger.info("数据重复！");
					return null;
				}
				addressMap.put(bindAddress.getParentId() + "-" + bindAddress.getName(), bindAddress);
				bindMap.put(bindAddress.getId(), bindAddress);

				return bindAddress;
			}
		} else {
			RawAddressService.logger.info("导入格式不合规范");
			return null;
		}
	}

	private boolean validateDetail(AddressDetail detail) {
		boolean flag = true;
		if (StringUtils.isBlank(detail.getProvince()) || StringUtils.isBlank(detail.getCity()) || StringUtils.isBlank(detail.getDistrict())) {
			return false;
		} else {
			if (StringUtils.isBlank(detail.getAddressName1())) {
				RawAddressService.logger.info("关键字为空！");
				return false;
			} else {
				if (StringUtils.isBlank(detail.getAddressName2()) && StringUtils.isNotBlank(detail.getAddressName3())) {
					flag = false;
				}
			}
		}
		return flag;
	}

	private RawAddress createAndBind(RawAddress parent, String name, Long customerId) {
		RawAddress rawAddress = new RawAddress();
		rawAddress.setAddressLevel(parent.getAddressLevel() + 1);
		rawAddress.setParentId(parent.getId());
		rawAddress.setCreationTime(new Date());
		rawAddress.setIndexed(false);
		rawAddress.setName(name);
		rawAddress.setPath(parent.getPath() + "-" + parent.getId());
		rawAddress.setStatus(AddressStatusEnum.valid.getValue());
		if (StringUtil.length(rawAddress.getName()) < AddressService.MIN_ADDRESS_LENGTH) {
			throw new ExplinkRuntimeException("关键字长度不能小于2");
		}
		rawAddress = this.rawAddressDao.save(rawAddress);
		RawAddressPermission permission = new RawAddressPermission();
		permission.setRawAddressId(rawAddress.getId());
		permission.setCustomerId(customerId);
		this.rawAddressPermissionDao.save(permission);
		// TODO 创建索引
		return rawAddress;
	}

	private void bindAddress(RawAddress rawAddress, Long customerId) {
		RawAddressPermission rawAddressPermission = new RawAddressPermission();
		rawAddressPermission.setRawAddressId(rawAddress.getId());
		rawAddressPermission.setCustomerId(customerId);
		this.rawAddressPermissionDao.save(rawAddressPermission);
	}

	private boolean addRule(RawDeliveryStationRule rawDeliveryStationRule, Long customerId) {
		List<Long> idList = this.rawDeliveryStationRuleDao.getByAddressAndStation(rawDeliveryStationRule.getRawAddress().getId(), rawDeliveryStationRule.getRawDeliveryStation().getId(), customerId);
		if ((idList != null) && !idList.isEmpty()) {
			RawAddressService.logger.info("该关键字已绑定默认站点");
			return false;
		}
		this.rawDeliveryStationRuleDao.save(rawDeliveryStationRule);
		return true;
	}

	private void addNonNullValue(Collection<String> collection, String element) {
		if (!StringUtil.isEmpty(element)) {
			collection.add(element);
		}
	}
}
