<%@ page language="java" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
<head>
<title>配送站点关联维护</title>
<script type="text/javascript">
var cxt='<%=request.getContextPath()%>';
</script>
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/easyui/themes/default/easyui.css" />
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/easyui/themes/icon.css" />
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-1.8.0.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/easyui/jquery.easyui.min.js"></script>
<%-- <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/zTree/demo.css"/> --%>
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/zTree/zTreeStyle/zTreeStyle.css"/>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/zTree/js/jquery.ztree.all-3.5.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/zTree/js/jquery.ztree.exhide-3.5.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/crudutil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/address/getZAddress.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/address/mutitleTree.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/ajaxfileupload.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/address/stationAdressRelation.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/address/exportAddress.js"></script>
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
			 //折叠
			 $("#collapseAllBtn").bind("click", {type:"collapseAll"}, expandNode);
			//刷新
			 $("#refreshAllBtn").click(function(){
	        	getAll();
	        });
			 //未绑定
		        $("#unbindAllBtn").click(function(){
		       		unbind();
		       })
		       
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
			 		url:cxt+"/address/getAddressTree",
			 		data:{isBind:true},
			 		success:function(optionData){
			 	        var t = $("#tree");
			 	        zTree = $.fn.zTree.init(t, setting, optionData);
			 			
			 		}
			 	});
			 }
		 
		
</script>

</head>
<body>
<div class="easyui-layout" style="height:600px;">
  <div data-options="region:'north',split:true" title="条件搜索" style="width:330px;height: 600px;">
    <form action="" method="get">
      <table width="100%" border="0" cellspacing="0" cellpadding="10">
       <tr>
          <td><a href="javascript:void(0)" id="upup" class="easyui-linkbutton">拆合站</a>&nbsp;
          &nbsp;<a href="javascript:" class="easyui-linkbutton" id="exportAddress">导出关键字</a>
<a href="javascript:" class="easyui-linkbutton" id="importAddress">导入关键字</a></td>
        </tr>
        <tr>
          <td><input style="width:150px" id="searchA">
            <a href="javascript:void(0)" class="easyui-linkbutton" iconCls="icon-search" plain="false"  onclick="searchVal('searchA','tree')">查询</a></td>
        </tr>
        <tr>
          <td><a href="javascript:void(0)" id="collapseAllBtn" class="easyui-linkbutton">全部折叠</a>&nbsp;
          <a href="javascript:void(0)" id="refreshAllBtn" class="easyui-linkbutton">刷新节点</a>&nbsp;
          <a href="javascript:void(0)" id="unbindAllBtn" class="easyui-linkbutton">未绑定</a></td>
        </tr>
        <tr>
          <td><ul id="tree" class="ztree " style="width:auto;height:auto; overflow:auto;"></ul></td>
        </tr>
      </table>
    </form>
    
    <div id="dlgStation" class="easyui-dialog" title="请选择需要导出关键字的站点" style="width:500px;height:320px;padding:10px;">
	 <div id="stationShow" style="overflow:auto;height:200px;padding:10px;"></div>
	 <div style="margin:auto;text-align:center;"><a href="javascript:$.messager.alert('提示', '请选择站点！')"  id="startExport">导出</a></div>
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
   <form method="post" >  
          <table width="100%" height="550px;" border="0" cellspacing="1" cellpadding="5" style="background:#CCC">
  <tr height="20px">
    <td width="45%"   bgcolor="#FFFFFF">原站点：
      <select id="sourceStation" name="station4combobox" style="width:120px;">
      </select></td>
    <td width="50" rowspan="3" align="center" bgcolor="#FFFFFF">
      <p><a href="javascript:void(0)" class="easyui-linkbutton" id="toRight">&gt;</a></p>
      <p><a href="javascript:void(0)" class="easyui-linkbutton" id="toLeft">&lt;</a></p></td>
    <td width="45%" bgcolor="#FFFFFF">原站点：
      <select id="targetStation" name="state" style="width:120px;">
      </select></td>
    </tr>
  <tr >
    <td bgcolor="#FFFFFF"><select class="easyui-combobox" name="state2" style="width:120px;">
      <option value="1">模糊搜索</option>
      </select>
      <input name="textfield5" id="sourceStrVal" type="text" id="textfield5" size="15" />
       <a href="javascript:void(0)" class="easyui-linkbutton" iconCls="icon-search" plain="false"  onclick="searchVal('sourceStrVal','sourceStationtree')">查询</a>
      </td>
    <td bgcolor="#FFFFFF"><select class="easyui-combobox" name="state2" style="width:120px;">
      <option value="1">模糊搜索</option>
      </select>
      <input name="textfield5" type="text" id="targetStrVal" size="15" />
       <a href="javascript:void(0)" class="easyui-linkbutton" iconCls="icon-search" plain="false"  onclick="searchVal('targetStrVal','targetStationtree')">查询</a>
      </td>
    </tr>
  <tr height="90%">
    <td bgcolor="#FFFFFF"><ul class="ztree" id="sourceStationtree" style="width:auto;height:auto; overflow:auto;">
              
            </ul></td>
    <td bgcolor="#FFFFFF"><ul class="ztree" id="targetStationtree" style="width:auto;height:auto; overflow:auto;">
      
    </ul></td>
    </tr>
    <tr height="20px"><td></td><td></td><td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
    		<a href="javascript:void(0)" id="saveRelation" class="easyui-linkbutton">保存</a>&nbsp;
          <a href="javascript:void(0)" id="cancel" class="easyui-linkbutton">取消</a>&nbsp;
    </tr>
</table>

        </form> 
  </div>
</div>
</body>
</html>
