package cn.explink.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "ADDRESS_DETAIL")
public class AddressDetail implements Cloneable {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	private String province;
	private String city;
	private String district;
	@Column(name = "ADDRESS_ID1")
	private Long addressId1;
	@Column(name = "ADDRESS_NAME1")
	private String addressName1;
	@Column(name = "ADDRESS_ID2")
	private Long addressId2;
	@Column(name = "ADDRESS_NAME2")
	private String addressName2;
	@Column(name = "ADDRESS_ID3")
	private Long addressId3;
	@Column(name = "ADDRESS_NAME3")
	private String addressName3;
	@Column(name = "DELIVERY_STATION_NAME")
	private String deliveryStationName;
	@Column(name = "CUSTOMER_ID")
	private Long customerId;

	public AddressDetail() {
		super();
	}

	public AddressDetail(String province, String city, String district, String addressName1, String addressName2, String addressName3, String deliveryStationName) {
		super();
		this.province = province;
		this.city = city;
		this.district = district;
		this.addressName1 = addressName1;
		this.addressName2 = addressName2;
		this.addressName3 = addressName3;
		this.deliveryStationName = deliveryStationName;
	}

	public AddressDetail(Long id, String province, String city, String district, Long addressId1, String addressName1, Long addressId2, String addressName2, Long addressId3, String addressName3,
			String deliveryStationName, Long customerId) {
		super();
		this.id = id;
		this.province = province;
		this.city = city;
		this.district = district;
		this.addressId1 = addressId1;
		this.addressName1 = addressName1;
		this.addressId2 = addressId2;
		this.addressName2 = addressName2;
		this.addressId3 = addressId3;
		this.addressName3 = addressName3;
		this.deliveryStationName = deliveryStationName;
		this.customerId = customerId;
	}

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
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

	public Long getAddressId1() {
		return this.addressId1;
	}

	public void setAddressId1(Long addressId1) {
		this.addressId1 = addressId1;
	}

	public String getAddressName1() {
		return this.addressName1;
	}

	public void setAddressName1(String addressName1) {
		this.addressName1 = addressName1;
	}

	public Long getAddressId2() {
		return this.addressId2;
	}

	public void setAddressId2(Long addressId2) {
		this.addressId2 = addressId2;
	}

	public String getAddressName2() {
		return this.addressName2;
	}

	public void setAddressName2(String addressName2) {
		this.addressName2 = addressName2;
	}

	public Long getAddressId3() {
		return this.addressId3;
	}

	public void setAddressId3(Long addressId3) {
		this.addressId3 = addressId3;
	}

	public String getAddressName3() {
		return this.addressName3;
	}

	public void setAddressName3(String addressName3) {
		this.addressName3 = addressName3;
	}

	public String getDeliveryStationName() {
		return this.deliveryStationName;
	}

	public void setDeliveryStationName(String deliveryStationName) {
		this.deliveryStationName = deliveryStationName;
	}

	public Long getCustomerId() {
		return this.customerId;
	}

	public void setCustomerId(Long customerId) {
		this.customerId = customerId;
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

}