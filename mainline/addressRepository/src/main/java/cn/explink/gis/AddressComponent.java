package cn.explink.gis;

/**
 * 地址对象 逆地理编码的返回结果
 *
 * @author Administrator
 *
 */
public class AddressComponent {
	/**
	 * 城市名称
	 */
	private String city;
	/**
	 * 国家名称
	 */
	private String country;
	/**
	 * 方向
	 */
	private String direction;
	/**
	 * 距离
	 */
	private String distance;
	/**
	 * 区
	 */
	private String district;
	/**
	 * 省
	 */
	private String province;
	/**
	 * 街道
	 */
	private String street;
	/**
	 * 街道号
	 */
	private String street_number;

	public String getCity() {
		return this.city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getCountry() {
		return this.country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getDirection() {
		return this.direction;
	}

	public void setDirection(String direction) {
		this.direction = direction;
	}

	public String getDistance() {
		return this.distance;
	}

	public void setDistance(String distance) {
		this.distance = distance;
	}

	public String getDistrict() {
		return this.district;
	}

	public void setDistrict(String district) {
		this.district = district;
	}

	public String getProvince() {
		return this.province;
	}

	public void setProvince(String province) {
		this.province = province;
	}

	public String getStreet() {
		return this.street;
	}

	public void setStreet(String street) {
		this.street = street;
	}

	public String getStreet_number() {
		return this.street_number;
	}

	public void setStreet_number(String street_number) {
		this.street_number = street_number;
	}

	public String getCountry_code() {
		return this.country_code;
	}

	public void setCountry_code(String country_code) {
		this.country_code = country_code;
	}

	private String country_code;

	@Override
	public String toString() {
		return this.province + " " + this.city + " " + this.district + " " + this.street + " " + this.street_number + " ";
	}
}
