package cn.explink.dao;

import java.util.List;

import org.hibernate.Query;
import org.springframework.stereotype.Repository;

import cn.explink.dao.support.BasicHibernateDaoSupport;
import cn.explink.domain.DelivererRule;

@Repository
public class DelivererRuleDao extends BasicHibernateDaoSupport<DelivererRule, Long> {

	public DelivererRuleDao() {
		super(DelivererRule.class);
	}

	public List<DelivererRule> getDelivererRuleList(Long customerId, Long addressId) {
		StringBuilder hql = new StringBuilder("select r from DelivererRule r, Deliverer d");
		hql.append(" where r.deliverer.id = d.id");
		hql.append(" and d.customer.id = :customerId");
		hql.append(" r.address.id = :addressId");
		Query query = getSession().createQuery(hql.toString());
		query.setLong("customerId", customerId);
		query.setLong("addressId", addressId);
		return query.list();
	}

}
