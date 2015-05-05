package cn.explink.filter;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import cn.explink.service.RawAddressService;
import cn.explink.service.RawDeliveryStationService;
import cn.explink.test.support.BaseTestCase;

public class DmpAccessFilterTest extends BaseTestCase {
	@Autowired
	private RawAddressService rawAddressService;
	@Autowired
	private RawDeliveryStationService rawDeliveryStationService;

	@Override
	@Test
	public void test() {
	}
}
