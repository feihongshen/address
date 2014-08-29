package cn.explink.domain.enums;

public enum CustomerStausEnmu {
	valid(1), invalid(2);

	private int value;

	private CustomerStausEnmu(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}

}
