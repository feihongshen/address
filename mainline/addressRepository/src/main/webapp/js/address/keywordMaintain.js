var keywordEditLayer = "";
var keywordSuffixLayer = "";

$("#datagrid_keyword").datagrid({
	url : "",
	loadMsg : '数据加载中...',
	pagination : true,
	fitColumns : false,
	singleSelect : false,
	fit : true,
	pageSize : 20,
	pageList : [20, 30, 50, 100],
	sortOrder : 'desc',
	rownumbers : true,
	fitColumns : true,
	showFooter : true,
	frozenColumns : [[]],
	columns : [[{
		field : 'ck',
		checkbox : true
	}, {
		field : 'id',
		title : '编号',
		hidden : true,
		sortable : true
	}, {
		field : 'province',
		width : 150,
		title : '省/直辖市',
		sortable : true
	}, {
		field : 'city',
		width : 150,
		title : '市',
		sortable : false
	}, {
		field : 'district',
		width : 150,
		title : '区/县',
		sortable : false
	}, {
		field : 'addressId1',
		width : 150,
		title : '关键字1',
		hidden : true,
		sortable : true
	}, {
		field : 'addressName1',
		width : 150,
		title : '关键字1',
		sortable : true
	}, {
		field : 'addressId2',
		width : 150,
		title : '关键字2',
		hidden : true,
		sortable : true
	}, {
		field : 'addressName2',
		width : 150,
		title : '关键字2',
		sortable : true
	}, {
		field : 'addressId3',
		width : 150,
		title : '关键字3',
		hidden : true,
		sortable : true
	}, {
		field : 'addressName3',
		width : 150,
		title : '关键字3',
		sortable : true
	}, {
		field : 'deliveryStationName',
		width : 150,
		title : '站点名称',
		sortable : true
	}]]
});

init();

// click pagination
var p = $("#datagrid_keyword").datagrid('getPager');
$(p).pagination({
	onSelectPage : function(pageNumber, pageSize) {
		var params = $.extend({
			pageNum : pageNumber,
			pageSize : pageSize
		}, getQueryParams());
		Tools.doAction(ctx + '/keyword/query', params, false, function(data) {
			$("#datagrid_keyword").datagrid('loadData', {
				"rows" : data.list,
				"total" : data.count
			} || {});

		});
	}
});

// bind modify event
$("#keyword_edit")
		.click(
				function() {
					var selections = $("#datagrid_keyword").datagrid(
							"getSelections");
					if (selections && selections.length > 0) {
						if (selections.length > 1) {
							layer.tips('请选择一行数据', this, {
								guide : 2,
								time : 2
							});
						} else {
							keywordEditLayer = jQuery
									.layer({
										type : 1,
										title : '<div><span style="padding-left: 14px;line-height:14px;">关键字修改</span></div>',
										shadeClose : true,
										maxmin : false,
										fix : false,
										area : [800, 200],
										page : {
											dom : '#keyword_edit_panel'
										}
									});

							var id = selections[0].id;
							var province = selections[0].province;
							var city = selections[0].city;
							var district = selections[0].district;
							var addressId1 = selections[0].addressId1;
							var addressName1 = selections[0].addressName1;
							var addressId2 = selections[0].addressId2;
							var addressName2 = selections[0].addressName2;
							var addressId3 = selections[0].addressId3;
							var addressName3 = selections[0].addressName3;

							var deliveryStationName = selections[0].deliveryStationName;

							$("input[id='id']").val(id);
							$("input[id='province']").val(province);
							$("input[id='city']").val(city);
							$("input[id='district']").val(district);
							$("input[id='addressId1']").val(addressId1);
							$("input[id='addressName1']").val(addressName1);
							$("input[id='addressId2']").val(addressId2);
							$("input[id='addressName2']").val(addressName2);
							$("input[id='addressId3']").val(addressId3);
							$("input[id='addressName3']").val(addressName3);
							$("input[id='deliveryStationName']").val(
									deliveryStationName);
							isModify = true;
						}
					} else
						layer.tips('请选择要修改行', this, {
							guide : 2,
							time : 2
						});
				});

// keyword_suffix_panel
$("#keyword_suffix_maintain")
		.click(
				function() {
					keywordSuffixLayer = jQuery
							.layer({
								type : 1,
								title : '<div><span style="padding-left: 14px;line-height:14px;">关键字后缀维护</span></div>',
								shadeClose : true,
								maxmin : false,
								fix : false,
								area : [750, 500],
								page : {
									dom : '#keyword_suffix_panel'
								}
							});
					// 显示预制的关键词后缀
					showPresetKeywordSuffix();
					
					// 显示客户的关键词后缀
					showCustomKeywordSuffix();

				});

function showCustomKeywordSuffix(){
	Tools.doAction(
				ctx + '/keyword/getCustomKeywordSuffix',
				{},
				false,
				function(data) {
					$("#keywordSuffixUl").html("");
					for (var i = 0; i < data.length; i++) {
						var btn = $("<a href='javascript:void(0)' keywordSuffixId='"
								+ data[i].id
								+ "'>"
								+ data[i].name
								+ "</a></li>");
						var li = $("<li></li>").append(btn);
						li.appendTo($("#keywordSuffixUl"));
						btn.linkbutton({
							iconCls : 'icon-remove',
							iconAlign : 'right'
						});
					}
				});}

function showPresetKeywordSuffix(){
	Tools.doAction(
		ctx + '/keyword/getPresetKeywordSuffix',
		{},
		false,
		function(data) {
			$("#presetKeywordSuffix").html("");
			for (var i = 0; i < data.length; i++) {
				var btn = $("<a href='javascript:void(0)'>"+ data[i].name+ "</a></li>");
				var li = $("<li></li>").append(btn);
				li.appendTo($("#presetKeywordSuffix"));
				btn.linkbutton({
					iconAlign : 'right'
				});
			}
		});}

function addKeywordSuffix() {
	var keywordSuffix = $.trim($("#keywordSuffix").val());
	if (keywordSuffix == "") {
		Tip.alertError("请输入关键词后缀！");
		return false;
	}

	Tools.doAction(ctx + '/keyword/addKeywordSuffix', {
		keywordSuffix : keywordSuffix
	}, false, function(data) {
		if (data.success) {
			$("#keywordSuffix").val("");
			var btn = $("<a href='javascript:void(0)' keywordSuffixId='"
					+ data.obj.id + "'>" + data.obj.name + "</a></li>");
			var li = $("<li></li>").append(btn);
			li.appendTo($("#keywordSuffixUl"));
			btn.linkbutton({
				iconCls : 'icon-remove',
				iconAlign : 'right'
			});
		} else {
			Tip.alertError(data.msg);
		}

	});
}

$("a[keywordSuffixId]").live("click", function() {
	var obj = $(this);
	var id = obj.attr("keywordSuffixId");
	layer.confirm('您确认想要删除【' + obj.text() + '】吗？', function() {
		$.ajax({
			type : "POST",
			url : ctx + "/keyword/deleteKeywordSuffix",
			data : {
				id : id
			},
			async : false,
			success : function(resp) {
				if (resp.success) {
					obj.parent().remove();
					Tip.msgOk("删除成功！");
				}
			}
		});
	});
});

// bind delete event
$("#keyword_delete").click(function() {
	var selections = $("#datagrid_keyword").datagrid("getSelections");
	if (selections && selections.length > 0)
		layer.confirm('确定删除吗？', function() {
			Tools.doAction(ctx + '/keyword/delete', {
				"rowList" : JSON.stringify(selections)
			}, false, function(data) {
				if (data.success) {
					Tip.msgOk("删除成功！");
					init();
				}
			});
		});
	else
		layer.tips('请选择要删除行', this, {
			guide : 2,
			time : 2
		});
});

// 修改面板 提交按钮
$("#keyword_ok").click(function() {
	var addressName1 = $.trim($("input[id='addressName1']").val());
	var addressName2 = $.trim($("input[id='addressName2']").val());
	var addressName3 = $.trim($("input[id='addressName3']").val());
	var deliveryStationName = $.trim($("input[id='deliveryStationName']").val());

	if (!addressName3 == '') {
		if (addressName1 == '' && addressName2 == '') {
			Tip.alertError("关键字1、2不能为空！");
			return;
		}
		if (addressName1== '') {
			Tip.alertError("关键字1不能为空！");
			return;
		}
		if (addressName2 == '') {
			Tip.alertError("关键字2不能为空！");
			return;
		}
	} else if (!addressName2 == '') {
		if (addressName1 == '') {
			Tip.alertError("关键字1不能为空！");
			return;
		}
	}

	if (addressName1 == '') {
		Tip.alertError("关键字不能为空！");
		return;
	}
	if (deliveryStationName == '') {
		Tip.alertError("站点名称不能为空！");
		return;
	}

	var addressDetailList = getSingleSaveParams(addressName1,addressName2,addressName3,deliveryStationName);
	Tools.doAction(ctx + '/keyword/save', {
		addressDetailListJson : JSON.stringify(addressDetailList)
	}, false, function(data) {
		if (!data.success) {
			Tip.msgError(data.msg);
		} else {
			layer.close(keywordEditLayer);
			Tip.msgOk("提交成功！");
			init();
		}
	});
});

// 提交按钮
$("#keyword_submit").click(function() {
	var selections = $("#datagrid_keyword").datagrid("getSelections");
	if (selections && selections.length > 0) {
		var addressDetailList = getMultipleSaveParams(selections);
		Tools.doAction(ctx + '/keyword/save', {
			addressDetailListJson : JSON.stringify(addressDetailList)
		}, false, function(data) {
			if (!data.success) {
				Tip.alertError(data.msg);
			} else {
				layer.close(keywordEditLayer);
				Tip.msgOk("提交成功！");
			}
			init();
		});
	} else
		layer.tips('请选择要提交行', this, {
			guide : 2,
			time : 2
		});

});



function getMultipleSaveParams(selections) {
	var addressDetailList = new Array;

	for (var i = 0; i < selections.length; i++) {
		var addressDetail = {};

		addressDetail.id = selections[i].id;
		addressDetail.province = selections[i].province;
		addressDetail.city = selections[i].city;
		addressDetail.district = selections[i].district;
		addressDetail.addressId1 = selections[i].addressId1;
		addressDetail.addressName1 = selections[i].addressName1;
		addressDetail.addressId2 = selections[i].addressId2;
		addressDetail.addressName2 = selections[i].addressName2;
		addressDetail.addressId3 = selections[i].addressId3;
		addressDetail.addressName3 = selections[i].addressName3;
		addressDetail.deliveryStationName = selections[i].deliveryStationName;

		addressDetailList.push(addressDetail);
	}
	return addressDetailList;
}

function getSingleSaveParams(addressName1,addressName2,addressName3,deliveryStationName) {
	var addressDetailList = new Array;
	var addressDetail = {};
	addressDetail.id = $("input[id='id']").val();
	addressDetail.province = $("input[id='province']").val();
	addressDetail.city = $("input[id='city']").val();
	addressDetail.district = $("input[id='district']").val();
	addressDetail.addressId1 = $("input[id='addressId1']").val();
	addressDetail.addressName1 = addressName1;
	addressDetail.addressId2 = $("input[id='addressId2']").val();
	addressDetail.addressName2 = addressName2;
	addressDetail.addressId3 = $("input[id='addressId3']").val();
	addressDetail.addressName3 = addressName3;
	addressDetail.deliveryStationName =deliveryStationName;
	addressDetailList.push(addressDetail);
	return addressDetailList;
}

// 添加面板 取消按钮
$("#keyword_cancel").click(function() {
	layer.close(keywordEditLayer);
});

// get params
function getQueryParams() {
	return {
		keyword : $("input[code='keyword_query']").val(),
		station : $("input[code='station_query']").val()
	};
}

// init
function init() {
	// init
	var options = $("#datagrid_keyword").datagrid('getPager')
			.data("pagination").options;
	var pageSize = options.pageSize;
	var params = $.extend({
		pageNum : 1,
		pageSize : pageSize
	}, getQueryParams());

	Tools.doAction(ctx + '/keyword/query', params, false, function(data) {
		$("#datagrid_keyword").datagrid('loadData', {
			"rows" : data.list,
			"total" : data.count
		} || {});
		var p = $("#datagrid_keyword").datagrid('getPager');
		$("td:last", p).find("a").click();


	});

}


