package cn.explink.domain.enums;

public enum DelivererRuleTypeEnum {
	fallback(1), customization(2);

	private int value;

	private DelivererRuleTypeEnum(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}

}
