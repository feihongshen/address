package cn.explink.lucene;

import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import cn.explink.domain.Address;
import cn.explink.test.support.BaseTestCase;

public class ScoreFilterTest extends BaseTestCase {

	@Test
	public void testFilter() {
		ArrayList<Address> sourceAddressList = new ArrayList<Address>();
		List<Address> addressList = ScoreFilter.filter(sourceAddressList);
		assertNotNull(addressList);
	}
}
