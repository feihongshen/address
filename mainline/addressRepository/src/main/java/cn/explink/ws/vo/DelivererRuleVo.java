package cn.explink.ws.vo;

public class DelivererRuleVo {

	private Long addressId;

	private Long delivererId;

	private String rule;
	
	private Long ruleId;

	public Long getAddressId() {
		return addressId;
	}

	public void setAddressId(Long addressId) {
		this.addressId = addressId;
	}

	public Long getDelivererId() {
		return delivererId;
	}

	public void setDelivererId(Long delivererId) {
		this.delivererId = delivererId;
	}

	public String getRule() {
		return rule;
	}

	public void setRule(String rule) {
		this.rule = rule;
	}

	public Long getRuleId() {
		return ruleId;
	}

	public void setRuleId(Long ruleId) {
		this.ruleId = ruleId;
	}

}
