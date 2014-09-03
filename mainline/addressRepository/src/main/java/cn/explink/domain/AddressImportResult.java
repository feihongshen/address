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
import javax.persistence.Transient;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import cn.explink.annocation.Excel;

@Entity
@Table(name = "ADDRESS_IMPORT_RESULTS")
@JsonIgnoreProperties(value = { "addressImportDetails" })  
public class AddressImportResult {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Excel(exportName="用户")
	@Column(name = "USER_ID")
	private Long userId;
	
	private String userName;
	@Transient
	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	@Excel(exportName="导入关键词数量")
	@Column(name = "SUCCESS_COUNT")
	private Integer successCount;

	@Column(name = "FAILURE_COUNT")
	private Integer failureCount;
	@Excel(exportName="导入日期")
	@Column(name = "IMPORT_DATE")
	private Date importDate;
	


	@JsonIgnore
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "addressImportResult")
	private Set<AddressImportDetail> addressImportDetails = new HashSet<AddressImportDetail>();

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Integer getSuccessCount() {
		return successCount;
	}

	public void setSuccessCount(Integer successCount) {
		this.successCount = successCount;
	}

	public Integer getFailureCount() {
		return failureCount;
	}

	public void setFailureCount(Integer failureCount) {
		this.failureCount = failureCount;
	}

	public Date getImportDate() {
		return importDate;
	}

	public void setImportDate(Date importDate) {
		this.importDate = importDate;
	}

	public Set<AddressImportDetail> getAddressImportDetails() {
		return addressImportDetails;
	}

	public void setAddressImportDetails(Set<AddressImportDetail> sddressImportDetails) {
		this.addressImportDetails = sddressImportDetails;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("AddressImportResult [");
		if (id != null)
			builder.append("id=").append(id).append(", ");
		if (userId != null)
			builder.append("userId=").append(userId).append(", ");
		if (successCount != null)
			builder.append("successCount=").append(successCount).append(", ");
		if (failureCount != null)
			builder.append("failureCount=").append(failureCount).append(", ");
		if (importDate != null)
			builder.append("importDate=").append(importDate).append(", ");
		if (addressImportDetails != null)
			builder.append("addressImportDetails=").append(addressImportDetails);
		builder.append("]");
		return builder.toString();
	}

}