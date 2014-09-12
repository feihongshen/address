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
<script type="text/javascript" src="${pageContext.request.contextPath}/js/address/getZAddress.js"></script>
<script type="text/javascript">
var backNode;

		 $(document).ready(function(){
			 
			 $("#saveRule").click(function(){
				 if(!addressId){
					 alert("请选择地址");
					 return;
				 }
				 saveRule();
						
			});
			 getAll();
			 $("#collapseAllBtn").bind("click", {type:"collapseAll"}, expandNode);
	        $("#refreshAllBtn").click(function(){
	        	getAll();
	        });
	        $("#add").click(function(){
	           	var node=backNode.clone(true);
	           	$("#optionRule").before(node);
           });
	       // $("#unbindAllBtn").clcik(function(){
	        //	alert(1);
	        //})
	        
	        initOption();
			

    });
		 
		 
		 
		
</script>

</head>
<body>
<div class="easyui-layout" style="height:600px;">
  <div data-options="region:'west',split:true" title="条件搜索" style="width:300px;">
    <form action="" method="get">
      <table width="100%" border="0" cellspacing="0" cellpadding="10">
        <tr>
          <td><input style="width:150px" id="searchA">
            <a href="javascript:void(0)" class="easyui-linkbutton" iconCls="icon-search" plain="false"  onclick="searchTree()">查询</a></td>
        </tr>
        <tr>
          <td><a href="javascript:void(0)" id="collapseAllBtn" class="easyui-linkbutton">全部折叠</a>&nbsp;
          <a href="javascript:void(0)" id="refreshAllBtn" class="easyui-linkbutton">刷新节点</a>&nbsp;
          <a href="javascript:void(0)" id="unbindAllBtn" class="easyui-linkbutton">未绑定</a></td>
        </tr>
        <tr>
          <td><ul id="tree" class="ztree treeClass" style="width:auto;height:auto; overflow:auto;"></ul></td>
        </tr>
      </table>
    </form>
  </div>
  
  <div data-options="region:'center',title:'添加关键字'">
    <form action="" method="get">
      <table width="100%" border="0" cellspacing="0" cellpadding="10">
        <tr>
          <td><h2>关联站点：
            </h2>
            <p>大望路站</p>
          <p>清河站</p></td>
        </tr>
        <tr>
          <td><div style="padding:10px"><table width="100%" border="0" cellpadding="8" cellspacing="1" bgcolor="#CCCCCC">
    <thead>
      <tr>
        <th align="center" bgcolor="#f1f1f1">配送站点</th>
        <th align="center" bgcolor="#f1f1f1">规则</th>
      </tr>
      
      <tr id="deliveryStationRule">
        <td align="center" bgcolor="#FFFFFF"><select class="deliveryStationId" id="deliveryStationId" name="state" style="width:200px;">
         
        </select></td>
        <td align="center" bgcolor="#FFFFFF">
          <input type="text" name="rule" class="rule" id="rule" />
        </td>
      </tr>
      
      <tr id="optionRule">
        <td colspan="2" bgcolor="#FFFFFF"><a href="javascript:void(0)" id="saveRule" class="easyui-linkbutton">保存</a>
        &nbsp;&nbsp;<a href="javascript:void(0)" id="add" class="easyui-linkbutton">新增</a></td>
        </tr>
      </thead>
  </table>
          </div></td>
        </tr>
        <tr>
          <td><div style="padding:10px"><table width="100%" border="0" cellpadding="8" cellspacing="1" bgcolor="#CCCCCC">
    <thead>
      <tr>
        <th align="center" bgcolor="#f1f1f1">供货商</th>
        <th align="center" bgcolor="#f1f1f1">时效</th>
      </tr>
      <tr>
        <td align="center" bgcolor="#FFFFFF"><select class="easyui-combobox" name="state" style="width:200px;">
          <option value="1">1</option>
          <option value="2">23</option>
          <option value="3">123</option>
        </select></td>
        <td align="center" bgcolor="#FFFFFF">
          <input type="text" name="textfield" id="textfield" />
        时</td>
      </tr>
      <tr>
        <td colspan="2" bgcolor="#FFFFFF"><a href="javascript:void(0)" class="easyui-linkbutton">保存</a>&nbsp;&nbsp;<a href="javascript:void(0)" class="easyui-linkbutton">新增</a></td>
        </tr>
      </thead>
  </table>
          </div></td>
        </tr>
      </table>
</form>
  </div>
</div>
</body>
</html>
