package cn.explink.lucene;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import cn.explink.domain.Address;
import cn.explink.util.AddressUtil;

public class ScoreFilter {

	/**
	 * 从索引匹配的地址中过滤出符合条件的地址
	 * 1. 按地址父子关系，从源地址list里排列成行
	 * 2. 没一行中省市区和关键字至少各匹配上一个
	 * 3. 每一行中，选择最末一级地址返回
	 * 
	 * @param sourceAddressList
	 * @return
	 */
	public static List<Address> filter(List<Address> sourceAddressList) {
		List<Address> addressList = new ArrayList<Address>();
		Map<Address, Score> addressScoreMap = ScoreFilter.calculateScore(sourceAddressList);
		for (Address address : addressScoreMap.keySet()) {
			Score score = addressScoreMap.get(address);
			if (score.districtCount > 0 && score.keyCount > 0) {
				addressList.add(address);
			}
		}
		return addressList;
	}

	/**
	 * 根据索引匹配上的地址列表，计算每个地址的得分
	 * 
	 * @param addressList
	 * @return
	 */
	private static Map<Address, Score> calculateScore(List<Address> addressList) {
		Map<Address, Score> mappingAddressMap = new ConcurrentHashMap<Address, Score>();
		for (Address address : addressList) {
			boolean mergedResult = false;
			for (Address mappingAddress : mappingAddressMap.keySet()) {
				if (AddressUtil.contain(mappingAddress, address)) {
					addScore(address, mappingAddressMap.get(mappingAddress));
					mergedResult = true;
				} else if (AddressUtil.contain(address, mappingAddress)) {
					mappingAddressMap.put(address, mappingAddressMap.get(mappingAddress));
					mappingAddressMap.remove(mappingAddress);
					addScore(address, mappingAddressMap.get(address));
					mergedResult = true;
				}
			}
			if (!mergedResult) {
				Score score = mappingAddressMap.get(address);
				if (score == null) {
					score = new Score();
					mappingAddressMap.put(address, score);
				}
				addScore(address, score);
			}
		}
		return mappingAddressMap;
	}

	private static void addScore(Address address, Score score) {
		if (address.getAddressLevel() > 3) {
			score.keyCount++;
		} else {
			score.districtCount++;
		}
	}

}
