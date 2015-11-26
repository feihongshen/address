package cn.explink.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
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
import cn.explink.dao.VendorsAgingDao;
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

	@Autowired
	private VendorsAgingDao vendorsAgingDao;

	public DeliveryStationRule createDeliveryStationRule(Long addressId, Long deliveryStationId, Long customerId, String rule) {
		// 解析规则
		RuleExpression ruleExpression = this.parseRule(rule);

		Address address = this.addressDao.get(addressId);
		DeliveryStation deliveryStation = this.deliveryStationDao.get(deliveryStationId);

		// 一个关键词可以挂多个规则为空的站点 modified by songkaojun 2015-11-24
		if (ruleExpression != null) {
			// 判断是否与已有规则冲突
			Set<DeliveryStationRule> filterdRules = this.filter(customerId, address.getDeliveryStationRules());
			DeliveryStationRule confilctingRule = this.findConflictingRule(ruleExpression, filterdRules);
			if (confilctingRule != null) {
				String message = "无效规则";
				if (DeliveryStationRuleTypeEnum.fallback.getValue() == confilctingRule.getRuleType().intValue()) {
					message = "已有默认规则";
				} else {
					message = "与已有规则冲突, " + confilctingRule.getRule();
				}
				throw new ExplinkRuntimeException(message);
			}
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
		this.deliveryStationRuleDao.save(deliveryStationRule);

		return deliveryStationRule;
	}

	private Set<DeliveryStationRule> filter(Long customerId, Set<DeliveryStationRule> deliveryStationRules) {
		if (deliveryStationRules == null) {
			return null;
		}
		Set<DeliveryStationRule> filteredRules = new HashSet<DeliveryStationRule>();
		for (DeliveryStationRule rule : deliveryStationRules) {
			if (customerId.equals(rule.getDeliveryStation().getCustomer().getId())) {
				filteredRules.add(rule);
			}
		}
		return filteredRules;
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
			if (this.isConflict(ruleExpression, existingRule)) {
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
		if ((ruleExpression == null) && (DeliveryStationRuleTypeEnum.fallback.getValue() == existingRule.getRuleType().intValue())) {
			return true;
		}
		if ((ruleExpression != null) && (DeliveryStationRuleTypeEnum.fallback.getValue() == existingRule.getRuleType().intValue())) {
			return false;
		}
		if ((ruleExpression == null) && (DeliveryStationRuleTypeEnum.fallback.getValue() != existingRule.getRuleType().intValue())) {
			return false;
		}
		RuleExpression existingRuleExpression = JsonUtil.readValue(existingRule.getRuleExpression(), RuleExpression.class);
		return this.isConflict(ruleExpression, existingRuleExpression);
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
			boolean hasCustomerRule = false;
			// 存在别名时匹配报错[zhaoshb+]2015-01-16.

			String addressLine = StringUtil.full2Half(orderVo.getAddressLine());
			int index = orderVo.getAddressLine().lastIndexOf(address.getName());
			if (index >= 0) {
				addressLine = addressLine.substring(index + address.getName().length());
			}
			// TODO:此处待优化.
			List<DeliveryStationRule> list = this.getByCustormerAndAdressId(orderVo.getCustomerId(), address.getId());
			for (DeliveryStationRule rule : list) {
				if (DeliveryStationRuleTypeEnum.fallback.getValue() == rule.getRuleType().intValue()) {
					defaultRule = rule;
				} else {
					RuleExpression ruleExpression = JsonUtil.readValue(rule.getRuleExpression(), RuleExpression.class);
					boolean isMapping = this.isMapping(addressLine, ruleExpression);
					hasCustomerRule = hasCustomerRule || isMapping;
					if (isMapping) {
						ruleList.add(rule);
					}
				}
			}
			// 如果没有客户定制的规则，则将默认规则添加到规则列表
			if (!hasCustomerRule && (defaultRule != null)) {
				ruleList.add(defaultRule);
			}
		}
		return ruleList;
	}

	public DataGridReturn getDataGridReturnView(String addressId) {

		Query query = this.getSession().createQuery(
				"select new cn.explink.web.vo.DeliveryStationRuleVo(dsr.id, dsr.deliveryStation.name) from DeliveryStationRule dsr where dsr.address.id =:addressId");
		query.setLong("addressId", Long.parseLong(addressId));
		List<DeliveryStation> list = query.list();
		return new DataGridReturn(list.size(), list);
	}

	public List<Long> getAddressIds(Long parentId, Long customerId) {
		return this.deliveryStationRuleDao.getAddressIds(parentId, customerId);
	}

	public void addRule(DeliveryStationRule dsr, Long customerId) {
		List list = this.deliveryStationRuleDao.getByAddressAndStation(dsr.getAddress().getId(), dsr.getDeliveryStation().getId(), customerId);
		if ((list != null) && !list.isEmpty()) {
			throw new ExplinkRuntimeException("该关键字已绑定默认站点");
		}
		this.deliveryStationRuleDao.save(dsr);
	}

	public List<BeanVo> getStationAddressTree(Long customerId, String inIds) {
		Query query = this.getSession().createQuery(
				"select new cn.explink.ws.vo.BeanVo(dsr.address.id, dsr.deliveryStation.name) from DeliveryStationRule dsr where dsr.deliveryStation.status=1 and dsr.address.id in(" + inIds
						+ ") and dsr.deliveryStation.customer.id=:customerId group by dsr.deliveryStation,dsr.address");
		query.setLong("customerId", customerId);
		return query.list();
	}

	public List<DeliveryStationRule> getByCustormerAndAdressId(Long customerId, Long aId) {
		Query query = this.getSession().createQuery(
				"select dsr from DeliveryStationRule dsr where dsr.deliveryStation.status=1 and dsr.address.id =:aId and dsr.deliveryStation.customer.id=:customerId ");
		// group by dsr.deliveryStation,dsr.address
		query.setLong("customerId", customerId);
		query.setLong("aId", aId);
		return query.list();
	}

	public List<ZTreeNode> getAdressByStation(Long customerId, String stationId) {
		Query query = this
				.getSession()
				.createQuery(
						"select new cn.explink.tree.ZTreeNode( dsr.address.name,dsr.address.id,dsr.address.parentId,dsr.address.addressLevel,dsr.address.path ) from DeliveryStationRule dsr where  dsr.deliveryStation.id=:stationId and dsr.deliveryStation.customer.id=:customerId group by dsr.deliveryStation,dsr.address");
		query.setLong("customerId", customerId);
		query.setLong("stationId", Long.parseLong(stationId));
		return query.list();
	}

	public int removeAddressRule(Long addressId, Long stationId) {
		return this.deliveryStationRuleDao.removeAddressRule(addressId, stationId);

	}

	public int removeAddressRule(Long addressId, Long oldStationId, Long stationId) {
		return this.deliveryStationRuleDao.removeAddressRule(addressId, oldStationId, stationId);
	}

	public void changeStationRelation(Long sourceStationId, Long targetStationId, String sourceAddressId, String targetAddressId) {
		if (StringUtils.isNotBlank(sourceAddressId)) {
			String sourceSql = "UPDATE `DELIVERY_STATION_RULES` SET `DELIVERY_STATION_ID`=" + sourceStationId + " WHERE `DELIVERY_STATION_ID`=" + targetStationId + " and `ADDRESS_ID` in ("
					+ sourceAddressId + ");";
			Query sourceQuery = this.getSession().createSQLQuery(sourceSql);
			sourceQuery.executeUpdate();
		}
		if (StringUtils.isNotBlank(targetAddressId)) {
			String targetSql = "UPDATE `DELIVERY_STATION_RULES` SET `DELIVERY_STATION_ID`=" + targetStationId + " WHERE `DELIVERY_STATION_ID`=" + sourceStationId + " and `ADDRESS_ID` in ("
					+ targetAddressId + ");";
			Query targetQuery = this.getSession().createSQLQuery(targetSql);
			targetQuery.executeUpdate();
		}
	}

	public List<DeliveryStationRuleVo> getAllStationRule(String addressId, Long custmerId) {
		String sql = "SELECT DSR.ID id,S.NAME deliveryStationName   ,DSR.RULE rule,DSR.RULE_TYPE ruleType ,DSR.RULE_EXPRESSION ruleExpression  FROM DELIVERY_STATION_RULES DSR ,DELIVERY_STATIONS S WHERE DSR.ADDRESS_ID=:addressId"
				+ "  AND S.CUSTOMER_ID=:customerId AND S.ID=DSR.DELIVERY_STATION_ID";
		Query query = this.getSession().createSQLQuery(sql).setResultTransformer(Transformers.aliasToBean(DeliveryStationRuleVo.class));
		query.setLong("addressId", Long.parseLong(addressId));
		query.setLong("customerId", custmerId);
		return query.list();
	}

	public List<VendorsAging> getAllVendorAging(String addressId, Long custmerId) {
		Query query = this.getSession().createQuery("from VendorsAging where address.id=:addressId and customer.id=:custmerId ");
		query.setLong("custmerId", custmerId);
		query.setLong("addressId", Long.parseLong(addressId));
		return query.list();
	}

	public void createVendorAge(Long addressId, Long vendorId, Long customerId, String aging) {

		List existingVendorAgingList = this.vendorsAgingDao.getVendorAging(addressId, vendorId, customerId);
		if ((existingVendorAgingList != null) && !existingVendorAgingList.isEmpty()) {
			throw new ExplinkRuntimeException("同地址同一个供应商不能设置两个时效！");
		} else {
			VendorsAging va = new VendorsAging();
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
		for (VendorsAgingVo r : list) {
			this.createVendorAge(r.getAddressId(), r.getVendorId(), customerId, r.getAging());
		}
	}

	public void deleteVendorAge(Long id) {
		VendorsAging va = (VendorsAging) this.getSession().load(VendorsAging.class, id);
		this.getSession().delete(va);

	}

	public void createDeliveryStationRuleList(List<DeliveryStationRuleVo> list, Long customerId) {
		for (DeliveryStationRuleVo r : list) {
			this.createDeliveryStationRule(r.getAddressId(), r.getStationId(), customerId, r.getRule());
		}
	}

}
