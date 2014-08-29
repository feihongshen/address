package cn.explink.domain.enums;

public enum AddressImportDetailStatsEnum {

	success(0), failure(1);

	private int value;

	private AddressImportDetailStatsEnum(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}

}
