package cn.explink.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.stereotype.Service;

import cn.explink.dao.CustomerDao;
import cn.explink.dao.RawDeliveryStationDao;
import cn.explink.domain.Customer;
import cn.explink.domain.RawDeliveryStation;
import cn.explink.domain.enums.DeliveryStationStausEnmu;

@Service
public class RawDeliveryStationService extends CommonServiceImpl<RawDeliveryStation, Long> {

	public RawDeliveryStationService() {
		super(RawDeliveryStation.class);
	}

	@Autowired
	private RawDeliveryStationDao rawDeliveryStationDao;

	@Autowired
	private SecurityContextHolderStrategy securityContextHolderStrategy;

	@Autowired
	private CustomerDao customerDao;

	public void createDeliveryStation(Long customerId, List<String> deliveryStationNameList) {
		for (String deliveryStationName : deliveryStationNameList) {
			Customer customer = this.customerDao.get(customerId);
			if (customer == null) {
				throw new RuntimeException("customer is not exist");
			}
			RawDeliveryStation rawDeliveryStation = this.rawDeliveryStationDao.getDeliveryStation(customerId, deliveryStationName);
			if (rawDeliveryStation == null) {
				rawDeliveryStation = new RawDeliveryStation();
			}
			rawDeliveryStation.setName(deliveryStationName);
			rawDeliveryStation.setStatus(DeliveryStationStausEnmu.valid.getValue());
			rawDeliveryStation.setCustomer(customer);
			this.rawDeliveryStationDao.save(rawDeliveryStation);
		}
	}

}
