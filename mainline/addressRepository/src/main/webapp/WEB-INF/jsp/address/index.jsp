<%@ page language="java" pageEncoding="UTF-8"%>
<%@page import="java.util.*"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
<head>
<title>地址匹配</title>
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
	});
</script>
</head>
<body class="easyui-layout">
	<div data-options="region:'center'">
		<table id="dg" title="角色列表" class="easyui-datagrid" style="width:700px;height:250px"
            toolbar="#toolbar" pagination="false"
            rownumbers="true" fitColumns="true" singleSelect="true" data-options="fit:true">  
	        <thead>  
	            <tr>  
	                <th field="id" width="15">编号</th>  
	                <th field="name" width="30">角色名称</th>  
	                <th field="brief" width="30">简介</th>  
	                <th field="createTime" width="30">创建时间</th>  
	            </tr>  
	        </thead>  
	    </table>  
	    <div id="toolbar">
	      <table width="100%" border="0" cellspacing="0" cellpadding="0">
	      <tr><td>
	    	<a href="javascript:void(0)" class="easyui-linkbutton" iconCls="icon-blank" plain="true"></a>
	        <a href="javascript:void(0)" class="easyui-linkbutton" iconCls="icon-add" plain="true" onclick="newItem()">新增角色</a>  
	        <a href="javascript:void(0)" class="easyui-linkbutton" iconCls="icon-edit" plain="true" onclick="editItem()">修改角色</a>  
	    	<a href="javascript:void(0)" class="easyui-linkbutton" iconCls="icon-blank" plain="true"></a>
	        <a href="javascript:void(0)" class="easyui-linkbutton" iconCls="icon-edit" plain="true" onclick="configItem()">配置角色所含管理员</a>  
	      </td><td align="center">
	        <a href="javascript:void(0)" class="easyui-linkbutton" iconCls="icon-remove" plain="true" onclick="removeItem()">删除角色</a>  
	      </td></tr>
	      </table>
	    </div>
	</div>
	<div id="dlg" class="easyui-dialog" style="width:450px;height:300px;padding:10px 20px"  
            closed="true" buttons="#dlg-buttons">  
        <div class="ftitle">角色信息</div>
        <form id="fm" method="post" novalidate>
            <div class="fitem">
                <label>ID:</label>
                <input name="id" id="aId" readonly="readonly">
            </div>
            <div class="fitem">
                <label>角色名称:</label>
                <input name="name" class="easyui-validatebox" required="true">
            </div>
            <div class="fitem">
                <label>简介:</label>
                <input name="brief" size="30">
            </div>
        </form>
    </div>
    <div id="dlg-buttons">
        <a href="javascript:void(0)" class="easyui-linkbutton" iconCls="icon-ok" onclick="saveItem()">保存</a>
        <a href="javascript:void(0)" class="easyui-linkbutton" iconCls="icon-cancel" onclick="javascript:$('#dlg').dialog('close')">取消</a>
    </div>
	<div id="dlgConf" class="easyui-dialog" style="width:500px;height:300px;padding:10px 20px"  
            closed="true" buttons="#dlgConf-buttons">  
        <div class="ftitle">角色 <span id="roleName"></span> 所包含的管理员</div>
        <form id="fmConf" method="post" novalidate>
        </form>
    </div>
    <div id="dlgConf-buttons">
        <a href="javascript:void(0)" class="easyui-linkbutton" iconCls="icon-ok" onclick="saveItemConf()">保存</a>
        <a href="javascript:void(0)" class="easyui-linkbutton" iconCls="icon-cancel" onclick="javascript:$('#dlgConf').dialog('close')">取消</a>
    </div>
</body>
</html>
