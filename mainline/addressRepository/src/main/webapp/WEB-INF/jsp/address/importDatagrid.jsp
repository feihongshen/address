<%@ page language="java" pageEncoding="UTF-8"%>
<%@page import="java.util.*"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
<head>
<title>地址匹配</title>
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/easyui/themes/default/easyui.css" />
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/easyui/themes/icon.css" />
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-1.8.0.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/easyui/jquery.easyui.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/easyui/easyui-lang-zh_CN.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/ajaxfileupload.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/crudutil.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/js/lhgDialog/lhgdialog.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/address/import.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/address/importAdress.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/comm.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/address/result.js"></script>
<script type="text/javascript">
var process = {};
var t ;
var hasIniUI=false;
	$(function() {
		$("#tabs").tabs({ 
			width:$("#tabs").parent().width(), 
		    height:$("#tabs").parent().height() ,
		    border:false, 
			onSelect:function(title){ 
				if(title=='历史导入记录'){
					$('#addressImportResult').datagrid(resultRow);
				}else if(!hasIniUI){
					$('#importAddressList').datagrid(detailRow);
					hasIniUI=true;
				}; 
			}
		});
		
		$("#search").click(function(){
			var queryParams = $('#addressImportResult').datagrid('options').queryParams;
			queryParams['importDate_begin']=$("input[name='begin_date']").val();
			queryParams['importDate_end']=$("input[name='end_date']").val();
			$('#addressImportResult').datagrid({
				url : 'subdatagrid?',
				pageNumber : 1
			});
			
		});
		$("#file").hide();
		$("#startImport").click(function(){
			ajaxFileUpload();
		});
		
	});
	function reloadTable() {
		try {
			$('#addressImportResult').datagrid('reload');
		} catch (ex) {
		}
	}
	
	function getImportDetailInfo(url,name){
		$.ajax({
			async : false,
			cache : false,
			type : 'POST',
			url : url,// 请求的action路径
			success : function() {
				$('#importAddressList').datagrid(
						{
							idField : 'id',
							title : '关键词数据',
							url : url,
							fit : false,
							height : 500,
							loadMsg : '数据加载中...',
							pageSize : 10,
							pagination : true,
							pageList : [ 10, 20, 30],
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
							},
							{
								field : 'district',
								title : '区、县',
								sortable : false
							},{
								field : 'address1',
								title : '关键字1',
								sortable : true
							},{
								field : 'address2',
								title : '关键字2',
								sortable : true
							},{
								field : 'address3',
								title : '关键字3',
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
						}
				);
				$("#tabs").tabs('select',0);
			}
		});
}
	
</script>
</head>
<body>
<div  id="tabs">
    <div title="关键词导入" style="padding:10px;height: 600px">
      <table width="100%"  border="0" cellspacing="5" cellpadding="0">
      <input type="file" id="file" name="file" onchange="takefile();" >
        <tr>
          <td>
                               <div style="float:left">选择导入文件：
            <input class="easyui-filebox" onclick="fileSelected();" name="tools" id="tools" data-options="prompt:'请选择'" style="width:200px">
            &nbsp;<a href="javascript:void(0)" class="easyui-linkbutton" id="startImport">开始导入</a>&nbsp;
            <a href="downloadAddressTemplate">下载导入模板</a></div>
           <div id="procDiv" style="display:none"><div id="proc" class="easyui-progressbar" style="width:400px;float:left;margin-left:30px;"></div>
            <span id="importProc" style="padding:10px 0px 0px 10px;"></span></div></td>
        </tr>
      </table>
      <table width="100%"  border="0" style="height:200px;" cellpadding="8" id="importAddressList" cellspacing="1" bgcolor="#CCCCCC">
      </table>
    </div>
    <div title="历史导入记录" style="padding:10px;height: 600px">
    <table id="commissionBalanceListtb" width="100%" border="0" cellspacing="5" cellpadding="0">
        <tr>
          <td>导入日期：
            <input name="begin_date" id="begin_date" class="easyui-datebox"></input>
             至 <input name="end_date" id="end_date" class="easyui-datebox"></input>&nbsp;<a id="search" href="javascript:void(0)" class="easyui-linkbutton" >查询</a></td>
        </tr>
        <tr>
          <td></td>
        </tr>
      </table>
      <div  id="addressImportResult" width="100%" border="0" cellpadding="8"  cellspacing="1" bgcolor="#CCCCCC">
      </div>
    </div>
  </div>
</body>
</html>
