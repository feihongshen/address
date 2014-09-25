package cn.explink.modle;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * $.ajax后需要接受的JSON
 * 
 * @author
 * 
 */
public class AjaxJson<T> {

	private boolean success = true;// 是否成功
	private String msg = "操作成功";// 提示信息
	private String info="通过验证";
	private String status="y";
	private  List<T> list = new ArrayList<T> ();
	private Object obj = null;// 其他信息
	private Map<String, Object> attributes;// 其他参数
	public Map<String, Object> getAttributes() {
		return attributes;
	}

	public List<T> getList() {
		return list;
	}

	public void setList(List<T> list) {
		this.list = list;
	}

	public void setAttributes(Map<String, Object> attributes) {
		this.attributes = attributes;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public Object getObj() {
		return obj;
	}

	public void setObj(Object obj) {
		this.obj = obj;
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}
	 
	

}
