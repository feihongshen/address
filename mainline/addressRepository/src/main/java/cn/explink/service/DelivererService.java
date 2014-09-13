package cn.explink.service;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.explink.dao.CustomerDao;
import cn.explink.dao.DelivererDao;
import cn.explink.domain.Customer;
import cn.explink.domain.Deliverer;
import cn.explink.domain.enums.DelivererStausEnmu;
import cn.explink.ws.vo.DelivererVo;

@Service
public class DelivererService {
	
	@Autowired
	private DelivererDao delivererDao;
	
	@Autowired
	private CustomerDao customerDao;

	public Deliverer createDeliverer(DelivererVo delivererVo) {
		Customer customer = customerDao.get(delivererVo.getCustomerId());
		if (customer == null) {
			throw new RuntimeException("customer is not exist");
		}

		Deliverer deliverer = delivererDao.getDeliverer(delivererVo.getCustomerId(), delivererVo.getExternalId());
		if (deliverer == null) {
			deliverer = new Deliverer();
		}
		BeanUtils.copyProperties(delivererVo, deliverer);
		deliverer.setStatus(DelivererStausEnmu.valid.getValue());
		deliverer.setCustomer(customer);
		delivererDao.save(deliverer);
		return deliverer;
	}
	
	public Deliverer updateDeliverer(DelivererVo delivererVo) {
		Deliverer deliverer = delivererDao.getDeliverer(delivererVo.getCustomerId(), delivererVo.getExternalId());
		deliverer.setName(delivererVo.getName());
		deliverer.setStatus(DelivererStausEnmu.valid.getValue());
		delivererDao.save(deliverer);
		return deliverer;
	}
	
	public Deliverer deleteDeliverer(DelivererVo delivererVo) {
		Deliverer deliverer = delivererDao.getDeliverer(delivererVo.getCustomerId(), delivererVo.getExternalId());
		deliverer.setStatus(DelivererStausEnmu.invalid.getValue());
		delivererDao.save(deliverer);
		return deliverer;
	}
	
	public Deliverer getDeliverer(Long id) {
		return delivererDao.get(id);
	}
}
