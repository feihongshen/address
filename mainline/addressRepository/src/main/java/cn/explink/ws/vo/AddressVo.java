package cn.explink.ws.vo;

import java.util.List;

public class AddressVo {

	private Long id;

	private String name;

	private Integer addressTypeId;

	private Integer addressLevel;

	private Long parentId;

	private String path;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getAddressTypeId() {
		return addressTypeId;
	}

	public void setAddressTypeId(Integer addressTypeId) {
		this.addressTypeId = addressTypeId;
	}

	public Integer getAddressLevel() {
		return addressLevel;
	}

	public void setAddressLevel(Integer addressLevel) {
		this.addressLevel = addressLevel;
	}

	public Long getParentId() {
		return parentId;
	}

	public void setParentId(Long parentId) {
		this.parentId = parentId;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

}