package cn.explink.dao;

import java.util.List;

import org.hibernate.Query;
import org.springframework.stereotype.Repository;

import cn.explink.dao.support.BasicHibernateDaoSupport;
import cn.explink.domain.User;

@Repository
public class UserDao extends BasicHibernateDaoSupport<User, Long> {

	public UserDao() {
		super(User.class);
	}

	public List<User> getUsersByName(String name) {
		String hql = "from User where name = :name";
		Query query = getSession().createQuery(hql);
		query.setString("name", name);
		return query.list();
	}

}
