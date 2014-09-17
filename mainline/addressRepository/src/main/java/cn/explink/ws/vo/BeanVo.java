package cn.explink.ws.vo;

public class BeanVo {
	private String key;
	public BeanVo(Long key, String val) {
		super();
		this.key = String.valueOf(key);
		this.val = val;
	}
	public BeanVo() {
	}
	private String val;
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public String getVal() {
		return val;
	}
	public void setVal(String val) {
		this.val = val;
	}
	

}
