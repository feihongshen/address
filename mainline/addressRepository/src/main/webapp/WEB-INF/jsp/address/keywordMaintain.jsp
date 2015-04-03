<%@ page language="java" pageEncoding="UTF-8"%>
<%@page import="java.util.*"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
<head>
<title>地址匹配</title>
<%@include file="/WEB-INF/jsp/common/lib.jsp"%>
<!-- <link rel="stylesheet" type="text/css" -->
<%-- 	href="${pageContext.request.contextPath}/css/easyui/themes/default/easyui.css" /> --%>
<!-- <link rel="stylesheet" type="text/css" -->
<%-- 	href="${pageContext.request.contextPath}/css/easyui/themes/icon.css" /> --%>
<style type="text/css">
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
	<table id="datagrid_keyword" toolbar="#datagrid_keyword_toolbar"></table>
	<div style="margin: 5px; border: 0;" id="datagrid_keyword_toolbar">
		<table class="table table-striped table-hover table-condensed dop-table-deliveryman"
			id="dop_table_deliveryman_query">
			<tr>
				<th>&nbsp;&nbsp;&nbsp;</th>
				<th>地址：</th>
				<td><div>
						<input code="address_query">
					</div></td>
				<th>站点：</th>
				<td><div>
						<input code="station_query">
					</div></td>
			</tr>
		</table>

		<a href="javascript:void(0)" class="easyui-linkbutton" id="keyword_query" iconCls="icon-search">查询</a>
		<a href="javascript:void(0)" class="easyui-linkbutton" id="station_add_panel_btn_reset"
			iconCls="icon-edit">修改</a> <a href="javascript:void(0)" class="easyui-linkbutton"
			id="station_add_panel_btn_reset" iconCls="icon-ok">提交</a> <a href="javascript:void(0)"
			class="easyui-linkbutton" id="station_add_panel_btn_reset" iconCls="">后缀维护</a>
</body>

</div>
<div id="dop_deliveryman_add_panel">
	<table
		class="table table-striped table-hover table-condensed dop-table-deliveryman dop-table-deliveryman-panel">
		<tr>
			<th>业务员：</th>
			<td><div>
					<input id="delName">
				</div></td>
			<th>证件编号：</th>
			<td><div>
					<input id="delCertificates">
				</div></td>
			<th>照片：</th>
			<td><div>
					<input id="delPortrait" type="file">
				</div></td>
		</tr>
		<tr>
			<th>电话：</th>
			<td><div>
					<input id="delTeliPhone">
				</div></td>
			<th>每日最大送餐量：</th>
			<td><div>
					<input id="delMaxmum">
				</div></td>
			<th>对应用户：</th>
			<td><div>
					<select id="sUser"></select>
				</div></td>
		</tr>
		<tr>
			<th>业务员描述：</th>
			<td colspan="5"><div>
					<textarea id="delIntroduction"></textarea>
				</div></td>
		</tr>
	</table>
	<div class="pull-right" style="margin: 5px 10px 0 0;">
		<div class="btn btn-default" id="dop_deliveryman_add_panel_ok">
			<i class="icon-ok"></i>确定
		</div>
		<div class="btn btn-default" id="dop_deliveryman_add_panel_cancel">
			<i class="icon-remove"></i>取消
		</div>
	</div>
</div>
<div id="dop_deliveryman_assign_panel">
	<table
		class="table table-striped table-hover table-condensed dop-table-deliveryman dop-table-deliveryman-panel">
		<tr>
			<th width="30%">区域：</th>
			<td><div style="width: 30%">
					<select id="area" multiple="multiple">
					</select>
				</div></td>
		</tr>
	</table>
	<div class="pull-right" style="margin: 5px 200px 0 0;">
		<div class="btn btn-default" id="dop_deliveryman_assign_panel_ok">
			<i class="icon-ok"></i>确定
		</div>
		<div class="btn btn-default" id="dop_deliveryman_assign_panel_cancel">
			<i class="icon-remove"></i>取消
		</div>
	</div>
</div>
<%-- <script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-1.8.0.min.js"></script> --%>
<!-- <script type="text/javascript" -->
<%-- 	src="${pageContext.request.contextPath}/js/easyui/jquery.easyui.min.js"></script> --%>
<!-- <script type="text/javascript" -->
<%-- 	src="${pageContext.request.contextPath}/js/easyui/easyui-lang-zh_CN.js"></script> --%>
<%-- <script type="text/javascript" src="${pageContext.request.contextPath}/js/ajaxfileupload.js"></script> --%>
<%-- <script type="text/javascript" src="${pageContext.request.contextPath}/js/crudutil.js"></script> --%>
<%-- <script type="text/javascript" src="<%=request.getContextPath()%>/js/lhgDialog/lhgdialog.min.js"></script> --%>
<script type="text/javascript" src="${ctx}/js/tools.js"></script>
<script type="text/javascript"
	src="${pageContext.request.contextPath}/js/address/keywordMaintain.js"></script>
<script>
	$("#keyword_query").click(function() {
		init();
	});
</script>
</body>
</html>
