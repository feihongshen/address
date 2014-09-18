

var addressId;
var addressLevel;
var log, className = "dark";
var zTree;
var lastValue = "", nodeList = [], fontCss = {};


function beforeClick(treeId, treeNode, clickFlag) {
	className = (className === "dark" ? "":"dark");
	return (treeNode.click != false);
}
function onClick(event, treeId, treeNode, clickFlag) {
	addressId=treeNode.id;
	
	reloadTable();
	
	addressLevel=treeNode.level;
}	



function getUrl(treeId, treeNode) {
	var url=cxt+"/address/getAddressTree?id="+treeNode.id;
	return url;
}

function getStationUrl(treeId, treeNode) {
	var url=cxt+"/address/getStationAddressTree?id="+treeNode.id+"&level="+treeNode.level;
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

function searchVal(valName,treeName){
		var target = $.fn.zTree.getZTreeObj(treeName);
		//经过transformToArray转换后是一个Array数组，数组里的每个元素都是object对象，这个对象里包含了node的21个属性。
	    var nodes = target.transformToArray(target.getNodes()[0].children);
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
	     toggle(target,filterNodes[i].getParentNode())
	    }
	
}

function toggle(target,node){
	target.expandNode(node, true, false, false);
	target.showNode(node);
	var parentNode = node.getParentNode();
	if(parentNode){
		 toggle(target,parentNode);
	}
}


function searchTree(valName,treeName){
	var filterString = $("#"+valName).val();
	var target = $.fn.zTree.getZTreeObj(treeName);
	nodeList = target.getNodesByParamFuzzy("name", filterString);
	updateNodes(true);
	var nodes = target.getNodes();
	
	var cloneNodes = [];
	var removeNodes=[];
	$.each(nodes, function(index, node) {
		cloneNodes.push(node);
	});
	target.showNodes(cloneNodes);
	pushRemoveNodes(target,cloneNodes,removeNodes,filterString);
	target.hideNodes(removeNodes);
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
function pushRemoveNodes(target,cloneNodes,removeNodes,filterString){
	
	$.each(cloneNodes, function(index, node) {
		var childrens=node.children;
		if(!childrens)return;
		target.showNodes(childrens);
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


var unbindList=[];
function unbind(){
	$.ajax({
		 type: "POST",
			url:cxt+"/deliveryStationRule/getMatchTree",
			data:{"id":addressId},
			success:function(optionData){
				if(optionData.length>0){
					alert("成功");
					var node = zTree.getNodeByParam('id', addressId);
	        		for(i in optionData){
	        			var sonId=optionData[i];
	        			var temp = zTree.getNodeByParam('id', sonId);
	        			unbindList.push(temp);
	        		}
	        		
	        	zTree.hideNodes(unbindList);
				}
			}
		});
	
}





