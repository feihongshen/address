
//文件上传
function ajaxFileUpload() {
	$("#startImport").attr('disabled', true);
	$.ajaxFileUpload({
		url : 'importAddress',
		secureuri : false,
		fileElementId : 'file',
		dataType: 'json',
		success : function(data, status) {
			//alert(1);
			if (data.success) {
				var usedTable=$("#importAddressList");
				$("#tools").val('上传成功'+data.info);
				$('#importAddressList').datagrid(
						{
							url : 'datagrid?',
							pageNumber : 1
						});
//				var tableData=data.importTable;
//				for(var i=0;i<tableData.length;i++){
//					var tr="<tr class='trpp'> <td align='center' bgcolor='#FFFFFF'>"+(1+i)+"</td> " +
//							"<td align='center' bgcolor='#FFFFFF'>"+tableData[i]['province']+"</td>"+
//							"<td align='center' bgcolor='#FFFFFF'>"+tableData[i]['city']+"</td> " +
//							"<td align='center' bgcolor='#FFFFFF'>"+tableData[i]['district']+"</td> " +
//							"<td align='center' bgcolor='#FFFFFF'>"+tableData[i]['address1']+"</td> " +
//							"<td align='center' bgcolor='#FFFFFF'>"+tableData[i]['deliveryStationName']+"</td> " +
//							"<td align='center' bgcolor='#FFFFFF'>"+tableData[i]['delivererName']+"</td> " +
//							"<td align='center' bgcolor='#FFFFFF'>"+tableData[i]['status']+"</td> " +
//							"<td align='center' bgcolor='#FFFFFF'>"+tableData[i]['message']+"</td> " +
//							"<td align='center' bgcolor='#FFFFFF'>"+tableData[i]['delivererName']+"</td>  </tr>" 
//					usedTable.append(tr);
//				}
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