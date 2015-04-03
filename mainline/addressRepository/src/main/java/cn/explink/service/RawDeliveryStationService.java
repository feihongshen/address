package cn.explink.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.stereotype.Service;

import cn.explink.dao.CustomerDao;
import cn.explink.dao.RawDeliveryStationDao;
import cn.explink.domain.Customer;
import cn.explink.domain.RawDeliveryStation;
import cn.explink.domain.User;
import cn.explink.domain.enums.DeliveryStationStausEnmu;
import cn.explink.web.ExplinkUserDetail;

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

	@SuppressWarnings("unchecked")
	public void createDeliveryStation(List<String> deliveryStationNameList) {
		Long customerId = this.getCustomerId();
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

	private Long getCustomerId() {
		Authentication auth = this.securityContextHolderStrategy.getContext().getAuthentication();
		ExplinkUserDetail userDetail = (ExplinkUserDetail) auth.getPrincipal();
		User user = userDetail.getUser();

		return user.getCustomer().getId();
	}

}
