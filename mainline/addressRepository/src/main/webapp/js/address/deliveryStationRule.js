var pureStation;
function initStations(data){
	$(".deliveryStationRule").each(function(i){
		if(i==0){
			return;
		}
		$(this).remove();
	});
	$(".vendors4combobox").each(function(i){
		if(i==0){
			return;
		}
		$(this).remove();
	});
	$("#deliveryStationId").empty();
	optionData=data.rows;
	for(var i=0;i<optionData.length;i++){
		var option=$("  <option value="+optionData[i]['id']+">"+optionData[i]['deliveryStationName']+"</option>");
		$("#deliveryStationId").append(option);
		
	}
	backNode=$("#deliveryStationRule").clone(true);
}

var backvendors;
function initOption(){
	 $.ajax({
		 type: "POST",
			url:cxt+"/deliveryStationRule/vendors4combobox",
			success:function(optionData){
				for(var i=0;i<optionData.length;i++){
					var option=$("  <option value="+optionData[i]['id']+">"+optionData[i]['text']+"</option>");
					$("#vendorsId").append(option);
					
				}
				
				backvendors=$("#vendors4combobox").clone(true);
			}
		});
}

function getAll(){
$.ajax({
	 type: "POST",
		url:cxt+"/address/getZTree",
		data:{isBind:true},
		success:function(optionData){
	        var t = $("#tree");
	        zTree = $.fn.zTree.init(t, setting, optionData);
			
		}
	});
}

function saveRule(){
	if(addressLevel<3){
		alert("请选择区县进行绑定！");
		return;
	}
	deliveryStationRule="";
   	var len=$(".deliveryStationId").length-1;
   	//用#拼接参数字段
		$(".deliveryStationId").each(function(j){
			var c='.rule:eq('+j+')';
			var rule=$(c).val()+" ";
			var val=$(this).val();
			if(val){
				deliveryStationRule+=val+"#"+rule;
			}
			if(len!=j){
				deliveryStationRule+=",";
			}
		});
		if(deliveryStationRule){
			
		$.ajax({
			 type: "POST",
				url:cxt+"/deliveryStationRule/saveDeliveryStationRule",
				data:{"deliveryStationRule":deliveryStationRule,"addressId":addressId},
				success:function(optionData){
					if(optionData.success){
						alert("成功");
						
					}else{
						var msg="保存失败";
						if(optionData.attributes){
							$.each(optionData.attributes,function(i,term){
								if(!i){
									msg="默认";
								}
								msg+=term;
							})
						}
						alert(msg);
					}
				}
			});
		}
}

var detailRow={
		idField : 'id',
		title : '关联站点',
		url : cxt+'/deliveryStationRule/datagrid?addressId=0',
		fit : false,
		height : 300,
		loadMsg : '数据加载中...',
		pageSize : 10,
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
						href += "[<a href='#' onclick=delObj('delete?deliveryStationRuleId="
								+ rec.id + "','address')>";
						href += "删除</a>]";
						return href;
					}
				}, 

				{
					field : 'deliveryStationName',
					title : '站点名称',
					width : 50,
					sortable : true
				}

		] ],
		onLoadSuccess : function(data) {
			initStations(data);
			$("#stationList").datagrid("clearSelections");
		},
		onClickRow : function(rowIndex, rowData) {
			rowid = rowData.id;
			gridname = 'stationList';
		}
	};

function reloadTable(){
	$("#stationList").datagrid({
		url : cxt+'/deliveryStationRule/datagrid?addressId='+addressId,
		pageNumber : 1
	});
}