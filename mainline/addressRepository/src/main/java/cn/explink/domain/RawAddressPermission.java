package cn.explink.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "RAW_ADDRESS_PERMISSIONS")
public class RawAddressPermission {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Column(name = "RAW_ADDRESS_ID")
	private Long rawAddressId;

	@Column(name = "CUSTOMER_ID")
	private Long customerId;

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getRawAddressId() {
		return this.rawAddressId;
	}

	public void setRawAddressId(Long rawAddressId) {
		this.rawAddressId = rawAddressId;
	}

	public Long getCustomerId() {
		return this.customerId;
	}

	public void setCustomerId(Long customerId) {
		this.customerId = customerId;
	}

}