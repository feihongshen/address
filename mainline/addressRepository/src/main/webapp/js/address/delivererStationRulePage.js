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
var nodeid;
var nodeName;
$(document).ready(
		function() {
			getAll();
			$("#searchA").keydown(function() {
				$("#confirmAllBtn").click();
			});
		 
			$.messager.defaults = { ok: "确定", cancel: "否" }; 
			$("#confirmAllBtn").click(
					function() {

						var key = $("#searchA").val();
						var target = $.fn.zTree.getZTreeObj("tree");
						$.fn.zTree.init($("#tree"), setting, oldNodes);

						$.fn.zTree.init($("#tree"), setting, target
								.getNodesByParamFuzzy("name", key));
					});
			// 刷新
			$("#refreshAllBtn").click(function() {
				getAll();
			});
			$.ajax({
				type : "POST",
				url : ctx + "/station/listAll",
				data : {},
				async : false,
				success : function(resp) {
					if (resp.length > 0) {
						stationList = resp;
					}
				}
			});
		 
			
			
			$("#delivererids").combobox({

				onChange: function (n,o) {
					var delivererid=$("#delivererids").combobox('getValue');
					if(delivererid=='-1'){
						$("#stationRule>tbody>tr").each(function(){
								$(this).show();	 
						});
					}
					else{
						$("#stationRule>tbody>tr").each(function(){
							if($(this).attr("dsrid")==nodeid+'-'+delivererid){
								$(this).show();	 
							}else{
								$(this).hide();	
							}
							
						});
						}
				}
				});
 
		 
		});

function myBeforeClick(treeId, treeNode, clickFlag) {
	className = (className === "dark" ? "" : "dark");
	return (treeNode.click != false);
}

 

function myClick(event, treeId, treeNode, clickFlag) {
	$("#tips").html(treeNode.name);
	nodeid = treeNode.id;
	nodeName = treeNode.name;
	$('#relStation').panel('setTitle', '关联站点【' + treeNode.name + "】");
	$("#addressId").val(treeNode.id);
	$("#level").val(treeNode.level);
	$("#aliasUl").html("");
	$('#delivererids').combobox('clear');
	$("#stationRule tbody").html("");
	addressList = [];
	deliverer = [];
	getAddressList(treeNode.id);
	getDelivererByStation(treeNode.id, treeNode.name);

	
	var date1=new Date();
	$.ajax({
		type : "POST",
		url : ctx + "/delivererStationRule/getRuleInfoByStation",
		data : {
			stationId : treeNode.id
		},
		async : true,
		success : function(resp) {
			ruleShowVo = resp;
			$("#stationRule>tbody").html("");
			//i=0;
			//setTimeout(test(deliverer[0],treeNode.id, treeNode.name,ruleShowVo[deliverer[0].id]==null?null:ruleShowVo[deliverer[0].id].delivererStationRuleVo),200); 
	 
			if (!$.isEmptyObject(deliverer)) {
				for (var i = 0; i < deliverer.length; i++) {
					var item = deliverer[i];
					$("#stationRule>tbody").append(
							appendTr2(item, treeNode.id, treeNode.name));
				}
			} 
			var date3=(new Date()).getTime()-date1.getTime() ;
			console.log(date3);
		}
	});
	
	
	
 

	if (treeNode.level == 1) {
		$('#addRule').attr('disabled', true);
	} else {
		$('#addRule').attr('disabled', false);
	}

}
function getAll() {
	$.ajax({
		type : "POST",
		url : ctx + "/station/listAllToTreeNode?pageSize=1000000",
		data : {

		},
		success : function(optionData) {
			var t = $("#tree");
			zTree = $.fn.zTree.init(t, setting, optionData);

			oldNodes = zTree.getNodes();

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
 * 追加编辑行
 * 
 * @param item
 * @param stationId
 * @param stationName
 * @returns
 */
function appendTr2(item, stationId, stationName,listDlivers) {
	var tr = "<tr  style='width:80%' status='show' dsrId='"+stationId + "-" + item.id+"'><td style='width:15%'>"+stationName+"</td><td style='width:15%'>"+item.text;

	var delivererAddress =listDlivers;
 
	//tr.append($("<td style='width:15%'>"+stationName+"</td><td style='width:15%'>"+item.text+"</td>"));
	var tableTd = "<td style='width:60%'>";
	var childTable = 
			"<table  style='width:80%' id='tb_" + item.id + "_" + stationId+"'  class='table table-bordered'>" +
					"<tr><td style='width:30%'>关键词</td><td style='width:30%'>规则</td><td style='width:30%' ><a addAddrRow href='javascript:addAddrRow(\""+item.id+"\",\""+stationId+"\")'>新增</a></td></tr>" +
					"";

	 
	if (!$.isEmptyObject(delivererAddress)) {
		if (delivererAddress.length > 0) {
			for (var i = 0; i < delivererAddress.length; i++) {
				var itemAddr = delivererAddress[i];
				var select = generateSelectorSelect(addressList,
						itemAddr.addressId);
				var childRow =  "<tr ruleId='"+itemAddr.ruleId+"' addrId='"+itemAddr.addressId+"' rule='"+itemAddr.rule+"'><td><select disabled>" + select + "</select></td><td><input value='" + itemAddr.rule + "'  disabled/></td>" +
						"<td>"+
						"&nbsp;<a class='easyui-linkbutton delRow' delRow href='javascript:void(0)' onclick='delFn1(this)'>删除</a>&nbsp;</td></tr>";
				
				childTable+=childRow;
				//childTable.append(childRow);
			}
		}

	}
	childTable+="</table>";
	tableTd+=childTable;
	tableTd+="</td>";
	tr+=tableTd;
	tr+="</tr>";
	return tr;
};

 

function delFn1(obj) {
	delFn($(obj).parent().parent());
}
function delFn(trf) {

	$.messager.confirm('确认删除', '您确认想要删除小件员规则吗?', function(r) {
		if (r) {
			if (trf.attr("ruleId") == '0') {
				trf.remove();
			} else {
				var saveObj = {};
				saveObj.ruleId = trf.attr("ruleId");
				saveObj.type = 'del';

				var ret = submitRules(saveObj);
				if (ret.success) {
					trf.remove();
				} else {
					$.messager.alert('删除失败', ret.msg);
				}
			}
		}
	});
}

function cancelFn1(obj) {
	cancelFn($(obj).parent().parent());
}
function cancelFn(trf) {
	if (trf.attr("ruleId") == '0') {
		trf.remove();
	} else {
		trf.find("select").val(trf.attr("addrId"));
		trf.find("input").val(trf.attr("rule"));
		trf.find("select,input").attr("disabled", true);

		trf.find("a[delRow]").show();
		trf.find("a[confirmRow]").hide();
		trf.find("a[cancelRow]").hide();

	}
}

function confirmFn1(obj, deliverId, stationId) {
	confirmFn1($(obj).parent().parent(), deliverId, stationId);
}

function confirmFn(trf, deliverId, stationId) {
	if (trf.find("select").val() == '') {
		$.messager.alert('提示', '请选择关键词');
		trf.find("select").focus();
		return;
	}

	var saveObj = {};
	saveObj.type = 'edit';
	if (trf.attr("ruleId") == '0') {
		saveObj.type = 'new';
	}
	saveObj.ruleId = trf.attr("ruleId");
	saveObj.addressId = trf.find("select").val();

	saveObj.rule = trf.find("input").val();
	saveObj.delivererId = deliverId;
	saveObj.stationId = stationId;

	var resp = submitRules(saveObj, trf);

	if (resp.success) {
		trf.attr("ruleId", resp.attributes.ruleId);
		trf.attr("addrId", resp.attributes.addressId);
		trf.attr("rule", resp.attributes.rule);
		$.messager.alert('保存成功', resp.msg);
		trf.find("select,input").attr("disabled", true);

		trf.find("a[delRow]").show();
		trf.find("a[confirmRow]").hide();
		trf.find("a[cancelRow]").hide();
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
function addAddrRow(delivererid, stationId) {

	id = delivererid + "_" + stationId;
	var select = generateSelector(addressList);
	var tr = $("<tr></tr>");
	var confirm = $("<a class='easyui-linkbutton'>确认</a>").attr("href",
			'javascript:void(0)').attr("confirmRow", '');
	var cancel = $("<a class='easyui-linkbutton'>取消</a>").attr("href",
			'javascript:void(0)').attr("cancelRow", '');
	var del = $("<a class='easyui-linkbutton'>删除</a>").attr("href",
			'javascript:void(0)').attr("delRow", '');

	confirm.click(function() {
		var trf = $(this).parent().parent();
		confirmFn(trf, delivererid, stationId);
	});
	cancel.click(function() {
		var trf = $(this).parent().parent();
		cancelFn(trf);

	});

	del.click(function() {
		var trf = $(this).parent().parent();
		delFn(trf);

	}).hide();

	tr.attr("ruleid", '0').attr("addrId", '').attr("rule", '');
	tr.append($("<td></td>").html("<select>" + select + "</select>"));
	tr.append($("<td></td>").html("<input />"));
	tr.append($("<td></td>").append(confirm).append("&nbsp;").append(cancel)
			.append("&nbsp;").append(del));
	$("#tb_" + id).append(tr);

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
