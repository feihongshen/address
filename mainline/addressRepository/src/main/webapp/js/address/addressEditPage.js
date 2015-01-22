var inital=false;
var stationList=[];
	var mySettings = {
		async : {
			enable : true,
			url : getStationUrl
		},
		edit : {
			enable : true,
			showRenameBtn : false
		},
		data : {
			simpleData: {
				enable: true
			}
		},
		callback : {
			beforeClick : myBeforeClick,
			onClick : myClick,
			beforeRemove : zTreeBeforeRemove,
			onRemove : zTreeOnRemove,
			beforeDrag:function(){return false;},
			onAsyncSuccess: onAsyncSuccess
		}
	};
	 
	
	$(document).ready(function() {
		 getAll();
		 getPromtInfo();
		 //折叠
		 $("#collapseAllBtn").bind("click", {type:"collapseAll"}, expandNode);
		//刷新
		 
		 $("#refreshAllBtn").click(function(){
			//var  ids=getAllNodes();
			var  ids;
			 getAll(ids);
		 });
		  
		 //未绑定
	        $("#unbindAllBtn").bind("click",function(){
	        	unbind();
	        }); 
		 $.ajax({
			 	type: "POST",
				url:ctx+"/station/listAll",
				data:{},
				async:false,
				success : function(resp) {
					$("#stationId").empty();
					$("#stationId").append("<option value=''>  </option>");
					if(resp.length>0){
						for(var i = 0;i<resp.length;i++){
							$("#stationId").append("<option value='"+resp[i].id+"'>"+resp[i].name+"</option>"); 
						}
					} 
				}
			});
		
		
		
		
	});
	function zTreeBeforeRemove(treeId, treeNode) {
		if(treeNode.level<4){
			$.messager.alert("提示","不可删除省、市、区！");
			return false;
		}
		
		
		if (confirm("确认删除该节点以及以下数据？")) {
			var flag = true;
			$.ajax({
				type : "POST",
				url : ctx+"/address/delAddress",
						data:{addressId:treeNode.id},
						async:false,
						success : function(resp) {
							flag = resp.success;
							if(!flag){
								$.messager.alert("提示","地址删除失败，请联系管理员！");
							}
							
						}
					});
				 return flag;
			 }
			 return  false;
		 }
		 function zTreeOnRemove(event, treeId, treeNode) {
			$("#tips").html("");
			$('#panelAlias').panel('setTitle','别名管理') ;
			$("#addressId").val("");
			$("#aliasTips").val("");
			("#parentId").val("");
			$("#aliasUl").html("");
		 } 
		 
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
				$('#panelAlias').panel('setTitle','别名管理-'+treeNode.name) ;
				$("#addressId").val(treeNode.id);
				$("#aliasTips").val(treeNode.name);
				$("#parentId").val(treeNode.id);
				$("#level").val(treeNode.level);
				 $("#aliasUl").html("");
				 addressId=treeNode.id;
				 $.ajax({
					 	type: "POST",
						url:ctx+"/address/getAlias",
						data:{addressId:treeNode.id},
						async:false,
						success : function(resp) {
							if(resp.length>0){
								for(var i = 0 ;i<resp.length;i++){
									var btn = $("<a href='javascript:void(0)' aid='"+resp[i].id+"'>"+resp[i].name+"</a></li>");
									var li = $("<li></li>").append(btn);
									li.appendTo($("#aliasUl"));
									btn.linkbutton({    
									    iconCls:'icon-remove',
									    iconAlign:'right'
									});  
								}
							} 
						}
					});
				if(treeNode.level<3){
					$('#stationId').val("");
					$("input[name='stationId']").val("");
					$('#stationId').attr('disabled',true);
					//$('#stationId').combobox('disable');
					$('#addresses').attr('disabled',true);
				}else{
					$('#stationId').attr('disabled',false);
					$('#addresses').attr('disabled',false);
				}
				
			}	
			 function getAll(ids){
				 $.ajax({
				 	 type: "POST",
				 		url:ctx+"/address/getAddressTree?pageSize=1000000",
				 		data:{ids:ids},
				 		success:function(optionData){
				 	        var t = $("#tree");
				 	        zTree = $.fn.zTree.init(t, mySettings, optionData);
				 		}
				 	});
				 }
	function clearForm(){
		$("#ff")[0].reset();
		$("#tips").html("");
	}
	function submitForm(){
		var addresses = $("#addresses").val();
		var parentId = $("#parentId").val();
		var stationId = $("#stationId").val();
		if($("#level").val()<3){
			$.messager.alert("提示","仅支持区域以下设置关键字！");
			return false;
		}
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
						 var treeObj = $.fn.zTree.getZTreeObj("tree");
			 			 var node =  treeObj.getNodeByParam("id", parentId, null);
			 			 treeObj.reAsyncChildNodes(node, "refresh");
						 clearForm();
					}else{
						$.messager.alert("提示",resp.msg);
					}
				}
			});
	}
	function addAlias(){
		var alias = $("#alias").val();
		var addressId = $("#addressId").val();
		if(addressId==""){
			$.messager.alert("提示","请选择一个地址！");
			return false;
		}
		if(alias==""){
			$.messager.alert("提示","请输入别名！");
			return false;
		}
		if(alias.trim()==$("#aliasTips").val()){
			$.messager.alert("提示","别名和原名不能一致！");
			return false;
		}
		 $.ajax({
			 	type: "POST",
				url:ctx+"/address/addAlias",
				data:{addressId:addressId,alias:alias},
				success : function(resp) {
					if(resp.success){
						 $("#alias").val("");
						 var btn = $("<a href='javascript:void(0)' aid='"+resp.obj.id+"'>"+resp.obj.name+"</a></li>");
							var li = $("<li></li>").append(btn);
							li.appendTo($("#aliasUl"));
							btn.linkbutton({    
							    iconCls:'icon-remove',
							    iconAlign:'right'
							});  
					}else{
						$.messager.alert("提示",resp.msg);
					}
				}
			});
	}
	
	
	var allNodes;
	function searchVal(valName,treeName){
		    $("#loadingImage").show();
			if (event.keyCode!=13) return;  //回车键的键值为13
			event.stopPropagation();
			
			//var target = $.fn.zTree.getZTreeObj(treeName);
			//经过transformToArray转换后是一个Array数组，数组里的每个元素都是object对象，这个对象里包含了node的21个属性。
	        //var nodes = target.transformToArray(target.getNodes()[0].children);

			//到后台获取所有的节点
			getAllAddress();
		    var	target = $.fn.zTree.init($("#tree"), mySettings, allNodes);
			
		    var nodes = target.transformToArray(target.getNodes());
		    
		    var key=$("#"+valName).val();
		    //空格回车符 不做查询 直接显示全部
		    if(/^\s*$/.test(key)){
		     //updateNodes(false); 
		     target.showNodes(nodes);
		     return;
		    }
		    //首先隐藏
		    target.hideNodes(nodes);

		    nodeList=target.getNodesByParamFuzzy("name", key); //模糊匹配
		  
		    var filterNodes=[];
		    for(var i=0;i<nodeList.length;i++){
		       filterNodes.push(nodeList[i]);
		    }
		    target.showNodes(filterNodes);
		    for(var i=0;i<filterNodes.length;i++){
		     toggle(target,filterNodes[i].getParentNode());
		    }
		    $("#loadingImage").hide();
	}
	function toggle(target,node){
		target.expandNode(node, true, false, false);
		target.showNode(node);
		var parentNode = node.getParentNode();
		if(parentNode){
			 toggle(target,parentNode);
		}
	}


	function getAllAddress(){
		 $.ajax({
		 	 type: "POST",
		 	 async:false,
		 	 url:ctx+"/address/getAllAddress",
		 	 data:{},
		 	 success:function(optionData){
		 		allNodes=optionData;
		 	}
		 	});
	}
	