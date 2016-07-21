/*
 * Copyright (c) 2014, Vipshop (China). Co. Ltd. All rights reserved.
 */

package cn.explink.web.vo;

/**
 * 类概要
 * <p>
 * 类详细描述
 * </p>
 * @author yuxin.he
 * @since 1.0
 */
public class DelivererStationRuleVo {

    private Integer addressId;

    private Integer ruleId;

    private String rule;

    private Integer delivererId;

    private Integer stationId;

    private String type;

    /**
     * @return the type
     */
    public String getType() {
        return this.type;
    }

    /**
     * @param type the type to set
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * @return the addressId
     */
    public Integer getAddressId() {
        return this.addressId;
    }

    /**
     * @param addressId the addressId to set
     */
    public void setAddressId(Integer addressId) {
        this.addressId = addressId;
    }

    /**
     * @return the ruleId
     */
    public Integer getRuleId() {
        return this.ruleId;
    }

    /**
     * @param ruleId the ruleId to set
     */
    public void setRuleId(Integer ruleId) {
        this.ruleId = ruleId;
    }

    /**
     * @return the rule
     */
    public String getRule() {
        return this.rule;
    }

    /**
     * @param rule the rule to set
     */
    public void setRule(String rule) {
        this.rule = rule;
    }

    /**
     * @return the delivererId
     */
    public Integer getDelivererId() {
        return this.delivererId;
    }

    /**
     * @param delivererId the delivererId to set
     */
    public void setDelivererId(Integer delivererId) {
        this.delivererId = delivererId;
    }

    /**
     * @return the stationId
     */
    public Integer getStationId() {
        return this.stationId;
    }

    /**
     * @param stationId the stationId to set
     */
    public void setStationId(Integer stationId) {
        this.stationId = stationId;
    }

}
