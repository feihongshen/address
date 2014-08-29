package cn.explink.ws.vo;

import java.util.List;

public class AddressMappingResult {

	private ResultCodeEnum resultCode;

	private String message;

	private List<SingleAddressMappingResult> singleResultList;

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

	public List<SingleAddressMappingResult> getSingleResultList() {
		return singleResultList;
	}

	public void setSingleResultList(List<SingleAddressMappingResult> singleResultList) {
		this.singleResultList = singleResultList;
	}

}
