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
@Table(name = "RAW_DELIVERY_STATION_RULES")
public class RawDeliveryStationRule {

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
	@JoinColumn(name = "RAW_ADDRESS_ID")
	private RawAddress rawAddress;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "RAW_DELIVERY_STATION_ID")
	private RawDeliveryStation rawDeliveryStation;

	@Column(name = "CREATION_TIME")
	private Date creationTime;

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

	public RawAddress getRawAddress() {
		return this.rawAddress;
	}

	public void setRawAddress(RawAddress rawAddress) {
		this.rawAddress = rawAddress;
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

	public RawDeliveryStation getRawDeliveryStation() {
		return this.rawDeliveryStation;
	}

	public void setRawDeliveryStation(RawDeliveryStation rawDeliveryStation) {
		this.rawDeliveryStation = rawDeliveryStation;
	}

	public String getRuleExpression() {
		return this.ruleExpression;
	}

	public void setRuleExpression(String ruleExpression) {
		this.ruleExpression = ruleExpression;
	}

}