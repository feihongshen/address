$(function(){
	init();
});
$("#datagrid_logInfo").datagrid({
	url : "",
	loadMsg : '数据加载中...',
	nowrap : false,
	pagination : true,
	singleSelect : false,
	fit : true,
	pageSize : 20,
	pageList : [10, 20, 30, 40],
	sortOrder : 'desc',
	rownumbers : true,
	fitColumns : true,
	showFooter : true,
	frozenColumns : [[]],
	columns : [[ {
		field : 'id',
		title : 'id',
		hidden : true,
		sortable : true
	}, {
		field : 'operationType',
		width : 150,
		hidden : true,
		title : '操作类型编码',
		sortable : false
	}, {
		field : 'operationName',
		width : 100,
		title : '操作类型',
		sortable : false
	},{
		field : 'logText',
		width : 250,
		title : '日志内容',
		sortable : false
	}, {
		field : 'operationIP',
		width : 100,
		title : '操作人员IP',
		sortable : false
	}, {
		field : 'operationTime',
		width : 150,
		title : '操作时间',
		sortable : true ,
		formatter : function(value, rec, index) {
			return new Date(value).Format('yyyy-MM-dd hh:mm:ss');
		}
	}]]
});

  
var p = $("#datagrid_logInfo").datagrid('getPager');
$(p).pagination({
	onSelectPage : function(pageNumber, pageSize) {
		var params = $.extend({
			pageNum : pageNumber,
			pageSize : pageSize
		}, getQueryParams());
		Tools.doAction(ctx + '/bizLogController/list', params, false, function(data) {
			$("#datagrid_logInfo").datagrid('loadData', {
				"rows" : data.list,
				"total" : data.count
			} || {});

		});
	}
});

$("#search").click(function(){
	init();
});

function getQueryParams() {
	return {
		beginTime : $("input[name='beginTime']").val(),
		endTime : $("input[name='endTime']").val(),
		operationType : $("#operationType").val(),
		operationIP :  $("input[code='operationIP']").val()
	};
}

function init() {
	// init
	var options = $("#datagrid_logInfo").datagrid('getPager').data("pagination").options;
	var pageSize = options.pageSize;
	var params = $.extend({
		pageNum : 1,
		pageSize : pageSize
	}, getQueryParams());

	Tools.doAction(ctx + '/bizLogController/list', params, false, function(data) {
		$("#datagrid_logInfo").datagrid('loadData', {
			"rows" : data.list,
			"total" : data.count
		} || {});
		var p = $("#datagrid_logInfo").datagrid('getPager');
		$("td:last", p).find("a").click();

	});
}
$('#beginTime').datebox({
    formatter: function(date){ return date.getFullYear()+'-'+(date.getMonth()+1)+'-'+date.getDate();},
});
$('#endTime').datebox({
    formatter: function(date){ return date.getFullYear()+'-'+(date.getMonth()+1)+'-'+date.getDate();},
});
