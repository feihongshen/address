package cn.explink.dao;

import java.util.List;

import org.hibernate.Query;
import org.springframework.stereotype.Repository;

import cn.explink.dao.support.BasicHibernateDaoSupport;
import cn.explink.domain.DelivererRule;
import cn.explink.domain.enums.DelivererRuleTypeEnum;

@Repository
public class DelivererRuleDao extends BasicHibernateDaoSupport<DelivererRule, Long> {

	public DelivererRuleDao() {
		super(DelivererRule.class);
	}

	public List<DelivererRule> getDelivererRuleList(Long customerId, Long addressId) {
		
		StringBuilder hql = new StringBuilder("from DelivererRule where address.id = :addressId");
//		StringBuilder hql = new StringBuilder("select new cn.explink.domain.DelivererRule(r.id, r.rule, new cn.explink.domain.Deliverer(d.id, d.name, d.externalId))");
//		StringBuilder hql = new StringBuilder("select new cn.explink.domain.DelivererRule(r.id, r.rule)");
//		hql.append(" from DelivererRule r, Deliverer d");
//		hql.append(" where r.deliverer.id = d.id");
//		hql.append(" and d.customer.id = :customerId");
//		hql.append(" and r.address.id = :addressId");
		Query query = getSession().createQuery(hql.toString());
//		query.setLong("customerId", customerId);
		query.setLong("addressId", addressId);
		return query.list();
	}

	public List getByAddressAndDeliverer(Long addressId, Long did,Long customerId) {
		String sql = "SELECT r.id FROM DELIVERER_RULES r ,DELIVERERS" +
				" s WHERE r.RULE_TYPE=:ruleType AND r.RULE='' " +
				" AND s.CUSTOMER_ID=:customerId " +
				" AND r.ADDRESS_ID=:addressId";
		Query query = getSession().createSQLQuery(sql);
		query.setLong("addressId", addressId);
		query.setLong("customerId", customerId);
		query.setInteger("ruleType", DelivererRuleTypeEnum.fallback.getValue());
		return query.list();
	}

}
