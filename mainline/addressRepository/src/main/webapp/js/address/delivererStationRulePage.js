var inital = false;
var stationList = [];
var vendorList = [];
var addressList = [];
var deliverer = [];
var resultOp = [];
var checkOp = {};
var ruleShowVo = [];
var pageData = {};
pageData.pageNo=1;
pageData.deliverers=[];
pageData.deliverers=[];

var oldNodes = [];
var setting = {

	data : {
		simpleData : {
			enable : true
		}
	},
	callback : {

		onClick : myClick,
		beforeDrag : function() {
			return false;
		},
		onAsyncSuccess : onAsyncSuccess
	}
};
 
$(document).ready(
		
		function() {
			$('#win').window('close');
			$("#stationRule").datagrid({
				url : "",
				loadMsg : '数据加载中...',
				nowrap : false,
				pagination : true,
				singleSelect : false,
				fit : true,
				pageSize : 20,
				pageList : [10, 20, 30, 40,100],
				sortOrder : 'desc',
				rownumbers : true,
				fitColumns : true,
				showFooter : true,
				frozenColumns : [[]],
				columns : [[ {
					field : 'ruleId',
					title : '数据ID',
			 
					sortable : true
				}, {
					field : 'addressName',
					width : 100,
			 
					title : '关键词' 
				}, {
					field : 'rule',
					width : 150,
					title : '规则' 
				}
				]]
			});
			
			var p = $("#stationRule").datagrid('getPager');
			$(p).pagination({
				onSelectPage : function(pageNumber, pageSize) {
					var params = $.extend({
						pageNum : pageNumber,
						pageSize : pageSize
					}, getQueryParams());
					Tools.doAction(ctx + '/delivererStationRule/list', params, false, function(data) {
						$("#stationRule").datagrid('loadData', {
							"rows" : data.list,
							"total" : data.count
						} || {});
						 

					});
				}
			});

			
			 $.ajax({
				 	type: "POST",
					url:ctx+"/station/listAll",
					data:{},
					async:false,
					success : function(resp) {
						$("#stationId").empty();
						if(resp.length>0){
							var data=[];
							for(var i = 0;i<resp.length;i++){
								data.push({label:resp[i].name,value:resp[i].id});
							}
							$('#stationId').combobox('loadData', data);
						} 
					
					}
				});
			 
			
			 
			 $("#stationId").combobox({
					onSelect: function (n,o) {
						var stationId=$("#stationId").combobox('getValue');
						if(stationId==''||stationId==null)
							return ;
						 $("input[name='stationId']").val(stationId);
						$.ajax({
							type : "POST",
							url : ctx + "/deliverer/getDelivererByStation2",
							data : {
								stationId : stationId
							},
							async : false,
							success : function(resp) {
								var t = $("#tree");
								zTree = $.fn.zTree.init(t, setting, resp);
							}
						});
						 $("input[name='delivererId']").val("-1");
						init();
						 
						var data=[];
						 $.ajax({
							 	type: "POST",
								url:ctx+"/address/getAdressByStation2",
								data:{
									stationId:stationId
								},
								async:false,
								success : function(resp) {
									$("#addressId").empty();
									if(resp.length>0){
										
										for(var i = 0;i<resp.length;i++){
											if (resp[i].level >= 4) {
												data.push({label:resp[i].name,value:resp[i].id});
											}
										}
									} 
								}
							});
						
						 	$('#addressId').combobox('loadData', data);
					}
			});
			$.messager.defaults = { ok: "确定", cancel: "否" }; 
		});

function myBeforeClick(treeId, treeNode, clickFlag) {
	className = (className === "dark" ? "" : "dark");
	return (treeNode.click != false);
}

 

function myClick(event, treeId, treeNode, clickFlag) {
	$("#tips").html(treeNode.name);
	delivererId = treeNode.id;
	delivererName = treeNode.name;
	$("input[name='delivererId']").val(delivererId);
	$('#relStation').panel('setTitle', '维护小件员【' + treeNode.name + "】的关键词");
	$("#aliasUl").html("");
	 $("input[name='rule']").val("");
	 $('#addressId').combobox('clear');
	init();
	 $("#addressId").combobox({
			onChange: function (n,o) {
				 
	 
			}
	});
	
}
 

/**
 * 生成select options
 * 
 * @param list
 * @returns {String}
 */
function generateSelector(list) {
	var str = "";
	for (var i = 0; i < list.length; i++) {
		str += "<option value='" + list[i].id + "'>" + list[i].name
				+ "</option>";
	}
	return str;
}

/**
 * 生成select options , 如果id有值使用selected
 * 
 * @param list
 * @param id
 * @returns {String}
 */
function generateSelectorSelect(list, id) {
	var str = "";
	for (var i = 0; i < list.length; i++) {
		str += "<option value='" + list[i].id + "' "
				+ (list[i].id != id ? "" : "selected") + " >" + list[i].name
				+ "</option>";

	}
	return str;
}

/**
 * 通过站点id获取关键词
 * 
 * @param stationId
 */
function getAddressList(stationId) {
	$.ajax({
		type : "POST",
		url : ctx + "/address/getAdressByStation2",
		data : {
			stationId : stationId
		},
		async : false,
		success : function(resp) {
			if (resp.length > 0) {
				for (var i = 0; i < resp.length; i++) {
					if (resp[i].level >= 4) {
						addressList.push(resp[i]);
					}
				}

			}
		}
	});
}

/**
 * 通过站点获取小件员列表
 * 
 * @param stationId
 * @param stationName
 */
function getDelivererByStation(stationId, stationName) {
	$.ajax({
		type : "POST",
		url : ctx + "/deliverer/getDelivererByStation",
		data : {
			stationId : stationId
		},
		async : false,
		success : function(resp) {
			var data=[];
			data.push({label:'全部',value:'-1'});
			if (resp.length > 0) {
				deliverer = resp;
				if(resp.length>0){
					for(var i = 0;i<resp.length;i++){
						data.push({label:resp[i].text,value:resp[i].id});
					}

				} 
			} 
			$('#delivererids').combobox('loadData', data);
		}
	});
}
 

 

 
/**
 * 删除
 */
function delFn( ) {
	
	 
		var selectRows = $('#stationRule').datagrid('getSelections');	
		if(selectRows.length==0){
			$.messager.alert("系统提示","请选择一条数据然后重试！","warning");
			return;
		}else{
			$.messager.confirm('确认删除', '您确认想要删除小件员规则吗?', function(r) {
		    	if (r){
		    		
		    		var saveObj = {};
					 
					saveObj.type = 'del';

				 
					for(var i=0 ; i < selectRows.length; i++){
						saveObj.ruleId = selectRows[i].ruleId;
						var ret = submitRules(saveObj);
						if (ret.success) {
							$.messager.alert('删除['+selectRows[i].addressName+']成功', '删除关键词['+selectRows[i].addressName+']'+ret.msg);
						} else {
							$.messager.alert('删除失败', ret.msg);
						}
					}
					
					init();
					 
		    	}
			})
		 
	}
	
 
}

function confirmFn() {

	var addressId=$("#addressId").combobox('getValue');
	var delivererId=$("input[name='delivererId']").val();
	var stationId=$("input[name='stationId']").val();
	if (addressId== '') {
		$.messager.alert('提示', '请选择关键词');
		return;
	}

	if (stationId== ''||stationId==null||stationId=='-1') {
		$.messager.alert('提示', '请选择站点');
		return;
	}
	if (delivererId== ''||delivererId==null||delivererId=='-1') {
		$.messager.alert('提示', '请选择小件员');
	 
		return;
	}
	var saveObj = {};
	saveObj.type = 'new';
	saveObj.ruleId = 0;
	saveObj.addressId = addressId;

	saveObj.rule = $("input[name='rule']").val();
	saveObj.delivererId = delivererId;
	saveObj.stationId = stationId;
	var resp = submitRules(saveObj);

	if (resp.success) {
		 
		$.messager.alert('保存成功', resp.msg);
		$('#win').window('close');
		init();
	} else {
		$.messager.alert('保存失败', resp.msg);
	}

}

/**
 * @param delivererid
 *            小件员ID
 * @param stationId
 *            站点ID
 */
function addFn() {

	var param=	getQueryParams();
	 $("input[name='rule']").val("");
	 $('#addressId').combobox('clear');
	$('#win').window('open');
}

function submitRules(result, tr) {
	var rlist = result;
	var ret = {};
	if ($.isEmptyObject(rlist)) {
		$.messager.alert("提示", '你没有进行任何操作,无需保存!');
		return;
	}

	var str = JSON.stringify(rlist);
	$.ajax({
		type : "POST",
		url : ctx + "/delivererStationRule/saveDelivererRule",
		data : {
			jsonStr : str
		},
		async : false,
		success : function(resp) {
			ret = resp;

		}
	});
	return ret;
}

function searchByKeyword(valName, treeName) {
	if (event.keyCode != 13) {
		return; // 回车键的键值为13
	}
	event.stopPropagation();
	var key = $("#" + valName).val();
	var searchType = $("#searchType").val();
	var target = $.fn.zTree.getZTreeObj(treeName);
	$.fn.zTree.init($("#" + treeName), setting, oldNodes);

	$.fn.zTree.init($("#" + treeName), setting, target.getNodesByParamFuzzy(
			"name", key));

}
function toggle(target, node) {
	target.expandNode(node, true, false, false);
	target.showNode(node);
	var parentNode = node.getParentNode();
	if (parentNode) {
		toggle(target, parentNode);
	}
}

function checkNumber(ss) {
	var type = "^[0-9]*[1-9][0-9]*$";
	var re = new RegExp(type);
	if (ss.match(re) == null) {
		return false;
	} else {
		return true;
	}
}



  
 
 

function getQueryParams() {
	return {
		stationId : $("input[name='stationId']").val(),
		delivererId : $("input[name='delivererId']").val()
	};
}

function init() {
	// init
	var options = $("#stationRule").datagrid('getPager').data("pagination").options;
	var pageSize = options.pageSize;
	var params = $.extend({
		pageNum : 1,
		pageSize : pageSize
	}, getQueryParams());

	Tools.doAction(ctx + '/delivererStationRule/list', params, false, function(data) {
		$("#stationRule").datagrid('loadData', {
			"rows" : data.list,
			"total" : data.count
		} || {});
		var p = $("#stationRule").datagrid('getPager');
		$("td:last", p).find("a").click();

	});
}
 


