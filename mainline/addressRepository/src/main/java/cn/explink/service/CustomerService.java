package cn.explink.service;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.explink.dao.AliasDao;
import cn.explink.dao.CustomerDao;
import cn.explink.domain.Customer;
import cn.explink.domain.enums.CustomerStausEnmu;

@Service
public class CustomerService {

	@Autowired
	private CustomerDao customerDao;

	@Autowired
	private AliasDao aliasDao;

//	public Customer createCustomer(CustomerVo customerVo) {
//		Customer customer = new Customer();
//		BeanUtils.copyProperties(customerVo, customer);
//		customer.setStatus(CustomerStausEnmu.valid.getValue());
//		customerDao.save(customer);
//		return customer;
//	}
}
