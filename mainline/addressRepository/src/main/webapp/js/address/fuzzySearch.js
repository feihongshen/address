var matchedNodes;
function searchByKeyword(valName, treeName) {
	if (event.keyCode != 13) {
		return; // 回车键的键值为13
	}
	event.stopPropagation();
	var key = $("#" + valName).val();
	// 到后台进行搜索
	searchAddress(key);
	var target = $.fn.zTree.init($("#tree"), setting, matchedNodes);

	var nodes = target.transformToArray(target.getNodes());

	// 空格回车符 不做查询 直接显示全部
	if (/^\s*$/.test(key)) {
		// updateNodes(false);
		target.showNodes(nodes);
		return;
	}
	// 首先隐藏
	/*
	 * target.hideNodes(nodes);
	 * 
	 * nodeList=target.getNodesByParamFuzzy("name", key); //模糊匹配
	 * 
	 * var filterNodes=[]; for(var i=0;i<nodeList.length;i++){
	 * filterNodes.push(nodeList[i]); } target.showNodes(filterNodes); for(var
	 * i=0;i<filterNodes.length;i++){
	 * toggle(target,filterNodes[i].getParentNode()); }
	 */
}
function toggle(target, node) {
	target.expandNode(node, true, false, false);
	target.showNode(node);
	var parentNode = node.getParentNode();
	if (parentNode) {
		toggle(target, parentNode);
	}
}

function searchAddress(needMatched) {
	$.ajax({
		type : "POST",
		async : false,
		url : ctx + "/address/matchKeyword",
		data : {
			needMatched : needMatched
		},
		success : function(optionData) {
			matchedNodes = optionData['zTreeNodeList'];
		}
	});
}
