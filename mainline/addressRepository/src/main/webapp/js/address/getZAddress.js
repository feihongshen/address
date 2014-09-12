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


var log, className = "dark";
function beforeClick(treeId, treeNode, clickFlag) {
	className = (className === "dark" ? "":"dark");
	alert("beforeClick");
	return (treeNode.click != false);
}
function onClick(event, treeId, treeNode, clickFlag) {
	alert("onClick");
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