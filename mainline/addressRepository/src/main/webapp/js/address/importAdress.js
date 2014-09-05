

var detailRow={
		idField : 'id',
		title : '关键词数据',
		url : 'datagrid?',
		fit : true,
		height : 400,
		loadMsg : '数据加载中...',
		pageSize : 100000,
		pagination : true,
		pageList : [ 10, 20, 30 ],
		sortOrder : 'asc',
		rownumbers : true,
		singleSelect : true,
		fitColumns : true,
		showFooter : true,
		frozenColumns : [ [] ],
		columns : [ [ {
			field : 'id',
			title : '编号',
			hidden : true,
			sortable : true
		}, {
			field : 'province',
			title : '省',
			sortable : true
		}, {
			field : 'city',
			title : '市',
			sortable : false
		}, {
			field : 'address1',
			title : '关键字',
			sortable : true
		}, {
			field : 'deliveryStationName',
			title : '站点',
			sortable : true
		}, {
			field : 'delivererName',
			title : '配送员',
			sortable : true
		}, {
			field : 'status',
			title : '结果',
			sortable : true,
			formatter : function(value, rec, index) {
				if (!rec.status) {
					return '成功';
				}
				return '失败';
			}
		}, {
			field : 'message',
			title : '信息',
			sortable : true
		} ] ],
		onLoadSuccess : function(data) {
			$("#importAddressList").datagrid("clearSelections");
		},
		onClickRow : function(rowIndex, rowData) {
			rowid = rowData.id;
			gridname = 'importAddressList';
		}
	};