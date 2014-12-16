package cn.explink.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.beans.BeanUtils;

import cn.explink.domain.Address;
import cn.explink.domain.DelivererRule;
import cn.explink.ws.vo.AddressVo;
import cn.explink.ws.vo.DelivererRuleVo;

public class AddressUtil {

	/**
	 * 判断two是否one路径上的一个节点 即one是two的子节点或孙子节点等
	 *
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

	public static List<AddressVo> cloneToAddressVoList(List<Address> addressList) {
		if (addressList == null) {
			return null;
		}
		List<AddressVo> addressVoList = new ArrayList<AddressVo>(addressList.size());
		for (Address address : addressList) {
			AddressVo addressVo = AddressUtil.cloneToAddressVo(address);
			addressVoList.add(addressVo);
		}
		return addressVoList;
	}

	public static AddressVo cloneToAddressVo(Address address) {
		if (address == null) {
			return null;
		}
		AddressVo addressVo = new AddressVo();
		BeanUtils.copyProperties(address, addressVo);
		return addressVo;
	}

	public static List<DelivererRuleVo> cloneToDelivererRuleList(List<DelivererRule> delivererRuleList) {
		if (delivererRuleList == null) {
			return null;
		}
		List<DelivererRuleVo> delivererRuleVoList = new ArrayList<DelivererRuleVo>(delivererRuleList.size());
		for (DelivererRule delivererRule : delivererRuleList) {
			DelivererRuleVo delivererRuleVo = AddressUtil.cloneToDelivererRuleVo(delivererRule);
			delivererRuleVoList.add(delivererRuleVo);
		}
		return delivererRuleVoList;
	}

	public static DelivererRuleVo cloneToDelivererRuleVo(DelivererRule delivererRule) {
		if (delivererRule == null) {
			return null;
		}
		DelivererRuleVo delivererRuleVo = new DelivererRuleVo();
		BeanUtils.copyProperties(delivererRule, delivererRuleVo);
		delivererRuleVo.setDelivererId(delivererRule.getDeliverer().getExternalId());
		delivererRuleVo.setRuleId(delivererRule.getId());
		return delivererRuleVo;
	}

	public static <T extends Number> String getInPara(Collection<T> collection) {
		if ((collection == null) || collection.isEmpty()) {
			return null;
		}
		StringBuilder str = new StringBuilder();
		for (T t : collection) {
			if (t == null) {
				continue;
			}
			str.append(t.toString());
			str.append(" , ");
		}
		return str.substring(0, str.length() - 2);
	}
}
