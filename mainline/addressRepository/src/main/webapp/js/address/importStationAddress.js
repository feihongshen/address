var resultRow = {
	idField : 'id',
	url : ctx + '/station/list',
	fit : true,
	height : 400,
	loadMsg : '数据加载中...',
	pageSize : 100000,
	pagination : true,
	pageList : [50, 100, 200],
	sortOrder : 'asc',
	rownumbers : false,
	singleSelect : true,
	fitColumns : true,
	showFooter : true,
	frozenColumns : [[]],
	columns : [[
			{
				field : 'id',
				title : '站点ID',
				width : 10,
			},
			{
				field : 'name',
				title : '站点名称',
				width : 40,
			},
			{
				field : 'status',
				title : '操作',
				width : 50,
				sortable : false,
				formatter : function(value, rec, index) {
					if (!rec.id) {
						return '';
					}
					var href = '';
					href += "[<a href='javascript:editStation()'>绘制区域</a>]";
					href += "[<a href='" + ctx
							+ "/station/downloadStationAddress?id=" + rec.id
							+ "'>导出关键字地址</a>]";
					href += "[<a href='" + ctx
					+ "/station/downloadStationDeliverer?id=" + rec.id
					+ "'>导出小件员关键词</a>]";
					return href;
				}
			}]],
	onLoadSuccess : function(data) {
		$("#stationList").datagrid("clearSelections");
	},
	onClickRow : function(rowIndex, rowData) {
		rowid = rowData.id;
		gridname = 'stationList';
	}
};
$(document).ready(function() {
	$('#stationList').datagrid(resultRow);
	$("#file").hide();
	$("#startImport").click(function() {
		ajaxFileUpload('file');
	});
	$('#dlg').dialog('close');
});

function importAddress(id) {
	$("#stationDlgId").val(id);
	$("#resultTable").html("");
	$('#dlg').dialog('open');
}
function fileSelected(id) {
	$("#" + id).trigger("click");
}
function takefile(id) {
	var file = document.getElementById(id).files[0];
	var fileName = file.name;
	var file_typename = fileName.substring(fileName.lastIndexOf('.'),
			fileName.length);
	if (file_typename == '.xls' || file_typename == '.xlsx') {// 这里限定上传文件文件类型
		if (file) {

			var fileSize = 0;
			if (file.size > 1024 * 1024)
				fileSize = (Math.round(file.size * 100 / (1024 * 1024)) / 100)
						.toString()
						+ 'MB';
			else
				fileSize = (Math.round(file.size * 100 / 1024) / 100)
						.toString()
						+ 'KB';

			var temp = '文件名: ' + file.name + '大小: ' + fileSize + '类型: '
					+ file.type;
			$("#tools").val(temp);

		}

	} else {
		var temp = "上传文件应该是.xls后缀而不应该是" + file_typename + ",请重新选择文件";
		alert(temp);
	}
}
// 文件上传
function ajaxFileUpload(id) {
	$("#startImport").attr('disabled', true);
	var file = document.getElementById(id).files[0];
	if (file == null) {
		$.messager.alert("提示", "请选择文件！");
		return;
	}
	var stationId = $("#stationDlgId").val();
	$.ajaxFileUpload({
		url : ctx + '/address/importAddress?stationId=' + stationId
				+ "&importType=2",
		secureuri : false,
		data : {
			importType : 2,
			stationId : stationId
		},
		fileElementId : 'file',
		dataType : 'json',
		success : function(data, status) {
			if (data.success) {
				$("#tools").val('上传成功' + data.info);
				$("#startImport").attr('disabled', false);
				$.ajax({
					type : "POST",
					url : ctx + "/address/getImportDetail",
					async : false,
					success : function(resp) {
						if (resp != null && resp.length > 0) {
							$("#resultTable").html("");
							$("#resultTable").append(generateResult(resp));
						}
					}
				});

			} else {
				$.messager.alert("提示", data.info);
			}
		},
		error : function(AjaxJson, status, e) {
			alert("网络异常！");
		}
	});
	return false;

}
function generateResult(list) {
	var html = "<thead><tr><td>省份</td><td>城市</td><td>区域</td><td>关键字1-关键字2-关键字3</td><td>站点</td><td>错误原因</td></tr></thead>";
	html += "<tbody>";
	for (var i = 0; i < list.length; i++) {
		var item = list[i];
		if (item.status == 1) {
			html += "<tr><td>"
					+ (item.province == null ? "" : item.province)
					+ "</td><td>"
					+ (item.city == null ? "" : item.city)
					+ "</td><td>"
					+ (item.district == null ? "" : item.district)
					+ "</td><td>"
					+ (item.address1 == null ? "" : item.address1)
					+ "-"
					+ (item.address2 == null ? "" : item.address2)
					+ "-"
					+ (item.address3 == null ? "" : item.address3)
					+ "</td><td>"
					+ (item.deliveryStationName == null
							? ""
							: item.deliveryStationName) + "</td><td>"
					+ (item.message == null ? "" : item.message) + "</td></tr>";
		}
	}
	html += "</tbody>";
	return html;
}