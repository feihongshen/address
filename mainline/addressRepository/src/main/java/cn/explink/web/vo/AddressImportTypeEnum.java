package cn.explink.web.vo;

public enum AddressImportTypeEnum {

	init(1), stationImport(2),stationMove(3);

	private int value;

	private AddressImportTypeEnum(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}

}
