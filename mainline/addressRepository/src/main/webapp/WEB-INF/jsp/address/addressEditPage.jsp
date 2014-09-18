<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>地址库维护</title>
<script type="text/javascript">
var cxt='<%=request.getContextPath()%>';
</script>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/easyui/themes/default/easyui.css" />
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/easyui/themes/icon.css" />
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-1.8.0.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/easyui/jquery.easyui.min.js"></script>
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/zTree/zTreeStyle/zTreeStyle.css"/>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/zTree/js/jquery.ztree.all-3.5.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/zTree/js/jquery.ztree.exhide-3.5.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/crudutil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/address/getZAddress.js"></script>
<script type="text/javascript">
	var inital=false;
	var mySettings = {
		async : {
			enable : true,
			url : getStationUrl
		},
		edit : {
			enable : true,
			showRenameBtn : false
		},
		data : {
			simpleData : {
				enable : true
			}
		},
		callback : {
			beforeClick : myBeforeClick,
			onClick : myClick,
			beforeRemove : zTreeBeforeRemove,
			onRemove : zTreeOnRemove
		}
	};
	$(document).ready(function() {
		 getAll();
		 //折叠
		 $("#collapseAllBtn").bind("click", {type:"collapseAll"}, expandNode);
		//刷新
		 $("#refreshAllBtn").click(function(){
       	getAll();
       });
		 //未绑定
	        $("#unbindAllBtn").bind("click",function(){
	        	unbind();
	        }); 
		$('#stationId').combobox('disable');
	});
	function zTreeBeforeRemove(treeId, treeNode) {
		if (confirm("确认删除该节点以及以下数据？")) {
			var flag = true;
			$.ajax({
				type : "POST",
				url : "<%=request.getContextPath()%>/address/delAddress",
						data:{addressId:treeNode.id},
						async:false,
						success : function(resp) {
							flag = resp.success;
							if(!flag){
								$.messager.alert("提示","地址删除失败，请联系管理员！");
							}
							
						}
					});
				 return flag;
			 }
			 return  false;
		 }
		 function zTreeOnRemove(event, treeId, treeNode) {
			   $("#tips").html("");
				$('#panelAlias').panel('setTitle','别名管理') ;
				$("#addressId").val("");
				$("#aliasTips").val("");
				$("#parentId").val("");
				 $("#aliasUl").html("");
		 } 
		 
		 function myBeforeClick(treeId, treeNode, clickFlag) {
				className = (className === "dark" ? "":"dark");
				return (treeNode.click != false);
			}
		 $("a[aid]").live("click",function(){
			 var obj = $(this);
			 var id = obj.attr("aid");
			 $.messager.confirm('确认删除','您确认想要删除【'+obj.text()+'】别名吗？',function(r){    
				    if (r){    
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
				    }    
				});  
		 });
		 
			function myClick(event, treeId, treeNode, clickFlag) {
				$("#tips").html(treeNode.name);
				$('#panelAlias').panel('setTitle','别名管理-'+treeNode.name) ;
				$("#addressId").val(treeNode.id);
				$("#aliasTips").val(treeNode.name);
				$("#parentId").val(treeNode.id);
				$("#level").val(treeNode.level);
				 $("#aliasUl").html("");
				 addressId=treeNode.id;
				 $.ajax({
					 	type: "POST",
						url:"<%=request.getContextPath()%>/address/getAlias",
						data:{addressId:treeNode.id},
						async:false,
						success : function(resp) {
							if(resp.length>0){
								for(var i = 0 ;i<resp.length;i++){
									var btn = $("<a href='javascript:void(0)' aid='"+resp[i].id+"'>"+resp[i].name+"</a></li>");
									var li = $("<li></li>").append(btn);
									li.appendTo($("#aliasUl"));
									btn.linkbutton({    
									    iconCls:'icon-remove',
									    iconAlign:'right'
									});  
								}
							} 
						}
					});
				if(treeNode.level<3){
					$('#stationId').val("");
					$("input[name='stationId']").val("");
					$('#stationId').combobox('disable');
				}else{
					$('#stationId').combobox('enable');
				}
				
			}	
			 function getAll(){
				 $.ajax({
				 	 type: "POST",
				 		url:cxt+"/address/getAddressTree",
				 		data:{isBind:true},
				 		success:function(optionData){
				 	        var t = $("#tree");
				 	        zTree = $.fn.zTree.init(t, mySettings, optionData);
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
			$.messager.alert("提示","最多支持第六级关键字！");
			return false;
		}
		if(parentId==""){
			$.messager.alert("提示","请选择上级地址！");
			return false;
		}
		if(addresses==""){
			$.messager.alert("提示","请选择关键词！");
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
						$.messager.alert("提示",resp.msg);
					}
				}
			});
	}
	function addAlias(){
		var alias = $("#alias").val();
		var addressId = $("#addressId").val();
		if(addressId==""){
			$.messager.alert("提示","请选择一个地址！");
			return false;
		}
		if(alias==""){
			$.messager.alert("提示","请输入别名！");
			return false;
		}
		if(alias.trim()==$("#aliasTips").val()){
			$.messager.alert("提示","别名和原名不能一致！");
			return false;
		}
		 $.ajax({
			 	type: "POST",
				url:"<%=request.getContextPath()%>/address/addAlias",
				data:{addressId:addressId,alias:alias},
				success : function(resp) {
					if(resp.success){
						 $("#alias").val("");
						 var btn = $("<a href='javascript:void(0)' aid='"+resp.obj.id+"'>"+resp.obj.name+"</a></li>");
							var li = $("<li></li>").append(btn);
							li.appendTo($("#aliasUl"));
							btn.linkbutton({    
							    iconCls:'icon-remove',
							    iconAlign:'right'
							});  
					}else{
						$.messager.alert("提示",resp.msg);
					}
				}
			});
	}
</script>
<style type="text/css">
.alias>li{
	float:left;
	margin:10px 5px 10px 0px;
}
.alias {
	list-style:none;
	padding:2px;
}

</style>
</head>
<body> 
<div class="easyui-layout" style="height:560px;">
	 <div data-options="region:'west',split:true" title="条件搜索" style="width:450px;">
      <table width="100%" border="0" cellspacing="0" cellpadding="10">
        <tr>
          <td align="left"><input style="width:180px" id="searchA" >
            <a href="javascript:void(0)" class="easyui-linkbutton" iconCls="icon-search" plain="false"   onclick="searchVal('searchA','tree')">查询</a></td>
        </tr>
        <tr>
          <td  align="left"><a href="javascript:void(0)" id="collapseAllBtn" class="easyui-linkbutton">全部折叠</a>&nbsp;
          <a href="javascript:void(0)" id="refreshAllBtn" class="easyui-linkbutton">刷新节点</a>&nbsp;
          <a href="javascript:void(0)" id="unbindAllBtn" class="easyui-linkbutton">未绑定</a></td>
        </tr>
        <tr>
          <td><ul id="tree" class="ztree" style="width:auto;height:auto; overflow:auto;"></ul></td>
        </tr>
      </table>
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
	 <div class="easyui-panel" title="别名管理" id="panelAlias" style="padding:10px;">
		     <input type="hidden" id="addressId" name="addressId" value=""/>
		      <input type="hidden" id="aliasTips"  value=""/>
		     
		    <div style="paddding:30px 0px;"><ul class="alias" id="aliasUl"></ul> 
		    </div>
		     <div style="margin:40px 10px;clear:both;">
		     <input type="text"  name = "alias" id="alias" /><a href="javascript:addAlias();" id="addAlias" class="easyui-linkbutton">添加别名</a>
			 </div>
	 </div>
	</div>
</div>
</body>
</html>
