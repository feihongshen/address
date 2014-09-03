package cn.explink.web.controller;

import java.io.UnsupportedEncodingException;
import java.util.Date;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.bind.annotation.InitBinder;

import cn.explink.domain.User;
import cn.explink.web.ExplinkUserDetail;

public class BaseController {

	@Autowired
	private SecurityContextHolderStrategy securityContextHolderStrategy;
	@InitBinder
	public void initBinder(ServletRequestDataBinder binder) {
		binder.registerCustomEditor(Date.class, new DateConvertEditor());
	}
	protected User getLogginedUser() {
		Authentication auth = getSecurityContextHolderStrategy().getContext().getAuthentication();
		ExplinkUserDetail userDetail = (ExplinkUserDetail) auth.getPrincipal();
		User user = userDetail.getUser();
		return user;
	}

	protected Long getCustomerId() {
		return getLogginedUser().getCustomer().getId();
	}

	/**
	 * 设置导出文件名
	 * 
	 * @param response
	 * @param fileName
	 */
	protected void setDownloadFileName(HttpServletResponse response, String fileName) {
		response.setContentType("application/x-msdownload");
		try {
			response.setHeader("Content-Disposition", "attachment;filename=" + new String(fileName.getBytes(), "iso-8859-1"));
		} catch (UnsupportedEncodingException e) {
		}
	}

	public SecurityContextHolderStrategy getSecurityContextHolderStrategy() {
		return securityContextHolderStrategy;
	}

	public void setSecurityContextHolderStrategy(SecurityContextHolderStrategy securityContextHolderStrategy) {
		this.securityContextHolderStrategy = securityContextHolderStrategy;
	}

}
