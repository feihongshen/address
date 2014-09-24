<%@ page language="java" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>配送站点关联维护</title>
<%@include file="/WEB-INF/jsp/common/lib.jsp"%>
<script type="text/javascript" src="${ctx}/js/address/deliveryStationPage.js"></script>
<style type="text/css">
.table {
    border-collapse: collapse;
    border-left: 1px solid #cccccc;
    border-top: 1px solid #cccccc;
    color: #333333;
    font-family: Verdana,Arial,Helvetica,sans-serif;
    width: 100%;
    font-size:12px;
}
.table > tbody {
    display: table-row-group;
    vertical-align: middle;
}
 .table > tbody > tr td, table th {
    border-bottom: 1px solid #cccccc;
    border-right: 1px solid #cccccc;
    line-height: 1.4em;
    padding: 7px 8px;
    vertical-align: middle;
    font-size:12px;
}
.table{
	width:auto;height:auto;
}
</style>
</head>
<body> 
<div class="easyui-layout" style="height:500px;">
	 <div data-options="region:'west',split:true" title="条件搜索"  style="width:450px;">
      <table width="100%" border="0" cellspacing="0" cellpadding="10">
        <tr>
          <td align="left">
          	<input style="width:180px" id="searchA" />
            <a href="javascript:void(0)" class="easyui-linkbutton" iconCls="icon-search" plain="false"   onclick="searchVal('searchA','tree')">查询</a>
            </td>
        </tr>
        <tr>
          <td  align="left">
          <a href="javascript:void(0)" id="collapseAllBtn" class="easyui-linkbutton">全部折叠</a>&nbsp;
          <a href="javascript:void(0)" id="refreshAllBtn" class="easyui-linkbutton">刷新节点</a>
          </td>
        </tr>
        <tr>
          <td><div id="promtInfo" ></div><ul id="tree" class="ztree" style="width:auto;height:auto; overflow:auto;"></ul></td>
        </tr>
      </table>
      </div>
	<div data-options="region:'center'" style="border:0px;">
	 <div class="easyui-panel" title="关联站点-" id="relStation" style="padding-left:-1px;margin-bottom:10px;min-height:180px;width:auto">
	    	<input type="hidden" id="addressId" />
	    	<table id="stationRule" class="table table-bordered" >
		    	<thead> 
					<tr> 
						<th width="200px">配送站点</th> 
						<th width="320px">匹配规则</th> 
						<th width="60px">操作</th> 
					</tr> 
				</thead> 
				<tbody>
					<tr id="stationRuleOpe"> 
						<td colspan="3" align="left"><a href="javascript:void(0)" class="easyui-linkbutton" onclick="submitRules()">保存</a>
						<a href="javascript:void(0)" class="easyui-linkbutton" onclick="addRule()"  id="addRule">新增</a></td> 
					</tr> 
				</tbody>
	    	</table>
	 </div>
	   <div class="easyui-panel" title="设置时效-" id="relCust" style="padding:-1px;margin-bottom:10px;min-height:180px;width:auto">
	    	<table class="table table-bordered" id="vendorAge" style="width:auto;height:auto;" >
		    	<thead> 
					<tr> 
						<th width="200px">供货商</th> 
						<th width="320px">实效</th> 
						<th width="60px">操作</th> 
					</tr> 
				</thead> 
				<tbody>
					<tr id="ageOpe" > 
						<td colspan="3" align="left"><a href="javascript:void(0)" class="easyui-linkbutton" onclick="submitVendorAge()">保存</a>
						<a href="javascript:void(0)" class="easyui-linkbutton" onclick="addAge()" id="addAge">新增</a></td> 
					</tr> 
				</tbody>
	    	</table>
	 </div>
	</div>
</div>
</body>
</html>
