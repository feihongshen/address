//add map area panelId 
var StationAdd = "";
var isModify = false;

// show map
mapManager = new AR.ExpdopMap();
mapManager.initializeMap({
	map : 'station_add_panel_map'
});
// 新添加区域 。 因为需要进行确认，所以不自动更新
mapManager.add(AR.ExpdopDrawRegionManager.EventType.NEWPOLYGON, function(e) {
});
// 编辑区域
mapManager.add(AR.ExpdopDrawRegionManager.EventType.SAVEEDIT, function(e) {
	// 编辑结果
	var modifyRegion = e.target;

	// 对编辑的区域进行更新
	// 直接调用方法进行更新
	// 1.更新数据库
	// id 和 baseAreaName 无法取到.可以根据uid进行更新

	Tools.doAction(ctx + '/station/modifyByUid', {
		"uid" : modifyRegion.id,
		"coordinate" : JSON.stringify(modifyRegion),
		"mapcenterLng" : modifyRegion.center.lng,
		"mapcenterLat" : modifyRegion.center.lat
	}, false, function(data) {
		if (data.success) {
			layer.close(StationAdd);
			Tip.msgOk("保存成功！");
			init();
		}
	});
	// 2.更新列表
	// 获取当前页的数据，按照uid进行匹配

});

// 警告
mapManager.add(AR.ExpdopDrawRegionManager.EventType.WARNING, function(e) {

});

// 开始编辑对象时发生
mapManager.add(AR.ExpdopDrawRegionManager.EventType.EDITING, function(e) {

	resetColorPicker(e.target);

});

mapManager.add(AR.ExpdopDrawRegionManager.OverlayEventType.POLYGONCLICK,
		function(e) {
			var uid = e.target.id;
			Tools.doAction(ctx + '/station/getDeliveryStationByUid', {
				"uid" : uid
			}, false, function(data) {
				var name;
				//如果用户重新点击新绘制的区域，此时通过UID取到的是null
				if(data===null){
					var selections = $("#stationList").datagrid("getSelections");
					name=selections[0].name;
				}else{
					name=data.name;
				}
				// 更新区域名称的显示
				$("#station_add_panel_input_area").val(
						name);
			});
		});

function resetColorPicker(styleOptions) {
	// 填充颜色选择器
	var fillColor = new tinycolor(styleOptions.fillColor);
	fillColor.setAlpha(styleOptions.fillOpacity);
	$("#station_add_panel_top_fillcolor").spectrum('set', fillColor);

	// 边框颜色选择器
	var lineColor = new tinycolor(styleOptions.strokeColor);
	lineColor.setAlpha(styleOptions.strokeOpacity);
	$("#station_add_panel_top_linecolor").spectrum('set', lineColor);

	// 线宽
	$("#station_add_panel_top_linewidth").val(styleOptions.strokeWeight);
}

// 绘制站点区域
function editStation(){
	var selections = $("#stationList").datagrid("getSelections");
	// 显示地图div
	showMapPanel();
	// 获得此项记录的多边形信息
	var polygon = selections[0].coordinate;
	if (null == polygon) {
		polygon = undefined;
	}

	// 获取相关显示的区域
	var existsRegions = [];
	// 暂时可以选择添加全部的列表中的区域
	// 第一页 50个元素
	var params = {
		pageNum : 1,
		pageSize : 50,
		baseAreaName : ""
	};
	// 同步ajax调用
	Tools
			.doAction(
					ctx + '/station/listAll',
					params,
					false,
					function(data) {
						if (data) {
							for (var inte = 0, length = data.length; inte < length; inte++) {
								if (selections[0].id != data[inte].id) {
									// 根据id排除当前选中对象
									existsRegions
											.push(data[inte].coordinate);
								}
							}
						}
					});

	// 开启编辑状态
	mapManager.startDrawRegion({
		overlay : polygon,
		exists : existsRegions
	})
	// 更新区域名称的显示
	$("#station_add_panel_input_area").val(
			selections[0].name);

	// 更新风格选择器中的当前区域的渲染风格。
	var styles = JSON.parse(polygon);
	resetColorPicker(styles.styleOptions);
	// 隐藏区域编辑中不会使用到的按钮
	$(".dop-region-edit").hide();

}

// bind map panel reset event
$("#station_add_panel_btn_reset").click(function() {
	mapManager.reDraw();
});

// bind map panel modify event
$("#station_add_panel_btn_edit").click(function() {
	mapManager.editDrawRegion();
});

// bind map panel save event
$("#station_add_panel_btn_save").click(function() {
	mapManager.saveEditDrawRegion();
});

// bind map panel submit event
$("#station_add_panel_btn_submit").click(
		function() {
			var selections = $("#stationList").datagrid("getSelections");
			var id = selections[0].id;

			var polygon = mapManager.getNewDrawRegion();

			if (!polygon||(polygon&&polygon.path.length<2)) {
				Tip.msgError("请绘制完成后再提交！");
				return;
			}
			if (polygon) {
				Tools.doAction(ctx + '/station/modifyById', {
					"id" : id,
					"coordinate" : JSON.stringify(polygon),
					"uid" : polygon.id,
					"mapcenterLng" : polygon.center.lng,
					"mapcenterLat" : polygon.center.lat
				}, false, function(data) {
					if (data.success) {
						layer.close(StationAdd);
						Tip.msgOk("提交成功！");
						init();
					}
				});
			}

			// 结束地图的编辑状态
			mapManager.stopDrawRegion();

		});

// bind map panel cancel event
$("#dop_btn_baseInfo_cancel").click(function() {
	layer.close(dopBtnBaseInfoAdd);

	// 结束地图的编辑状态
	mapManager.stopDrawRegion();
});

// init
function init() {
	Tools.doAction(ctx + '/station/listAll', {}, false, function(data) {
		$('#stationList').datagrid(data);
	});
}

/**
 * 显示地图所在的dialog
 */
function showMapPanel() {
	StationAdd = jQuery
			.layer({
				type : 1,
				title : '<div><i class="icon-map-marker" style="margin-top:3px;"></i><span style="padding-left: 14px;line-height: 14px;">站点区域维护</span></div>',
				shadeClose : true,
				maxmin : false,
				fix : false,
				area : [1000, 550],
				page : {
					dom : '#station_add_panel'
				}
			});
}

/**
 * 设置区域的风格选项
 */
initializeStyleOptions();

function resetColorPicker(styleOptions) {
	// 填充颜色选择器
	var fillColor = new tinycolor(styleOptions.fillColor);
	fillColor.setAlpha(styleOptions.fillOpacity);
	$("#station_add_panel_top_fillcolor").spectrum('set', fillColor);

	// 边框颜色选择器
	var lineColor = new tinycolor(styleOptions.strokeColor);
	lineColor.setAlpha(styleOptions.strokeOpacity);
	$("#station_add_panel_top_linecolor").spectrum('set', lineColor);

	// 线宽

	$("#station_add_panel_top_linewidth").val(styleOptions.strokeWeight);
}

/**
 * 初始化区域风格选项
 */
function initializeStyleOptions() {
	// 边框颜色
	var lineC = new tinycolor("00f");
	lineC.setAlpha(0.6);
	$("#station_add_panel_top_linecolor")
			.spectrum(
					{
						color : lineC,
						showAlpha : true,
						chooseText : "应用",
						cancelText : "取消",
						change : function(color) {
							var lineWidth = $(
									'#station_add_panel_top_linewidth').val();
							var fillColor = $(
									"#station_add_panel_top_fillcolor")
									.spectrum('get');
							setStyleOptions(color.toHexString(), color
									.getAlpha(), 3, fillColor.toHexString(),
									fillColor.getAlpha());;
						}
					});

	// 填充颜色改变
	var fillC = new tinycolor("000");
	fillC.setAlpha(0.2);
	$("#station_add_panel_top_fillcolor")
			.spectrum(
					{
						color : fillC,
						showAlpha : true,
						chooseText : "应用",
						cancelText : "取消",
						change : function(color) {
							var lineWidth = $(
									'#station_add_panel_top_linewidth').val();
							var lineColor = $(
									"#station_add_panel_top_linecolor")
									.spectrum('get');
							setStyleOptions(lineColor.toHexString(), lineColor
									.getAlpha(), lineWidth,
									color.toHexString(), color.getAlpha());;
						}
					});

	// 线宽改变
	$('#station_add_panel_top_linewidth').change(
			function(e) {
				var lineWidth = $('#station_add_panel_top_linewidth').val();
				var fillColor = $("#station_add_panel_top_fillcolor").spectrum(
						'get');
				var lineColor = $("#station_add_panel_top_linecolor").spectrum(
						'get');
				setStyleOptions(lineColor.toHexString(), lineColor.getAlpha(),
						lineWidth, fillColor.toHexString(), fillColor
								.getAlpha());;
			});

	function setStyleOptions(lineColor, lineAlpha, lineWidth, fillColor,
			fillAlpha) {
		mapManager.setDefaultStyleOptions({
			strokeColor : lineColor, // 边线颜色。
			fillColor : fillColor, // 填充颜色。当参数为空时，圆形将没有填充效果。
			strokeWeight : lineWidth, // 边线的宽度，以像素为单位。
			strokeOpacity : lineAlpha, // 边线透明度，取值范围0 - 1。
			fillOpacity : fillAlpha, // 填充的透明度，取值范围0 - 1。
			strokeStyle : 'solid' // 边线的样式，solid或dashed。
		});
	}

}
