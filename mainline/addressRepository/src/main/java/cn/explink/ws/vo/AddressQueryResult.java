package cn.explink.ws.vo;

import java.util.List;

public class AddressQueryResult {

	private ResultCodeEnum resultCode;

	private String message;

	private List<DelivererRuleVo> delivererRuleVoList;

	private List<AddressVo> addressVoList;

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

	public List<AddressVo> getAddressVoList() {
		return addressVoList;
	}

	public void setAddressVoList(List<AddressVo> addressVoList) {
		this.addressVoList = addressVoList;
	}

	public List<DelivererRuleVo> getDelivererRuleVoList() {
		return delivererRuleVoList;
	}

	public void setDelivererRuleVoList(List<DelivererRuleVo> delivererRuleVoList) {
		this.delivererRuleVoList = delivererRuleVoList;
	}

}
