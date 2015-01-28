package cn.explink.domain;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
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
import javax.persistence.Transient;

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

	// added by songkaojun 2015-01-28 添加别名权重
	@Transient
	private List<Alias> aliasList;

	public Address() {
		super();
	}

	public Address(Long id, String name, Integer addressLevel, Long parentId, String path) {
		super();
		this.id = id;
		this.name = name;
		this.addressLevel = addressLevel;
		this.parentId = parentId;
		this.path = path;
	}

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getOldName() {
		return this.oldName;
	}

	public void setOldName(String oldName) {
		this.oldName = oldName;
	}

	public Integer getStatus() {
		return this.status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Integer getAddressTypeId() {
		return this.addressTypeId;
	}

	public void setAddressTypeId(Integer addressTypeId) {
		this.addressTypeId = addressTypeId;
	}

	public Integer getAddressLevel() {
		return this.addressLevel;
	}

	public void setAddressLevel(Integer addressLevel) {
		this.addressLevel = addressLevel;
	}

	public Long getParentId() {
		return this.parentId;
	}

	public void setParentId(Long parentId) {
		this.parentId = parentId;
	}

	public Date getCreationTime() {
		return this.creationTime;
	}

	public void setCreationTime(Date creationTime) {
		this.creationTime = creationTime;
	}

	public String getPath() {
		return this.path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public Boolean getIndexed() {
		return this.indexed;
	}

	public void setIndexed(Boolean indexed) {
		this.indexed = indexed;
	}

	public Set<DeliveryStationRule> getDeliveryStationRules() {
		return this.deliveryStationRules;
	}

	public void setDeliveryStationRules(Set<DeliveryStationRule> deliveryStationRules) {
		this.deliveryStationRules = deliveryStationRules;
	}

	public Set<DelivererRule> getDelivererRules() {
		return this.delivererRules;
	}

	public void setDelivererRules(Set<DelivererRule> delivererRules) {
		this.delivererRules = delivererRules;
	}

	public List<Alias> getAliasList() {
		return this.aliasList;
	}

	public void setAliasList(List<Alias> aliasList) {
		this.aliasList = aliasList;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Address [");
		if (this.id != null) {
			builder.append("id=").append(this.id).append(", ");
		}
		if (this.name != null) {
			builder.append("name=").append(this.name).append(", ");
		}
		if (this.oldName != null) {
			builder.append("oldName=").append(this.oldName).append(", ");
		}
		if (this.status != null) {
			builder.append("status=").append(this.status).append(", ");
		}
		if (this.addressTypeId != null) {
			builder.append("addressTypeId=").append(this.addressTypeId).append(", ");
		}
		if (this.addressLevel != null) {
			builder.append("addressLevel=").append(this.addressLevel).append(", ");
		}
		if (this.parentId != null) {
			builder.append("parentId=").append(this.parentId).append(", ");
		}
		if (this.path != null) {
			builder.append("path=").append(this.path).append(", ");
		}
		if (this.indexed != null) {
			builder.append("indexed=").append(this.indexed).append(", ");
		}
		if (this.creationTime != null) {
			builder.append("creationTime=").append(this.creationTime).append(", ");
		}
		if (this.deliveryStationRules != null) {
			builder.append("deliveryStationRules=").append(this.deliveryStationRules).append(", ");
		}
		if (this.delivererRules != null) {
			builder.append("delivererRules=").append(this.delivererRules);
		}
		if (this.aliasList != null) {
			builder.append("aliasList=").append(this.aliasList);
		}
		builder.append("]");
		return builder.toString();
	}

}