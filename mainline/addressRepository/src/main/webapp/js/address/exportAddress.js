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
					$("#startExport").attr("href", "javascript:void(0);");
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
		$("#resultTable").html("");
	});
	var wait=30
	function time(o) {  
		
        if (wait == 0) {  
//              $("#startExport").show();
        	$("#time").html("");
            wait = 30;  
        } else {  
            wait--;  
            $("#startExport").attr("href","javascript:void(0);");
//            $("#startExport").hide();
        	$("#time").html(wait+"秒");
            setTimeout(function() {  
                time(o)  
            },  
            1000)  
        }  
    }  
	
	$("#startExport").mousedown(function(e){
		
		var ids = [];
		$("#stationShow").find("input[name='stationIds']").each(function(){
			var obj = $(this);
			if(obj.attr("checked")=="checked"){
				ids.push(obj.val());
			}
		});
		
		if(ids.length==0 || ids.length>10){
			if(e.which==1) {
				// 1 = 鼠标左键 left; 2 = 鼠标中键; 3 = 鼠标右键
			     return false;//阻止链接跳转
			} 
		}   
		
		  
		 })
	$("#startExport").bind("click",function(){
		var ids = [];
		$("#stationShow").find("input[name='stationIds']").each(function(){
			var obj = $(this);
			if(obj.attr("checked")=="checked"){
				ids.push(obj.val());
			}
		});
		if(ids.length<=10 && ids.length>0&&wait==30){
			time(this);
		}
		if(ids.length>10){
			$.messager.alert('提示', '请选择站点数不能超过10个！');
		}
		if(ids.length==0){
			$.messager.alert('提示', '请选择站点！');
		}
		
		
		 
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
			//$.messager.alert('提示', '请选择站点！');
		}else{
			if(ids.length<=10){
				$("#startExport").attr("href", ctx+"/station/downloadStationAddresses?ids="+ids.join(","));
			}else{
				
					//$.messager.alert('提示', '请选择站点数不能超过10个！');
				
			}
		}
	});
	$("#startKwImport").click(function(){
		$("#startKwImport").attr('disabled', true);
		var file = document.getElementById('file').files[0];
		if(file==null){
			$.messager.alert("提示","请选择文件！");
			return ;
		}
		$.ajaxFileUpload({
			url : ctx+'/address/moveAddress',
			secureuri : false,
			fileElementId : 'file',
			dataType: 'json',
			success : function(data, status) {
				if (data.success) {
					$("#tools").val('上传成功'+data.info);
					$("#startKwImport").attr('disabled', false);
					 var ids=getAllNodes();
					 getAll(ids);
					$.ajax({
						type : "POST",
						url : ctx+"/address/getImportDetail",
						 async:false,
						 success : function(resp) {
						if (resp != null && resp.length > 0) {
							$("#resultTable").html("");
							$("#resultTable").append(generateResult(resp));
						}
					}
				  });
				} else {
					$.messager.alert("提示",data.info);
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
	function generateResult(list){
		var html = "<thead><tr><td>省份</td><td>城市</td><td>区域</td><td>关键字1-关键字2-关键字3</td><td>原站点</td><td>新站点</td><td>状态</td><td>错误原因</td></tr></thead>";
		html+="<tbody>";
		for(var i = 0;i<list.length;i++){
			var item = list[i];
					html+="<tr><td>"
					+(item.province==null?"":item.province)+"</td><td>"
					+(item.city==null?"":item.city)+"</td><td>"
				    +(item.district==null?"":item.district)+"</td><td>"
				    +(item.address1==null?"":item.address1)+"-"
				    +(item.address2==null?"":item.address2)+"-"
				    +(item.address3==null?"":item.address3)+"</td><td>"
				    +(item.deliveryStationOldName==null?"":item.deliveryStationOldName)+"</td><td>"
				    +(item.deliveryStationName==null?"":item.deliveryStationName)+"</td><td>"
				    +(item.status==0?"成功":"失败")+"</td><td>"
				    +(item.message==null?"":item.message)+"</td></tr>";
		}
		html+="</tbody>";
		return html;
	}
 