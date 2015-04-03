var keywordEditLayer = "";

$("#datagrid_keyword").datagrid({
	url : "",
	height : 350,
	pagination : true,
	fitColumns : false,
	singleSelect : false,
	fit : true,
	pageSize : 10,
	pageList : [10, 30, 50, 100],
	sortOrder : 'desc',
	rownumbers : true,
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
		field : 'address1',
		width : 150,
		title : '关键字1',
		sortable : true
	}, {
		field : 'address2',
		width : 150,
		title : '关键字2',
		sortable : true
	}, {
		field : 'address3',
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
										title : '<div><span style="padding-left: 14px;line-height:14px;">修改关键字</span></div>',
										shadeClose : true,
										maxmin : false,
										fix : false,
										area : [1000, 250],
										page : {
											dom : '#keyword_edit_panel'
										}
									});

							var province = selections[0].province;
							var city = selections[0].city;
							var district = selections[0].district;
							var address1 = selections[0].address1;
							var address2 = selections[0].address2;
							var address3 = selections[0].address3;
							var deliveryStationName = selections[0].deliveryStationName;

							$("input[id='province']").val(province);
							$("input[id='city']").val(city);
							$("input[id='district']").val(district);
							$("input[id='address1']").val(address1);
							$("input[id='address2']").val(address2);
							$("input[id='address3']").val(address3);
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

// bind delete event
// $("#dop_btn_deliveryman_delete").click(function() {
// var selections = $("#dop_datagrid_deliveryman").datagrid("getSelections");
// if (selections && selections.length > 0)
// layer.confirm('确定删除吗？', function() {
// Tools.doAction("deliveryManController.do?delete", {
// "rowList" : JSON.stringify(selections)
// }, false, function(data) {
// if (data.success) {
// Tip.msgOk("删除成功！");
// init();
// }
// });
// });
// else
// layer.tips('请选择要删除行', this, {
// guide : 2,
// time : 2
// });
// });

// 修改面板 提交按钮
$("#keyword_ok").click(function() {
	// TODO 校验
	// var delName = $("input[id='delName']").val();
	var addressDetailList = getSingleSaveParams();
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
				init();
			}
		});
	} else
		layer.tips('请选择要修改行', this, {
			guide : 2,
			time : 2
		});

});

function getMultipleSaveParams(selections) {
	var addressDetailList = new Array;

	for (var i = 0; i < selections.length; i++) {
		var addressDetail = {};

		addressDetail.province = selections[i].province;
		addressDetail.city = selections[i].city;
		addressDetail.district = selections[i].district;
		addressDetail.address1 = selections[i].address1;
		addressDetail.address2 = selections[i].address2;
		addressDetail.address3 = selections[i].address3;
		addressDetail.deliveryStationName = selections[i].deliveryStationName;

		addressDetailList.push(addressDetail);
	}
	return addressDetailList;
}

function getSingleSaveParams() {
	var addressDetailList = new Array;
	var addressDetail = {};
	addressDetail.province = $("input[id='province']").val();
	addressDetail.city = $("input[id='city']").val();
	addressDetail.district = $("input[id='district']").val();
	addressDetail.address1 = $("input[id='address1']").val();
	addressDetail.address2 = $("input[id='address2']").val();
	addressDetail.address3 = $("input[id='address3']").val();
	addressDetail.deliveryStationName = $("input[id='deliveryStationName']")
			.val();
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
		address : $("input[code='address_query']").val(),
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
