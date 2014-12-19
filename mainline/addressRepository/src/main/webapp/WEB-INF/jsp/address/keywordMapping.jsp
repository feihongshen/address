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

</style>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/address/mutitleTree.js"></script>
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
            //onClick : menuOnClick
        }
    };
var zNodes;
var keywords;
var keywordsTable;
$(function(){
	$("#mapping").click(function(){
		$.ajax({
		 type: "POST",
			url:"<%=request.getContextPath()%>/address/matchKeyword",
			data:{needMatched:$("#needMatched").val()},
			success:function(optionData){
				zNodes=optionData['zTreeNodeList'];
				keywords=optionData['keywordList'];
				setKeywords(keywords);
				
				zTreeObj1 = $.fn.zTree.init($("#resultTree"), setting, zNodes);
			}
		});
	});
})

function setFontCss(treeId, treeNode) {
	var color;
	for (var i=0;i<keywords.length;i++){
		if(keywords[i]==treeNode.name){
			color = { color: "blue" };
		}
	}
	return color;
};

function setKeywords(keywords){
	keywordsTable="<table>";
	for (var i=0;i<keywords.length;i++){
		keywordsTable=keywordsTable+"<tr><td>"+keywords[i]+"</td></tr>";
	}
	keywordsTable=keywordsTable+"</table>";
	$("#keywordDiv").html(keywordsTable);
}

</script>
</head>
<body>

<textarea name="needMatched" id="needMatched" class="textbox" style="height:50px; width:50%" data-options="multiline:true"></textarea>
<a href="javascript:void(0)" class="easyui-linkbutton" id="mapping" iconCls="icon-ok" >匹配</a>

<div id="keywordDiv" style="width:50%;float:left;color: blue"> 
</div>
  <div style="overflow-y: scroll;width:50%;float:right">
    <td bgcolor="#FFFFFF" style="vertical-align: top;">
    <div style="overflow-y: scroll;height: 444px">
         <ul  class="ztree" id="resultTree" > </ul>
    </div>
    </td>
 </div>
</body>
</html>