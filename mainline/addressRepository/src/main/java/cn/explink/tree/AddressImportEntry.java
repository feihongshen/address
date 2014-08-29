package cn.explink.tree;

import cn.explink.domain.Address;
import cn.explink.domain.AddressImportDetail;

public class AddressImportEntry {

	private Address address;

	private AddressImportDetail detail;

	public Address getAddress() {
		return address;
	}

	public void setAddress(Address address) {
		this.address = address;
	}

	public AddressImportDetail getDetail() {
		return detail;
	}

	public void setDetail(AddressImportDetail detail) {
		this.detail = detail;
	}

}
