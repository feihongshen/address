package cn.explink.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import cn.explink.dao.UserDao;
import cn.explink.domain.User;
import cn.explink.web.ExplinkUserDetail;

@Service
public class UserService implements UserDetailsService {

	@Autowired
	private UserDao userDao;

	public UserService() {
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		List<User> users = userDao.getUsersByName(username);
		if(users.size()==0){
			throw new UsernameNotFoundException("沒有找到用戶名"+username);
		}
		if(users.size()>1){
			throw new RuntimeException("违反了用户名唯一约束");
		}
		User user=users.get(0);
		ExplinkUserDetail explinkUserDetail=new ExplinkUserDetail();
		explinkUserDetail.setUser(user);
		return explinkUserDetail;
	}

}
