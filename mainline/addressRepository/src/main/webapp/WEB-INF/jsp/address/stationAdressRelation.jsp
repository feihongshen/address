<%@ page language="java" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
<head>
<title>拆合站维护</title>
<%@include file="/WEB-INF/jsp/common/lib.jsp"%>
<script type="text/javascript" src="${ctx}/js/address/mutitleTree.js"></script>
<script type="text/javascript" src="${ctx}/js/address/stationAdressRelation.js"></script>
<script type="text/javascript" src="${ctx}/js/address/exportAddress.js"></script>
<script type="text/javascript" src="${ctx}/js/address/fuzzySearch.js"></script>
<style type="text/css">
.ul {
	list-style: none;
	text-align: left;
}

.ul>li {
	width: 120px;
	float: left;
	overflow: hidden;
	padding: 0px;
}

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

<script type="text/javascript">
	var backNode;
	// var inital=false;
	var setting = {
		async : {
			enable : true,
			url : getStationUrl
		},
		data : {
			simpleData : {
				enable : true
			}
		},
		callback : {
			beforeClick : beforeClick,
			onAsyncSuccess : onAsyncSuccess,
			onAsyncError : onAsyncError,
			onClick : onClick
		}
	};

	$(document).ready(function() {

		getAll();
		getPromtInfo();
		//折叠
		$("#collapseAllBtn").bind("click", {
			type : "collapseAll"
		}, expandNode);
		//刷新
		$("#refreshAllBtn").click(function() {
// 			var ids = getAllNodes();
			var ids;
			getAll(ids);
		});

		$("#upup").click(function() {
			//初始化站点信息前，先清空站点，模糊查询文本框和地址树信息
			$("#targetStation").empty();
			$("#sourceStation").empty();
			$("#sourceStrVal").val("");
			$("#targetStrVal").val("");
			$("#sourceStationtree").empty();
			$("#targetStationtree").empty();

			initStations();

			$('.easyui-layout').layout('collapse', 'north');
		});

		$("#saveRelation").click(saveRelation);
		$("#cancel").click(function() {
			$('.easyui-layout').layout('expand', 'north');
		})

		$(".sourceStation4combobox").change(function() {
			initDemoTree('sourceStation');
		})
		
		$(".targetStation4combobox").change(function() {
			initDemoTree('targetStation');
		})
		
		
		$(".sourceDeliverer4combobox").change(function(){
			getDeliveryTree('sourceDeliverer');
		})
		$(".targetDeliverer4combobox").change(function(){
			getDeliveryTree('targetDeliverer');
		})

	});

	function getAll(ids) {
		$.ajax({
			type : "POST",
			url : ctx + "/address/getAddressTree?pageSize=1000000",
			data : {
				'ids' : ids
			},
			success : function(optionData) {
				var t = $("#tree");
				zTree = $.fn.zTree.init(t, setting, optionData);

			}
		});
	}
</script>

</head>
<body>
	<div class="easyui-layout" style="height: 600px;">
		<div data-options="region:'north',split:true" title="条件搜索" style="width: 330px; height: 600px;">
			<table width="100%" border="0" cellspacing="0" cellpadding="10">
				<tr>
					<td><a href="javascript:void(0)" id="upup" class="easyui-linkbutton">拆合站</a>&nbsp; &nbsp;<a
						href="javascript:" class="easyui-linkbutton" id="exportAddress">导出关键字</a> <a
						href="javascript:" class="easyui-linkbutton" id="importAddress">导入关键字</a></td>
				</tr>
				<tr>
					<td><input style="width: 150px" id="searchA" onkeydown="searchByKeyword('searchA','tree');">
							<a href="javascript:void(0)" id="collapseAllBtn" class="easyui-linkbutton">折叠</a> <a
							href="javascript:void(0)" id="refreshAllBtn" class="easyui-linkbutton">刷新</a></td>
				</tr>
				<tr>
					<td><div id="promtInfo"></div>
						<ul id="tree" class="ztree " style="width: 400px; height: auto; overflow: auto;"></ul></td>
				</tr>
			</table>

			<div id="dlgStation" class="easyui-dialog" title="请选择需要导出关键字的站点"
				style="width: 500px; height: 320px;">
				<div id="stationShow" style="overflow: auto; height: 240px;"></div>
				<div style="margin: auto; text-align: center;">
					<a href="" class="easyui-linkbutton" id="startExport">导出</a>
				</div>
			</div>
			<div id="dlgImport" class="easyui-dialog" title="导入关键字"
				style="width: 650px; height: 440px; padding: 10px;">
				<table width="100%" border="0" cellspacing="5" cellpadding="0">
					<tr>
						<td><input type="file" id="file" name="file" onchange="takefile('file');" /> 选择导入文件： <input
							class="easyui-filebox" onclick="fileSelected('file');" name="tools" id="tools"
							data-options="prompt:'请选择'" style="width: 400px" /></td>
						<td><a href="javascript:void(0)" class="easyui-linkbutton" id="startKwImport">开始导入</a>&nbsp;</td>
					</tr>
				</table>
				<table width="100%" id="resultTable" border="0" cellspacing="5" cellpadding="0" class="table"
					style="margin-top: 0px;">

				</table>
			</div>
		</div>

		<div data-options="region:'center'">
			<table width="100%" height="550px;" border="0" cellspacing="1" cellpadding="5"
				style="background: #CCC">
				<tr height="20px">
					<td width="45%" bgcolor="#FFFFFF">原站点： <select id="sourceStation" class="sourceStation4combobox"
						style="width: 120px;">
					</select>  &nbsp;&nbsp;&nbsp;原小件员： <select id="sourceDeliverer" class="sourceDeliverer4combobox"
						style="width: 120px;">
					</select></td>
				
					<td width="50" rowspan="3" align="center" bgcolor="#FFFFFF">
						<p>
							<a href="javascript:void(0)" class="easyui-linkbutton" id="toRight">&gt;</a>
						</p>
						<p>
							<a href="javascript:void(0)" class="easyui-linkbutton" id="toLeft">&lt;</a>
						</p>
					</td>
					<td width="45%" bgcolor="#FFFFFF">拆到目标站点： <select id="targetStation"
						class="targetStation4combobox" style="width: 120px;">
					</select> &nbsp;&nbsp;&nbsp; 拆到目标小件员： <select id="targetDeliverer"
						class="targetDeliverer4combobox" style="width: 120px;">
					</select></td>
				</tr>
				<tr>
					<td bgcolor="#FFFFFF">模糊搜索: <input name="textfield5" id="sourceStrVal" type="text"
						id="textfield5" size="15" onkeydown="searchByEnter(event,'sourceStrVal','sourceStationtree')" />
					</td>
					<td bgcolor="#FFFFFF">模糊搜索: <input name="textfield5" type="text" id="targetStrVal"
						size="15" onkeydown="searchByEnter(event,'targetStrVal','targetStationtree')" />
					</td>
				</tr>
				<tr height="90%" style="overflow-y: scroll">
					<div style="overflow-y: scroll">
						<td bgcolor="#FFFFFF" style="vertical-align: top;">
							<div style="overflow-y: scroll; height: 444px">
								<ul class="ztree" id="sourceStationtree">
								</ul>
							</div>
						</td>
						<td bgcolor="#FFFFFF" style="vertical-align: top;">
							<div style="overflow-y: scroll; height: 444px">
								<ul class="ztree" id="targetStationtree" style="width: auto; height: auto; overflow: auto;">
								</ul>
							</div>
						</td>
					</div>
				</tr>
				<tr height="20px">
					<td></td>
					<td></td>
					<td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
						<div style="float: right">

							<a href="javascript:void(0)" id="saveRelation" class="easyui-linkbutton">确定</a>&nbsp; <a
								href="javascript:void(0)" id="cancel" class="easyui-linkbutton">取消</a>&nbsp;
						</div>
				</tr>
			</table>

		</div>
	</div>
</body>
</html>
