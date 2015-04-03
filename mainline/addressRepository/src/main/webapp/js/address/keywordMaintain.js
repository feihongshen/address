// add map area panelId
var dopBtndeliverymanAdd = "";
var dopBtndeliverymanAssign = "";
var mapManager = null;
var isModify = false;
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
		title : '省',
		sortable : true
	}, {
		field : 'city',
		width : 150,
		title : '市',
		sortable : false
	}, {
		field : 'district',
		width : 150,
		title : '区、县',
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
		title : '站点',
		sortable : true
	}]]
});

init();

// click pagination
var p = $("#datagrid_keyword").datagrid('getPager');
$(p).pagination(
		{
			onSelectPage : function(pageNumber, pageSize) {
				var params = $.extend({
					pageNum : pageNumber,
					pageSize : pageSize
				}, getQueryParams());
				Tools.doAction("deliveryManController.do?queryList", params,
						false, function(data) {
							$("#dop_datagrid_deliveryman").datagrid('loadData',
									{
										"rows" : data.list,
										"total" : data.count
									} || {});
						});
			}
		});

// bind add event
//$("#dop_btn_deliveryman_add")
//		.click(
//				function() {
//					isModify = false;
//					dopBtndeliverymanAdd = jQuery
//							.layer({
//								type : 1,
//								title : '<div><span style="padding-left: 14px;line-height: 14px;">添加店面</span></div>',
//								shadeClose : true,
//								maxmin : false,
//								fix : false,
//								area : [1000, 250],
//								page : {
//									dom : '#dop_deliveryman_add_panel'
//								}
//							});
//				});

// bind modify event
//$("#dop_btn_deliveryman_modify").click(
//		function() {
//			var selections = $("#dop_datagrid_deliveryman").datagrid(
//					"getSelections");
//			if (selections && selections.length > 0) {
//				if (selections.length > 1) {
//					layer.tips('请选择一行数据', this, {
//						guide : 2,
//						time : 2
//					});
//				} else {
//					// 显示地图div
//					$("#dop_btn_deliveryman_add").click();
//					// 获得此项记录的坐标字符串
//					var delName = selections[0].delName;
//					// 更新区域名称的显示
//					$("input[id='delName']").val(delName);
//					$("input[id='delCertificates']").val(
//							selections[0].delCertificates);
//					$("input[id='delTeliPhone']").val(
//							selections[0].delTeliPhone);
//					$("input[id='delMaxmum']").val(selections[0].delMaxmum);
//					$("select[id='sUser']").val(selections[0].sUser);
//					isModify = true;
//				}
//			} else
//				layer.tips('请选择要修改行', this, {
//					guide : 2,
//					time : 2
//				});
//		});

// bind delete event
//$("#dop_btn_deliveryman_delete").click(function() {
//	var selections = $("#dop_datagrid_deliveryman").datagrid("getSelections");
//	if (selections && selections.length > 0)
//		layer.confirm('确定删除吗？', function() {
//			Tools.doAction("deliveryManController.do?delete", {
//				"rowList" : JSON.stringify(selections)
//			}, false, function(data) {
//				if (data.success) {
//					Tip.msgOk("删除成功！");
//					init();
//				}
//			});
//		});
//	else
//		layer.tips('请选择要删除行', this, {
//			guide : 2,
//			time : 2
//		});
//});

// 添加面板 确定按钮
//$("#dop_deliveryman_add_panel_ok").click(
//		function() {
//			var delName = $("input[id='delName']").val();
//			sUser = $("select[id='sUser']").val();
//			if (!sUser) {
//				layer.tips('请填对应用户信息', document.getElementById("sUser"), {
//					guide : 2,
//					time : 2
//				});
//				return;
//			}
//			if (delName)
//				if (isModify)
//					Tools.doAction("deliveryManController.do?modify", {
//						delName : $("input[id='delName']").val(),
//						delCertificates : $("input[id='delCertificates']")
//								.val(),
//						delPortrait : $("input[id='delPortrait']").val(),
//						delTeliPhone : $("input[id='delTeliPhone']").val(),
//						delMaxmum : $("input[id='delMaxmum']").val(),
//						sUser : $("select[id='sUser']").val(),
//						id : $("#dop_datagrid_deliveryman").datagrid(
//								"getSelections")[0].id
//					}, false, function(data) {
//						layer.close(dopBtndeliverymanAdd);
//						Tip.msgOk("修改成功");
//						$("input[id='delName']").val("");
//						$("input[id='delCertificates']").val("");
//						$("input[id='delTeliPhone']").val("");
//						$("input[id='delMaxmum']").val("");
//						$("select[id='sUser']").val("");
//						init();
//					});
//				else
//					Tools.doAction("deliveryManController.do?create", {
//						delName : $("input[id='delName']").val(),
//						delCertificates : $("input[id='delCertificates']")
//								.val(),
//						delTeliPhone : $("input[id='delTeliPhone']").val(),
//						delMaxmum : $("input[id='delMaxmum']").val(),
//						sUser : $("select[id='sUser']").val()
//					}, false, function(data) {
//						layer.close(dopBtndeliverymanAdd);
//						Tip.msgOk("添加成功");
//						$("input[id='delName']").val("");
//						$("input[id='delCertificates']").val("");
//						$("input[id='delTeliPhone']").val("");
//						$("input[id='delMaxmum']").val("");
//						$("select[id='sUser']").val("");
//						init();
//					});
//			else
//				layer.tips('请填写店面名称', document.getElementById("delName"), {
//					guide : 2,
//					time : 2
//				});
//		});
// 分配面板 确定按钮
//$("#dop_deliveryman_assign_panel_ok").click(function() {
//	var selectedAreaId = $("#area").val();
//
//	Tools.doAction("deliveryManController.do?assignArea", {
//		id : $("#dop_datagrid_deliveryman").datagrid("getSelections")[0].id,
//		areaIdList : selectedAreaId
//	}, false, function(data) {
//		layer.close(dopBtndeliverymanAssign);
//		Tip.msgOk("分配成功");
//		init();
//	});
//
//});

// 添加面板 取消按钮
//$("#dop_deliveryman_add_panel_cancel").click(function() {
//	layer.close(dopBtndeliverymanAdd);
//});
// 分配面板 取消按钮
//$("#dop_deliveryman_assign_panel_cancel").click(function() {
//	layer.close(dopBtndeliverymanAssign);
//});




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
	var options = $("#datagrid_keyword").datagrid('getPager').data(
			"pagination").options;
	var pageSize = options.pageSize;
	var params = $.extend({
		pageNum : 1,
		pageSize : pageSize
	}, getQueryParams());
	
	Tools.doAction(ctx+'/keyword/query', params, false,
			function(data) {
				$("#datagrid_keyword").datagrid('loadData', {
					"rows" : data.list,
					"total" : data.count
				} || {});
				var p = $("#datagrid_keyword").datagrid('getPager');
				$("td:last", p).find("a").click();
			});
}
