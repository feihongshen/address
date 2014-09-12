package cn.explink.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import cn.explink.annocation.Excel;

@Entity
@Table(name = "ADDRESS_IMPORT_DETAILS")

public class AddressImportDetail {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Column(name = "ADDRESS_ID")
	private Long addressId;

	
	
	@Excel(exportName="省")
	@Column(name = "PROVINCE")
	private String province;
	@Excel(exportName="市")
	@Column(name = "CITY")
	private String city;
	@Excel(exportName="区/县")
	@Column(name = "DISTRICT")
	private String district;
	@Excel(exportName="关键字")
	@Column(name = "ADDRESS1")
	private String address1;

	@Column(name = "ADDRESS2")
	private String address2;

	@Column(name = "ADDRESS3")
	private String address3;

	@Column(name = "DELIVERY_STATION_ID")
	private Long deliveryStationId;
	@Excel(exportName="站点")
	@Column(name = "DELIVERY_STATION_NAME")
	private String deliveryStationName;

	@Column(name = "DELIVERER_ID")
	private Long delivererId;
	@Excel(exportName="配送员")
	@Column(name = "DELIVERER_NAME")
	private String delivererName;
	@Excel(exportName="结果")
	@Column(name = "STATUS")
	private Integer status;
	@Excel(exportName="信息")
	@Column(name = "MESSAGE")
	private String message;
	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "RESULT_ID")
	private AddressImportResult addressImportResult;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getAddressId() {
		return addressId;
	}

	public void setAddressId(Long addressId) {
		this.addressId = addressId;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getProvince() {
		return province;
	}

	public void setProvince(String province) {
		this.province = province;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getDistrict() {
		return district;
	}

	public void setDistrict(String district) {
		this.district = district;
	}

	public String getAddress1() {
		return address1;
	}

	public void setAddress1(String address1) {
		this.address1 = address1;
	}

	public String getAddress2() {
		return address2;
	}

	public void setAddress2(String address2) {
		this.address2 = address2;
	}

	public String getAddress3() {
		return address3;
	}

	public void setAddress3(String address3) {
		this.address3 = address3;
	}

	public Long getDeliveryStationId() {
		return deliveryStationId;
	}

	public void setDeliveryStationId(Long deliveryStationId) {
		this.deliveryStationId = deliveryStationId;
	}

	public String getDeliveryStationName() {
		return deliveryStationName;
	}

	public void setDeliveryStationName(String deliveryStationName) {
		this.deliveryStationName = deliveryStationName;
	}

	public Long getDelivererId() {
		return delivererId;
	}

	public void setDelivererId(Long delivererId) {
		this.delivererId = delivererId;
	}

	public String getDelivererName() {
		return delivererName;
	}

	public void setDelivererName(String delivererName) {
		this.delivererName = delivererName;
	}

	public AddressImportResult getAddressImportResult() {
		return addressImportResult;
	}

	public void setAddressImportResult(AddressImportResult addressImportResult) {
		this.addressImportResult = addressImportResult;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("AddressImportDetail [");
		if (id != null)
			builder.append("id=").append(id).append(", ");
		if (addressId != null)
			builder.append("addressId=").append(addressId).append(", ");
		if (status != null)
			builder.append("status=").append(status).append(", ");
		if (message != null)
			builder.append("message=").append(message).append(", ");
		if (province != null)
			builder.append("province=").append(province).append(", ");
		if (city != null)
			builder.append("city=").append(city).append(", ");
		if (district != null)
			builder.append("district=").append(district).append(", ");
		if (address1 != null)
			builder.append("address1=").append(address1).append(", ");
		if (address2 != null)
			builder.append("address2=").append(address2).append(", ");
		if (address3 != null)
			builder.append("address3=").append(address3).append(", ");
		if (deliveryStationId != null)
			builder.append("deliveryStationId=").append(deliveryStationId).append(", ");
		if (deliveryStationName != null)
			builder.append("deliveryStationName=").append(deliveryStationName).append(", ");
		if (delivererId != null)
			builder.append("delivererId=").append(delivererId).append(", ");
		if (delivererName != null)
			builder.append("delivererName=").append(delivererName).append(", ");
		if (addressImportResult != null)
			builder.append("addressImportResult=").append(addressImportResult);
		builder.append("]");
		return builder.toString();
	}

}