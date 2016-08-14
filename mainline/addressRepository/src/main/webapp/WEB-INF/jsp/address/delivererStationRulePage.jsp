<%@ page language="java" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>小件员关联维护</title>
<%@include file="/WEB-INF/jsp/common/lib.jsp"%>
<script type="text/javascript" src="${ctx}/js/address/delivererStationRulePage.js"></script>
<script type="text/javascript" src="${ctx}/js/address/maskUtil.js"></script>
	<script type="text/javascript" src="${ctx}/js/tools.js"></script>
<style type="text/css">
.table {
	border-collapse: collapse;
	border-left: 1px solid #cccccc;
	border-top: 1px solid #cccccc;
	color: #333333;
	font-family: Verdana, Arial, Helvetica, sans-serif;
	width: 100%;
	font-size: 12px;
}

.table>tbody {
	display: table-row-group;
	vertical-align: middle;
}

.table>tbody>tr td, table th {
	border-bottom: 1px solid #cccccc;
	border-right: 1px solid #cccccc;
	line-height: 1.4em;
	padding: 7px 8px;
	vertical-align: middle;
	font-size: 12px;
}

.table {
	width: auto;
	height: auto;
}

span.button.firstPage {
	float: right;
	margin-left: 2px;
	margin-right: 0;
	background-position: -144px -16px;
	vertical-align: top;
	*vertical-align: middle
}

span.button.prevPage {
	float: right;
	margin-left: 2px;
	margin-right: 0;
	background-position: -144px -48px;
	vertical-align: top;
	*vertical-align: middle
}

span.button.nextPage {
	float: right;
	margin-left: 2px;
	margin-right: 0;
	background-position: -144px -64px;
	vertical-align: top;
	*vertical-align: middle
}

span.button.lastPage {
	float: right;
	margin-left: 2px;
	margin-right: 0;
	background-position: -144px -32px;
	vertical-align: top;
	*vertical-align: middle
}
</style>
</head>
<body>
	<div class="easyui-layout" style="height: 540px;">
	 
		<div data-options="region:'west',split:true" title="条件搜索" style="width: 500px;">
		
			<table width="100%" border="0" cellspacing="0" cellpadding="10">
				<tr>
					<td align="left">选择站点:
					<select class="easyui-combobox" style="width: 100px;"
								id="stationId" name="stationId" data-options="valueField:'value', textField:'label'">
							</select></td>
						</tr>
						<tr>
								<td align="left">当前选中站点:<label id='stationName'></label>
							<input   type='hidden' name='hd_stationId'/></td>
						</tr>
						<tr><td>
						小件员列表 :<input  type='hidden'  name='delivererId'/>
		
						</td>	</tr>
							 
				<tr >
					<td colspan=4><div id="promtInfo"></div>
						<ul id="tree" class="ztree" style="width: auto; height: auto; overflow: auto;"></ul></td>
				</tr>
			</table>
		</div>
	
		<div data-options="region:'center'" style="border: 0px;">
			<div class="easyui-panel" title="维护小件员-" id="relStation"
				style="padding-left: -1px; margin-bottom: 10px; min-height: 510px; width: auto">
				<table id="stationRule" class="table table-bordered" toolbar="#tb" style="height:100%">
					<thead>
						<tr>
							<th width="200px">关键词</th>
							<th width="200px">规则</th>
							<th width="600px">操作</th>
						</tr>
					</thead>
					<tbody>
						
					</tbody>
				</table>
			</div>
	
		</div>
	</div>
	
	<div id="tb" >
		<a href="#" class="easyui-linkbutton" iconCls="icon-add" plain="true" onclick="javascript:addFn()">新增</a>
		<a href="#" class="easyui-linkbutton" iconCls="icon-cut" plain="true" onclick="javascript:delFn('')">删除</a> 
	</div>
	
	
	<div id="win" data-options="iconCls:'icon-save',resizable:false,modal:true,minimizable:false,maximizable:false,collapsible:false" class="easyui-window" title="新增小件员负责地址" style="width:300px;height:180px;">
	 
		<p>关键词:<select class="easyui-combobox" style="width: 100px;"
								id="addressId" name="addressId" data-options="valueField:'value', textField:'label'">
							</select>
							<input type='hidden' id='hd_addressId'  />
							</p>
		<p>规则: <input type="text" name="rule"></p>
		<div style="padding:5px;text-align:center;">
			<a href="javascript:confirmFn('')" class="easyui-linkbutton" icon="icon-ok">确定</a>
			<a href="javascript:closeFn()" class="easyui-linkbutton" icon="icon-cancel">取消</a>
		</div>
	 
</div>
</body>
</html>
