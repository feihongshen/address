<%@ page language="java" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<script type="text/javascript">
</script>
</head>
<body style="background:#eef9ff">
	创建地址：<br/>
	<form action="saveAddress" method="post">
		parentId: <input name="parentId"></input>
		name: <input name="name"></input>
		<input type="submit" value="saveAddress"></input>
	</form>
	
	<p/>
	搜索地址：<br/>
	<form action=searchAddress method="post">
		addressLine: <input name="addressLine"></input>
		<input type="submit" value="searchAddress"></input>
	</form>
	
	<p/>
	创建别名：<br/>
	<form action=createAlias method="post">
		addressId: <input name="addressId"></input>
		name: <input name="name"></input>
		<input type="submit" value="createAlias"></input>
	</form>
	
	<p/>
	导入地址：<br/>
	<form action=importAddress method="post" enctype="multipart/form-data">
		fileName: <input name="file" type="file"></input>
		<input type="submit" value="导入"></input>
	</form>
</body>
</html>
