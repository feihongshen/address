package cn.explink.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.CascadeType;

import org.apache.poi.ss.formula.functions.T;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.explink.dao.AddressDao;
import cn.explink.dao.AddressImportDetailDao;
import cn.explink.dao.AddressImportResultDao;
import cn.explink.domain.Address;
import cn.explink.domain.AddressImportDetail;
import cn.explink.domain.AddressImportResult;
import cn.explink.domain.DeliveryStationRule;
import cn.explink.domain.User;
import cn.explink.domain.enums.AddressImportDetailStatsEnum;
import cn.explink.domain.enums.AddressStatusEnum;
import cn.explink.exception.ExplinkRuntimeException;
import cn.explink.modle.DataGrid;
import cn.explink.modle.DataGridReturn;
import cn.explink.tree.AddressImportEntry;
import cn.explink.tree.TreeNode;
import cn.explink.util.StringUtil;

@Service
public class AddressImportService extends CommonServiceImpl<AddressImportDetail,Long> {

	public AddressImportService() {
		super(AddressImportDetail.class);
	}

	private static Logger logger = LoggerFactory.getLogger(AddressImportService.class);

	@Autowired
	private AddressDao addressDao;

	@Autowired
	private AddressImportResultDao addressImportResultDao;

	@Autowired
	private AddressService addressService;
	

	@Autowired
	private DeliveryStationRule deliveryStationRule;
	
	@Autowired
	private AddressImportDetailDao addressImportDetailDao;

	/**
	 * 创建导入模板
	 * @param headerNameList
	 * @return
	 */
	public XSSFWorkbook createAddressTemplate(List<String> headerNameList) {
		XSSFWorkbook wookbook = new XSSFWorkbook();
		XSSFSheet sheet = wookbook.createSheet();
		XSSFRow row = sheet.createRow(0);
		int columnIndex = 0;
		for (String headerName : headerNameList) {
			XSSFCell cell = row.createCell(columnIndex);
			cell.setCellValue(headerName);
			columnIndex++;
		}
		return wookbook;
	}
	
	/**
	 * 
	 * @param in
	 * @return
	 */
	public AddressImportResult importAddress(InputStream in, User user) {
		Long customerId = user.getCustomer().getId();
		AddressImportResult result = new AddressImportResult();
		List<AddressImportDetail> details = new ArrayList<AddressImportDetail>();
		try {
			XSSFWorkbook wb = new XSSFWorkbook(in);
			XSSFSheet sheet = wb.getSheetAt(0);
			int rowNum = 1;
			Set<String> addressNames = new HashSet<String>();
			while (true) {
				XSSFRow row = sheet.getRow(rowNum);
				if (row == null) {
					break;
				}
				rowNum++;
				String province = row.getCell(0) == null ? null : row.getCell(0).getStringCellValue();
				String city = row.getCell(1) == null ? null : row.getCell(1).getStringCellValue();
				String district = row.getCell(2) == null ? null : row.getCell(2).getStringCellValue();
				String address1 = row.getCell(3) == null ? null : row.getCell(3).getStringCellValue();
				String address2 = row.getCell(4) == null ? null : row.getCell(4).getStringCellValue();
				String address3 = row.getCell(5) == null ? null : row.getCell(5).getStringCellValue();
				String deliveryStationName = row.getCell(6) == null ? null : row.getCell(6).getStringCellValue();
				String delivererName = row.getCell(7) == null ? null : row.getCell(7).getStringCellValue();
				
				addNonNullValue(addressNames, province);
				addNonNullValue(addressNames, city);
				addNonNullValue(addressNames, district);
				addNonNullValue(addressNames, address1);
				addNonNullValue(addressNames, address2);
				addNonNullValue(addressNames, address3);
				
				AddressImportDetail detail = new AddressImportDetail();
				detail.setProvince(province);
				detail.setCity(city);
				detail.setDistrict(district);
				detail.setAddress1(address1);
				detail.setAddress2(address2);
				detail.setAddress3(address3);
				detail.setDeliveryStationName(deliveryStationName);
				detail.setDelivererName(delivererName);
				//TODO CascadeType.ALL
				detail.setAddressImportResult(result);
				details.add(detail);
			}
			
			List<Address> addressList = addressDao.getAddressByNames(addressNames);
			// 将现有的地址按名字分入map中，同一节点下的地址不能有重名，但按名字查询的地址可能有重名
			Map<String, List<Address>> addressMap = new HashMap<String, List<Address>>();
			for (Address address : addressList) {
				String name = address.getName();
				List<Address> tmpAddressList = addressMap.get(name);
				if (tmpAddressList == null) {
					tmpAddressList = new ArrayList<Address>();
					addressMap.put(name, tmpAddressList);
				}
				tmpAddressList.add(address);
			}
			
			TreeNode<AddressImportEntry> tree = new TreeNode<AddressImportEntry>();
			Address rootAddress = addressDao.get(1L);
			AddressImportEntry root = new AddressImportEntry();
			root.setAddress(rootAddress);
			tree.setSelf(root);
			//TODO REMOVE?
			for (AddressImportDetail detail : details) {
				TreeNode<AddressImportEntry> treeNode = getTreeNode(tree, addressMap, detail, 1, customerId);
			}
		} catch (IOException e) {
			logger.error("importAddress failed due to {}", e);
			return null;
		}

		Set<AddressImportDetail> detailSet = new HashSet<AddressImportDetail>();
		detailSet.addAll(details);
		result.setAddressImportDetails(detailSet);
		int successCount = 0;
		int failureCount = 0;
		for (AddressImportDetail detail : details) {
			if (detail.getStatus() != null && detail.getStatus().intValue() == AddressImportDetailStatsEnum.success.getValue()) {
				successCount++;
			} else {
				failureCount++;
			}
		}
		result.setSuccessCount(successCount);
		result.setFailureCount(failureCount);
		result.setImportDate(new Date());
		result.setUserId(user.getId());
		addressImportResultDao.save(result);
		return result;
	}

	/**
	 * 
	 * @param treeNode
	 * @param addressMap 
	 * @param detail
	 * @param level 级别，123对应省市区，456对应3级自定义关键字
	 * @param customerId 
	 * @return
	 */
	private TreeNode<AddressImportEntry> getTreeNode(TreeNode<AddressImportEntry> treeNode, Map<String, List<Address>> addressMap, AddressImportDetail detail, int level, Long customerId) {
		String name = getAddressName(detail, level);
		String nextLevelName = getAddressName(detail, level + 1);
		// 是否最后一级地址
		boolean isLast = false;
		if (StringUtil.isEmpty(nextLevelName)) {
			isLast = true;
		}
		
		// 维护tree结构
		AddressImportEntry childEntry = null;
		TreeNode<AddressImportEntry> childNode = treeNode.getChild(name);
		if (childNode == null) {
			childNode = new TreeNode<AddressImportEntry>();
			childNode.setName(name);
			childEntry = new AddressImportEntry();
			childNode.setSelf(childEntry);
			childNode.setParent(treeNode);
			treeNode.addChild(name, childNode);
		} else {
			childEntry = childNode.getSelf();
		}
		
		// 子节点对应的地址
		Address childAddress = childEntry.getAddress();
		Address parentAddress = null;
		if (childAddress == null) {
			List<Address> addressList = addressMap.get(name);
			TreeNode<AddressImportEntry> parentNode = childNode.getParent();
			if (parentNode == null || parentNode.getSelf() == null) {
				// 省市区为易普联科维护，不应当出现省/直辖市同名
				throw new ExplinkRuntimeException("导入地址第一级冲突");
			}
			parentAddress = parentNode.getSelf().getAddress();
			
			if (addressList != null) {
				// 根据父子关系挑选正确的同名地址
				for (Address tmpAddress : addressList) {
					if (tmpAddress.getParentId().equals(parentAddress.getId())) {
						childAddress = tmpAddress;
					}
				}
			}
			if (childAddress != null) {
				// 查找到子节点对应的地址
				childEntry.setAddress(childAddress);
			}
		}
		
		// 本节点对应的地址不存在
		if (childAddress == null) {
			if (isLast) {
				// 如果是当前已是最后一级地址，创建新地址
				childAddress = createAddress(parentAddress, detail, name, customerId);
				childEntry.setAddress(childAddress);
				detail.setStatus(AddressImportDetailStatsEnum.success.getValue());
				detail.setAddressId(childAddress.getId());
			} else {
				// 失败，父节点不存在
				detail.setStatus(AddressImportDetailStatsEnum.failure.getValue());
				detail.setMessage("父节点不存在：" + name);
			}
		} else {
			if (isLast) {
				// 最后一级地址已存在
				boolean bindResult = addressService.bindAddress(childAddress, customerId);
				if (bindResult) {
					detail.setStatus(AddressImportDetailStatsEnum.failure.getValue());
					detail.setMessage("地址重复:" + name);
				} else {
					detail.setStatus(AddressImportDetailStatsEnum.success.getValue());
					detail.setAddressId(childAddress.getId());
				}
			} else {
				// 不是最后一级，继续查找子节点
				return getTreeNode(childNode, addressMap, detail, level + 1, customerId);
			}
			
		}
		return childNode;
	}

	private Address createAddress(Address parentAddress, AddressImportDetail detail, String name, Long customerId) {
		Address address = new Address();
		address.setName(name);
		address.setAddressLevel(parentAddress.getAddressLevel() + 1);
//		address.setCustomerId(customerId);
		address.setParentId(parentAddress.getId());
		address.setPath(parentAddress.getPath() + "-" + parentAddress.getId());
		address.setStatus(AddressStatusEnum.valid.getValue());
		addressService.createAndBindAddress(address, parentAddress, customerId);
		return address;
	}

	protected String getAddressName(AddressImportDetail detail, int level) {
		String name = null;
		switch (level) {
			case 1: {
				name = detail.getProvince();
				break;
			}
			case 2: {
				name = detail.getCity();
				break;
			}
			case 3: {
				name = detail.getDistrict();
				break;
			}
			case 4: {
				name = detail.getAddress1();
				break;
			}
			case 5: {
				name = detail.getAddress2();
				break;
			}
			case 6: {
				name = detail.getAddress3();
				break;
			}
			default: {
				name = null;
			}
		}
		return name;
	}

	private void addNonNullValue(Collection<String> collection, String element) {
		if (!StringUtil.isEmpty(element)) {
			collection.add(element);
		}
	}

	/**
	 * 查询导入结果列表
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	public List<AddressImportResult> getImportAddressResults(Date startDate, Date endDate) {
		return addressImportResultDao.getImportAddressResults(startDate, endDate);
	}

	/**
	 * 删除导入结果
	 * @param resultId
	 */
	public void deleteImportAddressResult(Long resultId, Long customerId) {
		AddressImportResult addressImportResult = addressImportResultDao.get(resultId);
		Set<AddressImportDetail> details = addressImportResult.getAddressImportDetails();
		List<Long> addressIdList = new ArrayList<Long>();
		for (AddressImportDetail detail : details) {
			Integer i=AddressImportDetailStatsEnum.success.getValue();
			if (i == detail.getStatus()) {
				addressIdList.add(detail.getAddressId());
			}
		}
		if (addressIdList.size() > 0) {
			addressService.batchUnbindAddress(addressIdList, customerId);
		}
		addressImportResultDao.delete(addressImportResult);
	}


	public List<AddressImportDetail> getAllDetail() {
		return addressImportDetailDao.loadAll(AddressImportDetail.class);
		
	}
    /**
     * 变更地址挂靠站点
     * @param in
     * @param user
     */
	public AddressImportResult importKwAddress(InputStream in, User user) {
		Long customerId = user.getCustomer().getId();
		AddressImportResult result = new AddressImportResult();
		List<AddressImportDetail> details = new ArrayList<AddressImportDetail>();
		try {
			XSSFWorkbook wb = new XSSFWorkbook(in);
			XSSFSheet sheet = wb.getSheetAt(0);
			int rowNum = 1;
			Set<String> addressNames = new HashSet<String>();
			while (true) {
				XSSFRow row = sheet.getRow(rowNum);
				if (row == null) {
					break;
				}
				rowNum++;
				String province = row.getCell(0) == null ? null : row.getCell(0).getStringCellValue();
				String city = row.getCell(1) == null ? null : row.getCell(1).getStringCellValue();
				String district = row.getCell(2) == null ? null : row.getCell(2).getStringCellValue();
				String address1 = row.getCell(3) == null ? null : row.getCell(3).getStringCellValue();
				String address2 = row.getCell(4) == null ? null : row.getCell(4).getStringCellValue();
				String address3 = row.getCell(5) == null ? null : row.getCell(5).getStringCellValue();
				String deliveryStationName = row.getCell(6) == null ? null : row.getCell(6).getStringCellValue();
				String delivererName = row.getCell(7) == null ? null : row.getCell(7).getStringCellValue();
				
				addNonNullValue(addressNames, province);
				addNonNullValue(addressNames, city);
				addNonNullValue(addressNames, district);
				addNonNullValue(addressNames, address1);
				addNonNullValue(addressNames, address2);
				addNonNullValue(addressNames, address3);
				
				AddressImportDetail detail = new AddressImportDetail();
				detail.setProvince(province);
				detail.setCity(city);
				detail.setDistrict(district);
				detail.setAddress1(address1);
				detail.setAddress2(address2);
				detail.setAddress3(address3);
				detail.setDeliveryStationName(deliveryStationName);
				detail.setDelivererName(delivererName);
				//TODO CascadeType.ALL
				detail.setAddressImportResult(result);
				details.add(detail);
			}
			
			List<Address> addressList = addressDao.getAddressByNames(addressNames);
			// 将现有的地址按名字分入map中，同一节点下的地址不能有重名，但按名字查询的地址可能有重名
			Map<String, List<Address>> addressMap = new HashMap<String, List<Address>>();
			for (Address address : addressList) {
				String name = address.getName();
				List<Address> tmpAddressList = addressMap.get(name);
				if (tmpAddressList == null) {
					tmpAddressList = new ArrayList<Address>();
					addressMap.put(name, tmpAddressList);
				}
				tmpAddressList.add(address);
			}
			
			TreeNode<AddressImportEntry> tree = new TreeNode<AddressImportEntry>();
			Address rootAddress = addressDao.get(1L);
			AddressImportEntry root = new AddressImportEntry();
			root.setAddress(rootAddress);
			tree.setSelf(root);
			//TODO REMOVE?
			for (AddressImportDetail detail : details) {
				TreeNode<AddressImportEntry> treeNode = getTreeNodeForUpdate(tree, addressMap, detail, 1, customerId);
			}
		} catch (IOException e) {
			logger.error("importAddress failed due to {}", e);
			return null;
		}

		Set<AddressImportDetail> detailSet = new HashSet<AddressImportDetail>();
		detailSet.addAll(details);
		result.setAddressImportDetails(detailSet);
		int successCount = 0;
		int failureCount = 0;
		for (AddressImportDetail detail : details) {
			if (detail.getStatus() != null && detail.getStatus().intValue() == AddressImportDetailStatsEnum.success.getValue()) {
				successCount++;
			} else {
				failureCount++;
			}
		}
		result.setSuccessCount(successCount);
		result.setFailureCount(failureCount);
		result.setImportDate(new Date());
		result.setUserId(user.getId());
		addressImportResultDao.save(result);
		return result;
	}

	/**
	 * 更新地址挂靠站点（拆合站）
	 * @param treeNode
	 * @param addressMap 
	 * @param detail
	 * @param level 级别，123对应省市区，456对应3级自定义关键字
	 * @param customerId 
	 * @return
	 */
	private TreeNode<AddressImportEntry> getTreeNodeForUpdate(TreeNode<AddressImportEntry> treeNode, Map<String, List<Address>> addressMap, AddressImportDetail detail, int level, Long customerId) {
		String name = getAddressName(detail, level);
		String nextLevelName = getAddressName(detail, level + 1);
		// 是否最后一级地址
		boolean isLast = false;
		if (StringUtil.isEmpty(nextLevelName)) {
			isLast = true;
		}
		
		// 维护tree结构
		AddressImportEntry childEntry = null;
		TreeNode<AddressImportEntry> childNode = treeNode.getChild(name);
		if (childNode == null) {
			childNode = new TreeNode<AddressImportEntry>();
			childNode.setName(name);
			childEntry = new AddressImportEntry();
			childNode.setSelf(childEntry);
			childNode.setParent(treeNode);
			treeNode.addChild(name, childNode);
		} else {
			childEntry = childNode.getSelf();
		}
		
		// 子节点对应的地址
		Address childAddress = childEntry.getAddress();
		Address parentAddress = null;
		if (childAddress == null) {
			List<Address> addressList = addressMap.get(name);
			TreeNode<AddressImportEntry> parentNode = childNode.getParent();
			if (parentNode == null || parentNode.getSelf() == null) {
				// 省市区为易普联科维护，不应当出现省/直辖市同名
				throw new ExplinkRuntimeException("导入地址第一级冲突");
			}
			parentAddress = parentNode.getSelf().getAddress();
			
			if (addressList != null) {
				// 根据父子关系挑选正确的同名地址
				for (Address tmpAddress : addressList) {
					if (tmpAddress.getParentId().equals(parentAddress.getId())) {
						childAddress = tmpAddress;
					}
				}
			}
			if (childAddress != null) {
				// 查找到子节点对应的地址
				childEntry.setAddress(childAddress);
			}
		}
		
		// 本节点对应的地址存在
		if (childAddress != null) {
			if (isLast) {
				// 最后一级地址已存在
				
				boolean updateResult = deliveryStationRule.updateAddress(childAddress,detail.getDeliveryStationId(), customerId);
				if (updateResult) {
					detail.setStatus(AddressImportDetailStatsEnum.success.getValue());
					detail.setAddressId(childAddress.getId());
				}else{
					detail.setStatus(AddressImportDetailStatsEnum.failure.getValue());
					detail.setAddressId(childAddress.getId());
				}
			} else {
				// 不是最后一级，继续查找子节点
				return getTreeNodeForUpdate(childNode, addressMap, detail, level + 1, customerId);
			}
			
		}
		return childNode;
	}

}
