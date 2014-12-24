
var resultRow=
{
		idField : 'id',
		title : '关键词数据',
		url : 'subdatagrid?',
		fit : false,
		height : 500,
		loadMsg : '数据加载中...',
		pageSize : 10,
		pagination : true,
		pageList : [ 10, 20, 30],
		sortOrder : 'asc',
		sort:'importDate',
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
					width : 15,
					formatter : function(value, rec, index) {
						if (!rec.id) {
							return '';
						}
						var href = '';
						href += "[<a href='#' onclick=delObj('deleteImportAddressResult?id="
								+ rec.id + "','address')>";
						href += "删除</a>]";
						
						var href1 = '';
						href1 += "[<a href='#' onclick=getImportDetailInfo('datagrid?resultId="
							+ rec.id + "','address')>";
					    href1 += "明细</a>]";
						return href+"&nbsp;&nbsp;&nbsp;&nbsp;"+href1;
					}
				},  
				{
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
						return new Date(value).Format('yyyy-MM-dd hh:mm:ss');
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