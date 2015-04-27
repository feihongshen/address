package cn.explink.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "KEYWORD_SUFFIX")
public class KeywordSuffix {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Column(name = "CUSTOMER_ID")
	private Long customerId;

	@Column(name = "NAME", length = 50, nullable = false)
	private String name;

	public KeywordSuffix() {
		super();
	}

	public KeywordSuffix(Long id, Long customerId, String name) {
		super();
		this.id = id;
		this.customerId = customerId;
		this.name = name;
	}

	public KeywordSuffix(String name) {
		super();
		this.name = name;
	}

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getCustomerId() {
		return this.customerId;
	}

	public void setCustomerId(Long customerId) {
		this.customerId = customerId;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

}