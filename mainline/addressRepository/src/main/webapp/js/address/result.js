
var resultRow=
{
		idField : 'id',
		title : '关键词数据',
		url : 'subdatagrid?',
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
		columns : [ [
				{
					field : 'id',
					title : '编号',
					hidden : true,
					sortable : true
				},
				{
					field : 'opt',
					title : '操作',
					width : 10,
					formatter : function(value, rec, index) {
						if (!rec.id) {
							return '';
						}
						var href = '';
						href += "[<a href='#' onclick=delObj('del?id="
								+ rec.id + "','importAddressList')>";
						href += "删除</a>]";
						return href;
					}
				}, {
					field : 'userId',
					title : '用户',
					width : 50,
					sortable : false
				}, {
					field : 'successCount',
					title : '导入关键词数量',
					width : 50,
					sortable : true
				},

				{
					field : 'importDate',
					title : '导入日期',
					width : 50,
					sortable : true,
					formatter : function(value, rec, index) {
						return new Date(value).Format('yyyy-MM-dd');
					}
				}

		] ],
		onLoadSuccess : function(data) {
			$("#addressImportResult").datagrid("clearSelections");
		},
		onClickRow : function(rowIndex, rowData) {
			rowid = rowData.id;
			gridname = 'addressImportResult';
		}
	};