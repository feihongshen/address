package cn.explink.ws.vo;

public class VendorVo {

	private String name;

	private Long externalId;

	private Long customerId;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Long getExternalId() {
		return externalId;
	}

	public void setExternalId(Long externalVendorId) {
		this.externalId = externalVendorId;
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
		builder.append("VendorVo [");
		if (name != null)
			builder.append("name=").append(name).append(", ");
		if (externalId != null)
			builder.append("externalId=").append(externalId).append(", ");
		if (customerId != null)
			builder.append("customerId=").append(customerId);
		builder.append("]");
		return builder.toString();
	}

}
