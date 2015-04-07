package cn.explink.gis;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import cn.explink.service.RawAddressService;
import cn.explink.service.RawDeliveryStationService;
import cn.explink.spliter.AddressSplitter;
import cn.explink.spliter.vo.AddressDetail;
import cn.explink.spliter.vo.AddressStation;
import cn.explink.test.support.BaseTestCase;

public class AddressSplitterTest extends BaseTestCase {
	@Autowired
	private RawAddressService rawAddressService;
	@Autowired
	private RawDeliveryStationService rawDeliveryStationService;

	@Test
	public void testSplitAddressList() {
		List<AddressStation> addressStationList = new ArrayList<AddressStation>();
		AddressStation addressStation = new AddressStation("广西壮族自治区南宁市兴宁区广西南宁市花鸟市场A区瑶家寨恭城油茶", "兴宁站");
		addressStationList.add(addressStation);
		AddressSplitter addressSplitter = new AddressSplitter(addressStationList);
		List<AddressDetail> addressDetailList = addressSplitter.split();
		List<String> deliveryStationNameList = new ArrayList<String>();
		for (AddressDetail addressDetail : addressDetailList) {
			deliveryStationNameList.add(addressDetail.getDeliveryStationName());
		}
		this.rawDeliveryStationService.createDeliveryStation(deliveryStationNameList);
		// this.rawAddressService.importAddress(addressDetailList);
	}
}
