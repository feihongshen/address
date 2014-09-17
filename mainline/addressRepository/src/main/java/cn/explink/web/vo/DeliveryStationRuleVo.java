package cn.explink.web.vo;

public class DeliveryStationRuleVo {

	private Long id;

//	private String rule;
//
//	private Integer ruleType;
	private String deliveryStationName;

//	private Date creationTime;

	public DeliveryStationRuleVo(Long id, String deliveryStationName) {
		this.id = id;
		this.deliveryStationName = deliveryStationName;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getDeliveryStationName() {
		return deliveryStationName;
	}

	public void setDeliveryStationName(String deliveryStationName) {
		this.deliveryStationName = deliveryStationName;
	}

	

}