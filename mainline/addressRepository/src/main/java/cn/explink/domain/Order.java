package cn.explink.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "ADDRESS_ORDER")
public class Order {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Column(name = "EXTERNAL_ORDER_ID", nullable = false)
	private String externalOrderId;

	@Column(name = "CUSTOMER_ID", nullable = false)
	private Long customerId;

	@Column(name = "ADDRESS_LINE", length = 200, nullable = false)
	private String addressLine;

	@Column(name = "DELIVERY_STATION_IDS", length = 100, nullable = false)
	private String deliveryStationIds;

	@Column(name = "DELIVERER_IDS", length = 100, nullable = false)
	private String delivererIds;

	@Column(name = "CREATION_DATE")
	private Date creationDate;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getExternalOrderId() {
		return externalOrderId;
	}

	public void setExternalOrderId(String externalOrderId) {
		this.externalOrderId = externalOrderId;
	}

	public Long getCustomerId() {
		return customerId;
	}

	public void setCustomerId(Long customerId) {
		this.customerId = customerId;
	}

	public String getAddressLine() {
		return addressLine;
	}

	public void setAddressLine(String addressLine) {
		this.addressLine = addressLine;
	}

	public String getDeliveryStationIds() {
		return deliveryStationIds;
	}

	public void setDeliveryStationIds(String deliveryStationIds) {
		this.deliveryStationIds = deliveryStationIds;
	}

	public String getDelivererIds() {
		return delivererIds;
	}

	public void setDelivererIds(String delivererIds) {
		this.delivererIds = delivererIds;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

}