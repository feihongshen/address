<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>站点管理</title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
<script type="text/javascript">
var ctx = '${pageContext.request.contextPath}';
</script>
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/easyui/themes/default/easyui.css" />
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/easyui/themes/icon.css" />
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-1.8.0.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/easyui/jquery.easyui.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/ajaxfileupload.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/address/importStationAddress.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/address/exportAddress.js">

</script>
</head>
<body>
<div class="easyui-layout" style="height:560px;">
<div data-options="region:'center'" style="border:0px;">
<br/>
&nbsp;&nbsp;&nbsp;<a href="javascript:" class="easyui-linkbutton" id="exportAddress">导出关键字</a>
<a href="javascript:" class="easyui-linkbutton" id="importAddress">导入关键字</a>
<br/><br/>
<table width="80%"  border="1"  cellpadding="8" id="stationList" cellspacing="1" bgcolor="#CCCCCC"></table>
 <div id="dlg" class="easyui-dialog" title="导入地址模板" style="width:400px;height:200px;padding:10px;">
	 <table width="100%"  border="0" cellspacing="5" cellpadding="0">
        <tr>
          <td> <input type="file" id="file" name="file" onchange="takefile('file');" >
              	选择导入文件：
            <input class="easyui-filebox" onclick="fileSelected('file');" name="tools" id="tools" data-options="prompt:'请选择'" style="width:200px">
            </td>
        </tr>
        <tr>
          <td><a href="javascript:void(0)" class="easyui-linkbutton" id="startImport">开始导入</a>&nbsp;<a href="downloadAddressTemplate">下载导入模板</a></td>
        </tr>
      </table>
      </div>

<div id="dlgStation" class="easyui-dialog" title="请选择需要导出关键字的站点" style="width:500px;height:320px;padding:10px;">
	 <div id="stationShow" style="overflow:auto;height:200px;padding:10px;"></div>
	 <div style="margin:auto;text-align:center;"><a href="javascript:$.messager.alert('提示', '请选择站点！')"  id="startExport">导出</a></div>
</div>
<div id="dlgImport" class="easyui-dialog" title="导入关键字" style="width:500px;height:320px;padding:10px;">
 <table width="100%"  border="0" cellspacing="5" cellpadding="0">
        <tr>
          <td> <input type="file" id="file1" name="file1" onchange="takefile('file1');" >
              	选择导入文件：
            <input class="easyui-filebox" onclick="fileSelected('file1');"   data-options="prompt:'请选择'" style="width:200px">
            </td>
        </tr>
        <tr>
          <td><a href="javascript:void(0)" class="easyui-linkbutton" id="startKwImport">开始导入</a>&nbsp;</td>
        </tr>
      </table>
</div>
</div>
</div>
</body>
</html>
