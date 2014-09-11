package cn.explink.ws.vo;

import java.util.Map;

public class AddressMappingResult {

	private ResultCodeEnum resultCode;

	private String message;

	private Map<String, OrderAddressMappingResult> resultMap;

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

	public Map<String, OrderAddressMappingResult> getResultMap() {
		return resultMap;
	}

	public void setResultMap(Map<String, OrderAddressMappingResult> resultMap) {
		this.resultMap = resultMap;
	}

}
