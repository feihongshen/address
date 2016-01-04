package cn.explink.dao;

import java.util.List;

import org.hibernate.Query;
import org.springframework.stereotype.Repository;

import cn.explink.dao.support.BasicHibernateDaoSupport;
import cn.explink.domain.Address;
import cn.explink.domain.Alias;

@Repository
public class AliasDao extends BasicHibernateDaoSupport<Alias, Long> {

	public AliasDao() {
		super(Alias.class);
	}

	public List<Alias> getAliasByAddressId(Long addressId) {
		String hql = "from Alias where addressId = :addressId";
		Query query = this.getSession().createQuery(hql);
		query.setLong("addressId", addressId);
		return query.list();
	}

	public List<Alias> getAliasByIdList(List<Long> idList) {
		if ((idList != null) && !idList.isEmpty()) {
			String hql = "from Alias where id in :idList";
			Query query = this.getSession().createQuery(hql);
			query.setParameterList("idList", idList);
			return query.list();
		} else {
			return null;
		}
	}

	/**
	 *
	 * @Title: getAliasById
	 * @description 通过id获取别名信息
	 * @author 刘武强
	 * @date  2015年11月27日下午3:03:46
	 * @param  @param id
	 * @param  @return
	 * @return  Alias
	 * @throws
	 */
	public Alias getAliasById(Long id) {
		if (id != null) {
			String hql = "from Alias where id=:id";
			Query query = this.getSession().createQuery(hql);
			query.setLong("id", id);
			return (Alias) query.uniqueResult();
		} else {
			return null;
		}
	}

	public List<Alias> getAliasByCustomerId(Long customerId) {
		if (customerId != null) {
			String hql = "from Alias where customerId=:customerId";
			Query query = this.getSession().createQuery(hql);
			query.setLong("customerId", customerId);
			return  (List<Alias>)query.list();
		} else {
			return null;
		}
	}

	/**
	 *
	 * @Title: getAddressById
	 * @description 通过id获取关键词信息
	 * @author 刘武强
	 * @date  2015年11月27日下午3:33:01
	 * @param  @param id
	 * @param  @return
	 * @return  Alias
	 * @throws
	 */
	public Address getAddressById(Long id) {
		if (id != null) {
			String hql = "from Address where id=:id";
			Query query = this.getSession().createQuery(hql);
			query.setLong("id", id);
			return (Address) query.uniqueResult();
		} else {
			return null;
		}
	}

	public Alias getAliasByAddressIdAndAlias(Long addressId, String alias, Long customerId) {
		String hql = "from Alias where addressId = :addressId and name=:name and customerId=:customerId";
		Query query = this.getSession().createQuery(hql);
		query.setLong("addressId", addressId);
		query.setString("name", alias);
		query.setLong("customerId", customerId);
		return (Alias) query.uniqueResult();
	}

	public List<Alias> getAliasByAddressIdAndCustomerId(Long addressId, Long customerId) {
		String hql = "from Alias where addressId = :addressId and customerId=:customerId";
		Query query = this.getSession().createQuery(hql);
		query.setLong("addressId", addressId);
		query.setLong("customerId", customerId);
		return query.list();
	}

	public void deleteAliasByIds(List<Long> addressIdList, Long customerId) {
		String hql = " delete from  Alias where addressId in :addressIdList and customerId = :customerId";
		Query query = this.getSession().createQuery(hql);
		query.setParameterList("addressIdList", addressIdList);
		query.setLong("customerId", customerId);
		query.executeUpdate();
	}

}
