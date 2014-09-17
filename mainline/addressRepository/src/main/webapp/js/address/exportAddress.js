 $(document).ready(function(){
	$('#dlgStation').dialog('close');
	$('#dlgImport').dialog('close');
	$('#file1').hide();
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
						for(var i = 0;i<list.length;i++){
							var item = list[i];
							$("<span style='margin-right:30px;'><input type='checkbox' name='stationIds' value='"+item.id+"'><label>"+item.name+"</label></span>")
							.appendTo($("#stationShow"));
						}
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
			url : ctx+'/address/importKwAddress',
			secureuri : false,
			fileElementId : 'file1',
			dataType: 'json',
			success : function(data, status) {
				if (data.success) {
					$("#startKwImport").attr('disabled', false);
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
 
 