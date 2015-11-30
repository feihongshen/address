<%@ page language="java" pageEncoding="UTF-8"%>
<%@ page import="java.util.*"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ page import="cn.explink.domain.enums.LogTypeEnum"%>
<%@ include file="/WEB-INF/jsp/common/lib.jsp"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>地址库日志查看</title>
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/easyui/themes/default/easyui.css" />
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/easyui/themes/icon.css" />
<script type="text/javascript" src="${pageContext.request.contextPath}/js/comm.js"></script>

<style type="text/css">
.keywordSuffix>li {
	float: left;
	margin: 10px 5px 10px 0px;
}

.keywordSuffix {
	list-style: none;
	padding: 2px;
}

.dop-table-deliveryman {
	margin-left: -30px;
	margin-bottom: 10px;
}

.dop-table-deliveryman-panel {
	margin: 15px 0 10px 0;
	width: 980px;
}

.dop-table-deliveryman th {
	border: 0 !important;
	vertical-align: middle !important;
	text-align: right !important;
}

.dop-table-deliveryman td {
	border: 0 !important;
	vertical-align: middle !important;
}

.dop-table-deliveryman td input, select, textarea {
	width: 100%;
	margin-bottom: 0;
}

#dop_deliveryman_map_panel {
	height: 315px;
	width: 100%;
	border-top: 1px solid #CCCCCC;
	border-bottom: 1px solid #CCCCCC;
}

#dop_deliveryman_add_panel {
	display: none;
	width: 1000px;
}

#dop_deliveryman_assign_panel {
	display: none;
	width: 1000px;
}

#delPortrait {
	background-color: #FFF;
	border: 0;
	line-height: 0;
}
</style>
</head>
<body> 
  	<div style="margin: 0px; border: 0" id="datagrid_log_toolbar">
		<table  id="dop_table_deliveryman_query">
			<tr>
				<td style="padding-top:15px;">操作日期：
		        	<input name="beginTime" id="beginTime" class="easyui-datebox"/>
					至 <input name="endTime" id="endTime" class="easyui-datebox"/>&nbsp;
	 	 		</td>
				<td>操作类型：</td>
				<td>
					<select name="operationType" id="operationType" style="width:80px;">
			 	 		<option value="0"></option>
			 	 		<%for(LogTypeEnum temp : LogTypeEnum.getAllStatus()){ %>
			 	 			<option value="<%=temp.getValue()%>"><%=temp.getText()%></option>
			 	 		<%}%>
		 	 		</select>
	 	 		</td>
	 	 		<td>客户IP：</td>
				<td>
					<input code="operationIP">
	 	 		</td>
				<td>
					<a href="javascript:void(0)" class="easyui-linkbutton" id="search" iconCls="icon-search">查询</a>	
	 	 		</td>
			</tr>
		</table>
	</div>
	<table id="datagrid_logInfo" toolbar="#datagrid_log_toolbar"></table>
	
	<script type="text/javascript" src="${pageContext.request.contextPath}/js/address/bizlogSearch.js"></script>
	<script type="text/javascript" src="${ctx}/js/tools.js"></script>
</body>
</html>