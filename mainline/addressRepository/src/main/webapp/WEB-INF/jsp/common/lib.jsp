<%@ page import="java.util.*,cn.explink.domain.SystemConfig,cn.explink.util.ConfigManager,org.apache.commons.lang3.StringUtils,org.apache.commons.lang3.ObjectUtils" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
 
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<%
	String defaultTheme = "redmond";
	String themeVersion = "1.9.2";
	session.setAttribute("themeName", defaultTheme);
	session.setAttribute("themeVersion", themeVersion);
	pageContext.setAttribute("timeInMillis", System.currentTimeMillis());
	SystemConfig sc = (SystemConfig)ConfigManager.getInstance().get("pageSize4Tree");
%>
<link rel="stylesheet" type="text/css" href="${ctx}/css/easyui/themes/default/easyui.css" />
<link rel="stylesheet" type="text/css" href="${ctx}/css/easyui/themes/icon.css" />
<link rel="stylesheet" type="text/css" href="${ctx}/css/zTree/zTreeStyle/zTreeStyle.css"/>
<script type="text/javascript" src="${ctx}/js/jquery-1.8.0.min.js"></script>
<script type="text/javascript" src="${ctx}/js/easyui/jquery.easyui.min.js"></script>
<script type="text/javascript" src="${ctx}/js/zTree/js/jquery.ztree.all-3.5.min.js"></script>
<script type="text/javascript" src="${ctx}/js/zTree/js/jquery.ztree.exhide-3.5.js"></script>
<script type="text/javascript" src="${ctx}/js/ajaxfileupload.js"></script>
<script type="text/javascript" src="${ctx}/js/address/getZAddress.js"></script>
<script type="text/javascript">
	var ctx = '<%=request.getContextPath() %>';
	var pageSize = '<%=sc.getValue() %>';
	if(pageSize!='null'){
		pageSize=parseInt(pageSize);
	}else{
		pageSize=100;
	}
</script>