package cn.explink.service;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import cn.explink.dao.AddressDao;
import cn.explink.dao.CustomerDao;
import cn.explink.dao.DelivererDao;
import cn.explink.domain.Address;
import cn.explink.domain.Customer;
import cn.explink.domain.Deliverer;
import cn.explink.domain.DelivererRule;
import cn.explink.domain.enums.CustomerStausEnmu;
import cn.explink.test.support.BaseTestCase;
import cn.explink.ws.vo.DelivererVo;
import cn.explink.ws.vo.OrderVo;

public class DelivererRuleServiceTest extends BaseTestCase {

	private static final String UPDATED_NAME = "updatedName";

	private static final String TEST_NAME = "testName";

	private static final Long TEST_EXTERNAL_ID = 1234567L;

	@Autowired
	private DelivererService delivererService;
	
	@Autowired
	private DelivererRuleService delivererRuleService;

	@Autowired
	private DelivererDao delivererDao;
	
	@Autowired
	private CustomerDao customerDao;
	
	@Autowired
	private AddressDao addressDao;


	// 北京市的id
	private Long addressId = 2L;

	@Test
	public void testCreateDelivererRule() {
		Customer customer = prepareTestCustomer();
		DelivererVo delivererVo = prepareDeliverer(customer);
		Deliverer deliverer = delivererService.createDeliverer(delivererVo);
		
		String rule = null;
		DelivererRule fallbackRule = delivererRuleService.createDelivererRule(addressId, deliverer.getId(), customer.getId(), rule);
		assertNotNull("create fallbackRule should be success", fallbackRule);
		
		// create the same fallbackRule should be failed
		Exception exception = null;
		try {
			fallbackRule = delivererRuleService.createDelivererRule(addressId, deliverer.getId(), customer.getId(), rule);
			fail("should not be here");
		} catch (Exception e) {
			exception = e;
		}
		assertNotNull("the excepted exception should not be null", exception);
		
		rule = "100|号";
		DelivererRule simpleRule = delivererRuleService.createDelivererRule(addressId, deliverer.getId(), customer.getId(), rule);
		assertNotNull("create simpleRule should be success", simpleRule);
		
		rule = "11-21单|号";
		simpleRule = delivererRuleService.createDelivererRule(addressId, deliverer.getId(), customer.getId(), rule);
		assertNotNull("create simpleRule should be success", simpleRule);
		
		rule = "10-30双|号";
		simpleRule = delivererRuleService.createDelivererRule(addressId, deliverer.getId(), customer.getId(), rule);
		assertNotNull("create simpleRule should be success", simpleRule);
		
		exception = null;
		try {
			rule = "13-19单|号";
			fallbackRule = delivererRuleService.createDelivererRule(addressId, deliverer.getId(), customer.getId(), rule);
			fail("should not be here");
		} catch (Exception e) {
			exception = e;
		}
		assertNotNull("the excepted exception should not be null", exception);
		
		exception = null;
		try {
			rule = "3-11单|号";
			fallbackRule = delivererRuleService.createDelivererRule(addressId, deliverer.getId(), customer.getId(), rule);
			fail("should not be here");
		} catch (Exception e) {
			exception = e;
		}
		assertNotNull("the excepted exception should not be null", exception);
		
		exception = null;
		try {
			rule = "1-11单|号";
			fallbackRule = delivererRuleService.createDelivererRule(addressId, deliverer.getId(), customer.getId(), rule);
			fail("should not be here");
		} catch (Exception e) {
			exception = e;
		}
		assertNotNull("the excepted exception should not be null", exception);
		
//		TODO 解决边缘冲突问题, "10-20单|号"与"20-30单|号"并不冲突;
//		TODO 解决非同类型冲突问题，"10-20单|号"与"15|号"冲突;
	}
	
	@Test
	public void testSearchDelivererRule() {
		Customer customer = prepareTestCustomer();
		DelivererVo delivererVo = prepareDeliverer(customer);
		Deliverer deliverer = delivererService.createDeliverer(delivererVo);
		
		String rule = null;
		DelivererRule simpleRule = null;
//		DelivererRule fallbackRule = delivererRuleService.createDelivererRule(addressId, deliverer.getId(), customer.getId(), rule);
//		assertNotNull("create fallbackRule should be success", fallbackRule);
//		
//		rule = "100|号";
//		simpleRule = delivererRuleService.createDelivererRule(addressId, deliverer.getId(), customer.getId(), rule);
//		assertNotNull("create simpleRule should be success", simpleRule);
		
		rule = "11-21单|号";
		simpleRule = delivererRuleService.createDelivererRule(addressId, deliverer.getId(), customer.getId(), rule);
		assertNotNull("create simpleRule should be success", simpleRule);
		
//		rule = "10-30双|号";
//		simpleRule = delivererRuleService.createDelivererRule(addressId, deliverer.getId(), customer.getId(), rule);
//		assertNotNull("create simpleRule should be success", simpleRule);
		
		List<Address> addressList = new ArrayList<Address>();
		Address address = addressDao.get(addressId);
		addressList.add(address);
		
		OrderVo orderVo = new OrderVo();
		String addressLine = "北京市15号";
		orderVo.setAddressLine(addressLine);
		List<DelivererRule> ruleList = delivererRuleService.search(addressList, orderVo);
		assertNotNull("ruleList should not be null", ruleList);
		assertEquals("only one rule mapped", 1, ruleList.size());
		assertEquals("应当匹配到正确的规则", "11-21单|号", ruleList.get(0).getRule());
	}

	private DelivererVo prepareDeliverer(Customer customer) {
		DelivererVo deliverer = new DelivererVo();
		deliverer.setCustomerId(customer.getId());
		deliverer.setExternalId(TEST_EXTERNAL_ID);
		deliverer.setName(TEST_NAME);
		return deliverer;
	}

	private Customer prepareTestCustomer() {
		Customer customer = new Customer();
		customer.setName("unit test customer");
		customer.setStatus(CustomerStausEnmu.valid.getValue());
		customerDao.save(customer);
		assertNotNull("customerId should not be null", customer.getId());
		return customer;
	}
}
