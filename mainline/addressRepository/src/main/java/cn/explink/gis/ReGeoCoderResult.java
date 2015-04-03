package cn.explink.gis;

import java.util.List;

/**
 * 逆地理编码返回结果
 * @author Administrator
 *
 */
public class ReGeoCoderResult {

	/**
	 * 逆地理编码返回的地址组合
	 */
	private AddressComponent addressComponent;
	/**
	 * 目的地周边的兴趣点
	 */
	private List<POI> pois;
	
	public AddressComponent getAddressComponent() {
		return addressComponent;
	}

	public void setAddressComponent(AddressComponent addressComponent) {
		this.addressComponent = addressComponent;
	}

	public List<POI> getPois() {
		return pois;
	}

	public void setPois(List<POI> pois) {
		this.pois = pois;
	}

}
