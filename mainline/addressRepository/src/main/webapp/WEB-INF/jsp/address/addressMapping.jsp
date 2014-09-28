<%@ page language="java" pageEncoding="UTF-8"%>
<%@ page import="java.util.*"%>
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
<script type="text/javascript">
var arrsum=['insum','unsum','susum','dsum','pper'];
var usedData;
var tableData;
$(function(){
	$("#mapping").click(function(){
		$.ajax({
		 type: "POST",
			url:"<%=request.getContextPath()%>/address/parseAdress",
			data:{needMatched:$("#needMatched").val()},
			success:function(optionData){
				var data=optionData['attributes'];
				if(!data){
					alert("没有相关匹配");
				}
				for(var i=0;i<arrsum.length;i++){
					var temp=arrsum[i];
					$("."+temp).text(data[temp]);
				}
				usedData=data;
				
			}
		});
	});
	$("#tabs").tabs({ 
		onSelect:function(title){ 
			var usedTable;
			if(!usedData){
				return;
			}
			switch (title) {
			case '未匹配':
				tableData=usedData['unList'];
				usedTable=$("#untable");
				break;
			case '匹配成功':
				tableData=usedData['suList'];
				usedTable=$("#sutable");
				break;
			case '匹配多站':
				tableData=usedData['dList'];
				usedTable=$("#dtable");
				break;
			case '命中关键字未匹配站点':
				tableData=usedData['kList'];
				usedTable=$("#keytable");
				break;

			default:
				break;
			}
			$(".tr"+title).remove();
			if(usedTable){
				for(var i=0;i<tableData.length;i++){
					var tr="<tr class='tr"+title+"'> <td align='center' bgcolor='#FFFFFF'>"+(1+i)+"</td> <td align='center' bgcolor='#FFFFFF'>"+tableData[i]['key']+"</td> <td align='center' bgcolor='#FFFFFF'>"+tableData[i]['val']+"</td>"
					usedTable.append(tr);
				}
			}
		}
	});
})
</script>
</head>

<body>
<div class="easyui-tabs" id="tabs">
    <div title="执行匹配" style="padding:10px">
      <textarea name="needMatched" id="needMatched" class="textbox" style="height:450px; width:100%" data-options="multiline:true"></textarea>
      </input>
      <table width="100%" border="0" cellpadding="5" cellspacing="0">
        <tr>
          <td><a href="javascript:void(0)" class="easyui-linkbutton" id="mapping" iconCls="icon-ok" >匹配</a></td>
          <td>本次匹配地址共<span class="insum">0条</span>,未匹配<span class="unsum">0</span>,成功匹配<span class="susum">0</span>,匹配多站<span class="dsum">0</span>,匹配率:<span class="pper">0</span>%</td>
        </tr>
      </table>
    </div>
    <div title="未匹配" style="padding:10px">
      <h2 align="right">未匹配共<span class="unsum">0</span>条</h2>
      <table id="untable" width="100%" border="0" cellpadding="8" cellspacing="1" bgcolor="#CCCCCC">
        <thead>
          <tr>
            <th align="center" bgcolor="#f1f1f1">序号</th>
            <th align="center" bgcolor="#f1f1f1">地址</th>
            <th align="center" bgcolor="#f1f1f1">站点</th>
          </tr>
          
         
        </thead>
      </table>
    </div>
    <div title="匹配成功" style="padding:10px">
      <h2 align="right">匹配成功共<span class="susum">0条</span></h2>
      <table id="sutable" width="100%" border="0" cellpadding="8" cellspacing="1" bgcolor="#CCCCCC">
        <thead>
          <tr>
            <th align="center" bgcolor="#f1f1f1">序号</th>
            <th align="center" bgcolor="#f1f1f1">地址</th>
            <th align="center" bgcolor="#f1f1f1">站点</th>
          </tr>
         
        </thead>
      </table>
    </div>
    <div title="匹配多站" style="padding:10px">
      <h2 align="right">匹配多站共<span class="dsum">0条</span></h2>
      <table  id="dtable" width="100%" border="0" cellpadding="8" cellspacing="1" bgcolor="#CCCCCC">
        <thead>
          <tr>
            <th align="center" bgcolor="#f1f1f1">序号</th>
            <th align="center" bgcolor="#f1f1f1">地址</th>
            <th align="center" bgcolor="#f1f1f1">站点</th>
          </tr>
         
        </thead>
      </table>
    </div>
    <div title="命中关键字未匹配站点" style="padding:10px">
      <h2 align="right">关键字未匹配站点共<span class="unsum">0条</span></h2>
      <table id="keytable" width="100%" border="0" cellpadding="8" cellspacing="1" bgcolor="#CCCCCC">
        <thead>
          <tr>
            <th align="center" bgcolor="#f1f1f1">序号</th>
            <th align="center" bgcolor="#f1f1f1">关键字</th>
            <th align="center" bgcolor="#f1f1f1">站点</th>
          </tr>
         
        </thead>
      </table>
    </div>
  </div>
</body>
</html>
