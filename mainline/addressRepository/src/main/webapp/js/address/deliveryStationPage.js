var inital=false;
var stationList = [];
var vendorList = [];
var mySettings = {
	async : {
		enable : true,
		url : getStationUrl
	}, 
	data : {
		 
	},
	callback : {
		beforeClick : myBeforeClick,
		onClick : myClick,
		beforeDrag:function(){return false;}
	}
};
	$(document).ready(function() {
		 getAll();
		 //折叠
		 $("#collapseAllBtn").bind("click", {type:"collapseAll"}, expandNode);
		//刷新
		 $("#refreshAllBtn").click(function(){
			 var treeObj = $.fn.zTree.getZTreeObj("tree");
 			 var node =  treeObj.getNodeByParam("id", 1, null);
			 treeObj.reAsyncChildNodes(node, null,null);
		 });
		 $.ajax({
			 	type: "POST",
				url:ctx+"/station/listAll",
				data:{},
				async:false,
				success : function(resp) {
					if(resp.length>0){
						stationList = resp; 
					} 
				}
			});
		 $.ajax({
			 	type: "POST",
				url:ctx+"/station/listAllVendor",
				data:{},
				async:false,
				success : function(resp) {
					if(resp.length>0){
						vendorList = resp; 
					} 
				}
			});
		 $("a[delRow]").live("click",function(){
			 var obj = $(this);
			 if( obj.parent().parent().attr("status")=="show"){
				 if(confirm("确定删除该规则？")){
					 var rId = obj.parent().parent().attr("dsrid");
					 $.ajax({
						 	type: "POST",
							url:ctx+"/deliveryStationRule/delete",
							data:{deliveryStationRuleId:rId},
							async:false,
							success : function(resp) {
								if(resp.success){
									 obj.parent().parent().remove();
								} 
							}
						});
				 }
			 }else{
				 obj.parent().parent().remove();
			 }
		 });
		 $("a[delAgeRow]").live("click",function(){
			 var obj = $(this);
			 if( obj.parent().parent().attr("status")=="show"){
				 if(confirm("确定删除该时效？")){
					 var id = obj.parent().parent().attr("vaid");
					 $.ajax({
						 	type: "POST",
							url:ctx+"/deliveryStationRule/deleteVendorAge",
							data:{id:id},
							async:false,
							success : function(resp) {
								if(resp.success){
									 obj.parent().parent().remove();
								} 
							}
						});
				 }
			 }else{
				 obj.parent().parent().remove();
			 }
		 });
	});
	  
		 function myBeforeClick(treeId, treeNode, clickFlag) {
				className = (className === "dark" ? "":"dark");
				return (treeNode.click != false);
			}
		 $("a[aid]").live("click",function(){
			 var obj = $(this);
			 var id = obj.attr("aid");
			 $.messager.confirm('确认删除','您确认想要删除【'+obj.text()+'】别名吗？',function(r){    
				    if (r){    
						 $.ajax({
							 	type: "POST",
								url:ctx+"/address/delAlias",
								data:{id:id},
								async:false,
								success : function(resp) {
									if(resp.success){
										obj.parent().remove();
									} 
								}
							});
				    }    
				});  
		 });
		 
			function myClick(event, treeId, treeNode, clickFlag) {
				$("#tips").html(treeNode.name);
				$('#relStation').panel('setTitle','关联站点【'+treeNode.name+"】") ;
				$('#relCust').panel('setTitle','供货商时效【'+treeNode.name+"】") ;
				$("#addressId").val(treeNode.id);
				$("#level").val(treeNode.level);
				 $("#aliasUl").html("");
				 getAddressStation(treeNode.id);
				 getAddressAges(treeNode.id);
				 addressId=treeNode.id;
				if(treeNode.level<3){
					$('#stationId').val("");
					$("input[name='stationId']").val("");
					$('#stationId').combobox('disable');
					$('#addresses').attr('disabled',true);
				}else{
					$('#stationId').combobox('enable');
					$('#addresses').attr('disabled',false);
				}
				
			}	
			 function getAll(){
				 $.ajax({
				 	 type: "POST",
				 		url:ctx+"/address/getAddressTree",
				 		data:{isBind:true},
				 		success:function(optionData){
				 	        var t = $("#tree");
				 	        zTree = $.fn.zTree.init(t, mySettings, optionData);
				 		}
				 	});
				 }
	function submitForm(){
		var addresses = $("#addresses").val();
		var parentId = $("#parentId").val();
		var stationId = $("input[name='stationId']").val();
		if($("#level").val()>5){
			$.messager.alert("提示","最多支持第六级关键字！");
			return false;
		}
		if(parentId==""){
			$.messager.alert("提示","请选择上级地址！");
			return false;
		}
		if(addresses==""){
			$.messager.alert("提示","请输入关键词！");
			return false;
		}
		$.ajax({
			 	type: "POST",
				url:ctx+"/address/add",
				data:{stationId:stationId,addresses:addresses,parentId:parentId},
				async:false,
				success : function(resp) {
					if(resp.success){
						 $.ajax({
						 	 type: "POST",
						 		url:ctx+"/address/getStationAddressTree",
						 		data:{id:parentId},
						 		async:false,
						 		success:function(optionData){
						 			 var treeObj = $.fn.zTree.getZTreeObj("tree");
						 			 var node =  treeObj.getNodeByParam("id", parentId, null);
						 			treeObj.removeChildNodes(node);
						 			 newNodes = treeObj.addNodes(node, optionData);
						 		}
						 	});
						 clearForm();
					}else{
						$.messager.alert("提示",resp.msg);
					}
				}
			});
	}
	
	function addRule(){
		$("<tr status='add'><td><select style='width:80%' name='stationId'><option value=''></option>"+generateSelector(stationList)+"</select></td>" +
				"<td><input type='text' name='rules' size='30'/></td>" +
				"<td><a href='#' delRow=''>删除</a></td></tr>").insertBefore($("#stationRuleOpe"));
	}
	function addAge(){
		$("<tr status='add'><td><select style='width:80%'><option value=''></option>"+generateSelector(vendorList)+"</select></td>" +
				"<td><input type='text' name='age' size='20'/></td>" +
				"<td><a href='#' delAgeRow=''>删除</a></td></tr>").insertBefore($("#ageOpe"));
	}
	function generateSelector(list){
		var str = "";
		for(var i = 0;i<list.length;i++){
			str +="<option value='"+list[i].id+"'>"+list[i].name+"</option>";
		}
		return str;
	}
	function getAddressStation(addressId){
		$.ajax({
		 	type: "POST",
			url:ctx+"/deliveryStationRule/getAllStationRule",
			data:{addressId:addressId},
			async:false,
			success : function(resp) {
				$("#stationRule>tbody").find("tr:not([id])").remove();
				if(resp.length>0){
					for(var i = 0;i<resp.length;i++){
						var item = resp[i];
						$(appendTr(item)).insertBefore($("#stationRuleOpe"));
					}
				}else{
					 
				}
			}
		});
	}
	function getAddressAges(addressId){
		$.ajax({
		 	type: "POST",
			url:ctx+"/deliveryStationRule/getAges",
			data:{addressId:addressId},
			async:false,
			success : function(resp) {
				$("#vendorAge>tbody").find("tr:not([id])").remove();
				if(resp.length>0){
					for(var i = 0;i<resp.length;i++){
						var item = resp[i];
						$(appendAgeTr(item)).insertBefore($("#ageOpe"));
					}
				} 
			}
		});
	};
	 function appendAgeTr(item){
	    	var tr = $("<tr></tr>").attr("status","show").attr("vaId",item.id);
	    	tr.append("<td>"+(item.vendor.name==null?'':item.vendor.name)+"</td>" +
					"<td>"+(item.aging==null?'':item.aging)+"</td>" +
					"<td><a href='javascript:' delAgeRow=''>删除</a></td>");
	    	return tr;
	    };
	
    function appendTr(item){
    	var tr = $("<tr></tr>").attr("status","show").attr("dsrId",item.id);
    	tr.append("<td>"+(item.deliveryStationName==null?'':item.deliveryStationName)+"</td>" +
				"<td>"+(item.rule==null?'':item.rule)+"</td>" +
				"<td><a href='javascript:' delRow=''>删除</a></td>");
    	return tr;
    };
    function submitRules(){
    	var rlist = [];
    	var addressId= $("#addressId").val();
    	if(addressId==""){
    		$.messager.alert("提示",'请选择关键字！');
    		return ;
    	}
    	$("#stationRule>tbody>tr[status='add']").each(function(){
    		var obj = $(this);
    		var rule = new Object();
    		rule.stationId=obj.find("select").val();
    		rule.ruleExpression=obj.find("input").val();
    		rule.addressId = addressId;
    		if(rule.stationId!=""){
        		rlist.push(rule);
    		}
    	});
    	var str = JSON.stringify(rlist) ;
    	$.ajax({
		 	type: "POST",
			url:ctx+"/deliveryStationRule/saveDeliveryStationRuleJson",
			data:{jsonStr:str},
			async:false,
			success : function(resp) {
				if(resp.success){
			    	 getAddressStation(addressId);
				}else{
					$.messager.alert('新增失败',resp.msg);
				}
			}
		});
    }
    function submitVendorAge(){
    	var rlist = [];
    	var addressId= $("#addressId").val();
    	if(addressId==""){
    		$.messager.alert("提示",'请选择关键字！');
    		return ;
    	}
    	$("#vendorAge>tbody>tr[status='add']").each(function(){
    		var obj = $(this);
    		var age = new Object();
    		age.vendorId=obj.find("select").val();
    		age.aging=obj.find("input").val();
    		age.addressId = addressId;
    		if(age.vendorId!=""){
        		rlist.push(age);
    		}
    	});
    	var str = JSON.stringify(rlist) ;
    	$.ajax({
		 	type: "POST",
			url:ctx+"/deliveryStationRule/saveVendorAge",
			data:{jsonStr:str},
			async:false,
			success : function(resp) {
				if(resp.success){
					getAddressAges(addressId);
				}else{
					$.messager.alert('新增失败',resp.msg);
				}
			}
		});
    }