package cn.explink.domain;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "ADDRESS")
public class Address {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Column(name = "NAME", length = 50, nullable = false)
	private String name;

	@Column(name = "OLD_NAME", length = 50, nullable = false)
	private String oldName;

	@Column(name = "STATUS", nullable = false)
	private Integer status;

	@Column(name = "ADDRESS_TYPE_ID")
	private Integer addressTypeId;

	@Column(name = "ADDRESS_LEVEL", nullable = false)
	private Integer addressLevel;

	@Column(name = "PARENT_ID")
	private Long parentId;

	@Column(name = "PATH")
	private String path;

	@Column(name = "INDEXED")
	private Boolean indexed;

	@Column(name = "CREATION_TIME")
	private Date creationTime;

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "address")
	private Set<DeliveryStationRule> deliveryStationRules = new HashSet<DeliveryStationRule>();

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "address")
	private Set<DelivererRule> delivererRules = new HashSet<DelivererRule>();
	
	public Address() {
		super();
	}

	public Address(Long id, String name, Integer addressLevel, Long parentId,
			String path) {
		super();
		this.id = id;
		this.name = name;
		this.addressLevel = addressLevel;
		this.parentId = parentId;
		this.path = path;
	}


	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getOldName() {
		return oldName;
	}

	public void setOldName(String oldName) {
		this.oldName = oldName;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Integer getAddressTypeId() {
		return addressTypeId;
	}

	public void setAddressTypeId(Integer addressTypeId) {
		this.addressTypeId = addressTypeId;
	}

	public Integer getAddressLevel() {
		return addressLevel;
	}

	public void setAddressLevel(Integer addressLevel) {
		this.addressLevel = addressLevel;
	}

	public Long getParentId() {
		return parentId;
	}

	public void setParentId(Long parentId) {
		this.parentId = parentId;
	}

	public Date getCreationTime() {
		return creationTime;
	}

	public void setCreationTime(Date creationTime) {
		this.creationTime = creationTime;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public Boolean getIndexed() {
		return indexed;
	}

	public void setIndexed(Boolean indexed) {
		this.indexed = indexed;
	}

	public Set<DeliveryStationRule> getDeliveryStationRules() {
		return deliveryStationRules;
	}

	public void setDeliveryStationRules(Set<DeliveryStationRule> deliveryStationRules) {
		this.deliveryStationRules = deliveryStationRules;
	}

	public Set<DelivererRule> getDelivererRules() {
		return delivererRules;
	}

	public void setDelivererRules(Set<DelivererRule> delivererRules) {
		this.delivererRules = delivererRules;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Address [");
		if (id != null)
			builder.append("id=").append(id).append(", ");
		if (name != null)
			builder.append("name=").append(name).append(", ");
		if (oldName != null)
			builder.append("oldName=").append(oldName).append(", ");
		if (status != null)
			builder.append("status=").append(status).append(", ");
		if (addressTypeId != null)
			builder.append("addressTypeId=").append(addressTypeId).append(", ");
		if (addressLevel != null)
			builder.append("addressLevel=").append(addressLevel).append(", ");
		if (parentId != null)
			builder.append("parentId=").append(parentId).append(", ");
		if (path != null)
			builder.append("path=").append(path).append(", ");
		if (indexed != null)
			builder.append("indexed=").append(indexed).append(", ");
		if (creationTime != null)
			builder.append("creationTime=").append(creationTime).append(", ");
		if (deliveryStationRules != null)
			builder.append("deliveryStationRules=").append(deliveryStationRules).append(", ");
		if (delivererRules != null)
			builder.append("delivererRules=").append(delivererRules);
		builder.append("]");
		return builder.toString();
	}

}