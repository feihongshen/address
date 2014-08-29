package cn.explink.dao;

import org.springframework.stereotype.Repository;

import cn.explink.dao.support.BasicHibernateDaoSupport;
import cn.explink.domain.Order;

@Repository
public class OrderDao extends BasicHibernateDaoSupport<Order, Long> {

	public OrderDao() {
		super(Order.class);
	}


}
