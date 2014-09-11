package cn.explink.ws.vo;

import java.util.List;

import cn.explink.domain.Address;
import cn.explink.domain.Deliverer;
import cn.explink.domain.DeliveryStation;

public class OrderAddressMappingResult {

	private AddressMappingResultEnum result;

	private String message;

	private List<AddressVo> addressList;

	/**
	 * 站点list
	 */
	private List<DeliveryStationVo> deliveryStationList;

	/**
	 * 配送员list
	 */
	private List<DelivererVo> delivererList;

	/**
	 * 时效/时限
	 */
	private List<Integer> timeLimitList;

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

	public List<AddressVo> getAddressList() {
		return addressList;
	}

	public void setAddressList(List<AddressVo> addressList) {
		this.addressList = addressList;
	}

	public List<DeliveryStationVo> getDeliveryStationList() {
		return deliveryStationList;
	}

	public void setDeliveryStationList(List<DeliveryStationVo> deliveryStationList) {
		this.deliveryStationList = deliveryStationList;
	}

	public List<DelivererVo> getDelivererList() {
		return delivererList;
	}

	public void setDelivererList(List<DelivererVo> delivererList) {
		this.delivererList = delivererList;
	}

	public List<Integer> getTimeLimitList() {
		return timeLimitList;
	}

	public void setTimeLimitList(List<Integer> timeLimit) {
		this.timeLimitList = timeLimit;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("OrderAddressMappingResult [");
		if (result != null)
			builder.append("result=").append(result).append(", ");
		if (message != null)
			builder.append("message=").append(message).append(", ");
		if (addressList != null)
			builder.append("addressList=").append(addressList).append(", ");
		if (deliveryStationList != null)
			builder.append("deliveryStationList=").append(deliveryStationList).append(", ");
		if (delivererList != null)
			builder.append("delivererList=").append(delivererList).append(", ");
		if (timeLimitList != null)
			builder.append("timeLimitList=").append(timeLimitList);
		builder.append("]");
		return builder.toString();
	}

}
