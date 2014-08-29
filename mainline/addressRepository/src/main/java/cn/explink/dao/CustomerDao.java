package cn.explink.dao;

import org.springframework.stereotype.Repository;

import cn.explink.dao.support.BasicHibernateDaoSupport;
import cn.explink.domain.Customer;


@Repository
public class CustomerDao extends BasicHibernateDaoSupport<Customer, Long> {

	public CustomerDao() {
		super(Customer.class);
	}
	
}
