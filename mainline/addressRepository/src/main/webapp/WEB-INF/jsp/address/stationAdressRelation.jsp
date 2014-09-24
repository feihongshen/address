<%@ page language="java" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
<head>
<title>配送站点关联维护</title>
<%@include file="/WEB-INF/jsp/common/lib.jsp"%>
<script type="text/javascript" src="${ctx}/js/address/mutitleTree.js"></script>
<script type="text/javascript" src="${ctx}/js/address/stationAdressRelation.js"></script>
<script type="text/javascript" src="${ctx}/js/address/exportAddress.js"></script>
<style type="text/css">
.ul{
	list-style:none;
	text-align:left;
}
.ul>li{
	width:120px;
	float:left;
	overflow:hidden;
	padding:0px;
}
</style>
<script type="text/javascript">
var backNode;
var inital=false;
var setting = {
		async: {
			enable: true,
			url: getStationUrl
		},
	data: {
		simpleData: {
			enable: true
		}
	},
	callback: {
		beforeClick: beforeClick,
		onAsyncSuccess: onAsyncSuccess,
		onAsyncError: onAsyncError,
		onClick: onClick
	}
};

		 $(document).ready(function(){
			
			 getAll();
			 getPromtInfo();
			 //折叠
			 $("#collapseAllBtn").bind("click", {type:"collapseAll"}, expandNode);
			//刷新
			 $("#refreshAllBtn").click(function(){
				 getAll();
				 var treeObj = $.fn.zTree.getZTreeObj("tree");
	 			 var node =  treeObj.getNodeByParam("id", 1, null);
				 treeObj.reAsyncChildNodes(node, null,null);
	        });
		       
	        $("#upup").click(function(){
	        	if(!inital){
	        		inital=true;
	        		initStations();
	        	}
	         	$('.easyui-layout').layout('collapse','north');	
	        });
	        
	        $("#saveRelation").click(saveRelation);
	        $("#cancel").click(function(){
	        	$('.easyui-layout').layout('expand','north');
	        })
	        
	        $("#sourceStation").change(function(){
	        	initDemoTree('sourceStation');
	        	
	        })
	        $("#targetStation").change(function(){
	        	initDemoTree('targetStation');
	        	
	        })

    });
		 
		 function getAll(){
			 $.ajax({
			 	 type: "POST",
			 		url:ctx+"/address/getAddressTree",
			 		data:{isBind:true},
			 		success:function(optionData){
			 	        var t = $("#tree");
			 	        zTree = $.fn.zTree.init(t, setting, optionData);
			 			
			 		}
			 	});
			 }
		 
		 function getPromtInfo(){
			 $.ajax({
			 	 type: "POST",
			 		url:ctx+"/address/getPromtInfo",
			 		data:{isBind:true},
			 		success:function(ajson){
			 			var temp=ajson.attributes;
			 			var keys=temp['keys'];
			 			var binds=temp['binds'];
			 			var unbids=keys*1-binds*1;
			 			var info="关键词共有"+keys+"个，已经绑定站点"+binds+"个，未绑定"+unbids+"个";
			 			$("#promtInfo").text(info);
			 			
			 		}
			 	});
		 }
		 
		
</script>

</head>
<body >
<div class="easyui-layout" style="height:600px;">
  <div data-options="region:'north',split:true" title="条件搜索" style="width:330px;height: 600px;">
      <table width="100%" border="0" cellspacing="0" cellpadding="10">
       <tr>
          <td><a href="javascript:void(0)" id="upup" class="easyui-linkbutton">拆合站</a>&nbsp;
          &nbsp;<a href="javascript:" class="easyui-linkbutton" id="exportAddress">导出关键字</a>
<a href="javascript:" class="easyui-linkbutton" id="importAddress">导入关键字</a></td>
        </tr>
        <tr>
          <td><input style="width:150px" id="searchA" onkeydown="searchVal('searchA','tree');" >
        </tr>
        <tr>
          <td><a href="javascript:void(0)" id="collapseAllBtn" class="easyui-linkbutton">全部折叠</a>&nbsp;
          <a href="javascript:void(0)" id="refreshAllBtn" class="easyui-linkbutton">刷新节点</a></td>
        </tr>
        <tr>
          <td><div id="promtInfo"></div>
          <ul id="tree" class="ztree " style="width:auto;height:auto; overflow:auto;"></ul></td>
        </tr>
      </table>
    
    <div id="dlgStation" class="easyui-dialog" title="请选择需要导出关键字的站点" style="width:500px;height:320px;">
	 <div id="stationShow" style="overflow:auto;height:240px;"></div>
	 <div style="margin:auto;text-align:center;">
	 <a href="javascript:$.messager.alert('提示', '请选择站点!')" class="easyui-linkbutton" id="startExport">导出</a>
	 </div>
</div>
<div id="dlgImport" class="easyui-dialog" title="导入关键字" style="width:500px;height:320px;padding:10px;">
 <table width="100%"  border="0" cellspacing="5" cellpadding="0">
        <tr>
          <td> <input type="file" id="file" name="file1" onchange="takefile('file');" >
              	选择导入文件：
            <input class="easyui-filebox" onclick="fileSelected('file');"  id="tools" data-options="prompt:'请选择'" style="width:200px">
            </td>
        </tr>
        <tr>
          <td><a href="javascript:void(0)" class="easyui-linkbutton" id="startKwImport">开始导入</a>&nbsp;</td>
        </tr>
      </table>
</div>
  </div>
  
  <div data-options="region:'center'">
          <table width="100%" height="550px;" border="0" cellspacing="1" cellpadding="5" style="background:#CCC">
  <tr height="20px">
    <td width="45%"   bgcolor="#FFFFFF">原站点：
      <select id="sourceStation" name="station4combobox" style="width:120px;">
      </select></td>
    <td width="50" rowspan="3" align="center" bgcolor="#FFFFFF">
      <p><a href="javascript:void(0)" class="easyui-linkbutton" id="toRight">&gt;</a></p>
      <p><a href="javascript:void(0)" class="easyui-linkbutton" id="toLeft">&lt;</a></p></td>
    <td width="45%" bgcolor="#FFFFFF">拆到目标站点：
      <select id="targetStation" name="state" style="width:120px;">
      </select></td>
    </tr>
  <tr >
    <td bgcolor="#FFFFFF">模糊搜索:
      <input name="textfield5" id="sourceStrVal" type="text" id="textfield5" size="15" onkeydown="searchVal('sourceStrVal','sourceStationtree')" />
      </td>
    <td bgcolor="#FFFFFF">模糊搜索:
      <input name="textfield5" type="text" id="targetStrVal" size="15" onkeydown="searchVal('targetStrVal','targetStationtree')" />
      </td>
    </tr>
  <tr height="90%">
    <td bgcolor="#FFFFFF" style=" vertical-align: top;"><ul class="ztree" id="sourceStationtree" style="width:auto;height:auto; overflow:auto;">
              
            </ul></td>
    <td bgcolor="#FFFFFF" style=" vertical-align: top;"><ul class="ztree" id="targetStationtree" style="width:auto;height:auto; overflow:auto;">
      
    </ul></td>
    </tr>
    <tr height="20px"><td></td><td></td><td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
    <div style="float: right">
    
    		<a href="javascript:void(0)" id="saveRelation" class="easyui-linkbutton">确定</a>&nbsp;
          <a href="javascript:void(0)" id="cancel" class="easyui-linkbutton">取消</a>&nbsp;
    </div>
    </tr>
</table>

  </div>
</div>
</body>
</html>
