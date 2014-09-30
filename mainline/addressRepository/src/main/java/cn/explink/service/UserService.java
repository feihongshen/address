package cn.explink.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import cn.explink.dao.UserDao;
import cn.explink.domain.User;
import cn.explink.exception.ExplinkRuntimeException;
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

	public void resetPsd(String oldpass, String password) {
		SecurityContext context = SecurityContextHolder.getContext();
		ExplinkUserDetail userDetail = (ExplinkUserDetail) context.getAuthentication().getPrincipal();
		if(userDetail==null){
			throw new ExplinkRuntimeException("用户未登录！");
		} 
		User u = userDetail.getUser();
			if(!u.getPassword().equals(oldpass)){
				throw new ExplinkRuntimeException("原密码输入不正确！");
			}
		  userDao.resetPsd(u.getId(),password);
		  User newUser = userDao.get(u.getId());
		  userDetail.setUser(newUser);
	}
	
}
