package cn.explink.spliter.vo;

import java.util.List;

import cn.explink.domain.RawDeliveryStation;

/**
 *
 * @author songkaojun 2015年4月3日
 */
public class FullRawAddressStationPair {

	private List<RawAddressQuickVO> addrList;

	private RawDeliveryStation rawDeliveryStation;

	public List<RawAddressQuickVO> getAddrList() {
		return this.addrList;
	}

	public void setAddrList(List<RawAddressQuickVO> addrList) {
		this.addrList = addrList;
	}

	public RawDeliveryStation getRawDeliveryStation() {
		return this.rawDeliveryStation;
	}

	public void setRawDeliveryStation(RawDeliveryStation rawDeliveryStation) {
		this.rawDeliveryStation = rawDeliveryStation;
	}

}
