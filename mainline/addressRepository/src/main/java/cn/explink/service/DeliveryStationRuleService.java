
package cn.explink.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Query;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.explink.dao.AddressDao;
import cn.explink.dao.AddressPermissionDao;
import cn.explink.dao.BizLogDAO;
import cn.explink.dao.DelivererDao;
import cn.explink.dao.DelivererRuleDao;
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
import cn.explink.domain.enums.LogTypeEnum;
import cn.explink.domain.fields.RuleExpression;
import cn.explink.exception.ExplinkRuntimeException;
import cn.explink.modle.DataGridReturn;
import cn.explink.tree.ZTreeNode;
import cn.explink.util.JsonUtil;
import cn.explink.util.StringUtil;
import cn.explink.util.SynInsertBizLogThread;
import cn.explink.web.controller.AddressController;
import cn.explink.web.vo.DeliveryStationRuleVo;
import cn.explink.web.vo.VendorsAgingVo;
import cn.explink.ws.vo.BeanVo;
import cn.explink.ws.vo.OrderVo;

@Service
public class DeliveryStationRuleService extends RuleService {

    @Autowired
    private AddressDao addressDao;

    @Autowired
    private DelivererDao delivererDao;

    @Autowired
    private DelivererRuleDao delivererRuleDao;

    @Autowired
    private DeliveryStationDao deliveryStationDao;

    @Autowired
    private AddressPermissionDao addressPermissionDao;

    @Autowired
    private DeliveryStationRuleDao deliveryStationRuleDao;

    @Autowired
    private VendorsAgingDao vendorsAgingDao;

    @Autowired
    private BizLogService bizLogService;

    @Autowired
    private BizLogDAO bizLogDAO;

    public DeliveryStationRule createDeliveryStationRule(Long addressId, Long deliveryStationId, Long customerId,
            String rule, String operateIP) {
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
        // 打印日志，并收集保存日志信息
        ExecutorService service = Executors.newCachedThreadPool();
        service.execute(new SynInsertBizLogThread(AddressController.class, customerId, LogTypeEnum.addRule.getValue(),
                operateIP, deliveryStationRule, this.bizLogDAO, this.bizLogService, null, null));
        service.shutdown();
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
     * @param ruleExpression
     * @param deliveryStationRules
     * @return
     */
    private DeliveryStationRule findConflictingRule(RuleExpression ruleExpression,
            Set<DeliveryStationRule> deliveryStationRules) {
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
     * @param ruleExpression
     * @param existingRule
     * @return
     */
    private boolean isConflict(RuleExpression ruleExpression, DeliveryStationRule existingRule) {
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
    public List<DeliveryStationRule> search(List<Address> addressList, OrderVo orderVo) {
        if (addressList == null) {
            return null;
        }
        List<DeliveryStationRule> ruleList = new ArrayList<DeliveryStationRule>();
        for (Address address : addressList) {
            // 默认规则
            // 支持多个默认规则 added by songkaojun 2015-11-26
            List<DeliveryStationRule> defaultRuleList = new ArrayList<DeliveryStationRule>();
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

    public DataGridReturn getDataGridReturnView(String addressId) {

        Query query = this
                .getSession()
                .createQuery(
                        "select new cn.explink.web.vo.DeliveryStationRuleVo(dsr.id, dsr.deliveryStation.name) from DeliveryStationRule dsr where dsr.address.id =:addressId");
        query.setLong("addressId", Long.parseLong(addressId));
        List<DeliveryStation> list = query.list();
        return new DataGridReturn(list.size(), list);
    }

    public List<Long> getAddressIds(Long parentId, Long customerId) {
        return this.deliveryStationRuleDao.getAddressIds(parentId, customerId);
    }

    public void addRule(DeliveryStationRule dsr, Long customerId) {
        List list = this.deliveryStationRuleDao.getByAddressAndStation(dsr.getAddress().getId(), dsr
                .getDeliveryStation().getId(), customerId);
        if ((list != null) && !list.isEmpty()) {
            throw new ExplinkRuntimeException("该关键字已绑定默认站点");
        }
        this.deliveryStationRuleDao.save(dsr);
    }

    public List<BeanVo> getStationAddressTree(Long customerId, String inIds) {
        Query query = this
                .getSession()
                .createQuery(
                        "select new cn.explink.ws.vo.BeanVo(dsr.address.id, dsr.deliveryStation.name) from DeliveryStationRule dsr where dsr.deliveryStation.status=1 and dsr.address.id in("
                                + inIds
                                + ") and dsr.deliveryStation.customer.id=:customerId group by dsr.deliveryStation,dsr.address");
        query.setLong("customerId", customerId);
        return query.list();
    }

    public List<DeliveryStationRule> getByCustormerAndAdressId(Long customerId, Long aId) {
        Query query = this
                .getSession()
                .createQuery(
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

    public void changeStationRelation(Long sourceStationId, Long targetStationId, Long sourceDelivererId,
            Long targetDelivererId, String sourceAddressId, String targetAddressId) {
        // v1.02 add by vince.zhou 原逻辑不变，增加小件员逻辑
        if ((0 == sourceDelivererId) && (0 == targetDelivererId)) {
            this.changeStationRelation(sourceStationId, targetStationId, sourceAddressId, targetAddressId);
        }
        // 如果没有原小件员，只有目的小件员
        else if ((0 == sourceDelivererId) && (0 != targetDelivererId)) {
            this.changeStationRelation(sourceStationId, targetStationId, sourceAddressId, targetAddressId);

            // 对目的关键词id中，首先更新delivery_rule这些关键词的stationId=targetStationId，小件员更新成目的小件员
            this.updateDeliveryRelation(targetStationId, targetAddressId, targetDelivererId);

            // 删除源关键词id中，存在于delivery_rule中并且stationId=targetStationId 小件员=目的小件员的记录
            this.deleteDeliveryRelation(targetStationId, sourceAddressId, targetDelivererId);

            // // 找出源关键词id与目的关键词id中的差异list
            // List<Long> idList = new ArrayList<Long>();
            // String[] sourceids = sourceAddressId.split(",");
            //
            // List<Long> sourceidsList = new ArrayList<Long>();
            // for (String id : sourceids) {
            // Long longid = Long.valueOf(id);
            // sourceidsList.add(longid);
            // }
            // String[] targetids = targetAddressId.split(",");
            //
            // for (String id : targetids) {
            // Long longid = Long.valueOf(id);
            // if (!sourceidsList.contains(longid)) {
            // idList.add(longid);
            // }
            // }
            //
            // // 循环差异list,判断在delivery_rule中是否存在stationId=targetStationId的记录，如果不存在，新增stationId=targetStationId；
            // if (CollectionUtils.isNotEmpty(idList)) {
            // for (Long id : idList) {
            //
            // }
            // }
            // List<Address> addressList = this.addressDao.getAddressByIdList(idList);
            // Deliverer deliverer = this.delivererDao.get(targetDelivererId);
            // DeliveryStation deliveryStation = this.deliveryStationDao.get(targetStationId);
            // // 循环所有list，保存一条小件员规则
            // for (Address address : addressList) {
            // // 如果是第四级关键词以下
            // if (address.getAddressLevel() > 3) {
            // DelivererRule delivererRule = new DelivererRule();
            // delivererRule.setAddress(address);
            // delivererRule.setDeliverer(deliverer);
            // delivererRule.setDeliveryStation(deliveryStation);
            // delivererRule.setCreationTime(new Date());
            // delivererRule.setRule("");
            // delivererRule.setRuleType(1);
            // delivererRule.setRuleExpression("");
            // this.delivererRuleDao.save(delivererRule);
            // }
            // }

        }
        // 原小件员不为空，没有目的小件员
        else if ((0 != sourceDelivererId) && (0 == targetDelivererId)) {
            this.changeStationRelation(sourceStationId, targetStationId, sourceAddressId, targetAddressId);

            // 对源关键词id中，首先更新delivery_rule这些关键词的stationId=targetStationId，deliverer_id = sourceDelivererId.
            this.updateDeliveryRelation(sourceStationId, sourceAddressId, sourceDelivererId);

            // 删除目的关键词id中，存在于delivery_rule中并且stationId=sourceStationId,deliverer_id = sourceDelivererId.的记录
            this.deleteDeliveryRelation(sourceStationId, targetAddressId, sourceDelivererId);

            // String[] ids = sourceAddressId.split(",");
            // List<Long> idList = new ArrayList<Long>();
            // for (String id : ids) {
            // Long longid = Long.valueOf(id);
            // idList.add(longid);
            // }
            // List<Address> addressList = this.addressDao.getAddressByIdList(idList);
            // for (Address address : addressList) {
            // DelivererRule delivererRule = this.delivererRuleDao.getDelivererRuleList(sourceDelivererId,
            // sourceStationId, address.getId());
            // if (null != delivererRule) {
            // this.delivererRuleDao.delete(delivererRule);
            // }
            // }
        }
        // 都不为空
        else if ((0 != sourceDelivererId) && (0 != targetDelivererId)) {
            this.changeStationRelation(sourceStationId, targetStationId, sourceAddressId, targetAddressId);
            // 对源关键词id中，首先更新delivery_rule这些关键词的stationId=targetStationId，deliverer_id = sourceDelivererId.
            this.updateDeliveryRelation(sourceStationId, sourceAddressId, sourceDelivererId);

            // 对目的关键词id中，首先更新delivery_rule这些关键词的stationId=targetStationId，deliverer_id=targetDelivererId
            this.updateDeliveryRelation(targetStationId, targetAddressId, targetDelivererId);

            // 删除目的关键词id中，存在于delivery_rule中并且stationId=sourceStationId,deliverer_id = sourceDelivererId.的记录
            this.deleteDeliveryRelation(sourceStationId, targetAddressId, sourceDelivererId);

            // 删除源关键词id中，存在于delivery_rule中并且stationId=targetStationId,deliverer_id=targetDelivererId的记录
            this.deleteDeliveryRelation(targetStationId, sourceAddressId, targetDelivererId);

            // String[] ids = sourceAddressId.split(",");
            // List<Long> idList = new ArrayList<Long>();
            // Deliverer targetDeliverer = this.delivererDao.get(targetDelivererId);
            // DeliveryStation targetDeliveryStation = this.deliveryStationDao.get(targetStationId);
            // for (String id : ids) {
            // Long longid = Long.valueOf(id);
            // idList.add(longid);
            // }
            // List<Address> addressList = this.addressDao.getAddressByIdList(idList);
            // for (Address address : addressList) {
            // DelivererRule delivererRule = this.delivererRuleDao.getDelivererRuleList(sourceDelivererId,
            // sourceStationId, address.getId());
            // if (null != delivererRule) {
            // this.delivererRuleDao.delete(delivererRule);
            // // 添加新的
            // DelivererRule targetDelivererRule = new DelivererRule();
            // delivererRule.setAddress(address);
            // delivererRule.setDeliverer(targetDeliverer);
            // delivererRule.setDeliveryStation(targetDeliveryStation);
            // delivererRule.setCreationTime(new Date());
            // delivererRule.setRule("");
            // delivererRule.setRuleType(1);
            // delivererRule.setRuleExpression("");
            // this.delivererRuleDao.save(targetDelivererRule);
            // }
            // }
        }
    }

    /**
     * 更新指定指点、指定站点的小件员关系
     * <p>
     * 方法详细描述
     * </p>
     * @param targetStationId
     * @param targetAddressId
     * @since 1.0
     */
    private void updateDeliveryRelation(Long stationId, String addressId, Long delivererId) {
        if (StringUtils.isNotBlank(addressId)) {
            String sql = "update deliverer_rules set DELIVERY_STATION_ID = '" + stationId + "', deliverer_id = '"
                    + delivererId + "'  where ADDRESS_ID in (" + addressId + ")";
            Query sourceQuery = this.getSession().createSQLQuery(sql);
            sourceQuery.executeUpdate();
        }

    }

    /**
     * 删除指定指点、指定小件员的小件员关系
     * <p>
     * 方法详细描述
     * </p>
     * @param stationId
     * @param addressId
     * @since 1.0
     */
    private void deleteDeliveryRelation(Long stationId, String addressId, Long delivererId) {
        if (StringUtils.isNotBlank(addressId)) {
            String sql = "delete from deliverer_rules where DELIVERY_STATION_ID = '" + stationId
                    + "' and deliverer_id  = " + delivererId + " and ADDRESS_ID in (" + addressId + ")";
            Query sourceQuery = this.getSession().createSQLQuery(sql);
            sourceQuery.executeUpdate();
        }
    }

    public void changeStationRelation(Long sourceStationId, Long targetStationId, String sourceAddressId,
            String targetAddressId) {
        if (StringUtils.isNotBlank(sourceAddressId)) {
            String sourceSql = "UPDATE `DELIVERY_STATION_RULES` SET `DELIVERY_STATION_ID`=" + sourceStationId
                    + " WHERE `DELIVERY_STATION_ID`=" + targetStationId + " and `ADDRESS_ID` in (" + sourceAddressId
                    + ");";
            Query sourceQuery = this.getSession().createSQLQuery(sourceSql);
            sourceQuery.executeUpdate();
        }
        if (StringUtils.isNotBlank(targetAddressId)) {
            String targetSql = "UPDATE `DELIVERY_STATION_RULES` SET `DELIVERY_STATION_ID`=" + targetStationId
                    + " WHERE `DELIVERY_STATION_ID`=" + sourceStationId + " and `ADDRESS_ID` in (" + targetAddressId
                    + ");";
            Query targetQuery = this.getSession().createSQLQuery(targetSql);
            targetQuery.executeUpdate();
        }

    }

    public List<DeliveryStationRuleVo> getAllStationRule(String addressId, Long custmerId) {
        String sql = "SELECT DSR.ID id,S.NAME deliveryStationName   ,DSR.RULE rule,DSR.RULE_TYPE ruleType ,DSR.RULE_EXPRESSION ruleExpression  FROM DELIVERY_STATION_RULES DSR ,DELIVERY_STATIONS S WHERE DSR.ADDRESS_ID=:addressId"
                + "  AND S.CUSTOMER_ID=:customerId AND S.ID=DSR.DELIVERY_STATION_ID";
        Query query = this.getSession().createSQLQuery(sql)
                .setResultTransformer(Transformers.aliasToBean(DeliveryStationRuleVo.class));
        query.setLong("addressId", Long.parseLong(addressId));
        query.setLong("customerId", custmerId);
        return query.list();
    }

    public List<VendorsAging> getAllVendorAging(String addressId, Long custmerId) {
        Query query = this.getSession().createQuery(
                "from VendorsAging where address.id=:addressId and customer.id=:custmerId ");
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

    public void createDeliveryStationRuleList(List<DeliveryStationRuleVo> list, Long customerId, String operateID) {
        for (DeliveryStationRuleVo r : list) {
            this.createDeliveryStationRule(r.getAddressId(), r.getStationId(), customerId, r.getRule(), operateID);
        }
    }

    /**
     * @Title: getRuleById
     * @description 通过配送规则id，查询规则信息
     * @author 刘武强
     * @date 2015年11月27日下午6:02:50
     * @param @param id
     * @param @return
     * @return DeliveryStation
     * @throws
     */
    public DeliveryStationRule getRuleById(Long id) {
        return this.deliveryStationRuleDao.getRuleById(id);
    }

}
