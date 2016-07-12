var inital = false;
var stationList = [];
var vendorList = [];
var addressList = [];
var deliverer = []
var resultOp = [];
var checkOp = {};
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

			// 折叠
			$("#collapseAllBtn").bind("click", {
				type : "collapseAll"
			}, expandNode);
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
			$("#delivererList").change(
					function() {
						$("#stationRule tbody").html("");
						if (!$.isEmptyObject(deliverer)) {
							for (var i = 0; i < deliverer.length; i++) {
								var item = deliverer[i];
								if ((item.id == $(this).val())
										|| ($(this).val() == '')) {
									$("#stationRule>tbody").append(
											appendTr(item, nodeid, nodeName));

								}
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
	$("#delivererList").html("<option selected value=''>请选择</option>");
	$("#stationRule tbody").html("");
	addressList = [];
	getAddressList(treeNode.id);
	getDelivererByStation(treeNode.id, treeNode.name);

	if (!$.isEmptyObject(deliverer)) {
		for (var i = 0; i < deliverer.length; i++) {
			var item = deliverer[i];
			$("#stationRule>tbody").append(
					appendTr(item, treeNode.id, treeNode.name));
			$("#delivererList").append(
					"<option value='" + item.id + "'>" + item.text
							+ "</option>");

		}
	}

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
		}
	});
}
function addRule() {
	$(
			"<tr status='add'><td><select style='width:80%' name='stationId'><option value=''></option>"
					+ generateSelector(stationList)
					+ "</select></td>"
					+ "<td><input type='text' name='rules' size='30'/></td>"
					+ "<td><a href='#' delRow=''>删除</a></td></tr>")
			.insertBefore($("#stationRuleOpe"));
}
function addAge() {
	$(
			"<tr status='add'><td><select style='width:80%'><option value=''></option>"
					+ generateSelector(vendorList) + "</select></td>"
					+ "<td><input type='text' name='age' size='20'/></td>"
					+ "<td><a href='#' delAgeRow=''>删除</a></td></tr>")
			.insertBefore($("#ageOpe"));
}
function generateSelector(list) {
	var str = "";
	for (var i = 0; i < list.length; i++) {
		str += "<option value='" + list[i].id + "'>" + list[i].name
				+ "</option>";
	}
	return str;
}

function generateSelectorSelect(list, id) {
	var str = "";
	for (var i = 0; i < list.length; i++) {
		str += "<option value='" + list[i].id + "' "
				+ (list[i].id != id ? "" : "selected") + " >" + list[i].name
				+ "</option>";

	}
	return str;
}

function getAddressList(stationId) {
	$.ajax({
		type : "POST",
		url : ctx + "/address/getAdressByStation",
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

function getDelivererByStation(stationId, stationName) {
	$.ajax({
		type : "POST",
		url : ctx + "/deliverer/getDelivererByStation",
		data : {
			stationId : stationId
		},
		async : false,
		success : function(resp) {
			if (resp.length > 0) {
				deliverer = resp;

			} else {

			}
		}
	});
}
function getAddressAges(addressId) {
	$.ajax({
		type : "POST",
		url : ctx + "/deliveryStationRule/getAges",
		data : {
			addressId : addressId
		},
		async : false,
		success : function(resp) {
			$("#vendorAge>tbody").find("tr:not([id])").remove();
			if (resp.length > 0) {
				for (var i = 0; i < resp.length; i++) {
					var item = resp[i];
					$(appendAgeTr(item)).insertBefore($("#ageOpe"));
				}
			}
		}
	});
};

/**
 * @param item
 * @param stationId
 * @param stationName
 * @returns
 */
function appendTr(item, stationId, stationName) {
	var tr = $("<tr  style='width:80%'></tr>").attr("status", "show").attr(
			"dsrId", stationId + "-" + item.id);

	var delivererAddress = [];
	$.ajax({
		type : "POST",
		url : ctx + "/delivererStationRule/getAddressByDeliverer",
		data : {
			stationId : stationId,
			delivererId : item.id
		},
		async : false,
		success : function(resp) {
			delivererAddress = resp;
		}
	});
	tr.append($("<td style='width:15%'></td>").html(stationName)).append(
			$("<td style='width:15%'></td>").html(item.text));
	var tableTd = $("<td style='width:60%'></td>");
	var childTable = $(
			"<table  style='width:80%'  class='table table-bordered'></table>")
			.attr("id", "tb_" + item.id + "_" + stationId)
			.append(
					$("<tr></tr>")
							.append(
									$("<td style='width:20%'></td>")
											.html("关键词"))
							.append($("<td style='width:20%'></td>").html("规则"))
							.append($("<td style='width:50%'></td>").html("操作")));
	var operRow = $("<tr></tr>").append($("<td></td>")).append($("<td></td>"))
			.append(
					$("<td></td>").append(
							$("<a></a>").attr(
									"href",
									"javascript:addAddrRow('" + item.id + "','"
											+ stationId + "')").attr(
									"addAddrRow", "").html("新增")));
	childTable.append(operRow);

	if (!$.isEmptyObject(delivererAddress)) {
		if (delivererAddress.length > 0) {
			for (var i = 0; i < delivererAddress.length; i++) {
				var itemAddr = delivererAddress[i];
				var select = generateSelectorSelect(addressList,
						itemAddr.addressId);
				var childRow = $("<tr></tr>").attr("ruleId", itemAddr.ruleId)
						.attr("addrId", itemAddr.addressId).attr("rule",
								itemAddr.rule);
				childRow.append($("<td></td>").html(
						"<select disabled>" + select + "</select>"));
				childRow.append($("<td></td>").html(
						"<input value='" + itemAddr.rule + "'  disabled/>"));

				var confirm = $("<a class='easyui-linkbutton'>确认</a>").attr(
						"href", 'javascript:void(0)').attr("confirmRow", '');
				var cancel = $("<a class='easyui-linkbutton'>取消</a>").attr(
						"href", 'javascript:void(0)').attr("cancelRow", '');
				var del = $("<a class='easyui-linkbutton'>删除</a>").attr("href",
						'javascript:void(0)').attr("delRow", '');
				childRow.append($("<td></td>").append(confirm).append("&nbsp;")
						.append(cancel).append("&nbsp;").append(del));
				childTable.append(childRow);
				confirm.click(function() {
					var trf = $(this).parent().parent();
					confirmFn(trf, item.id, stationId);

				}).hide();
				cancel.click(function() {
					var trf = $(this).parent().parent();
					cancelFn(trf);
				}).hide();

				del.click(function() {
					var trf = $(this).parent().parent();
					delFn(trf);
				});

			}
		}

	}

	tableTd.append(childTable);
	tr.append(tableTd);
	return tr;
};

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

function checkDelivererRuleExists(stationId, delivererId, addressId) {

	var ret = false;
	$.ajax({
		type : "POST",
		url : ctx + "/delivererStationRule/checkDelivererRule",
		data : {
			stationId : stationId,
			delivererId : delivererId,
			addressId : addressId
		},
		async : false,
		success : function(resp) {
			if (resp.success) {
				ret = true;
			}
		}
	});
	return ret;
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

function checkNumber(ss) {
	var type = "^[0-9]*[1-9][0-9]*$";
	var re = new RegExp(type);
	if (ss.match(re) == null) {
		return false;
	} else {
		return true;
	}
}
