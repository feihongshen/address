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
<script type="text/javascript" src="${pageContext.request.contextPath}/js/comm.js"></script>

<script type="text/javascript" src="${pageContext.request.contextPath}/js/address/import.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/address/importAdress.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/address/result.js"></script>
<script type="text/javascript">
var hasIniUI=false;
	$(function() {
		$("#tabs").tabs({ 
			width:$("#tabs").parent().width(), 
		    height:$("#tabs").parent().height() ,
		    border:false, 
			onSelect:function(title){ 
				if(title=='历史导入记录'&&!hasIniUI){
					alert(hasIniUI)
					hasIniUI=true
					$('#addressImportResult').datagrid(resultRow);
				}else{
					$('#importAddressList').datagrid(detailRow);
				}; 
			}
		});
		
		$("#search").click(function(){
			var queryParams = $('#addressImportResult').datagrid('options').queryParams;
			queryParams['begin_importDate']=$("input[name='begin_date']").val();
			queryParams['end_importDate']=$("input[name='end_date']").val();
			$('#addressImportResult').datagrid({
				url : 'subdatagrid?',
				pageNumber : 1
			});
			
		});
		$("#file").hide();
		$("#startImport").click(function(){
			ajaxFileUpload();
		})
		
		
		
		
		
	});
	
	
</script>
</head>
<body>
<div  id="tabs">
    <div title="关键词导入" style="padding:10px;height: 500px">
      <table width="100%"  border="0" cellspacing="5" cellpadding="0">
      <input type="file" id="file" name="file" onchange="takefile();" >
        <tr>
          <td>选择导入文件：
            <input class="easyui-filebox" onclick="fileSelected();" name="tools" id="tools" data-options="prompt:'请选择'" style="width:200px">
            &nbsp;<a href="javascript:void(0)" class="easyui-linkbutton" id="startImport">开始导入</a>&nbsp;<a href="downloadAddressTemplate">下载导入模板</a></td>
        </tr>
        <tr>
          <td></td>
        </tr>
      </table>
      <div width="100%"  border="0" style="height:200px;" cellpadding="8" id="importAddressList" cellspacing="1" bgcolor="#CCCCCC">
        
      </div>
    </div>
    <div title="历史导入记录" style="padding:10px;height: 500px">
    <table id="commissionBalanceListtb" width="100%" border="0" cellspacing="5" cellpadding="0">
        <tr>
          <td>导入日期：
            <input name="begin_date" id="begin_date" class="easyui-datebox"></input> 至 <input name="end_date" id="end_date" class="easyui-datebox"></input>&nbsp;<a id="search" href="javascript:void(0)" class="easyui-linkbutton" >查询</a></td>
        </tr>
        <tr>
          <td></td>
        </tr>
      </table>
      <div toolbar="#commissionBalanceListtb" id="addressImportResult" width="100%" border="0" cellpadding="8"  cellspacing="1" bgcolor="#CCCCCC">
      </div>
    </div>
  </div>
</body>
</html>
