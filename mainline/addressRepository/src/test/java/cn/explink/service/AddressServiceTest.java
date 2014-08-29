package cn.explink.service;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.queryparser.classic.ParseException;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import cn.explink.domain.Address;
import cn.explink.domain.Alias;
import cn.explink.test.support.BaseTestCase;

public class AddressServiceTest extends BaseTestCase {

	@Autowired
	private AddressService addressService;
	
	private Alias testCreateAlias() {
		Alias alias = new Alias();
		alias.setAddressId(1L);
		alias.setName("testAlias");
		alias.setCustomerId(0L);
		addressService.createAlias(alias);
		assertNotNull("id should not be null", alias.getId());
		return alias;
	}
	
	@Test
	public void testGetAliasByIdList() {
		testCreateAlias();
		List<Long> alias = new ArrayList<Long>();
		alias.add(1L);
		alias.add(2L);
		List<Alias> aliasList = addressService.getAliasByIdList(alias);
		assertNotNull(aliasList);
	}
}
