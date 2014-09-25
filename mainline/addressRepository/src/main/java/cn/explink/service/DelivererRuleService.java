package cn.explink.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.explink.dao.AddressDao;
import cn.explink.dao.AddressPermissionDao;
import cn.explink.dao.DelivererDao;
import cn.explink.dao.DelivererRuleDao;
import cn.explink.domain.Address;
import cn.explink.domain.Deliverer;
import cn.explink.domain.DelivererRule;
import cn.explink.domain.enums.DelivererRuleTypeEnum;
import cn.explink.domain.fields.RuleExpression;
import cn.explink.exception.ExplinkRuntimeException;
import cn.explink.util.JsonUtil;
import cn.explink.util.StringUtil;
import cn.explink.ws.vo.DelivererRuleVo;
import cn.explink.ws.vo.OrderVo;

@Service
public class DelivererRuleService extends RuleService {

	private static Logger logger = LoggerFactory.getLogger(DelivererRuleService.class);
	@Autowired
	private AddressDao addressDao;

	@Autowired
	private DelivererDao delivererDao;

	@Autowired
	private AddressPermissionDao addressPermissionDao;

	@Autowired
	private DelivererRuleDao delivererRuleDao;

	/**
	 * 批量创建配送员规则
	 * @param customerId
	 * @param delivererRuleVoList
	 */
	public void createDelivererRule(Long customerId, List<DelivererRuleVo> delivererRuleVoList) {
		for (DelivererRuleVo ruleVo : delivererRuleVoList) {
			createDelivererRule(customerId, ruleVo.getAddressId(), ruleVo.getDelivererId(), ruleVo.getRule());
		}
	}
	
	/**
	 * 创建配送员规则
	 * @param addressId
	 * @param delivererId
	 * @param customerId
	 * @param rule
	 * @return
	 */
	public DelivererRule createDelivererRule(Long customerId, Long addressId, Long delivererId, String rule) {
		logger.info("createDelivererRule for customer: {}, addressId: {}, delivererId: {}, rule: {}", new Object[]{
				customerId, addressId, delivererId, rule
		});
		// 解析规则
		RuleExpression ruleExpression = parseRule(rule);
		Address address = addressDao.get(addressId);
		Deliverer deliverer = delivererDao.getDeliverer(customerId,delivererId);
		// 判断是否与已有规则冲突
		DelivererRule confilctingRule = findConflictingRule(ruleExpression, address.getDelivererRules());
		if (confilctingRule != null) {
			String message = null;
			if (DelivererRuleTypeEnum.fallback.getValue() == confilctingRule.getRuleType().intValue()) {
				message = "已有默认规则";
			} else {
				message = "与已有规则冲突, " + confilctingRule.getRule();
			}
			throw new ExplinkRuntimeException(message);
		}

		DelivererRule delivererRule = new DelivererRule();
		delivererRule.setAddress(address);
		delivererRule.setDeliverer(deliverer);
		delivererRule.setCreationTime(new Date());
		if (StringUtil.isEmpty(rule)) {
			delivererRule.setRuleType(DelivererRuleTypeEnum.fallback.getValue());
		} else {
			delivererRule.setRuleType(DelivererRuleTypeEnum.customization.getValue());
			delivererRule.setRule(rule);
			delivererRule.setRuleExpression(JsonUtil.translateToJson(ruleExpression));
		}
		delivererRuleDao.save(delivererRule);
		return delivererRule;
	}
	
	public List<DelivererRule> getDelivererRuleList(Long customerId, Long addressId) {
		if (addressId == null) {
			return null;
		}
		List<DelivererRule> ruleList = delivererRuleDao.getDelivererRuleList(customerId, addressId);
		List<DelivererRule> resultList = new ArrayList<DelivererRule>();
		for (DelivererRule rule : ruleList) {
			if (customerId.equals(rule.getDeliverer().getCustomer().getId())) {
				resultList.add(rule);
			}
		}
		return resultList;
	}

	/**
	 * 查找冲突的规则
	 * 
	 * @param ruleExpression
	 * @param delivererRules
	 * @return
	 */
	private DelivererRule findConflictingRule(RuleExpression ruleExpression, Set<DelivererRule> delivererRules) {
		if (delivererRules == null) {
			return null;
		}
		for (DelivererRule existingRule : delivererRules) {
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
	private boolean isConflict(RuleExpression ruleExpression, DelivererRule existingRule) {
		if (DelivererRuleTypeEnum.fallback.getValue() == existingRule.getRuleType().intValue()) {
			return true;
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
	public List<DelivererRule> search(List<Address> addressList, OrderVo orderVo) {
		if (addressList == null) {
			return null;
		}
		List<DelivererRule> ruleList = new ArrayList<DelivererRule>();
		for (Address address : addressList) {
			// 默认规则
			DelivererRule defaultRule = null;
			DelivererRule mappingRule = null;
			int index = orderVo.getAddressLine().lastIndexOf(address.getName());
			// 从匹配的关键字的下一个字开始匹配规则，可提高匹配效率
			String addressLine = orderVo.getAddressLine().substring(index + address.getName().length());
            if(address.getDelivererRules()!=null){
            	for (DelivererRule rule : address.getDelivererRules()) {
    				if (DelivererRuleTypeEnum.fallback.getValue() == rule.getRuleType().intValue()) {
    					defaultRule = rule;
    				} else {
    					RuleExpression ruleExpression = JsonUtil.readValue(rule.getRuleExpression(), RuleExpression.class);
    					boolean isMapping = isMapping(addressLine, ruleExpression);
    					if (isMapping) {
    						mappingRule = rule; 
    					}
    				}
    			}
            }

			if (mappingRule == null) {
				// 针对每一个address，没有匹配上任何客户化规则时，则匹配到默认规则
				mappingRule = defaultRule;
			}
			if(mappingRule!=null){
				ruleList.add(mappingRule);
			}
		}
		return ruleList;
	}

	public void addRule(DelivererRule dr,Long customerId) {
		List list = delivererRuleDao.getByAddressAndDeliverer(dr.getAddress().getId(),dr.getDeliverer().getId(),customerId);
		if(list!=null&&!list.isEmpty()){
			  throw new ExplinkRuntimeException("配送站点已绑定默认小件员");
		}
		delivererRuleDao.save(dr);
	}

	public DelivererRule getById(Long ruleId) {
		return delivererRuleDao.get(ruleId);
	}

	@Override
	public void delete(Long id) {
		DelivererRule r = (DelivererRule) getSession().load(DelivererRule.class, id);
		getSession().delete(r);
	}

}
