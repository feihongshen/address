
package cn.explink.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "DELIVERER_RULES")
public class DelivererRule {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "RULE", length = 100)
    private String rule;

    @Column(name = "RULE_EXPRESSION", length = 1000)
    private String ruleExpression;

    @Column(name = "RULE_TYPE", nullable = false)
    private Integer ruleType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ADDRESS_ID")
    private Address address;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "DELIVERER_ID")
    private Deliverer deliverer;

    @Column(name = "CREATION_TIME")
    private Date creationTime;

    @Column(name = "DELIVERY_STATION_ID")
    private Long deliveryStationId;

    public Long getDeliveryStationId() {
        return this.deliveryStationId;
    }

    public void setDeliveryStationId(Long deliveryStationId) {
        this.deliveryStationId = deliveryStationId;
    }

    public DelivererRule() {
        super();
    }

    public DelivererRule(Long id, String rule) {
        super();
        this.id = id;
        this.rule = rule;
    }

    public DelivererRule(Long id, String rule, Deliverer deliverer) {
        super();
        this.id = id;
        this.rule = rule;
        this.deliverer = deliverer;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRule() {
        return this.rule;
    }

    public void setRule(String rule) {
        this.rule = rule;
    }

    public Address getAddress() {
        return this.address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public Date getCreationTime() {
        return this.creationTime;
    }

    public void setCreationTime(Date creationTime) {
        this.creationTime = creationTime;
    }

    public Integer getRuleType() {
        return this.ruleType;
    }

    public void setRuleType(Integer ruleType) {
        this.ruleType = ruleType;
    }

    public Deliverer getDeliverer() {
        return this.deliverer;
    }

    public void setDeliverer(Deliverer deliverer) {
        this.deliverer = deliverer;
    }

    public String getRuleExpression() {
        return this.ruleExpression;
    }

    public void setRuleExpression(String ruleExpression) {
        this.ruleExpression = ruleExpression;
    }

}
