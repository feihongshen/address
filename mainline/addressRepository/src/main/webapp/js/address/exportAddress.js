 $(document).ready(function(){
	$('#dlgStation').dialog('close');
	$('#dlgImport').dialog('close');
	$('#file').hide();
	$("#exportAddress").bind("click",function(){
		$('#dlgStation').dialog('open');
		 $.ajax({
			 	type: "POST",
				url:ctx+"/station/listAll",
				data:{ },
				async:false,
				success : function(list) {
					$("#stationShow").html("");
					if(list&&list.length>0){
						var ul=$("<ul></ul>").addClass("ul");
						for(var i = 0;i<list.length;i++){
							var item = list[i];
							$("<li><input type='checkbox' name='stationIds' value='"+item.id+"'><label>"+item.name+"</label></li>")
							.appendTo(ul);
						}
						ul.appendTo($("#stationShow"));
					}
				}
			});
	});
	$("#importAddress").bind("click",function(){
		$('#dlgImport').dialog('open');
	});
	
	$("#stationShow").find("input[name='stationIds']").live("click",function(){
		var ids = [];
		$("#stationShow").find("input[name='stationIds']").each(function(){
			var obj = $(this);
			if(obj.attr("checked")=="checked"){
				ids.push(obj.val());
			}
		});
		if(ids.length==0){
			$("#startExport").attr("href","javascript:$.messager.alert('提示', '请选择站点！')");
		}else{
			$("#startExport").attr("href", ctx+"/station/downloadStationAddresses?ids="+ids.join(","));
		}
	});
	$("#startKwImport").click(function(){
		$("#startKwImport").attr('disabled', true);
		$.ajaxFileUpload({
			url : ctx+'/address/importAddress?importType=3',
			secureuri : false,
			fileElementId : 'file',
			dataType: 'json',
			success : function(data, status) {
				if (data.success) {
					$("#startKwImport").attr('disabled', false);
					$('#dlgImport').dialog('close');
					getAll();
				} else {
					alert(AjaxJson.msg);
				}
			},
			error : function(AjaxJson, status, e) {
				alert("网络异常！");
			}
		});
		return false;
	});
	
});
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
 
 