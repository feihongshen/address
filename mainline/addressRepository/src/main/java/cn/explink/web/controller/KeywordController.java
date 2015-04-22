package cn.explink.web.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONArray;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.JavaType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.explink.domain.Address;
import cn.explink.domain.AddressDetail;
import cn.explink.domain.AddressImportDetail;
import cn.explink.domain.AddressImportResult;
import cn.explink.domain.Deliverer;
import cn.explink.domain.DeliveryStation;
import cn.explink.domain.KeywordSuffix;
import cn.explink.domain.User;
import cn.explink.domain.enums.AddressImportDetailStatsEnum;
import cn.explink.modle.AjaxJson;
import cn.explink.service.AddressDetailService;
import cn.explink.service.AddressImportResultService;
import cn.explink.service.AddressImportService;
import cn.explink.service.AddressService;
import cn.explink.service.DeliveryStationService;
import cn.explink.service.KeywordSuffixService;
import cn.explink.service.RawAddressPermissionService;
import cn.explink.service.RawAddressService;
import cn.explink.service.RawDeliveryStationRuleService;
import cn.explink.util.StringUtil;
import cn.explink.web.vo.AddressImportTypeEnum;

@RequestMapping("/keyword")
@Controller
public class KeywordController extends BaseController {

	private static Logger logger = LoggerFactory.getLogger(KeywordController.class);

	@Autowired
	private RawAddressService rawAddressService;

	@Autowired
	private AddressImportService addressImportService;

	@Autowired
	private AddressService addressService;

	@Autowired
	private DeliveryStationService deliveryStationService;

	@Autowired
	private AddressImportResultService addressImportResultService;

	@Autowired
	private RawAddressPermissionService rawAddressPermissionService;

	@Autowired
	private KeywordSuffixService keywordSuffixService;

	@Autowired
	private AddressDetailService addressDetailService;

	@Autowired
	private RawDeliveryStationRuleService rawDeliveryStationRuleService;

	public final ObjectMapper mapper = new ObjectMapper();

	@RequestMapping("/keywordMaintain")
	public String addressMapping(Model model) {
		return "address/keywordMaintain";
	}

	@RequestMapping("/loadData")
	@ResponseBody
	public List<AddressDetail> loadData(int pageNum, int pageSize, HttpServletRequest request) {
		try {
			Long customerId = this.getCustomerId();
			// List<FullRawAddressStationPair> fullRawAddressStationPairList =
			// this.rawAddressService.getFullRawAddressStationPair(customerId,
			// pageNum, pageSize);
			// List<AddressDetail> addressDetailList =
			// this.convertToAddressDetail(fullRawAddressStationPairList);
			// return addressDetailList;
			return new ArrayList<AddressDetail>();
		} catch (Exception e) {
			e.printStackTrace();
			return new ArrayList<AddressDetail>();
		}
	}

	@RequestMapping("/query")
	@ResponseBody
	public Map<String, Object> queryByPage(String keyword, String station, int pageNum, int pageSize, HttpServletRequest request) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		Long customerId = this.getCustomerId();

		List<AddressDetail> filtedAddressDetailList = this.addressDetailService.fuzzyQueryByPage(keyword, station, customerId, pageNum, pageSize);

		resultMap.put("count", this.addressDetailService.getAddressDetailCount(keyword, station, customerId));
		resultMap.put("list", filtedAddressDetailList);
		return resultMap;
	}

	@SuppressWarnings("unchecked")
	@RequestMapping("/save")
	@ResponseBody
	public AjaxJson save(String addressDetailListJson, HttpServletRequest request) {
		AjaxJson aj = new AjaxJson();
		AddressImportResult addressImportResult = null;
		List<AddressDetail> addressDetailList = null;

		JavaType javaType = this.getCollectionType(ArrayList.class, AddressDetail.class);
		try {
			addressDetailList = (List<AddressDetail>) this.mapper.readValue(addressDetailListJson, javaType);
		} catch (JsonParseException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		List<AddressImportDetail> addressImportDetailList = new ArrayList<AddressImportDetail>();
		if (null != addressDetailList) {
			for (AddressDetail addressDetail : addressDetailList) {
				AddressImportDetail addressImportDetail = new AddressImportDetail();
				addressImportDetail.setProvince(addressDetail.getProvince());
				addressImportDetail.setCity(addressDetail.getCity());
				addressImportDetail.setDistrict(addressDetail.getDistrict());
				addressImportDetail.setAddress1(addressDetail.getAddressName1());
				addressImportDetail.setAddress2(addressDetail.getAddressName2());
				addressImportDetail.setAddress3(addressDetail.getAddressName3());
				addressImportDetail.setDeliveryStationName(addressDetail.getDeliveryStationName());

				addressImportDetailList.add(addressImportDetail);
			}
		}

		try {
			addressImportResult = this.importAddress(addressImportDetailList, this.getLogginedUser(), AddressImportTypeEnum.init.getValue(), null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		// 解绑“粗”地址信息
		this.unbindRawAddress(addressImportDetailList, addressDetailList);

		Integer failureCount = addressImportResult.getFailureCount();
		if ((null != failureCount) && (failureCount > 0)) {
			aj.setSuccess(false);
		} else {
			aj.setSuccess(true);
		}
		if (null != addressImportResult) {
			StringBuffer sb = new StringBuffer();
			Set<AddressImportDetail> addressImportDetailSet = addressImportResult.getAddressImportDetails();
			for (AddressImportDetail addressImportDetail : addressImportDetailSet) {
				if (addressImportDetail.getStatus() == AddressImportDetailStatsEnum.success.getValue()) {
					sb.append(this.getLastAddress(addressImportDetail) + "--" + "同步成功<br/>");
				} else {
					sb.append(this.getLastAddress(addressImportDetail) + "--" + addressImportDetail.getMessage() + "<br/>");
				}
			}
			aj.setMsg(sb.toString());
		}
		return aj;
	}

	private String getLastAddress(AddressImportDetail addressImportDetail) {
		String address3 = addressImportDetail.getAddress3();
		String address2 = addressImportDetail.getAddress2();
		String address1 = addressImportDetail.getAddress1();
		if (StringUtil.isNotEmpty(address3)) {
			return address3;
		} else if (StringUtil.isNotEmpty(address2)) {
			return address2;
		} else {
			return address1;
		}
	}

	private void unbindRawAddress(List<AddressImportDetail> addressImportDetailList, List<AddressDetail> addressDetailList) {
		if (null == addressDetailList) {
			return;
		}
		List<Long> rawAddressIdList = new ArrayList<Long>();
		List<Long> addressDetailIdList = new ArrayList<Long>();
		for (AddressImportDetail addressImportDetail : addressImportDetailList) {
			if (addressImportDetail.getStatus() != AddressImportDetailStatsEnum.success.getValue()) {
				continue;
			}
			for (AddressDetail addressDetail : addressDetailList) {
				if ((addressDetail.getAddressId3() != null) && (addressDetail.getAddressId3() != 0)) {
					rawAddressIdList.add(addressDetail.getAddressId3());
				} else if ((addressDetail.getAddressId2() != null) && (addressDetail.getAddressId2() != 0)) {
					rawAddressIdList.add(addressDetail.getAddressId2());
				} else if ((addressDetail.getAddressId1() != null) && (addressDetail.getAddressId1() != 0)) {
					rawAddressIdList.add(addressDetail.getAddressId1());
				}
				addressDetailIdList.add(addressDetail.getId());
			}

		}

		// 删除ADDRESS_DETAIL中对应数据
		if (addressDetailIdList.size() > 0) {
			this.addressDetailService.deleteByIdList(addressDetailIdList);
		}
		// 删除raw_delivery_station_rules中“地址-站点”管理关系和raw_address_permissions中“地址-客户”关联关系
		if (rawAddressIdList.size() > 0) {
			this.rawDeliveryStationRuleService.batchUnbindRawAddressStationRule(rawAddressIdList);
			this.rawAddressPermissionService.batchUnbindAddress(rawAddressIdList, this.getCustomerId());
		}

	}

	/**
	 * 获取泛型的Collection Type
	 *
	 * @param collectionClass
	 *            泛型的Collection
	 * @param elementClasses
	 *            元素类
	 * @return JavaType Java类型
	 * @since 1.0
	 */
	private JavaType getCollectionType(Class<?> collectionClass, Class<?>... elementClasses) {
		return this.mapper.getTypeFactory().constructParametricType(collectionClass, elementClasses);
	}

	private AddressImportResult importAddress(List<AddressImportDetail> addressImportDetailList, User user, Integer importType, Long stationId) throws Exception {
		Long customerId = user.getCustomer().getId();
		AddressImportResult result = new AddressImportResult();
		Map<String, Address> map = new HashMap<String, Address>();// 省市区地址MAP(Key:省-市-区)
		Map<String, Address> addressMap = new HashMap<String, Address>();// 关键字MAP(Key:父ID-名称)
		Map<String, DeliveryStation> stationMap = new HashMap<String, DeliveryStation>(); // 站点MAP(Key:客户ID-名称)
		Map<String, Deliverer> delivererMap = new HashMap<String, Deliverer>();// 小件员MAP(Key:客户ID-名称)
		Map<Long, Address> bindMap = new HashMap<Long, Address>();// 客户已经包含的地址列表
		Set<String> addressNames = new HashSet<String>();
		Set<String> adminNames = new HashSet<String>();

		for (AddressImportDetail addressImportDetail : addressImportDetailList) {
			this.addressImportService.addNonNullValue(adminNames, addressImportDetail.getProvince());
			this.addressImportService.addNonNullValue(adminNames, addressImportDetail.getCity());
			this.addressImportService.addNonNullValue(adminNames, addressImportDetail.getDistrict());
			this.addressImportService.addNonNullValue(addressNames, addressImportDetail.getAddress1());
			this.addressImportService.addNonNullValue(addressNames, addressImportDetail.getAddress2());
			this.addressImportService.addNonNullValue(addressNames, addressImportDetail.getAddress3());
		}

		// 查找关键词并构造addressMap
		List<Address> addressList = this.addressService.getAddressByNames(addressNames);
		if ((addressList != null) && !addressList.isEmpty()) {
			for (Address a : addressList) {
				addressMap.put(a.getParentId() + "-" + a.getName(), a);
			}
		}
		// 查找所有行政关键词并构造map
		List<Address> list = this.addressService.getAdministrationAddress(adminNames, customerId);
		if ((list != null) && !list.isEmpty()) {
			Map<String, String> m = new HashMap<String, String>();
			for (Address a : list) {
				m.put(a.getId() + "", a.getName());
			}
			for (Address a : list) {
				if (Integer.valueOf(3).equals(a.getAddressLevel())) {
					String path = a.getPath();
					String[] ids = path.split("-");
					map.put(m.get(ids[1]) + "-" + m.get(ids[2]) + "-" + a.getName(), a);
				}
			}
		}
		// 构造所有站点Map
		List<DeliveryStation> stationList = this.deliveryStationService.listAll(customerId);
		if ((stationList != null) && !stationList.isEmpty()) {
			for (DeliveryStation ds : stationList) {
				stationMap.put(customerId + "-" + ds.getName(), ds);
			}
		}

		// 构造该客户的绑定地址
		List<Address> bandList = this.addressService.getAllBands(customerId);
		if (bandList != null) {
			for (Address a : bandList) {
				bindMap.put(a.getId(), a);
			}
		}
		for (AddressImportDetail detail : addressImportDetailList) {
			try {
				this.addressImportService.txNewImportDetail(map, detail, addressMap, stationMap, delivererMap, bindMap, customerId, importType, stationId);
			} catch (Exception e) {
				detail.setStatus(AddressImportDetailStatsEnum.failure.getValue());
				detail.setMessage(e.getMessage());
				KeywordController.logger.info(e.getMessage());
			}
		}
		Set<AddressImportDetail> detailSet = new HashSet<AddressImportDetail>();
		detailSet.addAll(addressImportDetailList);
		result.setAddressImportDetails(detailSet);
		int successCount = 0;
		int failureCount = 0;
		for (AddressImportDetail detail : addressImportDetailList) {
			if ((detail.getStatus() != null) && (detail.getStatus().intValue() == AddressImportDetailStatsEnum.success.getValue())) {
				successCount++;
			} else {
				failureCount++;
			}
		}
		result.setSuccessCount(successCount);
		result.setFailureCount(failureCount);
		result.setImportDate(new Date());
		result.setUserId(user.getId());
		return result;
	}

	/**
	 *
	 * @param rowList
	 * @param request
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping("/delete")
	@ResponseBody
	public AjaxJson deleteRows(String rowList, HttpServletRequest request) {
		AjaxJson j = new AjaxJson();
		List<AddressDetail> addressDetailList = new ArrayList<AddressDetail>();
		try {
			JSONArray json = JSONArray.fromObject(rowList);
			addressDetailList = JSONArray.toList(json, AddressDetail.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
		List<Long> rawAddressIdList = new ArrayList<Long>();
		List<Long> addressDetailIdList = new ArrayList<Long>();
		for (AddressDetail addressDetail : addressDetailList) {
			if (StringUtil.isNotEmpty(addressDetail.getAddressName3())) {
				rawAddressIdList.add(addressDetail.getAddressId3());
			} else if (StringUtil.isNotEmpty(addressDetail.getAddressName2())) {
				rawAddressIdList.add(addressDetail.getAddressId2());
			} else if (StringUtil.isNotEmpty(addressDetail.getAddressName1())) {
				rawAddressIdList.add(addressDetail.getAddressId1());
			}
			addressDetailIdList.add(addressDetail.getId());
		}
		if (addressDetailIdList.size() > 0) {
			this.addressDetailService.deleteByIdList(addressDetailIdList);
		}

		// 删除raw_delivery_station_rules中“地址-站点”管理关系和raw_address_permissions中“地址-客户”关联关系
		if (rawAddressIdList.size() > 0) {
			int unbindRawAddressStationRuleCount = this.rawDeliveryStationRuleService.batchUnbindRawAddressStationRule(rawAddressIdList);
			int unbindAddressCount = this.rawAddressPermissionService.batchUnbindAddress(rawAddressIdList, this.getCustomerId());
			if ((rawAddressIdList.size() == unbindAddressCount) && (rawAddressIdList.size() == unbindRawAddressStationRuleCount)) {
				j.setSuccess(true);
			}
		}

		return j;
	}

	@RequestMapping("/getKeywordSuffix")
	public @ResponseBody List<KeywordSuffix> getKeywordSuffix() {
		Long customerId = this.getCustomerId();
		return this.keywordSuffixService.getKeywordSuffixByCustomerId(customerId);
	}

	@RequestMapping("/addKeywordSuffix")
	public @ResponseBody AjaxJson addKeywordSuffix(@RequestParam(value = "keywordSuffix", required = false) String keywordSuffix) {
		AjaxJson aj = null;
		Long customerId = this.getCustomerId();
		if (keywordSuffix != null) {
			aj = this.keywordSuffixService.addKeywordSuffix(keywordSuffix, customerId);
		}
		return aj;
	}

	@RequestMapping("/deleteKeywordSuffix")
	public @ResponseBody AjaxJson deleteKeywordSuffix(@RequestParam(value = "id") Long id) {
		AjaxJson aj = new AjaxJson();
		this.keywordSuffixService.deleteKeywordSuffix(id);
		aj.setSuccess(true);
		return aj;
	}

}
