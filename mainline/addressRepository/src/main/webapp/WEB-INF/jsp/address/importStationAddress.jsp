<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>站点管理</title>
<%@include file="/WEB-INF/jsp/common/lib.jsp"%>
<script type="text/javascript" src="${ctx}/js/address/importStationAddress.js"></script>
<style type="text/css">
.table {
    border-collapse: collapse;
    border-left: 1px solid #cccccc;
    border-top: 1px solid #cccccc;
    color: #333333;
    font-family: Verdana,Arial,Helvetica,sans-serif;
    width: 100%;
    font-size:12px;
}
.table > tbody {
    display: table-row-group;
    vertical-align: middle;
}
 .table > tbody > tr td, table th {
    border-bottom: 1px solid #cccccc;
    border-right: 1px solid #cccccc;
    line-height: 1.4em;
    padding: 7px 8px;
    vertical-align: middle;
    font-size:12px;
   
}
 .table > thead > tr> td {
    border-bottom: 1px solid #cccccc;
    border-right: 1px solid #cccccc;
    line-height: 1.4em;
    padding: 7px 8px;
    background-color:#9BCD9B;
    vertical-align: middle;
    font-size:14px;
    color:#000;
     font-weight:bold;
}
</style>
</head>
<body>
<div class="easyui-layout" style="height:500px;">
<div data-options="region:'center'" style="border:0px;">
<table width="80%"  border="1"  cellpadding="8" id="stationList" cellspacing="1" bgcolor="#CCCCCC"></table>
 <div id="dlg" class="easyui-dialog" title="导入地址模板" style="width:650px;height:440px;padding:10px;">
    <input type="hidden" id="stationDlgId" value=""/>
	 <table width="100%"  border="0" cellspacing="5" cellpadding="0">
        <tr>
          <td> <input type="file" id="file" name="file" onchange="takefile('file');" />
              	选择导入文件：
            <input class="easyui-filebox" onclick="fileSelected('file');" name="tools" id="tools" data-options="prompt:'请选择'" style="width:400px"/>
            </td><td><a href="javascript:void(0)" class="easyui-linkbutton" id="startImport">开始导入</a>&nbsp;</td>
        </tr>
      </table>
       <table width="100%" id="resultTable" border="0" cellspacing="5" cellpadding="0" class="table" style="margin-top:0px;">
       	 
      </table>
      </div>
</div>
</div>
</body>
</html>
