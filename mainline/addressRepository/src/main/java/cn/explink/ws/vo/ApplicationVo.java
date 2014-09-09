package cn.explink.ws.vo;

public class ApplicationVo {

	private Long id;

	private String password;

	private Long customerId;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Long getCustomerId() {
		return customerId;
	}

	public void setCustomerId(Long customerId) {
		this.customerId = customerId;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ApplicationVo [");
		if (id != null)
			builder.append("id=").append(id).append(", ").append("customerId=").append(customerId).append(",");
		if (password != null)
			builder.append("password=").append(password);
		builder.append("]");
		return builder.toString();
	}

}
