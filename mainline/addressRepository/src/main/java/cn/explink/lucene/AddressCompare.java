package cn.explink.lucene;

import cn.explink.domain.Address;

public class AddressCompare{
	Address address;
	boolean isPath = false;
	boolean hasSsq = false;
	int score = 0;
	public AddressCompare(Address address) {
		this.address = address;
	}
}