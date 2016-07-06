package cn.explink.domain.enums;

public enum SearchTypeEnum {
	keyword(1), station(2);

	private int value;

	private SearchTypeEnum(int value) {
		this.value = value;
	}

	public int getValue() {
		return this.value;
	}

	public void setValue(int value) {
		this.value = value;
	}

}
