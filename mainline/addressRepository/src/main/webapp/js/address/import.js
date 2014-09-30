
//文件上传
function ajaxFileUpload() {
	$("#startImport").attr('disabled', true);
	process=null;
	flushProcess();	
	$("#procDiv").fadeIn("fast");
	$.ajaxFileUpload({
		url : 'importAddress',
		secureuri : false,
		fileElementId : 'file',
		dataType: 'json',
		success : function(data, status) {
			if (data.success) {
				var usedTable=$("#importAddressList");
				$("#tools").val('上传成功');
				$('#importAddressList').datagrid(
						{
							url : 'datagrid?resultId='+data.msg,
							pageNumber : 1
						});
				$("#startImport").attr('disabled', false);
				$("#procDiv").fadeOut("slow");
			} else {
				$.messager.alert('提示',data.info);
			}
			 clearInterval(t);
		},
		error : function(AjaxJson, status, e) {
			$.messager.alert('提示',"网络异常！");
		}
	});
	return false;
}
function flushProcess(){
	 t = setInterval(flush,2000);
}
function flush(){
	 $.ajax({
		 	type: "POST",
			url:"getImportProc",
			async:true,
			success : function(resp) {
				 if(resp!=null&&resp!=''){
					 process=resp;
					 showProcess(process);
				 } 
			}
    });
};
function showProcess(process){
	if(process.total==0){
		 $('#proc').progressbar('setValue', 0);
	}else{
		 $('#proc').progressbar('setValue', process.percent );
	}
	$("#importProc").html("总数："+process.total+"\t已完成:"+process.processed+"\t成功:"+process.success
				+"\t失败:"+process.failure);
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
		alert(temp);
	}
}

function fileSelected() {
	$("#file").trigger("click");

}