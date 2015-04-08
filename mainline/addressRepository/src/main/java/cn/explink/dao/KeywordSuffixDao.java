package cn.explink.dao;

import java.util.List;

import org.hibernate.Query;
import org.springframework.stereotype.Repository;

import cn.explink.dao.support.BasicHibernateDaoSupport;
import cn.explink.domain.KeywordSuffix;

@Repository
public class KeywordSuffixDao extends BasicHibernateDaoSupport<KeywordSuffix, Long> {

	public KeywordSuffixDao() {
		super(KeywordSuffix.class);
	}

	@SuppressWarnings("unchecked")
	public List<KeywordSuffix> getKeywordSuffixByCustomerId(Long customerId) {
		String hql = "from KeywordSuffix where customerId=:customerId";
		Query query = this.getSession().createQuery(hql);
		query.setLong("customerId", customerId);
		return query.list();
	}

	public KeywordSuffix getKeywordSuffixByName(String name, Long customerId) {
		String hql = "from KeywordSuffix where name=:name and customerId=:customerId";
		Query query = this.getSession().createQuery(hql);
		query.setString("name", name);
		query.setLong("customerId", customerId);
		return (KeywordSuffix) query.uniqueResult();
	}

}
