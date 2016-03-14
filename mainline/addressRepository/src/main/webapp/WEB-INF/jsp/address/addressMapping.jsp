<%@ page language="java" pageEncoding="UTF-8"%>
<%@ page import="java.util.*,cn.explink.gis.*"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
<head>
<title>地址库匹配</title>
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/easyui/themes/default/easyui.css" />
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/easyui/themes/icon.css" />
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-1.8.0.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/easyui/jquery.easyui.min.js"></script>
<script type="text/javascript" src="http://api.map.baidu.com/api?v=2.0&ak=<%=new BaiduApiKeyPool().getRandomKey() %>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/map/map.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/address/mapMatchResult.js"></script>
<script type="text/javascript">
var ctx = '<%=request.getContextPath() %>';
var arrsum=['insum','unsum','susum','dsum','ksum','pper'];
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
				var unsumCount=data['unsum']*1+data['ksum']*1;
				$("#unsumCount").text(unsumCount);
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
			case '命中关键词未匹配站点':
				tableData=usedData['kList'];
				usedTable=$("#keytable");
				break;
			case '地图匹配情况':
			    var mapAddressList=	usedData['mapAddressList'];
			    var needMatchedWords="";
			    for(var i=0;i<mapAddressList.length;i++){
			    	needMatchedWords=needMatchedWords+mapAddressList[i]+"\n";
			    }
			    initMap("mapMatchedResult",needMatchedWords);
			default:
				break;
			}
			$(".tr"+title).remove();
			if(usedTable){
				for(var i=0;i<tableData.length;i++){
					var tr="<tr class='tr"+title+"'> <td align='center' bgcolor='#FFFFFF'>"+(1+i)+"</td> <td align='left' bgcolor='#FFFFFF'>"+tableData[i]['key']+"</td> <td align='center' bgcolor='#FFFFFF'>"+tableData[i]['val']+"</td>"
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
          <td>本次匹配地址共<span class="insum">0条</span>,未匹配<span id="unsumCount">0</span>,成功匹配<span class="susum">0</span>,匹配多站<span class="dsum">0</span>,匹配率:<span class="pper">0</span>%</td>
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
      <h2 align="right">匹配成功共<span class="susum">0</span>条</h2>
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
      <h2 align="right">匹配多站共<span class="dsum">0</span>条</h2>
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
      <h2 align="right">关键字未匹配站点共<span class="ksum">0</span>条</h2>
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
    <div title="地图匹配情况" style="padding:10px">
	    <div id="mapMatchedResult" style="width: 100%; height: 600px; float: right;"></div>
    </div>
  </div>
</body>
</html>
