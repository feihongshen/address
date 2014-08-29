package cn.explink.web.filter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.TextEscapeUtils;

import cn.explink.domain.User;
import cn.explink.web.ExplinkUserDetail;

public class AddressLoginFilter extends UsernamePasswordAuthenticationFilter {

	private boolean postOnly = true;

	private boolean allowEmptyValidateCode = true;

	private String sessionvalidateCodeField = DEFAULT_SESSION_VALIDATE_CODE_FIELD;

	private String validateCodeParameter = DEFAULT_VALIDATE_CODE_PARAMETER;

	public static final String DEFAULT_SESSION_VALIDATE_CODE_FIELD = "validateCode";

	public static final String DEFAULT_VALIDATE_CODE_PARAMETER = "validateCode";

//	@Autowired
//	private UserService userService;

	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
		if (postOnly && !request.getMethod().equals("POST")) {
			throw new AuthenticationServiceException("Authentication method not supported: " + request.getMethod());
		}

		String userName = StringUtils.trimToEmpty(obtainUsername(request));
		String password = obtainPassword(request);
		if (password == null) {
			password = StringUtils.EMPTY;
		}

		if (!isAllowEmptyValidateCode()) {
			checkValidateCode(request);
		}
		request.getSession().removeAttribute(sessionvalidateCodeField);
		
		// Place the last username attempted into HttpSession for views
		HttpSession session = request.getSession(false);

		if (session != null || getAllowSessionCreation()) {
			request.getSession().setAttribute(SPRING_SECURITY_LAST_USERNAME_KEY, TextEscapeUtils.escapeEntities(userName));
		}

		UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(userName, password);
		// Allow subclasses to set the "details" property
		setDetails(request, authRequest);
		
		Authentication authentication = getAuthenticationManager().authenticate(authRequest);
		ExplinkUserDetail userDetail = (ExplinkUserDetail) authentication.getPrincipal();
		User user = userDetail.getUser();
		if (!user.getPassword().equals(password)) {
			throw new AuthenticationServiceException("用户名或者密码错误！");
		}
		return authentication;
	}

	/*
	 * public String getIpAddr(HttpServletRequest request) { String ip =
	 * request.getHeader("x-forwarded-for"); if (ip == null || ip.length() == 0
	 * || "unknown".equalsIgnoreCase(ip)) { ip =
	 * request.getHeader("Proxy-Client-IP"); } if (ip == null || ip.length() ==
	 * 0 || "unknown".equalsIgnoreCase(ip)) { ip =
	 * request.getHeader("WL-Proxy-Client-IP"); } if (ip == null || ip.length()
	 * == 0 || "unknown".equalsIgnoreCase(ip)) { ip = request.getRemoteAddr(); }
	 * return ip; }
	 */

	/**
	 * 
	 * <li>比较session中的验证码和用户输入的验证码是否相等</li>
	 * 
	 */
	protected void checkValidateCode(HttpServletRequest request) {
		String sessionValidateCode = obtainSessionValidateCode(request);
		String validateCodeParameter = obtainValidateCodeParameter(request);
		if (StringUtils.isEmpty(validateCodeParameter) || !sessionValidateCode.equalsIgnoreCase(validateCodeParameter)) {// &&!"TTTT".equals(validateCodeParameter)
			throw new AuthenticationServiceException("验证码不正确");
		}
	}

	private String obtainValidateCodeParameter(HttpServletRequest request) {
		return request.getParameter(validateCodeParameter);
	}

	protected String obtainSessionValidateCode(HttpServletRequest request) {
		Object obj = request.getSession().getAttribute(sessionvalidateCodeField);
		return null == obj ? "" : obj.toString();
	}

	public boolean isPostOnly() {
		return postOnly;
	}

	@Override
	public void setPostOnly(boolean postOnly) {
		this.postOnly = postOnly;
	}

	public String getValidateCodeName() {
		return sessionvalidateCodeField;
	}

	public void setValidateCodeName(String validateCodeName) {
		this.sessionvalidateCodeField = validateCodeName;
	}

	public boolean isAllowEmptyValidateCode() {
		return allowEmptyValidateCode;
	}

	public void setAllowEmptyValidateCode(boolean allowEmptyValidateCode) {
		this.allowEmptyValidateCode = allowEmptyValidateCode;
	}
}
