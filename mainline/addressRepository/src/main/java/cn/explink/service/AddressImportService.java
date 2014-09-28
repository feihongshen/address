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

import org.apache.commons.lang.StringUtils;
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
import cn.explink.dao.AddressPermissionDao;
import cn.explink.domain.Address;
import cn.explink.domain.AddressImportDetail;
import cn.explink.domain.AddressImportResult;
import cn.explink.domain.AddressPermission;
import cn.explink.domain.Deliverer;
import cn.explink.domain.DelivererRule;
import cn.explink.domain.DeliveryStation;
import cn.explink.domain.DeliveryStationRule;
import cn.explink.domain.User;
import cn.explink.domain.enums.AddressImportDetailStatsEnum;
import cn.explink.domain.enums.AddressStatusEnum;
import cn.explink.domain.enums.DelivererRuleTypeEnum;
import cn.explink.exception.ExplinkRuntimeException;
import cn.explink.modle.DataGrid;
import cn.explink.modle.DataGridReturn;
import cn.explink.schedule.Constants;
import cn.explink.tree.AddressImportEntry;
import cn.explink.tree.TreeNode;
import cn.explink.tree.ZTreeNode;
import cn.explink.util.StringUtil;
import cn.explink.web.vo.AddressImportTypeEnum;

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
	private DeliveryStationService deliveryStationService;

	@Autowired
	private AddressImportDetailDao addressImportDetailDao;
	
	@Autowired
	private DeliveryStationRuleService deliveryStationRuleService;
	
	@Autowired
	private DelivererService delivererService;
	
	@Autowired
	private DelivererRuleService delivererRuleService;
	
	@Autowired
	private AddressPermissionDao addressPermissionDao;

	@Autowired
	private ScheduledTaskService scheduledTaskService;
	
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
     *   导入单行记录（新事务处理）
                       地址格式是否正确（  - -大望路）否-->失败 ：上级地址不存在
    	  三级地址是否存在（北京-市辖区-朝阳区） 否-->失败：上级地址不存在
    	 四、伍、六级地址是否存在（不存在：创建 [如果是最后一级，]，存在：添加至地址MAP）
    	 站点非空（绑定判别-抛异常站点不存在）
    	  配送员非空（绑定判别-为空抛异常配送员不存在） 
     * @param map 名称-Address
     * @param detail 但行记录
     * @param addressMap 已经查出来的Map
     * @param customerId 
     */
    public void txNewImportDetail(Map<String,Address> map,AddressImportDetail detail,Map<String,Address> addressMap,Map<String,DeliveryStation> stationMap,Map<String,Deliverer> delivererMap,Map<Long,Address> bindMap,Long customerId,Integer importType,Long stationId){
       if(validateDetail(detail)){
    	   Address d =  map.get(detail.getProvince()+"-"+detail.getCity()+"-"+detail.getDistrict());
    	   if(d==null){
    		   throw new ExplinkRuntimeException("省/市/区地址不存在");
    	   }else{
    		   Address bindAddress = null;//需要绑定站点或者配送员的地址（动态变化，取最后一级地址）
    		   Address a1 = null;
    		   Address a2 = null;
    		   Address a3 = null;
    		   boolean isSaved = false;//一次导入只能保存一个关键字
    		   
    		   //处理第一关键字
    		   a1 = addressMap.get(d.getId()+"-"+detail.getAddress1());
    		   if(a1==null){//为空则创建并绑定
    			   a1 = createAndBind(d,detail.getAddress1(),customerId);
    			   addressMap.put(d.getId()+"-"+a1.getName(), a1);
    			   bindMap.put(a1.getId(),a1);
    			   isSaved = true;
    		   }else{
    			   if( bindMap.get(a1.getId())==null){
    				   bindAddress(a1,customerId);
    				   isSaved = true;
    				   bindMap.put(a1.getId(),a1);
    			   }
    		   }
    		   bindAddress=a1;

    		   //处理第二关键字
    		   if(StringUtils.isNotBlank(detail.getAddress2())){
    			   if(isSaved){
    				   throw new ExplinkRuntimeException("父节点不存在");
    			   }
    			   a2 = addressMap.get(a1.getId()+"-"+detail.getAddress2());
        		   if(a2==null){//为空则创建并绑定
        			   a2 = createAndBind(a1,detail.getAddress2(),customerId);
        			   addressMap.put(a1.getId()+"-"+a2.getName(), a2);
        			   bindMap.put(a2.getId(),a2);
        			   isSaved = true;
        		   }else{//是否已经绑定
        			   if( bindMap.get(a2.getId())==null){
        				   bindAddress(a2,customerId);
        				   isSaved = true;
        				   bindMap.put(a2.getId(),a2);
        			   }
        		   }
        		   bindAddress=a2;
    		   } 
    		   
    		   //处理第三个关键字
    		   if(StringUtils.isNotBlank(detail.getAddress3())){
    			   if(isSaved){
    				   throw new ExplinkRuntimeException("父节点不存在");
    			   }
    			   a3 = addressMap.get(a2.getId()+"-"+detail.getAddress3());
        		   if(a3==null){//为空则创建并绑定
        			   a3 = createAndBind(a2,detail.getAddress3(),customerId);
        			   addressMap.put(a2.getId()+"-"+a3.getName(), a3);
        			   bindMap.put(a3.getId(),a3);
        			   isSaved=true;
        		   }else{
        			   if( bindMap.get(a3.getId())==null){
        				   bindAddress(a3,customerId);
        				   isSaved = true;
        				   bindMap.put(a3.getId(),a3);
        			   }
        		   }
        		   bindAddress=a3;
    		   } 
    		   if(importType==AddressImportTypeEnum.init.getValue()){//初始化导入：绑定站点和小件员
    			   if(StringUtils.isNotBlank(detail.getDeliveryStationName())){ 
    	    			  DeliveryStation ds =  stationMap.get(customerId+"-"+detail.getDeliveryStationName());
    	    			  if(ds==null){
    	    				  throw new ExplinkRuntimeException("配送站点不存在");
    	    			  }else{
    	    				DeliveryStationRule dsr = new DeliveryStationRule();
    	  					dsr.setAddress(bindAddress );
    	  					dsr.setCreationTime(new Date());
    	  					dsr.setDeliveryStation(ds);
    	  					dsr.setRule("");
    	  					dsr.setRuleExpression("");
    	  					dsr.setRuleType(DelivererRuleTypeEnum.fallback.getValue());
    	  					deliveryStationRuleService.addRule(dsr,customerId);
    	  					isSaved=true;
    	    			  }
    	    		   }
    	    		   if(StringUtils.isNotBlank(detail.getDelivererName())){//绑定小件员
    		    			Deliverer dl = delivererMap.get(customerId+"-"+detail.getDelivererName());
    		   				if(dl!=null){
    		   					DelivererRule  dr = new DelivererRule();
    		   					dr.setAddress(bindAddress );
    		   					dr.setCreationTime(new Date());
    		   					dr.setDeliverer(dl);
    		   					dr.setRule("");
    		   					dr.setRuleExpression("");
    		   					dr.setRuleType(DelivererRuleTypeEnum.fallback.getValue());
    		   					delivererRuleService.addRule(dr,customerId);
    		   					isSaved=true;
    		   				}else{
    		   				    throw new ExplinkRuntimeException("小件员不存在");
    		   				}
    	    		   }
    		   }
    		   if(importType==AddressImportTypeEnum.stationImport.getValue()){//站点导入：绑定站点
    			   if(StringUtils.isNotBlank(detail.getDeliveryStationName())){ 
    	    			  DeliveryStation ds =  stationMap.get(customerId+"-"+detail.getDeliveryStationName());
    	    			  if(ds==null){
    	    				  throw new ExplinkRuntimeException("配送站点不存在");
    	    			  }else{
		    				  if(ds.getId()!=stationId){
	    	    				  throw new ExplinkRuntimeException("导入站点不匹配！");
	    	    			  }
    	    				DeliveryStationRule dsr = new DeliveryStationRule();
    	  					dsr.setAddress(bindAddress );
    	  					dsr.setCreationTime(new Date());
    	  					dsr.setDeliveryStation(ds);
    	  					dsr.setRule("");
    	  					dsr.setRuleExpression("");
    	  					dsr.setRuleType(DelivererRuleTypeEnum.fallback.getValue());
    	  					deliveryStationRuleService.addRule(dsr,customerId);
    	  					isSaved=true;
    	    			  }
    	    		   }
    		   }
    		   if(importType==AddressImportTypeEnum.stationMove.getValue()){//站点移动
    			   if(StringUtils.isNotBlank(detail.getDeliveryStationName())){ 
    	    			  DeliveryStation ds =  stationMap.get(customerId+"-"+detail.getDeliveryStationName());
    	    			  if(ds==null){
    	    				  throw new ExplinkRuntimeException("配送站点不存在");
    	    			  }else{
    	    				  deliveryStationRuleService.removeAddressRule(bindAddress.getId(),ds.getId());
    	    				  isSaved=true;
    	    			  }
    	    		   }
    		   }
    		   if(!isSaved){
    			   throw new ExplinkRuntimeException("数据重复！");
    		   }
    		   if(!new Integer(AddressImportDetailStatsEnum.failure.getValue()).equals(detail.getStatus())){
    				detail.setStatus(AddressImportDetailStatsEnum.success.getValue());
    				detail.setAddressId(bindAddress.getId());
    		   }
    	   }
       }else{
    	   detail.setStatus(AddressImportDetailStatsEnum.failure.getValue());
    	   detail.setMessage("导入格式不合规范");
       }
    }
    /**
     *   导入单行记录（新事务处理）
                       地址格式是否正确（  - -大望路）否-->失败 ：上级地址不存在
    	  三级地址是否存在（北京-市辖区-朝阳区） 否-->失败：上级地址不存在
    	 四、伍、六级地址是否存在（不存在：创建 [如果是最后一级，]，存在：添加至地址MAP）
    	 站点非空（绑定判别-抛异常站点不存在）
    	  配送员非空（绑定判别-为空抛异常配送员不存在） 
     * @param map 名称-Address
     * @param detail 但行记录
     * @param addressMap 已经查出来的Map
     * @param customerId 
     */
    public void txNewMoveDetail(Map<String,Address> map,AddressImportDetail detail,Map<String,Address> addressMap,Map<String,DeliveryStation> stationMap,Map<String,Deliverer> delivererMap,Map<Long,Address> bindMap,Long customerId){
       if(validateDetail(detail)){
    	   Address d =  map.get(detail.getProvince()+"-"+detail.getCity()+"-"+detail.getDistrict());
    	   if(d==null){
    		   throw new ExplinkRuntimeException("省/市/区地址不存在");
    	   }else{
    		   Address bindAddress = null;//需要绑定站点或者配送员的地址（动态变化，取最后一级地址）
    		   Address a1 = null;
    		   Address a2 = null;
    		   Address a3 = null;
    		   boolean isSaved = false;//一次导入只能保存一个关键字
    		   
    		   //处理第一关键字
    		   a1 = addressMap.get(d.getId()+"-"+detail.getAddress1());
    		   if(a1==null){//为空则创建并绑定
    			   a1 = createAndBind(d,detail.getAddress1(),customerId);
    			   addressMap.put(d.getId()+"-"+a1.getName(), a1);
    			   bindMap.put(a1.getId(),a1);
    			   isSaved = true;
    		   }else{
    			   if( bindMap.get(a1.getId())==null){
    				   bindAddress(a1,customerId);
    				   isSaved = true;
    				   bindMap.put(a1.getId(),a1);
    			   }
    		   }
    		   bindAddress=a1;

    		   //处理第二关键字
    		   if(StringUtils.isNotBlank(detail.getAddress2())){
    			   if(isSaved){
    				   throw new ExplinkRuntimeException("父节点不存在");
    			   }
    			   a2 = addressMap.get(a1.getId()+"-"+detail.getAddress2());
        		   if(a2==null){//为空则创建并绑定
        			   a2 = createAndBind(a1,detail.getAddress2(),customerId);
        			   addressMap.put(a1.getId()+"-"+a2.getName(), a2);
        			   bindMap.put(a2.getId(),a2);
        			   isSaved = true;
        		   }else{//是否已经绑定
        			   if( bindMap.get(a2.getId())==null){
        				   bindAddress(a2,customerId);
        				   isSaved = true;
        				   bindMap.put(a2.getId(),a2);
        			   }
        		   }
        		   bindAddress=a2;
    		   } 
    		   
    		   //处理第三个关键字
    		   if(StringUtils.isNotBlank(detail.getAddress3())){
    			   if(isSaved){
    				   throw new ExplinkRuntimeException("父节点不存在");
    			   }
    			   a3 = addressMap.get(a2.getId()+"-"+detail.getAddress3());
        		   if(a3==null){//为空则创建并绑定
        			   a3 = createAndBind(a2,detail.getAddress3(),customerId);
        			   addressMap.put(a2.getId()+"-"+a3.getName(), a3);
        			   bindMap.put(a3.getId(),a3);
        			   isSaved=true;
        		   }else{
        			   if( bindMap.get(a3.getId())==null){
        				   bindAddress(a3,customerId);
        				   isSaved = true;
        				   bindMap.put(a3.getId(),a3);
        			   }
        		   }
        		   bindAddress=a3;
    		   } 
    		  
			   if(StringUtils.isNotBlank(detail.getDeliveryStationName())){ 
				     if(StringUtils.isNotBlank(detail.getDeliveryStationOldName())){
				    	 DeliveryStation oldDs =  stationMap.get(customerId+"-"+detail.getDeliveryStationOldName());
				    	 DeliveryStation ds =  stationMap.get(customerId+"-"+detail.getDeliveryStationName());
		    			  if(oldDs==null){
		    				  throw new ExplinkRuntimeException("原配送站点不存在");
		    			  }else{
		    				 if(ds==null) {
		    					  throw new ExplinkRuntimeException("配送站点不存在");
		    				 }else{
			    				 int count =  deliveryStationRuleService.removeAddressRule(bindAddress.getId(),oldDs.getId(),ds.getId());
			    				 if(count>0){
			    					  isSaved=true;
			    				  }
		    				 }
		    			  }
				     }else{
				    	  DeliveryStation ds =  stationMap.get(customerId+"-"+detail.getDeliveryStationName());
		    			  if(ds==null){
		    				  throw new ExplinkRuntimeException("配送站点不存在");
		    			  }else{
		    				  int count =  deliveryStationRuleService.removeAddressRule(bindAddress.getId(),ds.getId());
		    				  if(count>0){
		    					  isSaved=true;
		    				  }
		    			  }
				     }
	    		 }
			   if(!isSaved){
				   throw new ExplinkRuntimeException("关键词无变化！");
			   }
    		   if(!new Integer(AddressImportDetailStatsEnum.failure.getValue()).equals(detail.getStatus())){
    				detail.setStatus(AddressImportDetailStatsEnum.success.getValue());
    				detail.setAddressId(bindAddress.getId());
    		   }
    	   }
       }else{
    	   detail.setStatus(AddressImportDetailStatsEnum.failure.getValue());
    	   detail.setMessage("导入格式不合规范");
       }
    }
   private void bindAddress(Address a1, Long customerId) {
	   AddressPermission permission = new AddressPermission();
	   permission.setAddressId(a1.getId());
	   permission.setCustomerId(customerId);
	   addressPermissionDao.save(permission);
	}

/**
    *创建地址并绑定客户ID
    * @param parent
    * @param name
    * @param customerId
    * @return
    */
   private Address createAndBind(Address parent, String name, Long customerId) {
	   Address a1 = new Address();
	   a1.setAddressLevel(parent.getAddressLevel()+1);
	   a1.setParentId(parent.getId());
	   a1.setCreationTime(new Date());
	   a1.setIndexed(false);
	   a1.setName(name);
	   a1.setPath(parent.getPath()+"-"+parent.getId());
	   a1.setStatus(AddressStatusEnum.valid.getValue());
	   if (StringUtil.length(a1.getName()) < AddressService.MIN_ADDRESS_LENGTH) {
			throw new ExplinkRuntimeException("关键字长度不能小于2");
			}
	   a1 = addressDao.save(a1);
	   AddressPermission permission = new AddressPermission();
	   permission.setAddressId(a1.getId());
	   permission.setCustomerId(customerId);
	   addressPermissionDao.save(permission);
	   scheduledTaskService.createScheduledTask(Constants.TASK_TYPE_SUB_UPDATE_INDEX, Constants.REFERENCE_TYPE_ADDRESS_ID, String.valueOf(a1.getId()));
	   return a1;
	}

private boolean validateDetail(AddressImportDetail detail) {
		boolean flag = true;
		if(StringUtils.isBlank(detail.getProvince())||StringUtils.isBlank(detail.getCity())||StringUtils.isBlank(detail.getDistrict())){
			return false;
		}else{
			if(StringUtils.isBlank(detail.getAddress1())){
				 throw new ExplinkRuntimeException("关键字为空！");
			}else{
				if(StringUtils.isBlank(detail.getAddress2())&&StringUtils.isNotBlank(detail.getAddress3())){
					flag = false;
				}
			}
		}
		return flag;
	}

/**
    * 绑定站点、收件人和地址联系
    * @param detail
    * @param customerId
    */
	private void bindRule(AddressImportDetail detail,Long customerId) {
		if(!detail.getStatus().equals(AddressImportDetailStatsEnum.failure.getValue())){
			//站点规则绑定
			if(StringUtils.isNotEmpty(detail.getDeliveryStationName())){
				DeliveryStation ds = deliveryStationService.getByNameAndCustomerId(detail.getDeliveryStationName(),customerId);
				if(ds!=null){
					DeliveryStationRule dsr = new DeliveryStationRule();
					Address a = new Address();
					a.setId(detail.getAddressId());
					dsr.setAddress(a );
					dsr.setCreationTime(new Date());
					dsr.setDeliveryStation(ds);
					dsr.setRule("");
					dsr.setRuleExpression("");
					dsr.setRuleType(DelivererRuleTypeEnum.fallback.getValue());
					deliveryStationRuleService.addRule(dsr,customerId);
				}else{
					detail.setStatus(AddressImportDetailStatsEnum.failure.getValue());
					detail.setMessage("站点不存在");
				}
			}
			//小件员规则
			if(StringUtils.isNotEmpty(detail.getDelivererName())){
				Deliverer d = delivererService.getByNameAndCustomerId(detail.getDelivererName(),customerId);
				if(d!=null){
					DelivererRule  dr = new DelivererRule();
					Address a = new Address();
					a.setId(detail.getAddressId());
					dr.setAddress(a );
					dr.setCreationTime(new Date());
					dr.setDeliverer(d);
					dr.setRule("");
					dr.setRuleExpression("");
					dr.setRuleType(DelivererRuleTypeEnum.fallback.getValue());
					delivererRuleService.addRule(dr,customerId);
				}else{
					detail.setStatus(AddressImportDetailStatsEnum.failure.getValue());
					detail.setMessage("小件员不存在");
				}
			}
		}
	}
	
	 	/**
	    * 迁移站点和地址联系绑定关系
	    * @param detail
	    * @param customerId
	    */
		private void removeRule(AddressImportDetail detail,Long customerId) {
			if(!detail.getStatus().equals(AddressImportDetailStatsEnum.failure.getValue())){
				//站点规则迁移
				if(StringUtils.isNotEmpty(detail.getDeliveryStationName())){
					DeliveryStation ds = deliveryStationService.getByNameAndCustomerId(detail.getDeliveryStationName(),customerId);
					if(ds!=null){
						deliveryStationRuleService.removeAddressRule(detail.getAddressId(),ds.getId());
					}
				}
			}
		}
	
   /**
    * 导入地址，同时判定是否同一个站点
    * @param detail
    * @param customerId
    * @param stationId
    */
	private void bindRuleWithStationId(AddressImportDetail detail,Long customerId,Long stationId) {
		if(!detail.getStatus().equals(AddressImportDetailStatsEnum.failure.getValue())){
			//站点规则绑定
			if(StringUtils.isNotEmpty(detail.getDeliveryStationName())){
				DeliveryStation ds = deliveryStationService.getByNameAndCustomerId(detail.getDeliveryStationName(),customerId);
				if(ds!=null&&ds.getId().equals(stationId)){
					DeliveryStationRule dsr = new DeliveryStationRule();
					Address a = new Address();
					a.setId(detail.getAddressId());
					dsr.setAddress(a );
					dsr.setCreationTime(new Date());
					dsr.setDeliveryStation(ds);
					dsr.setRule("");
					dsr.setRuleExpression("");
					dsr.setRuleType(DelivererRuleTypeEnum.fallback.getValue());
					deliveryStationRuleService.addRule(dsr,customerId);
				}
			}
			//小件员规则
			if(StringUtils.isNotEmpty(detail.getDelivererName())){
				Deliverer d = delivererService.getByNameAndCustomerId(detail.getDelivererName(),customerId);
				if(d!=null){
					DelivererRule  dr = new DelivererRule();
					Address a = new Address();
					a.setId(detail.getAddressId());
					dr.setAddress(a );
					dr.setCreationTime(new Date());
					dr.setDeliverer(d);
					dr.setRule("");
					dr.setRuleExpression("");
					dr.setRuleType(DelivererRuleTypeEnum.fallback.getValue());
					delivererRuleService.addRule(dr,customerId);
				}
			}
			
			
		}
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
					detail.setStatus(AddressImportDetailStatsEnum.success.getValue());
					detail.setAddressId(childAddress.getId());
				} else {
					detail.setAddressId(childAddress.getId());
					detail.setStatus(AddressImportDetailStatsEnum.duplicate.getValue());
					detail.setMessage("地址重复:" + name);
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

	public void addNonNullValue(Collection<String> collection, String element) {
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
			Integer i=AddressImportDetailStatsEnum.failure.getValue();
			if (i != detail.getStatus()&&null!=detail.getAddressId()) {
				addressIdList.add(detail.getAddressId());
			}
		}
		if (addressIdList.size() > 0) {
			List<String> appendedChildrenIdList= addressService.findCannotRemoveIds(addressIdList,customerId);
			Set<String> set=new HashSet<String>();
			for (String string : appendedChildrenIdList) {
				String[] ids=string.split("-");
				for (String id : ids) {
					set.add(id);
				}
				set.remove("");
			}
			for (String id : set) {
				addressIdList.remove(Long.parseLong(id));
			}
			if(addressIdList.size() > 0){
				addressService.batchUnbindAddress(addressIdList, customerId);
			}
		}
		addressImportResultDao.delete(addressImportResult);
	}


	public List<AddressImportDetail> getAllDetail() {
		return addressImportDetailDao.loadAll(AddressImportDetail.class);
		
	}

}
