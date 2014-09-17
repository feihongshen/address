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
<script type="text/javascript"
	src="<%=request.getContextPath()%>/js/lhgDialog/lhgdialog.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/address/getZAddress.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/address/deliveryStationRule.js"></script>
<script type="text/javascript">
var setting = {
		async: {
			enable: true,
			url: getUrl
		},
	data: {
		simpleData: {
			enable: true
		}
	},
	callback: {
		beforeClick: beforeClick,
		onClick: onClick,
		beforeExpand: beforeExpand,
		onAsyncSuccess: onAsyncSuccess,
		onAsyncError: onAsyncError
	}
};
var backNode;

		 $(document).ready(function(){
			 pureStation=$("#deliveryStationRule").clone(true);
			
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
	        //添加站点
	        $("#add").click(function(){
	           	var node=backNode.clone(true);
	           	$("#optionRule").before(node);
           });
			 //保存站点规则
	        $("#saveRule").click(function(){
				 if(!addressId){
					 alert("请选择地址");
					 return;
				 }
				 saveRule();
						
			});
	        //添加供货商时效
	        $("#addvendor").click(function(){
	        	var node=backvendors.clone(true);
	           	$("#optionvendor").before(node);
	        });
	       
	        
	        initOption();
	        $("#stationList").datagrid(detailRow);

    });
		 
		 
		 
		
</script>

</head>
<body>
<div class="easyui-layout" style="height:600px;">
  <div data-options="region:'west',split:true" title="条件搜索" style="width:330px;">
    <form action="" method="get">
      <table width="100%" border="0" cellspacing="0" cellpadding="10">
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
  </div>
  
  <div data-options="region:'center'">
    <form action="" method="get">
      <table width="100%" border="0" cellspacing="0" cellpadding="10" id="stationList">
        </table><div style="padding:10px"><table width="100%" border="0" cellpadding="8" cellspacing="1" bgcolor="#CCCCCC">
    <thead>
      <tr>
        <th align="center" bgcolor="#f1f1f1">配送站点</th>
        <th align="center" bgcolor="#f1f1f1">规则</th>
      </tr>
      
      <tr id="deliveryStationRule" class="deliveryStationRule">
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
      <tr id="vendors4combobox" class="vendors4combobox">
        <td align="center" bgcolor="#FFFFFF"><select class="vendorsId"  name="vendorsId" id="vendorsId" style="width:200px;">
          
        </select></td>
        <td align="center" bgcolor="#FFFFFF">
          <input type="text" name="textfield" id="textfield" />
        时</td>
      </tr>
      <tr id="optionvendor">
        <td colspan="2" bgcolor="#FFFFFF"><a href="javascript:void(0)" id="savevendor" class="easyui-linkbutton">保存</a>&nbsp;&nbsp;
        <a href="javascript:void(0)" class="easyui-linkbutton" id="addvendor">新增</a></td>
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
