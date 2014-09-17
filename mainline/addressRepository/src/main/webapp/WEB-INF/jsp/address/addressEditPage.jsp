<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>地址库维护</title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/easyui/themes/default/easyui.css" />
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/easyui/themes/icon.css" />
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-1.8.0.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/easyui/jquery.easyui.min.js"></script>
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/zTree/zTreeStyle/zTreeStyle.css"/>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/zTree/js/jquery.ztree.all-3.5.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/address/getZAddress.js"></script>
<script type="text/javascript">
		 $(document).ready(function(){
			getAll();
			$("#collapseAllBtn").bind("click", {type:"collapseAll"}, expandNode);
	        $("#refreshAllBtn").click(function(){
	        	getAll();
	        });
	        $("#unbindAllBtn").click(function(){
	        	if($("#unbindAllBtn").attr("isBand")=='0'){
	        		$("#unbindAllBtn").attr("isBand","1");
	        		$("#unbindAllBtn").find("span>span").text("已绑定");
	        	}else{
	        		$("#unbindAllBtn").attr("isBand","0");
	        		$("#unbindAllBtn").find("span>span").text("绑定");
	        	}
	        	getAll();
	        });
	        $('#stationId').combobox('disable');
    	 });
		 var mySettings = {
				 edit: {
						enable: true,
						showRenameBtn:false
					},
					data: {
						simpleData: {
							enable: true
						}
					},
					callback: {
						beforeClick: myBeforeClick,
						onClick: myClick,
						beforeRemove: zTreeBeforeRemove,
 						onRemove: zTreeOnRemove
					},
					check: {
						enable: true
					}
				};
		 function zTreeBeforeRemove(treeId, treeNode) {
			 if(confirm( "确认删除该节点以及以下数据？" )){
				 var flag = true;
				 $.ajax({
					 	type: "POST",
						url:"<%=request.getContextPath()%>/address/delAddress",
						data:{addressId:treeNode.id},
						async:false,
						success : function(resp) {
							flag = resp.success;
							if(!flag){
								alert("地址删除失败，请联系管理员！");
							}
							
						}
					});
				 return flag;
			 }
			 return  false;
		 }
		 function zTreeOnRemove(event, treeId, treeNode) {
			
		 } 
		 
		 function myBeforeClick(treeId, treeNode, clickFlag) {
				className = (className === "dark" ? "":"dark");
				return (treeNode.click != false);
			}
		 $("a[aid]").live("click",function(){
			 var obj = $(this);
			 var id = obj.attr("aid");
			 $.ajax({
				 	type: "POST",
					url:"<%=request.getContextPath()%>/address/delAlias",
					data:{id:id},
					async:false,
					success : function(resp) {
						if(resp.success){
							obj.parent().remove();
						} 
					}
				});
		 });
		 
			function myClick(event, treeId, treeNode, clickFlag) {
				$("#tips").html(treeNode.name);
				$("#aliasTips").html(treeNode.name);
				$("#addressId").val(treeNode.id);
				$("#parentId").val(treeNode.id);
				$("#level").val(treeNode.level);
				 $("#aliasUl").html("");
				 $.ajax({
					 	type: "POST",
						url:"<%=request.getContextPath()%>/address/getAlias",
						data:{addressId:treeNode.id},
						async:false,
						success : function(resp) {
							if(resp.length>0){
								for(var i = 0 ;i<resp.length;i++){
									$("<li>"+resp[i].name+"<a href='javascript:void(0)' aid='"+resp[i].id+"'>删除</a></li>" ).appendTo($("#aliasUl"));
								}
							} 
						}
					});
				
				
				
				if(treeNode.level<4){
					$('#stationId').val("");
					$("input[name='stationId']").val("");
					$('#stationId').combobox('disable');
				}else{
					$('#stationId').combobox('enable');
				}
				
			}	
	function getAll(){
		var isBand = $("#unbindAllBtn").attr("isBand");
		 $.ajax({
			 type: "POST",
				url:"<%=request.getContextPath()%>/address/getZTree",
				data:{band:isBand},
				success : function(optionData) {
					$.fn.zTree.init($("#tree"), mySettings, optionData);
				}
			});
	}
	function getTree(isBand){
		 $.ajax({
			 type: "POST",
				url:"<%=request.getContextPath()%>/address/getZTree",
				data:{band:isBand},
				success : function(optionData) {
					$.fn.zTree.init($("#tree"), mySettings, optionData);
				}
			});
	}
	
	function clearForm(){
		$("#ff")[0].reset();
	}
	function submitForm(){
		var addresses = $("#addresses").val();
		var parentId = $("#parentId").val();
		var stationId = $("input[name='stationId']").val();
		if($("#level").val()>5){
			alert("最多支持第六级关键字！");
			return false;
		}
		$.ajax({
			 	type: "POST",
				url:"<%=request.getContextPath()%>/address/add",
				data:{stationId:stationId,addresses:addresses,parentId:parentId},
				async:false,
				success : function(resp) {
					if(resp.success){
						 clearForm();
						 getAll();
					}else{
						alert(resp.msg);
					}
				}
			});
	}
	function addAlias(){
		var alias = $("#alias").val();
		var addressId = $("#addressId").val();
		if(addressId==""){
			alert("请选择一个地址！");
			return false;
		}
		if(alias==""){
			alert("请输入别名！");
			return false;
		}
		if(alias.trim()==$("#aliasTips").text()){
			alert("别名和原名不能一致！");
			return false;
		}
		 $.ajax({
			 	type: "POST",
				url:"<%=request.getContextPath()%>/address/addAlias",
				data:{addressId:addressId,alias:alias},
				success : function(resp) {
					if(resp.success){
						 $("#alias").val("");
						 $("#aliasUl").append("<li>"+alias+"<a href='javascript:void(0)' aid='"+resp.obj.id+"'>删除</a></li>");
					}else{
						alert(resp.msg);
					}
				}
			});
	}
</script>
<style type="text/css">
.alias>li{
	float:left;
	margin-left:10px;
}
.alias {
	list-style:none;
	padding:2px;
}

</style>
</head>
<body> 
<div class="easyui-layout" style="height:560px;">
	 <div data-options="region:'west',split:true" title="条件搜索" style="width:350px;">
 <form action="" method="get">
      <table width="100%" border="0" cellspacing="0" cellpadding="10">
        <tr>
          <td align="center"><input style="width:180px" id="searchA" >
            <a href="javascript:void(0)" class="easyui-linkbutton" iconCls="icon-search" plain="false" id="searchA" onclick="searchTree()">查询</a></td>
        </tr>
        <tr>
          <td  align="center"><a href="javascript:void(0)" id="collapseAllBtn" class="easyui-linkbutton">全部折叠</a>&nbsp;
          <a href="javascript:void(0)" id="refreshAllBtn" class="easyui-linkbutton">刷新</a>&nbsp;
          <a href="javascript:void(0)" id="unbindAllBtn" class="easyui-linkbutton" isBand="0" >未绑定</a></td>
        </tr>
        <tr>
          <td><ul id="tree" class="ztree" style="overflow:auto;"></ul></td>
        </tr>
      </table>
    </form>
      </div>
	<div data-options="region:'center'" style="border:0px;">
	 <div class="easyui-panel" title="添加关键词" style="padding:10px;margin-bottom:10px;height:350px;width:auto">
	    <form id="ff" method="post">
	    	<table cellpadding="5" style="width: 100%">
	    		<tr>
	    			<td width="60px">站点:</td>
	    			<td>
	    			<input type="hidden" id="parentId" name="parentId" value=""/>
	    			<input type="hidden" id="level" name="level" value=""/>
	    			<input class="easyui-combobox" id="stationId" name="stationId"
					data-options="url:'<%=request.getContextPath()%>/station/listAll',
					method:'get',
					valueField:'id',
					textField:'name',
					panelHeight:'auto'">&nbsp;&nbsp;上级关键词：<span id="tips" style="font-weight:bold;"></span>
	    			</td>
	    		</tr>
	    		<tr>
	    			<td width="60px">关键词:</td>
	    			<td>
	    			   <textarea rows="13" cols="60" id="addresses" name="addresses" class="easyui-validatebox textbox" ></textarea>
	    			</td>
	    		</tr>
	    		<tr><td width="60px"></td><td>	<a href="javascript:void(0)" class="easyui-linkbutton" onclick="submitForm()">提交</a>
	    	      <a href="javascript:void(0)" class="easyui-linkbutton" onclick="clearForm()">清空</a></td></tr>
	    	</table>
	    </form>
	 </div>
	 <div class="easyui-panel" title="别名管理" style="padding:10px;">
	 		<div><label>已选站点：</label><span id="aliasTips" style="font-weight:bold;"></span></div>
		     <input type="hidden" id="addressId" name="addressId" value=""/>
		     <label>别名列表：</label><ul class="alias" id="aliasUl"></ul>
		     <input type="text"  name = "alias" id="alias" /><a href="javascript:addAlias();" id="addAlias" class="easyui-linkbutton">添加别名</a>
	 </div>
	</div>
</div>
</body>
</html>
