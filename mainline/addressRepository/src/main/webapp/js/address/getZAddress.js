var setting = {
	data: {
		simpleData: {
			enable: true
		}
	},
	callback: {
		beforeClick: beforeClick,
		onClick: onClick
	}
};

var addressId;
var log, className = "dark";
function beforeClick(treeId, treeNode, clickFlag) {
	className = (className === "dark" ? "":"dark");
	return (treeNode.click != false);
}
function onClick(event, treeId, treeNode, clickFlag) {
	addressId=treeNode.id;
}		


function expandNode(e) {
	var zTree = $.fn.zTree.getZTreeObj("tree"),
	type = e.data.type,
	nodes = zTree.getSelectedNodes();
	if (type.indexOf("All")<0 && nodes.length == 0) {
		alert("请先选择一个父节点");
	}

	if (type == "expandAll") {
		zTree.expandAll(true);
	} else if (type == "collapseAll") {
		zTree.expandAll(false);
	} else {
		var callbackFlag = $("#callbackTrigger").attr("checked");
		for (var i=0, l=nodes.length; i<l; i++) {
			zTree.setting.view.fontCss = {};
			if (type == "expand") {
				zTree.expandNode(nodes[i], true, null, null, callbackFlag);
			} else if (type == "collapse") {
				zTree.expandNode(nodes[i], false, null, null, callbackFlag);
			} else if (type == "toggle") {
				zTree.expandNode(nodes[i], null, null, null, callbackFlag);
			} else if (type == "expandSon") {
				zTree.expandNode(nodes[i], true, true, null, callbackFlag);
			} else if (type == "collapseSon") {
				zTree.expandNode(nodes[i], false, true, null, callbackFlag);
			}
		}
	}
}


function searchTree(){
	var treeObj = $.fn.zTree.getZTreeObj("tree");
	var nodes = treeObj.getNodes();
	var cloneNodes = [];
	var removeNodes=[];
	$.each(nodes, function(index, node) {
		cloneNodes.push(node);
	});
	var filterString = $("#searchA").val();
	$.each(cloneNodes, function(index, node) {
		var childrens=node.children;
		if (node.name.indexOf(filterString) != -1) {
			return;
		}else{
			var count=0;
			$.each(childrens,function(ci,cn){
				if(!cn)return;
				if(cn.name.indexOf(filterString)==-1){
					removeNodes.push(cn);
					
					count++;
				}
			});
			if(count==childrens.length){
				removeNodes.push(node);
			}
		}
		
		
	});
	$.each(removeNodes,function(j,node){
		treeObj.removeNode(node);
	});
}


function initOption(){
	 $.ajax({
		 type: "POST",
			url:cxt+"/deliveryStationRule/station4combobox",
			success:function(optionData){
				for(var i=0;i<optionData.length;i++){
					var option=$("  <option value="+optionData[i]['id']+">"+optionData[i]['text']+"</option>");
					$("#deliveryStationId").append(option);
					
				}
				
				backNode=$("#deliveryStationRule").clone(true);
			}
		});
}
function getAll(){
$.ajax({
	 type: "POST",
		url:cxt+"/address/getZTree",
		data:{isBind:true},
		success:function(optionData){
	        var t = $("#tree");
	        t = $.fn.zTree.init(t, setting, optionData);
			
		}
	});
}

function saveRule(){
	deliveryStationRule="";
   	var len=$(".deliveryStationId").length-1;
   	//用#拼接参数字段
		$(".deliveryStationId").each(function(j){
			var c='.rule:eq('+j+')';
			var rule=$(c).val()+" ";
			var val=$(this).val();
			if(val){
				deliveryStationRule+=val+"#"+rule;
			}
			if(len!=j){
				deliveryStationRule+=",";
			}
		});
		if(deliveryStationRule){
			
		$.ajax({
			 type: "POST",
				url:cxt+"/deliveryStationRule/saveDeliveryStationRule",
				data:{"deliveryStationRule":deliveryStationRule,"addressId":addressId},
				success:function(optionData){
					if(optionData.success){
						alert("成功");
						
					}else{
						alert("失败");
					}
				}
			});
		}
}

