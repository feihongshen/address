package cn.explink.spliter.vo;

/**
 * ”地址串-站点“对
 *
 * @author songkaojun 2015年5月8日
 */
public class AddressLineStationPair {
	private String addressLine;
	private String stationName;

	public AddressLineStationPair(String addressLine, String stationName) {
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
