<%@ page language="java" pageEncoding="UTF-8"%>
<%@ page import="java.util.*"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
<head>
<title>管理员管理</title>
<link rel="stylesheet" type="text/css"
	href="${pageContext.request.contextPath}/css/easyui/themes/default/easyui.css" />
<link rel="stylesheet" type="text/css"
	href="${pageContext.request.contextPath}/css/easyui/themes/icon.css" />
<script type="text/javascript"
	src="${pageContext.request.contextPath}/js/jquery-1.8.0.min.js"></script>
<script type="text/javascript"
	src="${pageContext.request.contextPath}/js/easyui/jquery.easyui.min.js"></script>
<script type="text/javascript">
	$(function() {
		addTab('地址匹配','${pageContext.request.contextPath}/address/index');
	});
	function addTab(title, url) {
		if ($('#centerTabs').tabs('exists', title)) {
			$('#centerTabs').tabs('select', title);
		} else {
			var content = '<iframe scrolling="auto" frameborder="0" src="' + url + '" style="width:100%;height:100%;"></iframe>';
			$('#centerTabs').tabs('add', {
				title : title,
				content : content,
				closable : true
			});
		}
	}
</script>
</head>
<body class="easyui-layout">
	<div data-options="region:'north',border:false"
		style="height: 30px; background: #B3DFDA; padding: 10px; font-size: 14px">
		<table width="100%" border=0 cellspacing="0" cellpadding="0">
			<tr>
				<td><strong>地址库</strong></td>
				<td align="right" style="font-size: 12px"><a
					href="javascript:changePass();">修改密码</a> &nbsp;<a
					href="/user/logout.htm">退出</a></td>
			</tr>
		</table>
	</div>
	<div data-options="region:'west',split:true,title:'功能菜单'"
		style="width: 180px; padding: 10px;">
		<ul>
			<!-- class="focus" -->
			<li><a href="#" onclick="addTab('地址匹配','${pageContext.request.contextPath}/address/index')">地址匹配</a></li>
			<li>数据维护</li>
			<ul>
				<li><a href="#" onclick="addTab('关键词导入','/project/index.htm')">关键词导入</a></li>
				<li><a href="#" onclick="addTab('地址库维护','/project/index.htm')">地址库维护</a></li>
				<li><a href="#" onclick="addTab('站点管理','/project/index.htm')">站点管理</a></li>
				<li><a href="#" onclick="addTab('配送员管理','/project/index.htm')">配送员管理</a></li>
			</ul>
			<li>关联设置</li>
			<ul>
				<li><a href="#" onclick="addTab('拆合站维护','/project/index.htm')">拆合站维护</a></li>
				<li><a href="#" onclick="addTab('配送员站点关联维护','/project/index.htm')">配送站点关联维护</a></li>
				<li><a href="#" onclick="addTab('配送员关联维护','/project/index.htm')">配送员关联维护TODO</a></li>
			</ul>
		</ul>
	</div>
	<div data-options="region:'center'" id="mainDiv">
		<div class="easyui-tabs" id="centerTabs">
		</div>
	</div>
</body>
</html>
