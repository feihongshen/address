package cn.explink.web.vo;

public class VendorsAgingVo {

	private Integer id;
 	private String aging;
	private Long vendorId;
	private Long addressId;
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getAging() {
		return aging;
	}
	public void setAging(String aging) {
		this.aging = aging;
	}
	public Long getVendorId() {
		return vendorId;
	}
	public void setVendorId(Long vendorId) {
		this.vendorId = vendorId;
	}
	public Long getAddressId() {
		return addressId;
	}
	public void setAddressId(Long addressId) {
		this.addressId = addressId;
	}
	 
}