package cn.explink.ws.vo;

import java.util.List;

import cn.explink.domain.Address;
import cn.explink.domain.Deliverer;
import cn.explink.domain.DeliveryStation;

public class SingleAddressMappingResult {

	private AddressMappingResultEnum result;

	private String message;

	private List<Address> relatedAddressList;

	/**
	 * 站点list
	 */
	private List<DeliveryStation> deliveryStationList;

	/**
	 * 配送员list
	 */
	private List<Deliverer> delivererList;

	/**
	 * 时效/时限
	 */
	private Integer timeLimit;

	public AddressMappingResultEnum getResult() {
		return result;
	}

	public void setResult(AddressMappingResultEnum result) {
		this.result = result;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public List<Address> getRelatedAddressList() {
		return relatedAddressList;
	}

	public void setRelatedAddressList(List<Address> relatedAddressList) {
		this.relatedAddressList = relatedAddressList;
	}

	public List<DeliveryStation> getDeliveryStationList() {
		return deliveryStationList;
	}

	public void setDeliveryStationList(List<DeliveryStation> deliveryStationList) {
		this.deliveryStationList = deliveryStationList;
	}

	public List<Deliverer> getDelivererList() {
		return delivererList;
	}

	public void setDelivererList(List<Deliverer> delivererList) {
		this.delivererList = delivererList;
	}

	public Integer getTimeLimit() {
		return timeLimit;
	}

	public void setTimeLimit(Integer timeLimit) {
		this.timeLimit = timeLimit;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("SingleAddressMappingResult [");
		if (result != null)
			builder.append("result=").append(result).append(", ");
		if (message != null)
			builder.append("message=").append(message).append(", ");
		if (relatedAddressList != null)
			builder.append("relatedAddressList=").append(relatedAddressList).append(", ");
		if (deliveryStationList != null)
			builder.append("deliveryStationList=").append(deliveryStationList).append(", ");
		if (delivererList != null)
			builder.append("delivererList=").append(delivererList).append(", ");
		if (timeLimit != null)
			builder.append("timeLimit=").append(timeLimit);
		builder.append("]");
		return builder.toString();
	}

}
