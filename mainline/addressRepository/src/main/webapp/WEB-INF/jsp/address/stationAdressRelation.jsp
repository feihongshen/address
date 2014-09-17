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
<script type="text/javascript">
var backNode;
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
		onAsyncError: onAsyncError
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
          <td><input style="width:150px" id="searchA">
            <a href="javascript:void(0)" class="easyui-linkbutton" iconCls="icon-search" plain="false"  onclick="searchTree()">查询</a></td>
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
    sadfasdfa
    adfaf
  </div>
</div>
</body>
</html>
