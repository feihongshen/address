package cn.explink.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import cn.explink.dao.AddressDao;
import cn.explink.dao.AddressPermissionDao;
import cn.explink.dao.UserDao;
import cn.explink.domain.Address;
import cn.explink.domain.AddressPermission;
import cn.explink.domain.User;
import cn.explink.test.support.BaseTestCase;

public class AddressImportServiceTest extends BaseTestCase {

	private static final Long TEST_USER_ID = 1L;

	private static final Long TEST_CUSTOMER_ID = 1L;

	private static final String addressName1 = "健康南路";

	private static final String addressName2 = "京楼市场";

	private static final String addressName3 = "农机局家属院";

	@Autowired
	private AddressImportService addressImportService;

	@Autowired
	private UserDao userDao;

	@Autowired
	private AddressDao addressDao;

	@Autowired
	private AddressPermissionDao addressPermissionDao;

	@Test
	public void testImportAddress() {
		Address address1 = addressDao.getAddressByName(addressName1);
		Address address2 = addressDao.getAddressByName(addressName2);
		Address address3 = addressDao.getAddressByName(addressName3);
		assertNull("target address should be null before import", address1);
		assertNull("target address should be null before import", address2);
		assertNull("target address should be null before import", address3);

		URL url = this.getClass().getResource("addressImportTest.xlsx");
		try {
			InputStream in = url.openStream();
			User user = userDao.get(TEST_USER_ID);
		//	addressImportService.importAddress(in, user);
			address1 = addressDao.getAddressByName(addressName1);
			address2 = addressDao.getAddressByName(addressName2);
			address3 = addressDao.getAddressByName(addressName3);
			assertNotNull("imported address should be null", address1);
			assertNotNull("imported address should be null", address2);
			assertNotNull("imported address should be null", address3);
			assertEquals("address2 should be the child of address1", address1.getId(), address2.getParentId());
			assertEquals("address3 should be the child of address2", address2.getId(), address3.getParentId());

			AddressPermission permission = addressPermissionDao.getPermissionByAddressAndCustomer(address1.getId(), TEST_CUSTOMER_ID);
			assertNotNull("address should be bind to the created customer", permission);
			permission = addressPermissionDao.getPermissionByAddressAndCustomer(address2.getId(), TEST_CUSTOMER_ID);
			assertNotNull("address should be bind to the created customer", permission);
			permission = addressPermissionDao.getPermissionByAddressAndCustomer(address3.getId(), TEST_CUSTOMER_ID);
			assertNotNull("address should be bind to the created customer", permission);
		} catch (IOException e) {
			fail("testImportAddress failed due to " + e.getMessage());
		}
	}

}
