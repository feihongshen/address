package cn.explink.util;

import cn.explink.domain.Address;


public class AddressUtil {
	
	/**
	 * 判断two是否one路径上的一个节点
	 * 即one是two的子节点或孙子节点等
	 * @param one
	 * @param two
	 * @return
	 */
	public static boolean contain(Address one, Address two) {
		String[] paths = one.getPath().split("-");
		String id = two.getId() + "";
		for (String path : paths) {
			if (path.equals(id)) {
				return true;
			}
		}
		return false;
	}

}
