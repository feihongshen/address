package cn.explink.ws.vo;

public class DelivererVo {

	private Long externalId;

	private String name;

	private Long customerId;

	public Long getExternalId() {
		return externalId;
	}

	public void setExternalId(Long externalId) {
		this.externalId = externalId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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
		builder.append("DeliveryStationVo [");
		if (externalId != null)
			builder.append("externalId=").append(externalId).append(", ");
		if (name != null)
			builder.append("name=").append(name).append(", ");
		if (customerId != null)
			builder.append("customerId=").append(customerId);
		builder.append("]");
		return builder.toString();
	}

}
