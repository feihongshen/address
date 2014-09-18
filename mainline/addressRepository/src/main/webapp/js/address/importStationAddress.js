var resultRow=
{
		idField : 'id',
		url : ctx+'/station/list',
		fit : true,
		height : 400,
		loadMsg : '数据加载中...',
		pageSize : 100000,
		pagination : true,
		pageList : [ 100, 200, 300 ],
		sortOrder : 'asc',
		rownumbers : true,
		singleSelect : true,
		fitColumns : true,
		showFooter : true,
		frozenColumns : [ [] ],
		columns : [ [
			{
				field : 'id',
				title : '站点ID',
				width:10,
			},
			{
				field : 'name',
				title : '站点名称',
			    width:40,
			},
			{
				field : 'status',
				title : '操作',
				width:50,
				sortable : false,
				formatter : function(value, rec, index) {
					if (!rec.id) {
						return '';
					}
					var href = '';
					href += "[<a href='"+ctx+"/station/downloadStationAddress?id=" + rec.id+ "'>导出关键字地址</a>]";
					href += "[<a href='javascript:importAddress(" + rec.id+ ")'>导入关键字地址</a>]";
					return href;
				}
			} ] ],
		onLoadSuccess : function(data) {
			$("#stationList").datagrid("clearSelections");
		},
		onClickRow : function(rowIndex, rowData) {
			rowid = rowData.id;
			gridname = 'stationList';
		}
	};
	$(document).ready(function(){
		$('#stationList').datagrid(resultRow);
		$("#file").hide();
		$("#startImport").click(function(){
			ajaxFileUpload();
		});
		$('#dlg').dialog('close');
	});
	
	function importAddress(id){
		$("#stationDlgId").val(id);
		$('#dlg').dialog('open');
	}
	function fileSelected(id) {
		$("#"+id).trigger("click");
	}
	function takefile(id ) {
		var file = document.getElementById(id).files[0];
		var fileName = file.name;
		var file_typename = fileName.substring(fileName.lastIndexOf('.'),
				fileName.length);
		if (file_typename == '.xls' || file_typename == '.xlsx') {//这里限定上传文件文件类型
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
	//文件上传
	function ajaxFileUpload(id) {
		$("#startImport").attr('disabled', true);
		var stationId= $("#stationDlgId").val();
		$.ajaxFileUpload({
			url : ctx+'/address/importAddress?id='+id,
			secureuri : false,
			data:{importType:2,stationId:stationId},
			fileElementId : 'file',
			dataType: 'json',
			success : function(data, status) {
				if (data.success) {
					$("#tools").val('上传成功'+data.info);
					$("#startImport").attr('disabled', false);
				} else {
					alert(AjaxJson.msg);
				}
			},
			error : function(AjaxJson, status, e) {
				alert("网络异常！");
			}
		});
		return false;

	}