<%@ page language="java" pageEncoding="UTF-8"%>
<%@ page import="java.util.*"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
<head>
<title>管理员管理</title>
<link rel="stylesheet" type="text/css"
	href="${pageContext.request.contextPath}/css/easyui/themes/default/easyui.css" />
<link rel="stylesheet" type="text/css"
	href="${pageContext.request.contextPath}/css/easyui/themes/icon.css" />
<script type="text/javascript"
	src="${pageContext.request.contextPath}/js/jquery-1.8.0.min.js"></script>
<script type="text/javascript"
	src="${pageContext.request.contextPath}/js/easyui/jquery.easyui.min.js"></script>
<script type="text/javascript">
	/**
	$(function() {
		addTab('地址匹配','${pageContext.request.contextPath}/address/index');
	});
	function addTab(title, url) {
		if ($('#centerTabs').tabs('exists', title)) {
			$('#centerTabs').tabs('select', title);
		} else {
			var content = '<iframe scrolling="auto" frameborder="0" src="' + url + '" style="width:100%;height:100%;"></iframe>';
			$('#centerTabs').tabs('add', {
				title : title,
				content : content,
				closable : true
			});
		}
	}
	*/
</script>
<style type="text/css">
#fm {
	margin: 0;
	padding: 10px 30px;
}
.ftitle {
	font-size: 14px;
	font-weight: bold;
	padding: 5px 0;
	margin-bottom: 10px;
	border-bottom: 1px solid #ccc;
}
.fitem {
	margin-bottom: 5px;
}
.fitem label {
	display: inline-block;
	width: 80px;
}
.focus {
	font-weight: bold;
}
</style>
</head>
<body class="easyui-layout">
<div data-options="region:'north',border:false" style="height:30px;background:#B3DFDA;padding:10px;font-size:14px">
  <table width="100%" border=0 cellspacing="0" cellpadding="0">
    <tr>
      <td><strong>地址库管理平台</strong></td>
      <td align="right" style="font-size:12px"><a href="javascript:changePass();">修改密码</a>　&nbsp;<a href="/user/logout.htm">退出</a></td>
    </tr>
  </table>
</div>
<div data-options="region:'west',split:true,title:'功能菜单'" style="width:180px;padding:10px;">
  <ul class="easyui-tree">
    <li><a href="#" onclick="addTab('地址库匹配','dizhiku/dzkpp.html')">地址库匹配</a></li>
    <li> <span>数据维护</span>
      <ul>
        <li><a href="#" onclick="addTab('关键词导入','address/addressImportPage')">关键词导入</a></li>
        <li><a href="#" onclick="addTab('地址库维护','dizhiku/dzkwh.html')">地址库维护</a></li>
        <li><a href="#" onclick="addTab('站点管理','dizhiku/zdgl.html')">站点管理</a></li>
        <li><a href="#" onclick="addTab('配送员管理','dizhiku/psygl.html')">配送员管理</a></li>
      </ul>
    </li>
    <li> <span>关联设置</span>
      <ul>
        <li><a href="#" onclick="addTab('拆合站维护','dizhiku/chzwh.html')">拆合站维护</a></li>
        <li><a href="#" onclick="addTab('配送站点关联维护','dizhiku/pszdglwh.html')">配送站点关联维护</a></li>
        <li><a href="#" onclick="addTab('配送员关联维护','dizhiku/psyglwh.html')">配送员关联维护</a></li>
      </ul>
    </li>
    <li> <span>数据统计</span>
      <ul>
        <li><a href="#" onclick="addTab('站点匹配查询','dizhiku/zdppcx.html')">站点匹配查询</a></li>
        <li><a href="#" onclick="addTab('站点匹配率查询','dizhiku/zdpplcx.html')">站点匹配率查询</a></li>
      </ul>
    </li>
    <li> <span>系统设置</span>
      <ul>
        <li><a href="#" onclick="addTab('库户账号管理','dizhiku/khzhgl.html')">库户账号管理</a></li>
      </ul>
    </li>
  </ul>
</div>
<div data-options="region:'south',border:false" style="height:30px;background:#A9FACD;padding:10px;">Copyright ? 2008-2014 Lefeng.com All Rights Reserved. </div>
<div data-options="region:'center'" id="mainDiv">
  <div class="easyui-tabs" id="adminTabs">
    <div title="欢迎首页" style="padding:1px">
      <div style="line-height:500px; text-align:center"><h1>欢迎登陆地址库匹配系统</h1></div>
     </div>
  </div>
</div>
<div id="dlg" class="easyui-dialog" style="width:450px;height:405px;padding:10px 20px"  
            closed="true" buttons="#dlg-buttons">
  <div class="ftitle">管理员信息</div>
  <form id="fm" method="post" novalidate>
    <div class="fitem">
      <label>ID:</label>
      <input name="id" id="aId" readonly="readonly" />
    </div>
    <div class="fitem">
      <label>登陆帐号:</label>
      <input name="uid" class="easyui-validatebox" required="true" />
    </div>
    <div class="fitem">
      <label>登录密码:</label>
      <input type="password" id="password" name="pass" />
    </div>
    <div class="fitem">
      <label>确认密码:</label>
      <input type="password" id="cfmpass" name="cfmpass" />
    </div>
    <div class="fitem">
      <label>姓名:</label>
      <input name="name" class="easyui-validatebox" required="true" />
    </div>
    <div class="fitem">
      <label>部门:</label>
      <input name="department" class="easyui-validatebox" required="true"  size="32" />
    </div>
    <div class="fitem">
      <label>手机:</label>
      <input name="mobile" class="easyui-validatebox" required="true"  size="32" />
    </div>
    <div class="fitem">
      <label>邮箱:</label>
      <input name="email" class="easyui-validatebox" required="true"  size="32" />
    </div>
    <div class="fitem">
      <label>状态:</label>
      <select size="1" name="status">
        <option value="1"><font color=#33cc00>有效</font></option>
        <option value="0"><font color=#cc0033>无效</font></option>
      </select>
    </div>
    <div class="fitem">
      <label>管理级别:</label>
      <select size="1" name="lev">
        <option value="9">超级管理员</option>
        <option value="1">普通管理员</option>
      </select>
    </div>
  </form>
</div>
<div id="dlg-buttons"> <a href="javascript:void(0)" class="easyui-linkbutton" iconCls="icon-ok" onclick="saveAdmin()">保存</a> <a href="javascript:void(0)" class="easyui-linkbutton" iconCls="icon-cancel" onclick="javascript:$('#dlg').dialog('close')">取消</a> </div>
<div id="dlgChng" class="easyui-dialog" style="width:360px;height:220px;padding:10px 20px"  
            closed="true" buttons="#dlgChng-buttons">
  <div class="ftitle">修改密码</div>
  <form id="fmChng" method="post" novalidate>
    <div class="fitem">
      <label>原始密码:</label>
      <input type="password" id="oldpass" name="oldpass" class="easyui-validatebox" required="true"/>
    </div>
    <div class="fitem">
      <label>登录密码:</label>
      <input type="password" id="password" name="pass" class="easyui-validatebox" required="true"/>
    </div>
    <div class="fitem">
      <label>确认密码:</label>
      <input type="password" id="cfmpass" name="cfmpass" class="easyui-validatebox" required="true"/>
    </div>
  </form>
</div>
<div id="dlgChng-buttons"> <a href="javascript:void(0)" class="easyui-linkbutton" iconCls="icon-ok" onclick="chngPass()">保存</a> <a href="javascript:void(0)" class="easyui-linkbutton" iconCls="icon-cancel" onclick="javascript:$('#dlgChng').dialog('close')">取消</a> </div>
	
	<script type="text/javascript">
		function newAdmin() {
			$('#dlg').dialog('open').dialog('setTitle', '添加管理员');
			$('#fm').form('clear');
			var init = {
				status : 1,
				lev : 1
			};
			$('#fm').form('load', init);
		}
		function editAdmin() {
			var row = $('#dg').datagrid('getSelected');
			if (row) {
				if (row.uid == "root") {
					alert("超级管理员不能修改！");
					return false;
				}
				$('#fm').form('clear');
				$('#dlg').dialog('open').dialog('setTitle', '编辑管理员');
				$('#fm').form('load', row);
			} else {
				$.messager.show({
					title : 'Warnning',
					msg : "请先选择管理员进行编辑",
					style : {
						right : '',
						top : 200 + document.body.scrollTop
								+ document.documentElement.scrollTop,
						bottom : ''
					}
				});
			}
		}
		function removeAdmin() {
			var row = $('#dg').datagrid('getSelected');
			if (!row) {
				alert("请先选择管理员进行删除操作！");
				return false;
			}
			if (row.uid == "root") {
				alert("超级管理员不能删除！");
				return false;
			}
			if (!confirm("你确认要删除该管理员？\n如果该管理员已有角色则不能删除！")) {
				return false;
			}
			var surl = "/user/remove.htm";
			var paras = {
				keyword : $("#keyword").val(),
				id : row.id
			};
			dealAjaxOpration(surl, $.param(paras), "setData", false);
		}
		function saveAdmin() {
			if ($('#aId').val() == "" && $('#password').val() == "") {
				alert("新增管理员[登录密码]必须填写，且不能小于5位!");
				return false;
			}
			if ($('#password').val() != ""
					&& $('#password').val() != $('#cfmpass').val()) {
				alert("[登录密码]和[确认密码]不相同，请重新输入！");
				return false;
			}
			if ($('#password').val() != "" && $('#password').val().length < 5) {
				alert("[登录密码]不能小于5位，请重新填写!");
				return false;
			}
			$('#fm').form('submit', {
				url : "/user/save.htm",
				onSubmit : function() {
					return $(this).form('validate');
				},
				success : function(result) {
					var result = eval('(' + result + ')');
					if (result.errorMsg) {
						alert(result.errorMsg);
					} else {
						$('#dlg').dialog('close'); // close the dialog
						searchAdmin(); // reload the user data
					}
				}
			});
		}
		$(function() {
			searchAdmin();
		});
		function setData(json) {
			$("#dg").datagrid('loadData', JSON.parse(json));
		}
		function searchAdmin() {
			var surl = "/user/search.htm";
			var paras = {
				keyword : $("#keyword").val()
			};
			dealAjaxOpration(surl, $.param(paras), "setData", false);
		}
		function chngPass() {
			if ($('#password').val() != ""
					&& $('#password').val() != $('#cfmpass').val()) {
				alert("[登录密码]和[确认密码]不相同，请重新输入！");
				return false;
			}
			if ($('#password').val() != "" && $('#password').val().length < 5) {
				alert("[登录密码]不能小于5位，请重新填写!");
				return false;
			}
			$('#fmChng').form('submit', {
				url : "/user/chngpass.htm",
				onSubmit : function() {
					return $(this).form('validate');
				},
				success : function(result) {
					var result = eval('(' + result + ')');
					if (result.errorMsg) {
						alert(result.errorMsg);
					} else {
						$('#dlgChng').dialog('close');
					}
				}
			});
		}
		function changePass() {
			$('#dlgChng').dialog('open').dialog('setTitle', '修改密码');
		}
		function addTab(title, url) {
			if ($('#adminTabs').tabs('exists', title)) {
				$('#adminTabs').tabs('select', title);
			} else {
				var content = '<iframe scrolling="auto" frameborder="0" src="'
						+ url + '" style="width:100%;height:100%;"></iframe>';
				$('#adminTabs').tabs('add', {
					title : title,
					content : content,
					closable : true
				});
			}
		}
		$('#adminTabs').width($(window).width() - 165);
		$('#adminTabs').height($(window).height() - 62);
	</script>
</body>
</html>