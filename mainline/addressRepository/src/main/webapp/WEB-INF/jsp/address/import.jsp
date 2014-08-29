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
<script type="text/javascript" src="${pageContext.request.contextPath}/js/ajaxfileupload.js"></script>
<script type="text/javascript">
	$(function() {
	});
</script>
</head>
<body>
<div class="easyui-tabs">
    <div title="关键词导入" style="padding:10px">
      <table width="100%" border="0" cellspacing="5" cellpadding="0">
        <tr>
          <td>选择导入文件：
            <input class="easyui-filebox" name="file1" data-options="prompt:'请选择'" style="width:200px">
            &nbsp;<a href="javascript:void(0)" class="easyui-linkbutton">开始导入</a>&nbsp;<a href="downloadAddressTemplate">下载导入模板</a></td>
        </tr>
        <tr>
          <td></td>
        </tr>
      </table>
      <table width="100%" border="0" cellpadding="8" cellspacing="1" bgcolor="#CCCCCC">
        <thead>
          <tr>
            <th align="center" bgcolor="#f1f1f1">省</th>
            <th align="center" bgcolor="#f1f1f1">市</th>
            <th align="center" bgcolor="#f1f1f1">区/县</th>
            <th align="center" bgcolor="#f1f1f1">关键字</th>
            <th align="center" bgcolor="#f1f1f1">站点</th>
            <th align="center" bgcolor="#f1f1f1">配送员</th>
            <th align="center" bgcolor="#f1f1f1">结果</th>
            <th align="center" bgcolor="#f1f1f1">信息</th>
          </tr>
          <tr>
            <td align="center" bgcolor="#FFFFFF">云南</td>
            <td align="center" bgcolor="#FFFFFF">曲靖市</td>
            <td align="center" bgcolor="#FFFFFF">麒麟区</td>
            <td align="center" bgcolor="#FFFFFF">南宁廖廓西山白石口建宁街道以外南宁西路政府一号院<</td>
            <td align="center" bgcolor="#FFFFFF">云南分站</td>
            <td align="center" bgcolor="#FFFFFF">张小三</td>
            <td align="center" bgcolor="#FFFFFF">成功</td>
            <td align="center" bgcolor="#FFFFFF">重复</td>
          </tr>
          <tr>
            <td align="center" bgcolor="#FFFFFF">云南</td>
            <td align="center" bgcolor="#FFFFFF">曲靖市</td>
            <td align="center" bgcolor="#FFFFFF">麒麟区</td>
            <td align="center" bgcolor="#FFFFFF">南宁廖廓西山白石口建宁街道以外南宁西路政府一号院<</td>
            <td align="center" bgcolor="#FFFFFF">云南分站</td>
            <td align="center" bgcolor="#FFFFFF">张小三</td>
            <td align="center" bgcolor="#FFFFFF">成功</td>
            <td align="center" bgcolor="#FFFFFF">重复</td>
          </tr>
          <tr>
            <td align="center" bgcolor="#FFFFFF">云南</td>
            <td align="center" bgcolor="#FFFFFF">曲靖市</td>
            <td align="center" bgcolor="#FFFFFF">麒麟区</td>
            <td align="center" bgcolor="#FFFFFF">南宁廖廓西山白石口建宁街道以外南宁西路政府一号院<</td>
            <td align="center" bgcolor="#FFFFFF">云南分站</td>
            <td align="center" bgcolor="#FFFFFF">张小三</td>
            <td align="center" bgcolor="#FFFFFF">成功</td>
            <td align="center" bgcolor="#FFFFFF">重复</td>
          </tr>
          <tr>
            <td align="center" bgcolor="#FFFFFF">云南</td>
            <td align="center" bgcolor="#FFFFFF">曲靖市</td>
            <td align="center" bgcolor="#FFFFFF">麒麟区</td>
            <td align="center" bgcolor="#FFFFFF">南宁廖廓西山白石口建宁街道以外南宁西路政府一号院<</td>
            <td align="center" bgcolor="#FFFFFF">云南分站</td>
            <td align="center" bgcolor="#FFFFFF">张小三</td>
            <td align="center" bgcolor="#FFFFFF">成功</td>
            <td align="center" bgcolor="#FFFFFF">重复</td>
          </tr>
          <tr>
            <td align="center" bgcolor="#FFFFFF">云南</td>
            <td align="center" bgcolor="#FFFFFF">曲靖市</td>
            <td align="center" bgcolor="#FFFFFF">麒麟区</td>
            <td align="center" bgcolor="#FFFFFF">南宁廖廓西山白石口建宁街道以外南宁西路政府一号院<</td>
            <td align="center" bgcolor="#FFFFFF">云南分站</td>
            <td align="center" bgcolor="#FFFFFF">张小三</td>
            <td align="center" bgcolor="#FFFFFF">成功</td>
            <td align="center" bgcolor="#FFFFFF">重复</td>
          </tr>
          <tr>
            <td align="center" bgcolor="#FFFFFF">云南</td>
            <td align="center" bgcolor="#FFFFFF">曲靖市</td>
            <td align="center" bgcolor="#FFFFFF">麒麟区</td>
            <td align="center" bgcolor="#FFFFFF">南宁廖廓西山白石口建宁街道以外南宁西路政府一号院<</td>
            <td align="center" bgcolor="#FFFFFF">云南分站</td>
            <td align="center" bgcolor="#FFFFFF">张小三</td>
            <td align="center" bgcolor="#FFFFFF">成功</td>
            <td align="center" bgcolor="#FFFFFF">重复</td>
          </tr>
        </thead>
      </table>
    </div>
    <div title="历史导入记录" style="padding:10px">
    <table width="100%" border="0" cellspacing="5" cellpadding="0">
        <tr>
          <td>导入日期：
            <input class="easyui-datebox"></input> 至 <input class="easyui-datebox"></input>&nbsp;<a href="javascript:void(0)" class="easyui-linkbutton" >查询</a></td>
        </tr>
        <tr>
          <td></td>
        </tr>
      </table>
      <table width="100%" border="0" cellpadding="8" cellspacing="1" bgcolor="#CCCCCC">
        <thead>
          <tr>
            <th align="center" bgcolor="#f1f1f1">省</th>
            <th align="center" bgcolor="#f1f1f1">市</th>
            <th align="center" bgcolor="#f1f1f1">区/县</th>
            <th align="center" bgcolor="#f1f1f1">关键字</th>
            <th align="center" bgcolor="#f1f1f1">站点</th>
            <th align="center" bgcolor="#f1f1f1">配送员</th>
            <th align="center" bgcolor="#f1f1f1">结果</th>
            <th align="center" bgcolor="#f1f1f1">信息</th>
          </tr>
          <tr>
            <td align="center" bgcolor="#FFFFFF">云南</td>
            <td align="center" bgcolor="#FFFFFF">曲靖市</td>
            <td align="center" bgcolor="#FFFFFF">麒麟区</td>
            <td align="center" bgcolor="#FFFFFF">南宁廖廓西山白石口建宁街道以外南宁西路政府一号院<</td>
            <td align="center" bgcolor="#FFFFFF">云南分站</td>
            <td align="center" bgcolor="#FFFFFF">张小三</td>
            <td align="center" bgcolor="#FFFFFF">成功</td>
            <td align="center" bgcolor="#FFFFFF">重复</td>
          </tr>
          <tr>
            <td align="center" bgcolor="#FFFFFF">云南</td>
            <td align="center" bgcolor="#FFFFFF">曲靖市</td>
            <td align="center" bgcolor="#FFFFFF">麒麟区</td>
            <td align="center" bgcolor="#FFFFFF">南宁廖廓西山白石口建宁街道以外南宁西路政府一号院<</td>
            <td align="center" bgcolor="#FFFFFF">云南分站</td>
            <td align="center" bgcolor="#FFFFFF">张小三</td>
            <td align="center" bgcolor="#FFFFFF">成功</td>
            <td align="center" bgcolor="#FFFFFF">重复</td>
          </tr>
          <tr>
            <td align="center" bgcolor="#FFFFFF">云南</td>
            <td align="center" bgcolor="#FFFFFF">曲靖市</td>
            <td align="center" bgcolor="#FFFFFF">麒麟区</td>
            <td align="center" bgcolor="#FFFFFF">南宁廖廓西山白石口建宁街道以外南宁西路政府一号院<</td>
            <td align="center" bgcolor="#FFFFFF">云南分站</td>
            <td align="center" bgcolor="#FFFFFF">张小三</td>
            <td align="center" bgcolor="#FFFFFF">成功</td>
            <td align="center" bgcolor="#FFFFFF">重复</td>
          </tr>
          <tr>
            <td align="center" bgcolor="#FFFFFF">云南</td>
            <td align="center" bgcolor="#FFFFFF">曲靖市</td>
            <td align="center" bgcolor="#FFFFFF">麒麟区</td>
            <td align="center" bgcolor="#FFFFFF">南宁廖廓西山白石口建宁街道以外南宁西路政府一号院<</td>
            <td align="center" bgcolor="#FFFFFF">云南分站</td>
            <td align="center" bgcolor="#FFFFFF">张小三</td>
            <td align="center" bgcolor="#FFFFFF">成功</td>
            <td align="center" bgcolor="#FFFFFF">重复</td>
          </tr>
          <tr>
            <td align="center" bgcolor="#FFFFFF">云南</td>
            <td align="center" bgcolor="#FFFFFF">曲靖市</td>
            <td align="center" bgcolor="#FFFFFF">麒麟区</td>
            <td align="center" bgcolor="#FFFFFF">南宁廖廓西山白石口建宁街道以外南宁西路政府一号院<</td>
            <td align="center" bgcolor="#FFFFFF">云南分站</td>
            <td align="center" bgcolor="#FFFFFF">张小三</td>
            <td align="center" bgcolor="#FFFFFF">成功</td>
            <td align="center" bgcolor="#FFFFFF">重复</td>
          </tr>
          <tr>
            <td align="center" bgcolor="#FFFFFF">云南</td>
            <td align="center" bgcolor="#FFFFFF">曲靖市</td>
            <td align="center" bgcolor="#FFFFFF">麒麟区</td>
            <td align="center" bgcolor="#FFFFFF">南宁廖廓西山白石口建宁街道以外南宁西路政府一号院<</td>
            <td align="center" bgcolor="#FFFFFF">云南分站</td>
            <td align="center" bgcolor="#FFFFFF">张小三</td>
            <td align="center" bgcolor="#FFFFFF">成功</td>
            <td align="center" bgcolor="#FFFFFF">重复</td>
          </tr>
        </thead>
      </table>
    </div>
  </div>
</body>
</html>
