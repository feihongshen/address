package cn.explink.web.vo;

import java.util.List;

import cn.explink.domain.Address;
import cn.explink.domain.Deliverer;
import cn.explink.domain.DeliveryStation;
import cn.explink.ws.vo.AddressMappingResultEnum;

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
	private List<Integer> timeLimitList;

	public AddressMappingResultEnum getResult() {
		return this.result;
	}

	public void setResult(AddressMappingResultEnum result) {
		this.result = result;
	}

	public String getMessage() {
		return this.message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public List<Address> getRelatedAddressList() {
		return this.relatedAddressList;
	}

	public void setRelatedAddressList(List<Address> relatedAddressList) {
		this.relatedAddressList = relatedAddressList;
	}

	public List<DeliveryStation> getDeliveryStationList() {
		return this.deliveryStationList;
	}

	public void setDeliveryStationList(List<DeliveryStation> deliveryStationList) {
		this.deliveryStationList = deliveryStationList;
	}

	public List<Deliverer> getDelivererList() {
		return this.delivererList;
	}

	public void setDelivererList(List<Deliverer> delivererList) {
		this.delivererList = delivererList;
	}

	public List<Integer> getTimeLimitList() {
		return this.timeLimitList;
	}

	public void setTimeLimitList(List<Integer> timeLimitList) {
		this.timeLimitList = timeLimitList;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("SingleAddressMappingResult [");
		if (this.result != null) {
			builder.append("result=").append(this.result).append(", ");
		}
		if (this.message != null) {
			builder.append("message=").append(this.message).append(", ");
		}
		if (this.relatedAddressList != null) {
			builder.append("relatedAddressList=").append(this.relatedAddressList).append(", ");
		}
		if (this.deliveryStationList != null) {
			builder.append("deliveryStationList=").append(this.deliveryStationList).append(", ");
		}
		if (this.delivererList != null) {
			builder.append("delivererList=").append(this.delivererList).append(", ");
		}
		if (this.timeLimitList != null) {
			builder.append("timeLimitList=").append(this.timeLimitList);
		}
		builder.append("]");
		return builder.toString();
	}

}
