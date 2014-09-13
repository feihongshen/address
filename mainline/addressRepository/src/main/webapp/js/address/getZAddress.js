var setting = {
		async: {
			enable: true,
			url: getUrl
		},
	data: {
		simpleData: {
			enable: true
		}
	},
	callback: {
		beforeClick: beforeClick,
		onClick: onClick,
		beforeExpand: beforeExpand,
		onAsyncSuccess: onAsyncSuccess,
		onAsyncError: onAsyncError
	}
};

var addressId;
var log, className = "dark";
var zTree;
var lastValue = "", nodeList = [], fontCss = {};


function beforeClick(treeId, treeNode, clickFlag) {
	className = (className === "dark" ? "":"dark");
	return (treeNode.click != false);
}
function onClick(event, treeId, treeNode, clickFlag) {
	addressId=treeNode.id;
	alert(treeNode.level);
}	

function getUrl(treeId, treeNode) {
	var url=cxt+"/address/getAddressTree?id="+treeNode.id;
	return url;
}

function beforeExpand(treeId, treeNode) {
	if (!treeNode.isAjaxing) {
		treeNode.times = 1;
		ajaxGetNodes(treeNode, "refresh");
		return true;
	} else {
		alert("zTree 正在下载数据中，请稍后展开节点。。。");
		return false;
	}
}
function onAsyncSuccess(event, treeId, treeNode, msg) {
	if (!msg || msg.length == 0) {
		treeNode.icon = cxt+"/css/zTree/zTreeStyle/img/loading.gif";
		return;
	}
	//每次最多加载100个
	totalCount = 0;
	if (treeNode.children.length < totalCount) {
		setTimeout(function() {ajaxGetNodes(treeNode);}, perTime);
	} else {
		treeNode.icon = "";
		zTree.updateNode(treeNode);
		if(!treeNode.children[0]){
			treeNode.icon = cxt+"/css/zTree/zTreeStyle/img/loading.gif";
		}else{
			
			zTree.selectNode(treeNode.children[0]);
		}
	}
}
function onAsyncError(event, treeId, treeNode, XMLHttpRequest, textStatus, errorThrown) {
	alert("异步获取数据出现异常。");
	treeNode.icon = "";
	zTree.updateNode(treeNode);
}
function ajaxGetNodes(treeNode, reloadType) {
	if (reloadType == "refresh") {
		treeNode.icon = "../../../css/zTreeStyle/img/loading.gif";
		zTree.updateNode(treeNode);
	}
	zTree.reAsyncChildNodes(treeNode, reloadType, true);
}


function expandNode(e) {
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
	treeObj= $.fn.zTree.getZTreeObj("tree");
	var filterString = $("#searchA").val();
	nodeList = treeObj.getNodesByParamFuzzy("name", filterString);
	updateNodes(true);
	var nodes = treeObj.getNodes();
	
	var cloneNodes = [];
	var removeNodes=[];
	$.each(nodes, function(index, node) {
		cloneNodes.push(node);
	});
	treeObj.showNodes(cloneNodes);
	pushRemoveNodes(cloneNodes,removeNodes,filterString);
	treeObj.hideNodes(removeNodes);
}
function updateNodes(highlight) {
	var nodes = zTree.getNodes();
	zTree.hideNodes(nodes);
	
	for( var i=0, l=nodeList.length; i<l; i++) {
		nodeList[i].highlight = highlight;
		zTree.updateNode(nodeList[i]);
	}
	zTree.showNodes(nodeList);
}
function pushRemoveNodes(cloneNodes,removeNodes,filterString){
	
	$.each(cloneNodes, function(index, node) {
		var childrens=node.children;
		if(!childrens)return;
		treeObj.showNodes(childrens);
		if (node.name.indexOf(filterString) != -1) {
			return;
		}else{
			var count=0;
			$.each(childrens,function(ci,cn){
				if(!cn)return;
				//递归查询
				pushRemoveNodes(childrens,removeNodes,filterString);
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
		url:cxt+"/address/getAddressTree",
		data:{isBind:true},
		success:function(optionData){
	        var t = $("#tree");
	        zTree = $.fn.zTree.init(t, setting, optionData);
			
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

