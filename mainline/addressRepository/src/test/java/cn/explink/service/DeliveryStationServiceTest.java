package cn.explink.service;

import static org.junit.Assert.*;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import cn.explink.dao.CustomerDao;
import cn.explink.dao.DeliveryStationDao;
import cn.explink.domain.Customer;
import cn.explink.domain.DeliveryStation;
import cn.explink.domain.enums.CustomerStausEnmu;
import cn.explink.domain.enums.DeliveryStationStausEnmu;
import cn.explink.test.support.BaseTestCase;
import cn.explink.ws.vo.DeliveryStationVo;

public class DeliveryStationServiceTest extends BaseTestCase {

	private static final String UPDATED_NAME = "updatedName";

	private static final String TEST_NAME = "testName";

	private static final Long TEST_EXTERNAL_ID = 1234567L;

	@Autowired
	private DeliveryStationService deliveryStationService;

	@Autowired
	private DeliveryStationDao deliveryStationDao;
	
	@Autowired
	private CustomerDao customerDao;

	@Test
	public void testCreateDeliveryStation() {
		Customer customer = prepareTestCustomer();
		DeliveryStationVo deliveryStationVo = prepareDeliveryStation(customer);
		DeliveryStation deliveryStation = deliveryStationService.createDeliveryStation(deliveryStationVo);
		assertNotNull("deliveryStation should not be null", deliveryStation);
		assertNotNull("deliveryStationId should not be null", deliveryStation.getId());
		assertEquals("the customerId should be same as prepared customerId", customer.getId(), deliveryStation.getCustomer().getId());
		assertEquals("the name should be same as prepared name", TEST_NAME, deliveryStation.getName());
	}

	@Test
	public void testUpdateDeliveryStation() {
		Customer customer = prepareTestCustomer();
		DeliveryStationVo deliveryStationVo = prepareDeliveryStation(customer);
		DeliveryStation deliveryStation = deliveryStationService.createDeliveryStation(deliveryStationVo);
		assertNotNull("deliveryStation should not be null", deliveryStation);
		assertNotNull("deliveryStationId should not be null", deliveryStation.getId());
		assertEquals("the customerId should be same as prepared customerId", customer.getId(), deliveryStation.getCustomer().getId());
		assertEquals("the name should be same as prepared name", TEST_NAME, deliveryStation.getName());
		
		deliveryStationVo.setName(UPDATED_NAME);
		deliveryStation = deliveryStationService.updateDeliveryStation(deliveryStationVo);
		assertNotNull("deliveryStation should not be null", deliveryStation);
		assertNotNull("deliveryStationId should not be null", deliveryStation.getId());
		assertEquals("the customerId should be same as prepared customerId", customer.getId(), deliveryStation.getCustomer().getId());
		assertEquals("the name should be updated", UPDATED_NAME, deliveryStation.getName());
		
	}

	@Test
	public void testDeleteDeliveryStation() {
		Customer customer = prepareTestCustomer();
		DeliveryStationVo deliveryStationVo = prepareDeliveryStation(customer);
		DeliveryStation deliveryStation = deliveryStationService.createDeliveryStation(deliveryStationVo);
		assertNotNull("deliveryStation should not be null", deliveryStation);
		assertNotNull("deliveryStationId should not be null", deliveryStation.getId());
		assertEquals("the customerId should be same as prepared customerId", customer.getId(), deliveryStation.getCustomer().getId());
		assertEquals("the name should be same as prepared name", TEST_NAME, deliveryStation.getName());
		
		deliveryStation = deliveryStationService.deleteDeliveryStation(deliveryStationVo);
		assertNotNull("deliveryStation should not be null", deliveryStation);
		deliveryStation = deliveryStationDao.get(deliveryStation.getId());
		assertEquals("the deliveryStation should be deleted", DeliveryStationStausEnmu.invalid.getValue(), deliveryStation.getStatus().intValue());
	}
	
	private DeliveryStationVo prepareDeliveryStation(Customer customer) {
		DeliveryStationVo deliveryStation = new DeliveryStationVo();
		deliveryStation.setCustomerId(customer.getId());
		deliveryStation.setExternalId(TEST_EXTERNAL_ID);
		deliveryStation.setName(TEST_NAME);
		return deliveryStation;
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
