﻿<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
<head>
<title>配送站点关联维护</title>
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/easyui/themes/default/easyui.css" />
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/easyui/themes/icon.css" />
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-1.8.0.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/easyui/jquery.easyui.min.js"></script>
</head>
<body>
<div class="easyui-layout" style="height:600px;">
  <div data-options="region:'west',split:true" title="条件搜索" style="width:300px;">
    <form action="" method="get">
      <table width="100%" border="0" cellspacing="0" cellpadding="10">
        <tr>
          <td><input style="width:150px">
            <a href="javascript:void(0)" class="easyui-linkbutton" iconCls="icon-search" plain="false" onclick="searchAdmin()">查询</a></td>
        </tr>
        <tr>
          <td><a href="javascript:void(0)" class="easyui-linkbutton">全部折叠</a>&nbsp;<a href="javascript:void(0)" class="easyui-linkbutton">刷新节点</a>&nbsp;<a href="javascript:void(0)" class="easyui-linkbutton">未绑定</a></td>
        </tr>
        <tr>
          <td><ul class="easyui-tree">
              <li><a href="javascript:void(0)" onclick="addTab('地址库匹配','dizhiku/dzkpp.html')">地址库匹配</a></li>
              <li> <span>数据维护</span>
                <ul>
                  <li><a href="javascript:void(0)" onclick="addTab('关键词导入','dizhiku/gjcdr.html')">关键词导入</a></li>
                  <li><a href="javascript:void(0)" onclick="addTab('地址库维护','dizhiku/dzkwh.html')">地址库维护</a></li>
                  <li><a href="javascript:void(0)" onclick="addTab('站点管理','dizhiku/zdgl.html')">站点管理</a></li>
                  <li><a href="javascript:void(0)" onclick="addTab('配送员管理','dizhiku/psygl.html')">配送员管理</a></li>
                </ul>
              </li>
              <li> <span>关联设置</span>
                <ul>
                  <li><a href="javascript:void(0)" onclick="addTab('拆合站维护','dizhiku/chzwh.html')">拆合站维护</a></li>
                  <li><a href="javascript:void(0)" onclick="addTab('配送站点关联维护','dizhiku/pszdglwh.html')">配送站点关联维护</a></li>
                  <li><a href="javascript:void(0)" onclick="addTab('配送员关联维护','dizhiku/psyglwh.html')">配送员关联维护</a></li>
                </ul>
              </li>
              <li> <span>数据统计</span>
                <ul>
                  <li><a href="javascript:void(0)" onclick="addTab('站点匹配查询','dizhiku/zdppcx.html')">站点匹配查询</a></li>
                  <li><a href="javascript:void(0)" onclick="addTab('站点匹配率查询','dizhiku/zdpplcx.html')">站点匹配率查询</a></li>
                </ul>
              </li>
              <li> <span>系统设置</span>
                <ul>
                  <li><a href="javascript:void(0)" onclick="addTab('库户账号管理','dizhiku/khzhgl.html')">库户账号管理</a></li>
                </ul>
              </li>
            </ul></td>
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
      <tr>
        <td align="center" bgcolor="#FFFFFF"><select class="easyui-combobox" name="state" style="width:200px;">
          <option value="1">1</option>
          <option value="2">23</option>
          <option value="3">123</option>
        </select></td>
        <td align="center" bgcolor="#FFFFFF">
          <input type="text" name="textfield" id="textfield" />
        </td>
      </tr>
      <tr>
        <td colspan="2" bgcolor="#FFFFFF"><a href="javascript:void(0)" class="easyui-linkbutton">保存</a>&nbsp;&nbsp;<a href="javascript:void(0)" class="easyui-linkbutton">新增</a></td>
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
