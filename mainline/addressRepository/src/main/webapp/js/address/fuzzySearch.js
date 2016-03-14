function searchByKeyword(valName, treeName) {
	if (event.keyCode != 13)
		return; // 回车键的键值为13
	event.stopPropagation();
	var key = $("#" + valName).val();
	var searchType = $("#searchType").val();
	// 到后台进行搜索
	searchAddress(searchType,key,function(matchedNodes){
		var target = $.fn.zTree.init($("#tree"), setting, matchedNodes);
		var nodes = target.transformToArray(target.getNodes());
		// 空格回车符 不做查询 直接显示全部
		if (/^\s*$/.test(key)) {
			target.showNodes(nodes);
			return;
		}
	});
	
}
function toggle(target, node) {
	target.expandNode(node, true, false, false);
	target.showNode(node);
	var parentNode = node.getParentNode();
	if (parentNode) {
		toggle(target, parentNode);
	}
}

function searchAddress(searchType,needMatched,fn) {
	MaskUtil.mask();
	$.ajax({
		type : "POST",
		url : ctx + "/address/searchByKeywordOrStation",
		data : {
			searchType:searchType,
			needMatched : needMatched
		},
		success : function(optionData) {
			MaskUtil.unmask();
			fn(optionData['zTreeNodeList']);
		}
	});
}




