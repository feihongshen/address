<%@ page language="java" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>地址库维护</title>
<%@include file="/WEB-INF/jsp/common/lib.jsp"%>
<script type="text/javascript" src="${ctx}/js/address/addressEditPage.js"></script>
<style type="text/css">
.alias>li{float:left;margin:10px 5px 10px 0px;}
.alias {list-style:none;padding:2px;}
 span.button.firstPage {float:right; margin-left:2px; margin-right: 0; background-position:-144px -16px; vertical-align:top; *vertical-align:middle}
 span.button.prevPage {float:right; margin-left:2px; margin-right: 0; background-position:-144px -48px; vertical-align:top; *vertical-align:middle}
 span.button.nextPage {float:right; margin-left:2px; margin-right: 0; background-position:-144px -64px; vertical-align:top; *vertical-align:middle}
 span.button.lastPage {float:right; margin-left:2px; margin-right: 0; background-position:-144px -32px; vertical-align:top; *vertical-align:middle}
</style>
</head>
<body> 
<div class="easyui-layout" style="height:540px;">
	 <div data-options="region:'west',split:true" title="条件搜索" style="width:450px;">
      <table width="100%" border="0" cellspacing="0" cellpadding="10">
        <tr>
          <td align="left">
          	<input style="width:180px" id="searchA" onkeydown="searchVal('searchA','tree');"/>
            </td>
            <td  align="left" width="160px;">
         	 <a href="javascript:void(0)" id="collapseAllBtn" class="easyui-linkbutton">折叠</a> 
          	 <a href="javascript:void(0)" id="refreshAllBtn" class="easyui-linkbutton">刷新</a>
          </td>
        </tr>
        <tr>
          <td colspan=2><div id="promtInfo" ></div><ul id="tree" class="ztree" style="width:auto;height:auto; overflow:auto;"></ul></td>
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
	    			<select id="stationId" name="stationId" >
	    			</select>
	    			</td>
	    		</tr>
	    		<tr><td colspan=2> 上级关键词：<span id="tips" style="font-weight:bold;"></span></td></tr>
	    		<tr>
	    			<td width="60px">关键词:</td>
	    			<td>
	    			   <textarea rows="13" cols="60" id="addresses" name="addresses" class="easyui-validatebox textbox" disabled="true"></textarea>
	    			</td>
	    		</tr>
	    		<tr ><td width="60px"></td><td >	<a href="javascript:void(0)" class="easyui-linkbutton" onclick="submitForm()">提交</a>
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
