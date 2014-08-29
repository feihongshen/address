package cn.explink.domain.enums;

public enum AddressStatusEnum {
	valid(1), invalid(2);

	private int value;

	private AddressStatusEnum(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}

}
