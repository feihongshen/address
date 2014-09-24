package cn.explink.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Query;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.explink.dao.AddressDao;
import cn.explink.dao.AddressPermissionDao;
import cn.explink.dao.DeliveryStationDao;
import cn.explink.dao.DeliveryStationRuleDao;
import cn.explink.domain.Address;
import cn.explink.domain.Customer;
import cn.explink.domain.DeliveryStation;
import cn.explink.domain.DeliveryStationRule;
import cn.explink.domain.Vendor;
import cn.explink.domain.VendorsAging;
import cn.explink.domain.enums.DeliveryStationRuleTypeEnum;
import cn.explink.domain.fields.RuleExpression;
import cn.explink.exception.ExplinkRuntimeException;
import cn.explink.modle.DataGridReturn;
import cn.explink.tree.ZTreeNode;
import cn.explink.util.JsonUtil;
import cn.explink.util.StringUtil;
import cn.explink.web.vo.DeliveryStationRuleVo;
import cn.explink.web.vo.VendorsAgingVo;
import cn.explink.ws.vo.BeanVo;
import cn.explink.ws.vo.OrderVo;

@Service
public class DeliveryStationRuleService extends RuleService {

	@Autowired
	private AddressDao addressDao;

	@Autowired
	private DeliveryStationDao deliveryStationDao;

	@Autowired
	private AddressPermissionDao addressPermissionDao;

	@Autowired
	private DeliveryStationRuleDao deliveryStationRuleDao;

	public DeliveryStationRule createDeliveryStationRule(Long addressId, Long deliveryStationId, Long customerId, String rule)  {
		// 解析规则
		RuleExpression ruleExpression = parseRule(rule);

		Address address = addressDao.get(addressId);
		DeliveryStation deliveryStation = deliveryStationDao.get(deliveryStationId);
		// 判断是否与已有规则冲突
		DeliveryStationRule confilctingRule = findConflictingRule(ruleExpression, address.getDeliveryStationRules());
		if (confilctingRule != null) {
			String message = "无效规则";
			if (DeliveryStationRuleTypeEnum.fallback.getValue() == confilctingRule.getRuleType().intValue()) {
				message = "已有默认规则";
			} else {
				message = "与已有规则冲突, " + confilctingRule.getRule();
			}
			throw new ExplinkRuntimeException(message);
		}

		DeliveryStationRule deliveryStationRule = new DeliveryStationRule();
		address.getDeliveryStationRules().add(deliveryStationRule);
		deliveryStationRule.setAddress(address);
		deliveryStationRule.setDeliveryStation(deliveryStation);
		deliveryStationRule.setCreationTime(new Date());
		if (StringUtil.isEmpty(rule)) {
			deliveryStationRule.setRuleType(DeliveryStationRuleTypeEnum.fallback.getValue());
		} else {
			deliveryStationRule.setRuleType(DeliveryStationRuleTypeEnum.customization.getValue());
			deliveryStationRule.setRule(rule);
			deliveryStationRule.setRuleExpression(JsonUtil.translateToJson(ruleExpression));
		}
		deliveryStationRuleDao.save(deliveryStationRule);
		
		return deliveryStationRule;
	}

	/**
	 * 查找冲突的规则
	 * 
	 * @param ruleExpression
	 * @param deliveryStationRules
	 * @return
	 */
	private DeliveryStationRule findConflictingRule(RuleExpression ruleExpression, Set<DeliveryStationRule> deliveryStationRules) {
		if (deliveryStationRules == null) {
			return null;
		}
		for (DeliveryStationRule existingRule : deliveryStationRules) {
			if (isConflict(ruleExpression, existingRule)) {
				return existingRule;
			}
		}
		return null;
	}

	/**
	 * 判断规则是否冲突
	 * 
	 * @param ruleExpression
	 * @param existingRule
	 * @return
	 */
	private boolean isConflict(RuleExpression ruleExpression, DeliveryStationRule existingRule) {
		if (ruleExpression == null && DeliveryStationRuleTypeEnum.fallback.getValue() == existingRule.getRuleType().intValue()) {
			return true;
		}
		if (ruleExpression != null && DeliveryStationRuleTypeEnum.fallback.getValue() == existingRule.getRuleType().intValue()) {
			return false;
		}
		if (ruleExpression == null && DeliveryStationRuleTypeEnum.fallback.getValue() != existingRule.getRuleType().intValue()) {
			return false;
		}
		RuleExpression existingRuleExpression = JsonUtil.readValue(existingRule.getRuleExpression(), RuleExpression.class);
		return isConflict(ruleExpression, existingRuleExpression);
	}

	/**
	 * 根据地址搜索匹配的站点规则
	 * 
	 * @param addressList
	 * @param orderVo
	 * @return
	 */
	public List<DeliveryStationRule> search(List<Address> addressList, OrderVo orderVo) {
		if (addressList == null) {
			return null;
		}
		List<DeliveryStationRule> ruleList = new ArrayList<DeliveryStationRule>();
		for (Address address : addressList) {
			// 默认规则
			DeliveryStationRule defaultRule = null;
			DeliveryStationRule mappingRule = null;
			int index = orderVo.getAddressLine().lastIndexOf(address.getName());
			// 从匹配的关键字的下一个字开始匹配规则，可提高匹配效率
			String addressLine = orderVo.getAddressLine().substring(index + address.getName().length());

			for (DeliveryStationRule rule : address.getDeliveryStationRules()) {
				if (DeliveryStationRuleTypeEnum.fallback.getValue() == rule.getRuleType().intValue()) {
					defaultRule = rule;
				} else {
					RuleExpression ruleExpression = JsonUtil.readValue(rule.getRuleExpression(), RuleExpression.class);
					boolean isMapping = isMapping(addressLine, ruleExpression);
					if (isMapping) {
						mappingRule = rule; 
					}
				}
			}

			if (mappingRule == null) {
				// 针对每一个address，没有匹配上任何客户化规则时，则匹配到默认规则
				mappingRule = defaultRule;
			}
			ruleList.add(mappingRule);
		}
		return ruleList;
	}

	public DataGridReturn getDataGridReturnView(String addressId) {
		
		Query query = getSession().createQuery("select new cn.explink.web.vo.DeliveryStationRuleVo(dsr.id, dsr.deliveryStation.name) from DeliveryStationRule dsr where dsr.address.id =:addressId");
		query.setLong("addressId", Long.parseLong(addressId));
		List<DeliveryStation> list=query.list();
		return new DataGridReturn(list.size(), list);
	}

	public List<Long> getAddressIds(Long parentId,Long customerId) {
		return deliveryStationRuleDao.getAddressIds(parentId,customerId);
	}

	public void addRule(DeliveryStationRule dsr) {
		List list = deliveryStationRuleDao.getByAddressAndStation(dsr.getAddress().getId(),dsr.getDeliveryStation().getId());
		if(list==null||list.isEmpty()){
			deliveryStationRuleDao.save(dsr);
		}
	}
	public List<BeanVo> getStationAddressTree(Long customerId,
			String inIds) {
		Query query = getSession().createQuery("select new cn.explink.ws.vo.BeanVo(dsr.address.id, dsr.deliveryStation.name) from DeliveryStationRule dsr where dsr.deliveryStation.status=1 and dsr.address.id in("+inIds+") and dsr.deliveryStation.customer.id=:customerId group by dsr.deliveryStation,dsr.address");
		query.setLong("customerId", customerId);
		return query.list();
	}

	public List<ZTreeNode> getAdressByStation(Long customerId, String stationId) {
		Query query = getSession().createQuery("select new cn.explink.tree.ZTreeNode( dsr.address.name,dsr.address.id,dsr.address.parentId,dsr.address.addressLevel,dsr.address.path ) from DeliveryStationRule dsr where  dsr.deliveryStation.id=:stationId and dsr.deliveryStation.customer.id=:customerId group by dsr.deliveryStation,dsr.address");
		query.setLong("customerId", customerId);
		query.setLong("stationId", Long.parseLong(stationId));
		return query.list();
	}

	public void removeAddressRule(Long addressId, Long stationId) {
		Query query = getSession().createSQLQuery("UPDATE DELIVERY_STATION_RULES SET  DELIVERY_STATION_ID = :stationId WHERE ADDRESS_ID=:addressId");
		query.setLong("addressId", addressId);
		query.setLong("stationId",  stationId );
		query.executeUpdate();
	}
	public void changeStationRelation(Long sourceStationId,Long targetStationId,
			String sourceAddressId,String targetAddressId) {
			if (StringUtils.isNotBlank(sourceAddressId)) {
				String sourceSql = "UPDATE `DELIVERY_STATION_RULES` SET `DELIVERY_STATION_ID`="
						+ sourceStationId
						+ " WHERE `DELIVERY_STATION_ID`="
						+ targetStationId
						+ " and `ADDRESS_ID` in ("
						+ sourceAddressId + ");";
				Query sourceQuery = getSession().createSQLQuery(sourceSql);
				sourceQuery.executeUpdate();
			}
			if (StringUtils.isNotBlank(targetAddressId)) {
				String targetSql = "UPDATE `DELIVERY_STATION_RULES` SET `DELIVERY_STATION_ID`="
						+ targetStationId
						+ " WHERE `DELIVERY_STATION_ID`="
						+ sourceStationId
						+ " and `ADDRESS_ID` in ("
						+ targetAddressId + ");";
				Query targetQuery = getSession().createSQLQuery(targetSql);
				targetQuery.executeUpdate();
			}
		}

	public List<DeliveryStationRuleVo> getAllStationRule(String addressId,Long custmerId) {
		String sql = "SELECT DSR.ID id,S.NAME deliveryStationName   ,DSR.RULE rule,DSR.RULE_TYPE ruleType ,DSR.RULE_EXPRESSION ruleExpression  FROM DELIVERY_STATION_RULES DSR ,DELIVERY_STATIONS S WHERE DSR.ADDRESS_ID=:addressId" +
				"  AND S.CUSTOMER_ID=:customerId AND S.ID=DSR.DELIVERY_STATION_ID";
		Query query = getSession().createSQLQuery( sql)   .setResultTransformer(Transformers.aliasToBean(DeliveryStationRuleVo.class)) ;
		query.setLong("addressId", Long.parseLong(addressId));
		query.setLong("customerId",  custmerId );
		return   query.list();
	}

	public List<VendorsAging> getAllVendorAging(String addressId, Long custmerId) {
		Query query = getSession().createQuery("from VendorsAging where address.id=:addressId and customer.id=:custmerId ");
		query.setLong("custmerId", custmerId);
		query.setLong("addressId", Long.parseLong(addressId));
		return query.list();
	}
   
	public void createVendorAge(Long addressId, Long vendorId, Long customerId,
			String aging) {
		
		Query query = getSession().createQuery("from VendorsAging where address.id=:addressId and customer.id=:custmerId and vendor.id=:vendorId");
		query.setLong("custmerId", customerId);
		query.setLong("addressId", addressId );
		query.setLong("vendorId", vendorId );
		List l =  query.list();
		if(l!=null&&!l.isEmpty()){
			throw new ExplinkRuntimeException("同地址同一个供应商不能设置两个实效！");
		}else{
			VendorsAging  va = new VendorsAging();
			Address a = new Address();
			a.setId(addressId);
			Vendor v = new Vendor();
			v.setId(vendorId);
			Customer c = new Customer();
			c.setId(customerId);
			va.setAddress(a);
			va.setCustomer(c);
			va.setVendor(v);
			va.setAging(aging);
			this.save(va);
		}
		
	}

	public void saveVendorAge(List<VendorsAgingVo> list, Long customerId) {
		for(VendorsAgingVo r:list){
		    createVendorAge(r.getAddressId(), r.getVendorId(), customerId, r.getAging());
		}
	}

	public void deleteVendorAge(Long id) {
		VendorsAging va =   (VendorsAging) getSession().load(VendorsAging.class, id);
		getSession().delete(va);
		
	}

	public void createDeliveryStationRuleList(List<DeliveryStationRuleVo> list,
			Long customerId) {
		for(DeliveryStationRuleVo r:list){
			 createDeliveryStationRule(r.getAddressId(), r.getStationId(), customerId, r.getRule());
		}
	}

}
