package cn.explink.domain;

import java.math.BigDecimal;
import java.util.Date;

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

@Entity
@Table(name = "DELIVERY_STATIONS")
@JsonIgnoreProperties(value = { "customer" })
public class DeliveryStation {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Column(name = "NAME", length = 50, nullable = false)
	private String name;

	@Column(name = "COORDINATE")
	private String coordinate;

	@Column(name = "MAPCENTER_LNG")
	private BigDecimal mapcenterLng;

	@Column(name = "MAPCENTER_LAT")
	private BigDecimal mapcenterLat;

	@Column(name = "UID")
	private String uid;

	@Column(name = "STATUS", nullable = false)
	private Integer status;

	@Column(name = "EXTERNAL_ID", nullable = false)
	private Long externalId;
	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "CUSTOMER_ID")
	private Customer customer;

	@Column(name = "CREATION_TIME")
	private Date creationTime;

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

	public String getCoordinate() {
		return this.coordinate;
	}

	public void setCoordinate(String coordinate) {
		this.coordinate = coordinate;
	}

	public BigDecimal getMapcenterLng() {
		return this.mapcenterLng;
	}

	public void setMapcenterLng(BigDecimal mapcenterLng) {
		this.mapcenterLng = mapcenterLng;
	}

	public BigDecimal getMapcenterLat() {
		return this.mapcenterLat;
	}

	public void setMapcenterLat(BigDecimal mapcenterLat) {
		this.mapcenterLat = mapcenterLat;
	}

	public String getUid() {
		return this.uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public Integer getStatus() {
		return this.status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Long getExternalId() {
		return this.externalId;
	}

	public void setExternalId(Long externalId) {
		this.externalId = externalId;
	}

	public Customer getCustomer() {
		return this.customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	public Date getCreationTime() {
		return this.creationTime;
	}

	public void setCreationTime(Date creationTime) {
		this.creationTime = creationTime;
	}

	@Override
	public boolean equals(Object o) {
		if (o == null) {
			return false;
		}
		if (this == o) {
			return true;
		}
		if (this.getClass() != o.getClass()) {
			return false;
		}
		DeliveryStation oStat = (DeliveryStation) o;

		return this.getId().equals(oStat.getId());
	}

	@Override
	public int hashCode() {
		if (this.getId() == null) {
			return super.hashCode();
		}
		return this.getId().hashCode();
	}
}