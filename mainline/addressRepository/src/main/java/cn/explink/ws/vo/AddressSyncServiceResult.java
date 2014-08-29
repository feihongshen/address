package cn.explink.ws.vo;

public class AddressSyncServiceResult {

	private ResultCodeEnum resultCode;

	private String message;

	private Long referenceId;

	public ResultCodeEnum getResultCode() {
		return resultCode;
	}

	public void setResultCode(ResultCodeEnum resultCode) {
		this.resultCode = resultCode;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Long getReferenceId() {
		return referenceId;
	}

	public void setReferenceId(Long deliveryStationId) {
		this.referenceId = deliveryStationId;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("AddressSyncServiceResult [");
		if (resultCode != null)
			builder.append("resultCode=").append(resultCode).append(", ");
		if (message != null)
			builder.append("message=").append(message).append(", ");
		if (referenceId != null)
			builder.append("referenceId=").append(referenceId);
		builder.append("]");
		return builder.toString();
	}

}
