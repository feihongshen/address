package cn.explink.lucene;

import java.util.ArrayList;
import java.util.List;

import cn.explink.domain.Address;

public class AddressFilter {
	/**
	 * 从索引匹配的地址中过滤出符合条件的地址 1. 每个地址和剩余地址匹配，判别1：是否在路径上 2：是否包含过省市区关键字 2.
	 * 返回：不再路径上，包含过省市区关键字的地址列表
	 *
	 * @param sourceAddressList
	 * @return
	 */
	public static List<Address> filter(List<Address> sourceAddressList) {
		if (sourceAddressList.isEmpty()) {
			return sourceAddressList;
		}
		List<Address> lvMtThrAddrList = AddressFilter.getLevelMoreThanThree(sourceAddressList);
		if (lvMtThrAddrList.isEmpty() || (lvMtThrAddrList.size() == 1)) {
			return lvMtThrAddrList;
		}
		AddressFilter.removePathAddr(lvMtThrAddrList);

		return lvMtThrAddrList;
	}

	private static void removePathAddr(List<Address> lvMtThrAddrList) {
		List<Address> removeList = new ArrayList<Address>();
		int size = lvMtThrAddrList.size();
		for (int i = 0; i < (size - 1); i++) {
			for (int j = i + 1; j < size; j++) {
				Address rmAddr = AddressFilter.getRemoveAddress(lvMtThrAddrList.get(i), lvMtThrAddrList.get(j));
				if (rmAddr == null) {
					continue;
				}
				removeList.add(rmAddr);
			}
		}
		lvMtThrAddrList.removeAll(removeList);
	}

	private static Address getRemoveAddress(Address addr1, Address addr2) {
		if (addr1.getPath().contains(addr2.getPath())) {
			return addr2;
		}
		if (addr2.getPath().contains(addr1.getPath())) {
			return addr1;
		}
		return null;
	}

	private static List<Address> getLevelMoreThanThree(List<Address> sourceAddressList) {
		List<Address> lvMtThrAddrList = new ArrayList<Address>();
		for (Address address : sourceAddressList) {
			if (address.getAddressLevel().intValue() < 3) {
				continue;
			}
			lvMtThrAddrList.add(address);
		}
		return lvMtThrAddrList;
	}

}
