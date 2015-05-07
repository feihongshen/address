<%@ page language="java" pageEncoding="UTF-8"%>
<%@ page import="java.util.*"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>关键词匹配</title>
<%@include file="/WEB-INF/jsp/common/lib.jsp"%>
<style type="text/css">
.keyword {
	overflow-y: scroll;
	height: 600px;
	width: 24%;
	float: left;
	color: blue;
	font-size: 12px;
	font-family: Verdana, Arial, Helvetica, AppleGothic, sans-serif;
	border: 1px solid #999;
	padding: 3px;
}
</style>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/address/mutitleTree.js"></script>
<script type="text/javascript" src="http://api.map.baidu.com/api?v=2.0&ak=szTBW9236HO8EDCYuk4xQlP4"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/map/map.js"></script>
<script type="text/javascript">
var setting = {
		view: {
			fontCss: setFontCss
		},
        edit: {
            enable: false,
            showRemoveBtn: false,
            showRenameBtn: false
        },
        check: {
			enable: false
		},
        data: {
            simpleData: {
                enable: true
            }
        },
        callback: {
        }
    };
var zNodes;
var keywords;
var keywordsTable;
$(function(){
	var needMatchedWords= $("#needMatched").val();
	$("#mapping").click(function(){
		needMatchedWords= $("#needMatched").val();
		if(needMatchedWords==undefined||needMatchedWords==null||needMatchedWords.length==0){
			alert("请输入要匹配的关键词");
			return;
		}
		$.ajax({
		 type: "POST",
			url:"<%=request.getContextPath()%>/address/matchKeyword",
			data:{needMatched:needMatchedWords},
			success:function(optionData){
				zNodes=optionData['zTreeNodeList'];
				keywords=optionData['keywordList'];
				setKeywords();
				if(zNodes==undefined||zNodes==null||zNodes.length==0){
					$("#resultTree").html("");
					return;
				}
				zTreeObj1 = $.fn.zTree.init($("#resultTree"), setting, zNodes);
			}
		});
		initMap(needMatchedWords);
	});
	
	
	initMap(needMatchedWords);
});

function setFontCss(treeId, treeNode) {
	var color;
	var treeNodeName = treeNode.name.split(" -- ");
	for (var i=0;i<keywords.length;i++){
		if(keywords[i]==treeNodeName[0]){
			color = { color: "blue" };
		}
	}
	return color;
};

function setKeywords(){
	if(keywords==undefined||keywords==null||keywords.length==0){
		$("#keywordDiv").html("");
		return;
	}
	keywordsTable="<table>";
	for (var i=0;i<keywords.length;i++){
		keywordsTable=keywordsTable+"<tr><td>"+keywords[i]+"</td></tr>";
	}
	keywordsTable=keywordsTable+"</table>";
	$("#keywordDiv").html(keywordsTable);
}


function initMap(needMatchedWords)
{
	var mapManager=new AR.ExpdopMap();
    mapManager.initializeMap({map:"addressmap"});
    mapManager.initializeDeliveryStation();
    var deliverySta=mapManager.getDeliveryStation();
    
	$.ajax({
		 type: "POST",
			url:"<%=request.getContextPath()%>/station/listAll",
			data:{},
			success:function(data){
   				 // 站点数据
   				 deliverySta.setDeliveryStationItems(data);
			}
		});
	if(needMatchedWords==undefined||needMatchedWords==null||needMatchedWords.length==0){
		return;
	}
	$.ajax({
		 type: "POST",
			url:"<%=request.getContextPath()%>/address/getPointByAddress",
			data : {
				needMatched : needMatchedWords
			},
			success : function(returnData) {
				// 地址点 
				var pointLabelArray = new Array(returnData.length);
				for (var i = 0; i < returnData.length; i++) {
					var pointLabel = new Object();
					pointLabel.point = new BMap.Point(returnData[i].lng,
							returnData[i].lat);
					pointLabel.label = returnData[i].addressLine;
					pointLabelArray[i] = pointLabel;
				}
				deliverySta.addAddressMarker(pointLabelArray);
			}
		});

	}
</script>
</head>
<body>
	<div>
		<textarea name="needMatched" id="needMatched" class="textbox"
			style="height: 40px; width: 47%; resize: none;" data-options="multiline:false"></textarea>
		<a href="javascript:void(0)" class="easyui-linkbutton" id="mapping" iconCls="icon-ok">匹配</a>
	</div>

	<div id="keywordDiv" class="keyword"></div>

	<div style="overflow-y: scroll; width: 24%; float: left">
		<td bgcolor="#FFFFFF" style="vertical-align: top;">
			<div style="overflow-y: scroll; height: 600px; border: 1px solid #999; padding: 3px;">
				<ul class="ztree" id="resultTree">
				</ul>
			</div>
		</td>
	</div>
	<div id=addressmap style="width: 50%; height: 600px; float: right;"></div>

</body>
</html>