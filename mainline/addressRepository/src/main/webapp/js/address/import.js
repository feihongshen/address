//文件上传
function ajaxFileUpload() {
	$("#startImport").attr('disabled', true);
	$.ajaxFileUpload({
		url : 'importAddress',
		secureuri : false,
		fileElementId : 'file',
		dataType: 'json',
		success : function(data, status) {
			alert(1);
			if (data.success) {
				$("#tools").val('上传成功');
				$('#importAddressList').datagrid(
						{
							url : 'datagrid?addressImportResult='+data.info,
							pageNumber : 1
						});
				$("#startImport").attr('disabled', false);
			} else {
				alert(AjaxJson.msg);
			}
		},
		error : function(AjaxJson, status, e) {
			alert("网络异常！");
		}
	})

	return false;

}
function takefile() {
	var file = document.getElementById('file').files[0];
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
		alert(temp)

	}
}

function fileSelected() {
	$("#file").trigger("click");

}