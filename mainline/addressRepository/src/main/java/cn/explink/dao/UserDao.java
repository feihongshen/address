package cn.explink.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
		Query query = this.getSession().createQuery(hql);
		query.setString("name", name);
		return query.list();
	}

	public Map<Long, String> getview() {
		Map<Long, String> view = new HashMap<Long, String>();
		List<User> ulist = this.loadAll(User.class);

		for (User user : ulist) {
			view.put(user.getId(), user.getName());
		}
		return view;
	}

	public void resetPsd(Long id, String password) {
		String hql = "update User set password=:password where id=:id";
		Query query = this.getSession().createQuery(hql);
		query.setString("password", password);
		query.setLong("id", id);
		query.executeUpdate();
	}

}
