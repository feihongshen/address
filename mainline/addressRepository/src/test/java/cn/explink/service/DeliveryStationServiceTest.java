package cn.explink.service;

import org.junit.Assert;
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
		Customer customer = this.prepareTestCustomer();
		DeliveryStationVo deliveryStationVo = this.prepareDeliveryStation(customer);
		DeliveryStation deliveryStation = this.deliveryStationService.createDeliveryStation(deliveryStationVo);
		Assert.assertNotNull("deliveryStation should not be null", deliveryStation);
		Assert.assertNotNull("deliveryStationId should not be null", deliveryStation.getId());
		Assert.assertEquals("the customerId should be same as prepared customerId", customer.getId(), deliveryStation.getCustomer().getId());
		Assert.assertEquals("the name should be same as prepared name", DeliveryStationServiceTest.TEST_NAME, deliveryStation.getName());
	}

	@Test
	public void testUpdateDeliveryStation() {
		Customer customer = this.prepareTestCustomer();
		DeliveryStationVo deliveryStationVo = this.prepareDeliveryStation(customer);
		DeliveryStation deliveryStation = this.deliveryStationService.createDeliveryStation(deliveryStationVo);
		Assert.assertNotNull("deliveryStation should not be null", deliveryStation);
		Assert.assertNotNull("deliveryStationId should not be null", deliveryStation.getId());
		Assert.assertEquals("the customerId should be same as prepared customerId", customer.getId(), deliveryStation.getCustomer().getId());
		Assert.assertEquals("the name should be same as prepared name", DeliveryStationServiceTest.TEST_NAME, deliveryStation.getName());

		deliveryStationVo.setName(DeliveryStationServiceTest.UPDATED_NAME);
		deliveryStation = this.deliveryStationService.updateDeliveryStation(deliveryStationVo);
		Assert.assertNotNull("deliveryStation should not be null", deliveryStation);
		Assert.assertNotNull("deliveryStationId should not be null", deliveryStation.getId());
		Assert.assertEquals("the customerId should be same as prepared customerId", customer.getId(), deliveryStation.getCustomer().getId());
		Assert.assertEquals("the name should be updated", DeliveryStationServiceTest.UPDATED_NAME, deliveryStation.getName());

	}

	@Test
	public void testDeleteDeliveryStation() {
		Customer customer = this.prepareTestCustomer();
		DeliveryStationVo deliveryStationVo = this.prepareDeliveryStation(customer);
		DeliveryStation deliveryStation = this.deliveryStationService.createDeliveryStation(deliveryStationVo);
		Assert.assertNotNull("deliveryStation should not be null", deliveryStation);
		Assert.assertNotNull("deliveryStationId should not be null", deliveryStation.getId());
		Assert.assertEquals("the customerId should be same as prepared customerId", customer.getId(), deliveryStation.getCustomer().getId());
		Assert.assertEquals("the name should be same as prepared name", DeliveryStationServiceTest.TEST_NAME, deliveryStation.getName());

		deliveryStation = this.deliveryStationService.deleteDeliveryStation(deliveryStationVo);
		Assert.assertNotNull("deliveryStation should not be null", deliveryStation);
		deliveryStation = this.deliveryStationDao.get(deliveryStation.getId());
		Assert.assertEquals("the deliveryStation should be deleted", DeliveryStationStausEnmu.invalid.getValue(), deliveryStation.getStatus().intValue());
	}

	private DeliveryStationVo prepareDeliveryStation(Customer customer) {
		DeliveryStationVo deliveryStation = new DeliveryStationVo();
		deliveryStation.setCustomerId(customer.getId());
		deliveryStation.setExternalId(DeliveryStationServiceTest.TEST_EXTERNAL_ID);
		deliveryStation.setName(DeliveryStationServiceTest.TEST_NAME);
		return deliveryStation;
	}

	private Customer prepareTestCustomer() {
		Customer customer = new Customer();
		customer.setName("unit test customer");
		customer.setStatus(CustomerStausEnmu.valid.getValue());
		this.customerDao.save(customer);
		Assert.assertNotNull("customerId should not be null", customer.getId());
		return customer;
	}

	@Test
	public void testDeleteDeliveryStation1() {
		int i = 1;
		Customer customer = this.prepareTestCustomer();
		DeliveryStationVo deliveryStationVo = this.prepareDeliveryStation(customer);
		DeliveryStation deliveryStation = this.deliveryStationService.createDeliveryStation(deliveryStationVo);
		Assert.assertNotNull("deliveryStation should not be null", deliveryStation);
		Assert.assertNotNull("deliveryStationId should not be null", deliveryStation.getId());
		Assert.assertEquals("the customerId should be same as prepared customerId", customer.getId(), deliveryStation.getCustomer().getId());
		Assert.assertEquals("the name should be same as prepared name", DeliveryStationServiceTest.TEST_NAME, deliveryStation.getName());

		deliveryStation = this.deliveryStationService.deleteDeliveryStation(deliveryStationVo);
		Assert.assertNotNull("deliveryStation should not be null", deliveryStation);
		deliveryStation = this.deliveryStationDao.get(deliveryStation.getId());
		Assert.assertEquals("the deliveryStation should be deleted", DeliveryStationStausEnmu.invalid.getValue(), deliveryStation.getStatus().intValue());
	}
}
