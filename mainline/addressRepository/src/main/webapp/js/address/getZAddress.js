
var setting = {
        edit: {
            enable: false,
            showRemoveBtn: false,
            showRenameBtn: false
        },
        check: {
			enable: false
		},
        data: {
            simpleData: {
                enable: true
            }
        },
        callback: {
            //onClick : menuOnClick
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
	reloadTable();
	addressLevel=treeNode.level;
}	

function getUrl(treeId, treeNode) {
	var url=ctx+"/address/getAddressTree?id="+treeNode.id+"&pageSize="+pageSize;
	return url;
}

function getStationUrl(treeId, treeNode) {
	var url=ctx+"/address/getStationAddressTreePage?id="+treeNode.id+"&level="+treeNode.level+"&page="+treeNode.page+"&pageSize="+pageSize;
	var aObj = $("#" + treeNode.tId + "_a");
	aObj.attr("title",treeNode.name+ "当前第 " + treeNode.page + " 页 / 共 " + treeNode.maxPage + " 页");
	return url;
}

function beforeExpand(treeId, treeNode) {
	if (!treeNode.isAjaxing) {
		treeNode.times = 1;
		ajaxGetNodes(treeNode, "refresh");
		return true;
	} else {
		$.messager.alert("提示","zTree 正在下载数据中，请稍后展开节点。。。");
		return false;
	}
}
function onAsyncSuccess(event, treeId, treeNode, msg) {
	if (!msg || msg.length == 0) {
		treeNode.icon = cxt+"/css/zTree/zTreeStyle/img/loading.gif";
		return;
	}
	var child = treeNode.children[0];
	if(child){
		if(child.maxPage>1){
			treeNode.maxPage=child.maxPage;
			addDiyDom(treeId,treeNode);
			child.page = 1;
		}
	}
	//每次最多加载100个
	/*totalCount = 0;
	if (treeNode.children.length < totalCount) {
		setTimeout(function() {ajaxGetNodes(treeNode);}, perTime);
	} else {
		treeNode.icon = "";
		zTree.updateNode(treeNode);
		if(!treeNode.children[0]){
			treeNode.icon = ctx+"/css/zTree/zTreeStyle/img/loading.gif";
		}else{
			zTree.selectNode(treeNode.children[0]);
		}
	}*/
}
function onAsyncError(event, treeId, treeNode, XMLHttpRequest, textStatus, errorThrown) {
	$.messager.alert("提示","异步获取数据出现异常。");
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
		$.message.alert("提示","请先选择一个父节点");
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
	    var	target = $.fn.zTree.init($("#tree"), setting, allNodes);
		
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
			url:ctx+"/deliveryStationRule/getMatchTree",
			data:{"id":addressId},
			success:function(optionData){
				if(optionData.length>0){
					//var node = zTree.getNodeByParam('id', addressId);
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

function getPromtInfo(){
	 $.ajax({
	 	 type: "POST",
	 		url:ctx+"/address/getPromtInfo",
	 		data:{isBind:true},
	 		success:function(ajson){
	 			var temp=ajson.attributes;
	 			var keys=temp['keys'];
	 			var binds=temp['binds'];
	 			var unbids=keys*1-binds*1;
	 			var info="关键词共有"+keys+"个，已经绑定站点"+binds+"个，未绑定";
	 			$("#promtInfo").text(info);
	 			$("#unbind").text(unbids);
	 		}
	 	});
}
function getUnbindInfo(){
	 $.ajax({
	 	 type: "POST",
	 		url:ctx+"/address/getUnbindInfo",
	 		data:{},
	 		success:function(optionData){
	 			zNodes=optionData['zTreeNodeList'];
				
				zTreeObj1 = $.fn.zTree.init($("#tree"), setting, zNodes);
	 		}
	 	});
}


function getAllChildrenNodes(treeNode,result){
	if (treeNode['isParent']) {
		var childrenNodes = treeNode.children;
		if (childrenNodes) {
			for (var i = 0; i < childrenNodes.length; i++) {
				result += ',' + childrenNodes[i].id;
				result = getAllChildrenNodes(childrenNodes[i], result);
			}
		}
	}
	return result;
}

function getAllNodes(){
	 var nodes = zTree.getNodes();  
	 var result=nodes[0].id;
	 result=getAllChildrenNodes(nodes[0],result);
	 //result=result.substring(1, result.length);
	 return result;
	
}
function addDiyDom(treeId, treeNode) {
	var aObj = $("#" + treeNode.tId + "_a");
	if($("#lastBtn_"+treeNode.id).size()>0){
		return ;
	}
	if ($("#addBtn_"+treeNode.id).length>0) return;
	var addStr = "<span class='button lastPage' id='lastBtn_" + treeNode.id
		+ "' title='last page' onfocus='this.blur();'></span><span class='button nextPage' id='nextBtn_" + treeNode.id
		+ "' title='next page' onfocus='this.blur();'></span><span class='button prevPage' id='prevBtn_" + treeNode.id
		+ "' title='prev page' onfocus='this.blur();'></span><span class='button firstPage' id='firstBtn_" + treeNode.id
		+ "' title='first page' onfocus='this.blur();'></span>";
	aObj.after(addStr);
	var first = $("#firstBtn_"+treeNode.id);
	var prev = $("#prevBtn_"+treeNode.id);
	var next = $("#nextBtn_"+treeNode.id);
	var last = $("#lastBtn_"+treeNode.id);
	//treeNode.maxPage = Math.round(treeNode.count/treeNode.pageSize - .5) + (treeNode.count%treeNode.pageSize == 0 ? 0:1);
	first.bind("click", function(){
		if (!treeNode.isAjaxing) {
			goPage(treeId,treeNode, 1);
		}
	});
	last.bind("click", function(){
		if (!treeNode.isAjaxing) {
			goPage(treeId,treeNode, treeNode.maxPage);
		}
	});
	prev.bind("click", function(){
		if (!treeNode.isAjaxing) {
			goPage(treeId,treeNode, treeNode.page-1);
		}
	});
	next.bind("click", function(){
		if (!treeNode.isAjaxing) {
			goPage(treeId,treeNode, treeNode.page+1);
		}
	});
};
var curPage = 0;
function goPage(treeId,treeNode, page) {
	//child = treeNode.children[0];
	//if(child&&child.maxPage>=page&&page>0){
	if(treeNode.maxPage>=page&&page>0){
		treeNode.page = page;
		if (treeNode.page<1) treeNode.page = 1;
		if (treeNode.page>treeNode.maxPage) treeNode.page = treeNode.maxPage;
		if (curPage == treeNode.page) return;
		curPage = treeNode.page;
		var zTree = $.fn.zTree.getZTreeObj(treeId);
		zTree.reAsyncChildNodes(treeNode, "refresh");
	}
}
