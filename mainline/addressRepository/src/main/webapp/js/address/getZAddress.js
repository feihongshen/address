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
	$("#stationList").datagrid({
		url : cxt+'/deliveryStationRule/datagrid?addressId='+addressId,
		pageNumber : 1
	});
	
	
	addressLevel=treeNode.level;
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
	var filterString = $("#searchA").val();
	nodeList = zTree.getNodesByParamFuzzy("name", filterString);
	updateNodes(true);
	var nodes = zTree.getNodes();
	
	var cloneNodes = [];
	var removeNodes=[];
	$.each(nodes, function(index, node) {
		cloneNodes.push(node);
	});
	zTree.showNodes(cloneNodes);
	pushRemoveNodes(cloneNodes,removeNodes,filterString);
	zTree.hideNodes(removeNodes);
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
		zTree.showNodes(childrens);
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

var pureStation;
function initStations(data){
	$(".deliveryStationRule").each(function(i){
		if(i==0){
			return;
		}
		$(this).remove();
	});
	$("#deliveryStationId").empty();
	optionData=data.rows;
	for(var i=0;i<optionData.length;i++){
		var option=$("  <option value="+optionData[i]['id']+">"+optionData[i]['name']+"</option>");
		$("#deliveryStationId").append(option);
		
	}
	backNode=$("#deliveryStationRule").clone(true);
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
	        zTree = $.fn.zTree.init(t, setting, optionData);
			
		}
	});
}

function saveRule(){
	if(addressLevel<3){
		alert("请选择区县进行绑定！");
		return;
	}
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
						var msg="保存失败";
						if(optionData.attributes){
							$.each(optionData.attributes,function(i,term){
								if(!i){
									msg="默认";
								}
								msg+=term;
							})
						}
						alert(msg);
					}
				}
			});
		}
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

var detailRow={
		idField : 'id',
		title : '关联站点',
		url : cxt+'/deliveryStationRule/datagrid?addressId=0',
		fit : false,
		height : 300,
		loadMsg : '数据加载中...',
		pageSize : 10,
		pagination : true,
		pageList : [ 10, 20, 30 ],
		sortOrder : 'asc',
		rownumbers : true,
		singleSelect : true,
		fitColumns : true,
		showFooter : true,
		frozenColumns : [ [] ],
		columns : [ [
				{
					field : 'id',
					title : '编号',
					hidden : true,
					sortable : true
				},
				{
					field : 'opt',
					title : '操作',
					width : 10,
					formatter : function(value, rec, index) {
						if (!rec.id) {
							return '';
						}
						var href = '';
						href += "[<a href='#' onclick=delObj('deleteImportAddressResult?id="
								+ rec.id + "','address')>";
						href += "删除</a>]";
						return href;
					}
				}, 

				{
					field : 'name',
					title : '站点名称',
					width : 50,
					sortable : true
				}

		] ],
		onLoadSuccess : function(data) {
			initStations(data);
			$("#stationList").datagrid("clearSelections");
		},
		onClickRow : function(rowIndex, rowData) {
			rowid = rowData.id;
			gridname = 'stationList';
		}
	};

