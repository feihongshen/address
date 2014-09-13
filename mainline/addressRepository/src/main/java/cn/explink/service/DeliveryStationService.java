package cn.explink.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.explink.dao.CustomerDao;
import cn.explink.dao.DeliveryStationDao;
import cn.explink.domain.Customer;
import cn.explink.domain.DeliveryStation;
import cn.explink.domain.enums.DeliveryStationStausEnmu;
import cn.explink.modle.ComboBox;
import cn.explink.ws.vo.DeliveryStationVo;

@Service ("deliverySationtService")
public class DeliveryStationService extends CommonServiceImpl<DeliveryStation, Long> {
	
	public DeliveryStationService() {
		super(DeliveryStation.class);
		// TODO Auto-generated constructor stub
	}

	@Autowired
	private DeliveryStationDao deliveryStationDao;
	
	@Autowired
	private CustomerDao customerDao;

	public DeliveryStation createDeliveryStation(DeliveryStationVo deliveryStationVo) {
		Customer customer = customerDao.get(deliveryStationVo.getCustomerId());
		if (customer == null) {
			throw new RuntimeException("customer is not exist");
		}
		
		DeliveryStation deliveryStation = deliveryStationDao.getDeliveryStation(deliveryStationVo.getCustomerId(), deliveryStationVo.getExternalId());
		if (deliveryStation == null) {
			deliveryStation = new DeliveryStation();
		}
		deliveryStation.setName(deliveryStationVo.getName());
		deliveryStation.setStatus(DeliveryStationStausEnmu.valid.getValue());
		deliveryStation.setCustomer(customer);
		deliveryStation.setExternalId(deliveryStationVo.getExternalId());
		deliveryStationDao.save(deliveryStation);
		return deliveryStation;
	}
	
	public DeliveryStation updateDeliveryStation(DeliveryStationVo deliveryStationVo) {
		DeliveryStation deliveryStation = deliveryStationDao.getDeliveryStation(deliveryStationVo.getCustomerId(), deliveryStationVo.getExternalId());
		deliveryStation.setName(deliveryStationVo.getName());
		deliveryStation.setStatus(DeliveryStationStausEnmu.valid.getValue());
		deliveryStationDao.save(deliveryStation);
		return deliveryStation;
	}
	
	public DeliveryStation deleteDeliveryStation(DeliveryStationVo deliveryStationVo) {
		DeliveryStation deliveryStation = deliveryStationDao.getDeliveryStation(deliveryStationVo.getCustomerId(), deliveryStationVo.getExternalId());
		deliveryStation.setStatus(DeliveryStationStausEnmu.invalid.getValue());
		deliveryStationDao.save(deliveryStation);
		return deliveryStation;
	}

	public List<ComboBox> getAllSationt(Long customerId) {
		return deliveryStationDao.getComBoxDeliveryStation(customerId);
	}

}
