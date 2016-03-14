package cn.explink.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "USERS")
public class User implements Serializable {

	private static final long serialVersionUID = -5613879653685264592L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Column(name = "NAME", length = 20, nullable = false)
	private String name;

	@Column(name = "PASSWORD", length = 20)
	private String password;

	@Column(name = "STATUS", nullable = false)
	private Integer status;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "CUSTOMER_ID")
	private Customer customer;

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

	public String getPassword() {
		return this.password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Integer getStatus() {
		return this.status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Customer getCustomer() {
		return this.customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("User [");
		if (this.id != null) {
			builder.append("id=").append(this.id).append(", ");
		}
		if (this.name != null) {
			builder.append("name=").append(this.name).append(", ");
		}
		if (this.password != null) {
			builder.append("password=").append(this.password).append(", ");
		}
		if (this.status != null) {
			builder.append("status=").append(this.status).append(", ");
		}
		if (this.customer != null) {
			builder.append("customer=").append(this.customer);
		}
		builder.append("]");
		return builder.toString();
	}

}