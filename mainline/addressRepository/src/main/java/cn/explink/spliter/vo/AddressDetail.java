package cn.explink.spliter.vo;

public class AddressDetail implements Cloneable {
	private String province;
	private String city;
	private String district;
	private String address1;
	private String address2;
	private String address3;
	private String deliveryStationName;

	public AddressDetail() {
		super();
	}

	public AddressDetail(String province, String city, String district, String address1, String address2, String address3, String deliveryStationName) {
		super();
		this.province = province;
		this.city = city;
		this.district = district;
		this.address1 = address1;
		this.address2 = address2;
		this.address3 = address3;
		this.deliveryStationName = deliveryStationName;
	}

	public String getProvince() {
		return this.province;
	}

	public void setProvince(String province) {
		this.province = province;
	}

	public String getCity() {
		return this.city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getDistrict() {
		return this.district;
	}

	public void setDistrict(String district) {
		this.district = district;
	}

	public String getAddress1() {
		return this.address1;
	}

	public void setAddress1(String address1) {
		this.address1 = address1;
	}

	public String getAddress2() {
		return this.address2;
	}

	public void setAddress2(String address2) {
		this.address2 = address2;
	}

	public String getAddress3() {
		return this.address3;
	}

	public void setAddress3(String address3) {
		this.address3 = address3;
	}

	public String getDeliveryStationName() {
		return this.deliveryStationName;
	}

	public void setDeliveryStationName(String deliveryStationName) {
		this.deliveryStationName = deliveryStationName;
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

}