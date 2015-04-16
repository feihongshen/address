package cn.explink.web.filter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import cn.explink.domain.User;
import cn.explink.util.StringUtil;
import cn.explink.web.ExplinkUserDetail;

public class DmpLoginFilter extends UsernamePasswordAuthenticationFilter {

	@Autowired
	private SecurityContextHolderStrategy securityContextHolderStrategy;

	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
		String username = request.getParameter("username") == null ? "" : request.getParameter("username");
		String password = request.getParameter("password") == null ? "" : request.getParameter("password");
		if (StringUtil.isEmpty(username) || StringUtil.isEmpty(password)) {
			return null;
		}

		UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(username, password);
		// Allow subclasses to set the "details" property
		this.setDetails(request, authRequest);
		Authentication authentication = this.getAuthenticationManager().authenticate(authRequest);

		this.securityContextHolderStrategy.getContext().setAuthentication(authentication);

		ExplinkUserDetail userDetail = (ExplinkUserDetail) authentication.getPrincipal();
		User user = userDetail.getUser();
		if (!user.getPassword().equals(password)) {
			throw new AuthenticationServiceException("用户名或者密码错误！");
		}
		return authentication;
	}

}
