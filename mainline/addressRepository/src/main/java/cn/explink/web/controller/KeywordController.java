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
import org.springframework.web.bind.annotation.ResponseBody;

import cn.explink.domain.Address;
import cn.explink.domain.AddressImportDetail;
import cn.explink.domain.AddressImportResult;
import cn.explink.domain.Deliverer;
import cn.explink.domain.DeliveryStation;
import cn.explink.domain.RawAddress;
import cn.explink.domain.User;
import cn.explink.domain.enums.AddressImportDetailStatsEnum;
import cn.explink.modle.AjaxJson;
import cn.explink.service.AddressImportResultService;
import cn.explink.service.AddressImportService;
import cn.explink.service.AddressService;
import cn.explink.service.DeliveryStationService;
import cn.explink.service.RawAddressService;
import cn.explink.spliter.vo.AddressDetail;
import cn.explink.spliter.vo.FullRawAddressStationPair;
import cn.explink.spliter.vo.RawAddressQuickVO;
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

	public final ObjectMapper mapper = new ObjectMapper();

	@RequestMapping("/keywordMaintain")
	public String addressMapping(Model model) {
		return "address/keywordMaintain";
	}

	@RequestMapping("/loadData")
	@ResponseBody
	public List<RawAddress> loadData(int pageNum, int pageSize, HttpServletRequest request) {
		// try {
		// Map<String, Object> map = this.orderService.getListDataByPage(new
		// OrderVO(), pageNum, pageSize);
		// return (List<OrderVO>) map.get("list");
		// } catch (Exception e) {
		// e.printStackTrace();
		// return new ArrayList<OrderVO>();
		// }
		return null;
	}

	@RequestMapping("/query")
	@ResponseBody
	public Map<String, Object> queryByPage(String address, String station, int pageNum, int pageSize, HttpServletRequest request) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		List<AddressDetail> addressDetailList = new ArrayList<AddressDetail>();
		Long customerId = this.getCustomerId();
		List<FullRawAddressStationPair> fullRawAddressStationPairList = this.rawAddressService.getFullRawAddressStationPair(customerId, address, station, pageNum, pageSize);
		for (FullRawAddressStationPair fullRawASPair : fullRawAddressStationPairList) {
			AddressDetail addressDetail = new AddressDetail();
			List<RawAddressQuickVO> rawAddressList = fullRawASPair.getAddrList();

			addressDetail.setProvince(rawAddressList.get(1).getName());
			addressDetail.setCity(rawAddressList.get(2).getName());
			addressDetail.setDistrict(rawAddressList.get(3).getName());
			if ((rawAddressList.size() > 4) && (null != rawAddressList.get(4))) {
				addressDetail.setAddress1(rawAddressList.get(4).getName());
			}
			if ((rawAddressList.size() > 5) && (null != rawAddressList.get(5))) {
				addressDetail.setAddress2(rawAddressList.get(5).getName());
			}
			if ((rawAddressList.size() > 6) && (null != rawAddressList.get(6))) {
				addressDetail.setAddress3(rawAddressList.get(6).getName());
			}
			addressDetail.setDeliveryStationName(fullRawASPair.getRawDeliveryStation().getName());

			addressDetailList.add(addressDetail);
		}
		resultMap.put("count", addressDetailList.size());
		resultMap.put("list", addressDetailList);
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
		for (AddressDetail addressDetail : addressDetailList) {
			AddressImportDetail addressImportDetail = new AddressImportDetail();
			addressImportDetail.setProvince(addressDetail.getProvince());
			addressImportDetail.setCity(addressDetail.getCity());
			addressImportDetail.setDistrict(addressDetail.getDistrict());
			addressImportDetail.setAddress1(addressDetail.getAddress1());
			addressImportDetail.setAddress2(addressDetail.getAddress2());
			addressImportDetail.setAddress3(addressDetail.getAddress3());
			addressImportDetail.setDeliveryStationName(addressDetail.getDeliveryStationName());

			addressImportDetailList.add(addressImportDetail);
		}

		try {
			addressImportResult = this.importAddress(addressImportDetailList, this.getLogginedUser(), AddressImportTypeEnum.init.getValue(), null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		Integer failureCount = addressImportResult.getFailureCount();
		if (failureCount > 0) {
			aj.setSuccess(false);
		} else {
			aj.setSuccess(true);
		}
		if (null != addressImportResult) {
			StringBuffer sb = new StringBuffer();
			Set<AddressImportDetail> addressImportDetailSet = addressImportResult.getAddressImportDetails();
			for (AddressImportDetail addressImportDetail : addressImportDetailSet) {
				sb.append(addressImportDetail.getMessage() + "<br/>");
			}
			aj.setMsg(sb.toString());
		}
		return aj;
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
		if (importType == AddressImportTypeEnum.init.getValue()) {
			this.addressImportResultService.save(result);
		}
		return result;
	}

}
