package cn.explink.spliter.vo;

public class AddressStation {
	private String addressLine;
	private String stationName;

	public AddressStation(String addressLine, String stationName) {
		super();
		this.addressLine = addressLine;
		this.stationName = stationName;
	}

	public String getAddressLine() {
		return this.addressLine;
	}

	public void setAddressLine(String addressLine) {
		this.addressLine = addressLine;
	}

	public String getStationName() {
		return this.stationName;
	}

	public void setStationName(String stationName) {
		this.stationName = stationName;
	}

}
