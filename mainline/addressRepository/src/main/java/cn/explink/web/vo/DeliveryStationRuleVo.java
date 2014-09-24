package cn.explink.web.vo;

public class DeliveryStationRuleVo {

	private Integer id;
 	private String rule;
 	private String ruleExpression;
 	private Integer ruleType;
	private String deliveryStationName;
	private Long stationId;
	private Long addressId;
	
	public Long getAddressId() {
		return addressId;
	}

	public void setAddressId(Long addressId) {
		this.addressId = addressId;
	}

	public Long getStationId() {
		return stationId;
	}

	public void setStationId(Long stationId) {
		this.stationId = stationId;
	}

	public DeliveryStationRuleVo( ) {
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getDeliveryStationName() {
		return deliveryStationName;
	}

	public void setDeliveryStationName(String deliveryStationName) {
		this.deliveryStationName = deliveryStationName;
	}

	public String getRule() {
		return rule;
	}

	public void setRule(String rule) {
		this.rule = rule;
	}

	public String getRuleExpression() {
		return ruleExpression;
	}

	public void setRuleExpression(String ruleExpression) {
		this.ruleExpression = ruleExpression;
	}

	public Integer getRuleType() {
		return ruleType;
	}

	public void setRuleType(Integer ruleType) {
		this.ruleType = ruleType;
	}

	

}