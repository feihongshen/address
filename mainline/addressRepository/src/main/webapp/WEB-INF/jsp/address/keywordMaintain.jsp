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
	
	<div style="margin: 0px; border: 0" id="datagrid_keyword_toolbar" >
		<table class="table table-striped table-hover table-condensed dop-table-deliveryman"
			id="dop_table_deliveryman_query">
			<tr>
				<th>&nbsp;&nbsp;&nbsp;</th>
				<th>关键字：</th>
				<td><div>
						<input code="keyword_query">
					</div></td>
				<th>&nbsp;&nbsp;&nbsp;</th>
				<th>站点：</th>
				<td><div>
						<input code="station_query">
					</div></td>
			</tr>
		</table>

		<a href="javascript:void(0)" class="easyui-linkbutton" id="keyword_query" iconCls="icon-search">查询</a>
		<a href="javascript:void(0)" class="easyui-linkbutton" id="keyword_edit" iconCls="icon-edit">修改</a>
		<a href="javascript:void(0)" class="easyui-linkbutton" id="keyword_delete" iconCls="icon-remove">删除</a>
		<a href="javascript:void(0)" class="easyui-linkbutton" id="keyword_submit" iconCls="icon-ok">提交</a>
		<a href="javascript:void(0)" class="easyui-linkbutton" id="keyword_suffix_maintain" iconCls="">后缀维护</a>
	</div>

	<table id="datagrid_keyword" toolbar="#datagrid_keyword_toolbar"></table>


	<div id="keyword_edit_panel" style="display: none;">
		<table class="">
			<tr>
				<input id="id" type="hidden" />
				<td>省/直辖市：</td>
				<td><div>
						<input id="province" readonly="readonly" disabled="disabled">
					</div></td>
				<td>市：</td>
				<td><div>
						<input id="city" readonly="readonly" disabled="disabled">
					</div></td>
				<td>区/县：</td>
				<td><div>
						<input id="district" readonly="readonly" disabled="disabled">
					</div></td>
			</tr>
			<tr>
				<input id="addressId1" type="hidden" />
				<input id="addressId2" type="hidden" />
				<input id="addressId3" type="hidden" />

				<td>关键字1：</td>
				<td><div>
						<input id="addressName1">
					</div></td>
				<td>关键字2：</td>
				<td><div>
						<input id="addressName2">
					</div></td>
				<td>关键字3：</td>
				<td><div>
						<input id="addressName3">
					</div></td>
			</tr>
			<tr>
				<th>&nbsp;</th>
			</tr>
			<tr>
				<td>站点名称：</td>
				<td><div>
						<input id="deliveryStationName">
					</div></td>
			</tr>
		</table>
		<div class="pull-right" align="right" style="margin: 5px 10px 0 0;">
			<a href="javascript:void(0)" class="easyui-linkbutton" id="keyword_ok" iconCls="icon-ok">提交</a> <a
				href="javascript:void(0)" class="easyui-linkbutton" id="keyword_cancel" iconCls="icon-cancel">取消</a>
		</div>
	</div>


	<div id="keyword_suffix_panel" title="关键词后缀维护" style="display: none;">
		<div style="height: 400px; width: 750px; overflow-y: auto; paddding: 30px 0px;">
			<ul class="keywordSuffix" id="keywordSuffixUl"></ul>
		</div>
		<br />
		<div style="float: right">
			<input type="text" name="keywordSuffix" id="keywordSuffix" /><a
				href="javascript:addKeywordSuffix();" id="addKeywordSuffix" class="easyui-linkbutton">添加关键词后缀</a>
		</div>
	</div>
</body>

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
