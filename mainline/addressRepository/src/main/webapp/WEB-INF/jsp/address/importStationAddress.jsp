<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>站点管理</title>
<%@include file="/WEB-INF/jsp/common/lib.jsp"%>

<link rel="stylesheet" type="text/css" href="${ctx}/css/spectrum.css" />

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

.table>thead>tr>td {
	border-bottom: 1px solid #cccccc;
	border-right: 1px solid #cccccc;
	line-height: 1.4em;
	padding: 7px 8px;
	background-color: #9BCD9B;
	vertical-align: middle;
	font-size: 14px;
	color: #000;
	font-weight: bold;
}
</style>
</head>
<body>
	<!-- 	<a href="javascript:void(0)" class="easyui-linkbutton" id="station_add" iconCls="icon-add">新增</a> -->
	<a href="javascript:void(0)" class="easyui-linkbutton" id="station_edit" iconCls="icon-edit">修改</a>
	<div class="easyui-layout" style="height: 500px;">
		<div data-options="region:'center'" style="border: 0px;">
			<table width="80%" border="1" cellpadding="8" id="stationList" cellspacing="1" bgcolor="#CCCCCC"></table>
			<div id="dlg" class="easyui-dialog" title="导入地址模板"
				style="width: 650px; height: 440px; padding: 10px;">
				<input type="hidden" id="stationDlgId" value="" />
				<table width="100%" border="0" cellspacing="5" cellpadding="0">
					<tr>
						<td><input type="file" id="file" name="file" onchange="takefile('file');" /> 选择导入文件： <input
							class="easyui-filebox" onclick="fileSelected('file');" name="tools" id="tools"
							data-options="prompt:'请选择'" style="width: 400px" /></td>
						<td><a href="javascript:void(0)" class="easyui-linkbutton" id="startImport">开始导入</a>&nbsp;</td>
					</tr>
				</table>
				<table width="100%" id="resultTable" border="0" cellspacing="5" cellpadding="0" class="table"
					style="margin-top: 0px;">

				</table>
			</div>
		</div>
	</div>

	<div id="station_add_panel" style="display: none; width: 1000px;">

		<div class="pull-right" style="margin-top: 5px; margin-right: 10px;">
			<b style="margin-left: 5px;">站点名称：</b><input name="station_add_panel_input_area"
				id="station_add_panel_input_area" readonly="readonly">
		</div>

		<div id="station_add_panel_map" style="height: 450px"></div>

		<hr style="margin: 0px 0px 2px 0px; border-top: 1px solid #bbbbbb;">

			<div id="station_add_panel_top" class="pull-left" style="overflow: show; margin-left: 10px; height: 50px">
				<span>边框宽度:</span> 
				<select id="station_add_panel_top_linewidth"
					style="width: 50px; margin-bottom: 0px;">
					<option value=1>1</option>
					<option value=2>2</option>
					<option value=3 selected='selected'>3</option>
					<option value=4>4</option>
					<option value=5>5</option>
				</select> 
				<span>边框颜色:</span> <input type=text id="station_add_panel_top_linecolor" style="width: 50px;" />
				<span>填充颜色:</span> <input type=text id="station_add_panel_top_fillcolor" style="width: 50px;" />
				
				<span style="position: absolute; right: 5px;"> <a href="javascript:void(0)"
					class="easyui-linkbutton" id="station_add_panel_btn_reset" iconCls="icon-add">绘制/重绘</a> <a
					href="javascript:void(0)" class="easyui-linkbutton" id="station_add_panel_btn_edit"
					iconCls="icon-edit">编辑</a> <a href="javascript:void(0)" class="easyui-linkbutton"
					id="station_add_panel_btn_save" iconCls="icon-save">保存</a> <a href="javascript:void(0)"
					class="easyui-linkbutton" id="station_add_panel_btn_submit" iconCls="icon-ok">提交</a>
				</span>

			</div>
			<!-- 		    <div id="station_add_panel_btn" style="margin-top: 0px; position: absolute; right: 5px;"> -->
			<!-- 			<div class="btn btn-default dop-btn dop-region-edit" id="station_add_panel_btn_reset"> -->
			<!-- 				<i class="icon-share-alt"></i>绘制/重绘 -->
			<!-- 			</div> -->
			<!-- 			<div class="btn btn-default dop-btn" id="station_add_panel_btn_edit"> -->
			<!-- 				<i class="icon-pencil"></i>编辑 -->
			<!-- 			</div> -->
			<!-- 			<div class="btn btn-default dop-btn" id="station_add_panel_btn_save"> -->
			<!-- 				<i class="icon-file"></i>保存 -->
			<!-- 			</div> -->
			<!-- 			<div class="btn btn-default dop-btn dop-region-edit" id="station_add_panel_btn_submit"> -->
			<!-- 				<i class="icon-ok"></i>提交 -->
			<!-- 			</div> -->
			<!-- 							<div class="btn btn-default dop-btn" id="dop_btn_baseInfo_cancel"><i class="icon-remove"></i>关闭</div> -->
			<!-- 		</div> -->
	</div>


	<script type="text/javascript" src="http://api.map.baidu.com/api?v=2.0&ak=szTBW9236HO8EDCYuk4xQlP4"></script>
	<script type="text/javascript"
		src="http://api.map.baidu.com/library/DrawingManager/1.4/src/DrawingManager_min.js"></script>
	<script type="text/javascript" src="${ctx}/js/map/map.js"></script>
	<script type="text/javascript" src="${ctx}/js/map/spectrum.js"></script>
	<script type="text/javascript" src="${ctx}/js/address/stationGIS.js"></script>
	<script type="text/javascript" src="${ctx}/js/address/importStationAddress.js"></script>
	<script type="text/javascript" src="${ctx}/js/tools.js"></script>

</body>
</html>
