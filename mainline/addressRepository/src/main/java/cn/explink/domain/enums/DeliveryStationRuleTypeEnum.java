package cn.explink.domain.enums;

public enum DeliveryStationRuleTypeEnum {
	fallback(1), customization(2);

	private int value;

	private DeliveryStationRuleTypeEnum(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}

}
