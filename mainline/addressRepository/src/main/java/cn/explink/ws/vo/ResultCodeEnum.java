package cn.explink.ws.vo;

public enum ResultCodeEnum {

	success(0), failure(1), recieved(2);

	private int code;

	private ResultCodeEnum(int code) {
		this.code = code;
	}

	public int getCode() {
		return code;
	}

}
