
package cn.explink.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
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
import cn.explink.dao.DeliveryStationDao;
import cn.explink.domain.Address;
import cn.explink.domain.Deliverer;
import cn.explink.domain.DelivererRule;
import cn.explink.domain.DeliveryStation;
import cn.explink.domain.enums.DelivererRuleTypeEnum;
import cn.explink.domain.enums.DeliveryStationRuleTypeEnum;
import cn.explink.domain.fields.RuleExpression;
import cn.explink.exception.ExplinkRuntimeException;
import cn.explink.tree.ZTreeNode;
import cn.explink.util.JsonUtil;
import cn.explink.util.StringUtil;
import cn.explink.web.vo.DelivererStationRuleVo;
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

    @Autowired
    private DeliveryStationDao deliveryStationDao;

    /**
     * 批量创建配送员规则
     * @param customerId
     * @param delivererRuleVoList
     */
    public void createDelivererRule(Long customerId, List<DelivererRuleVo> delivererRuleVoList) {
        for (DelivererRuleVo ruleVo : delivererRuleVoList) {
            this.createDelivererRule(customerId, ruleVo.getAddressId(), ruleVo.getDelivererId(), ruleVo.getRule());
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
        DelivererRuleService.logger.info(
                "createDelivererRule for customer: {}, addressId: {}, delivererId: {}, rule: {}", new Object[] {
                        customerId, addressId, delivererId, rule });
        // 解析规则
        RuleExpression ruleExpression = this.parseRule(rule);
        Address address = this.addressDao.get(addressId);
        Deliverer deliverer = this.delivererDao.getDeliverer(customerId, delivererId);

        // 判断是否与已有规则冲突
        Set<DelivererRule> filterdRules = this.filter(customerId, address.getDelivererRules());
        DelivererRule confilctingRule = this.findConflictingRule(ruleExpression, filterdRules);
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
        this.delivererRuleDao.save(delivererRule);
        return delivererRule;
    }

    public DelivererRule createDelivererRule(Long customerId, Long addressId, Long delivererId, String rule,
            Long stationId) {
        DelivererRuleService.logger.info(
                "createDelivererRule for customer: {}, addressId: {}, delivererId: {}, rule: {}", new Object[] {
                        customerId, addressId, delivererId, rule });
        // 解析规则
        RuleExpression ruleExpression = this.parseRule(rule);
        Address address = this.addressDao.get(addressId);
        Deliverer deliverer = this.delivererDao.getDelivererById(delivererId);
        DeliveryStation deliveryStation = this.deliveryStationDao.getDeliveryStationById(stationId);

        // 判断是否与已有规则冲突
        Set<DelivererRule> filterdRules = this.filter(customerId, address.getDelivererRules());
        DelivererRule confilctingRule = this.findConflictingRule(ruleExpression, filterdRules);
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
        delivererRule.setDeliveryStation(deliveryStation);
        delivererRule.setCreationTime(new Date());
        if (StringUtil.isEmpty(rule)) {
            delivererRule.setRuleType(DelivererRuleTypeEnum.fallback.getValue());
            delivererRule.setRule("");
            delivererRule.setRuleExpression("");
        } else {
            delivererRule.setRuleType(DelivererRuleTypeEnum.customization.getValue());
            delivererRule.setRule(rule);
            delivererRule.setRuleExpression(JsonUtil.translateToJson(ruleExpression));
        }
        this.delivererRuleDao.save(delivererRule);
        return delivererRule;
    }

    private Set<DelivererRule> filter(Long customerId, Set<DelivererRule> delivererRules) {
        if (delivererRules == null) {
            return null;
        }
        Set<DelivererRule> filteredRules = new HashSet<DelivererRule>();
        for (DelivererRule rule : delivererRules) {
            if (customerId.equals(rule.getDeliverer().getCustomer().getId())) {
                filteredRules.add(rule);
            }
        }
        return filteredRules;
    }

    /**
     * 查找冲突的规则
     * @param ruleExpression
     * @param delivererRules
     * @return
     */
    private DelivererRule findConflictingRule(RuleExpression ruleExpression, Set<DelivererRule> delivererRules) {
        if (delivererRules == null) {
            return null;
        }
        for (DelivererRule existingRule : delivererRules) {
            if (this.isConflict(ruleExpression, existingRule)) {
                return existingRule;
            }
        }
        return null;
    }

    public List<DelivererRule> getDelivererRuleList(Long customerId, Long addressId) {
        if (addressId == null) {
            return null;
        }
        List<DelivererRule> ruleList = this.delivererRuleDao.getDelivererRuleList(customerId, addressId);
        List<DelivererRule> resultList = new ArrayList<DelivererRule>();
        for (DelivererRule rule : ruleList) {
            if (customerId.equals(rule.getDeliverer().getCustomer().getId())) {
                resultList.add(rule);
            }
        }
        return resultList;
    }

    /**
     * 判断规则是否冲突
     * @param ruleExpression
     * @param existingRule
     * @return
     */
    private boolean isConflict(RuleExpression ruleExpression, DelivererRule existingRule) {
        if ((ruleExpression == null)
                && (DeliveryStationRuleTypeEnum.fallback.getValue() == existingRule.getRuleType().intValue())) {
            return true;
        }
        if ((ruleExpression != null)
                && (DeliveryStationRuleTypeEnum.fallback.getValue() == existingRule.getRuleType().intValue())) {
            return false;
        }
        if ((ruleExpression == null)
                && (DeliveryStationRuleTypeEnum.fallback.getValue() != existingRule.getRuleType().intValue())) {
            return false;
        }
        RuleExpression existingRuleExpression = JsonUtil.readValue(existingRule.getRuleExpression(),
                RuleExpression.class);
        return this.isConflict(ruleExpression, existingRuleExpression);
    }

    /**
     * 根据地址搜索匹配的站点规则
     * @param addressList
     * @param orderVo
     * @return
     */
    public List<DelivererRule> search(List<Address> addressList, OrderVo orderVo, DeliveryStation station) {
        if (addressList == null) {
            return null;
        }
        List<DelivererRule> ruleList = new ArrayList<DelivererRule>();
        for (Address address : addressList) {
            // 默认规则
            // 支持多个默认规则
            List<DelivererRule> defaultRuleList = new ArrayList<DelivererRule>();

            boolean hasCustomerRule = false;
            // 存在别名匹配时报错2015-01-16[zhaoshb+].
            String addressLine = StringUtil.full2Half(orderVo.getAddressLine());
            int index = orderVo.getAddressLine().lastIndexOf(address.getName());
            if (index >= 0) {
                addressLine = addressLine.substring(index + address.getName().length());
            }
            // 根据客户id+关键词id获取对应的小件员规则
            List<DelivererRule> list = this.getDelivererRuleList(orderVo.getCustomerId(), station.getId(),
                    address.getId());
            for (DelivererRule rule : list) {
                if (DeliveryStationRuleTypeEnum.fallback.getValue() == rule.getRuleType().intValue()) {
                    defaultRuleList.add(rule);
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
            if (!hasCustomerRule && !defaultRuleList.isEmpty()) {
                ruleList.addAll(defaultRuleList);
            }
        }
        return ruleList;
    }

    public void addRule(DelivererRule dr, Long customerId) {
        List list = this.delivererRuleDao.getByAddressAndDeliverer(dr.getAddress().getId(), dr.getDeliverer().getId(),
                customerId);
        if ((list != null) && !list.isEmpty()) {
            throw new ExplinkRuntimeException("配送站点已绑定默认小件员");
        }
        this.delivererRuleDao.save(dr);
    }

    public DelivererRule getById(Long ruleId) {
        return this.delivererRuleDao.get(ruleId);
    }

    @Override
    public void delete(Long id) {
        DelivererRule r = (DelivererRule) this.getSession().load(DelivererRule.class, id);
        this.getSession().delete(r);
    }

    public void deleteRuleByIds(List<Long> addressIdList, Long customerId) {
        String sql = "SELECT R.ID FROM DELIVERER_RULES  R ,DELIVERERS S  WHERE R.ADDRESS_ID IN :idList AND S.CUSTOMER_ID =:customerId AND S.ID=R.DELIVERER_ID";
        List<Integer> list = this.getSession().createSQLQuery(sql).setParameterList("idList", addressIdList)
                .setLong("customerId", customerId).list();
        for (Integer l : list) {
            this.delete(Long.parseLong(l + ""));
        }

    }

    /**
     * 根据客户编码+站点编码+addressId获取对应的小件员匹配信息
     * <p>
     * 方法详细描述
     * </p>
     * @param customerId
     * @param stationId
     * @param id
     * @return
     * @since 1.0
     */
    public List<DelivererRule> getDelivererRuleList(Long customerId, Long stationId, Long addressId) {
        if (addressId == null) {
            return null;
        }
        List<DelivererRule> ruleList = this.delivererRuleDao.getDelivererRuleList(customerId, stationId, addressId);
        List<DelivererRule> resultList = new ArrayList<DelivererRule>();
        for (DelivererRule rule : ruleList) {
            if (customerId.equals(rule.getDeliverer().getCustomer().getId())) {
                resultList.add(rule);
            }
        }
        return resultList;
    }

    /**
     * 根据客户id+站点id+配送员id获取前端所需要的树形结构
     * <p>
     * 方法详细描述
     * </p>
     * @param customerId
     * @param stationId
     * @param delivererId
     * @return
     * @since 1.0
     */
    public List<ZTreeNode> getAddressByDeliverer(Long customerId, String stationId, String delivererId) {
        return this.delivererRuleDao.getAddressByDeliverer(customerId, stationId, delivererId);

    }

    public List<DelivererStationRuleVo> getAddressByDeliverer(Long customerId, Long stationId, Long delivererId) {
        if ((delivererId == null) || (stationId == null)) {
            return null;
        }

        List<DelivererStationRuleVo> ruleList = this.delivererRuleDao.getDelivererRule2(customerId, stationId,
                delivererId);

        return ruleList;
    }

    public DelivererStationRuleVo saveDelivererRule(DelivererStationRuleVo vo, Long customerId) {

        try {
            if (("new".equals(vo.getType())) || "edit".equals(vo.getType())) {
                DelivererRule dr = this.createDelivererRule(customerId, Long.parseLong(vo.getAddressId() + ""),
                        Long.parseLong(vo.getDelivererId() + ""), vo.getRule(), Long.parseLong(vo.getStationId() + ""));
                vo.setRuleId(Integer.parseInt(dr.getId() + ""));
            }

            if ("del".equals(vo.getType())) {
                this.delete(Long.parseLong(vo.getRuleId() + ""));
            }
        } catch (Exception ex) {
            DelivererRuleService.logger.error("保存小件员规则报错,错误内容:" + ex.getMessage(), ex);
            throw new ExplinkRuntimeException("保存小件员规则报错,错误内容:" + ex.getMessage());

        }
        return vo;

    }

    /**
     * 方法概要
     * <p>
     * 方法详细描述
     * </p>
     * @param customerId
     * @param delivererId
     * @param stationId
     * @param addressId
     * @return
     * @since 1.0
     */
    public Boolean checkDelivererRule(Long customerId, Long delivererId, Long stationId, Long addressId, Long ruleId) {
        return false;
    }
}
