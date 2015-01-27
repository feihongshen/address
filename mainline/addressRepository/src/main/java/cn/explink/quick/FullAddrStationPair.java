package cn.explink.quick;

import java.util.List;

import cn.explink.domain.Address;
import cn.explink.domain.DeliveryStation;

/**
 * 快捷维护工具.
 *
 * @author zhaoshb
 * @since AR1.0
 */
public class FullAddrStationPair {

	private List<Address> addrList = null;

	private DeliveryStation delStat = null;

	public List<Address> getAddrList() {
		return this.addrList;
	}

	public void setAddrList(List<Address> addrList) {
		this.addrList = addrList;
	}

	public DeliveryStation getDelStat() {
		return this.delStat;
	}

	public void setDelStat(DeliveryStation delStat) {
		this.delStat = delStat;
	}

}
