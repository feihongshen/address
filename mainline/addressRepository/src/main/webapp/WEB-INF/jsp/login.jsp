<%@ page contentType="text/html; charset=UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<title>用户登录</title>
<link href="${pageContext.request.contextPath}/images/login.css" rel="stylesheet" type="text/css" />
<!-- 
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/easyui/themes/default/easyui.css" />
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/easyui/themes/icon.css" />
 -->
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-1.8.0.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/easyui/jquery.easyui.min.js"></script>

<script>
	$(function() {
	});
	
	function login() {
		if($("#username").val()==""){
			alert("用户名不能为空");
			return false;
		}
		if($("#password").val()==""){
			alert("密码不能为空");
			return false;
		}
		$("#loginForm").submit();
		return false;
	}
</script>

</head>
<body>
	<div class="login">
		<div class="top">
			<div class="top_left"><img src="${pageContext.request.contextPath}/images/login_03.gif" /></div>
			<div class="top_center"></div>
		</div>
		<div id="center">
			<form id="loginForm" action="<%=request.getContextPath()%>/resources/j_spring_security_check" method="post">
				<div class="center_left"></div>
				<div class="center_middle">
				<div class="user">用 户
					<input id="username" name="j_username" type="text" />
				</div>
				<div class="password">密   码
					<input name="j_password" type="password" id="password" />
				</div>
				<div class="btn">
					<a href="#" onclick="login()">登录</a><a href="#" onclick="$('#loginForm')[0].reset()">清空</a></div>
				</div>
				<div class="center_right"></div>
			</form>
		</div>
		<div class="down">
			<div class="down_left">
				<div class="inf">
					<span class="inf_text">版本信息</span>
					<span class="copyright">地址库登录系统 V1.0</span>
				</div>
			</div>
			<div id="down_center"></div>
		</div>
	</div>
</body>
</html>
