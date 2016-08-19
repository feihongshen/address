<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
<head>
<title>阡陌地址库</title>

<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-1.8.0.min.js"></script>

<script type="text/javascript">
$(function(){
	
});
function _submit(){
	var customerId=$("#customerId").val();
	//显示当前登录用户信息
	var txt=$("#customerId").find("option:selected").text();
	$("#_check_submit").hide();
	$("#customerId").hide();
	$("#msg").html( txt+"正在处理......");
	$("#compare").html("");
	$.ajax({
		type : "POST",
		url : "<%=request.getContextPath()%>/common/mergeUpdate",
		data:{customerId:customerId},
		success : function(resp) {
			$("#customerId").show();
			$("#_check_submit").show();
			$("#msg").html(txt+resp.info);
			validate();
		}
	});
	
	
}


function validate(){
	var customerId=$("#customerId").val();
	var txt="";
	$.ajax({
		type : "POST",
		url : "<%=request.getContextPath()%>/common/validate",
		data:{customerId:customerId},
		success : function(resp) {
			
			txt=txt+"address(所有的关键词存放位置):					(源)"+resp.obj.countOldAddress+"->(新)"+resp.obj.countNewAddress+"<br/>";
			//txt=txt+"address_station_relation:	"+resp.obj.countOldAddressStationRelation+"->"+resp.obj.countNewAddressStationRelation+"<br/>";
			txt=txt+"address_permissions(地址权限表):		(源)"+resp.obj.countOldAddressPermissions+"->(新)"+resp.obj.countNewAddressPermissions+"<br/>";
			txt=txt+"alias(别名表):						(源)"+resp.obj.countOldAlias+"->(新)"+resp.obj.countNewAlias+"<br/>";
			txt=txt+"client_applications(应用客户表):		(源)"+resp.obj.countOldClientApplications+"->(新)"+resp.obj.countNewClientApplications+"<br/>";
			txt=txt+"deliverer_rules(小件员规则表):			(源)"+resp.obj.countOldDelivererRules+"->(新)"+resp.obj.countNewDelivererRules+"<br/>";
			txt=txt+"deliverers(小件员信息表):				(源)"+resp.obj.countOldDeliverer+"->(新)"+resp.obj.countNewDeliverer+"<br/>";
			txt=txt+"delivery_station_rules(配送站点规则表):	(源)"+resp.obj.countOldDeliveryStationRules+"->(新)"+resp.obj.countNewDeliveryStationRules+"<br/>";
			txt=txt+"delivery_stations(配送站点):			(源)"+resp.obj.countOldDeliveryStations+"->(新)"+resp.obj.countNewDeliveryStations+"<br/>";
			txt=txt+"keyword_suffix(关键词后缀表):			(源)"+resp.obj.countOldKeywordSuffix+"->(新)"+resp.obj.countNewKeywordSuffix+"<br/>";
			txt=txt+"system_config(系统配置表):				(源)"+resp.obj.countOldSystemConfig+"->(新)"+resp.obj.countNewSystemConfig+"<br/>";
			txt=txt+"users(用户表):						(源)"+resp.obj.countOldUsers+"->(新)"+resp.obj.countNewUsers+"<br/>";
			txt=txt+"vendors(供应商):					(源)"+resp.obj.countOldVendors+"->(新)"+resp.obj.countNewVendors+"<br/>";
			
	         $("#compare").html(txt);
		}
	});
}

 //var t2 = window.setInterval("validate()",60000); 
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
	<br/>
	<br/>
	<form action="">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;省份：
		<select id="customerId" name="customerId">
		<option value=20 >沈阳	20</option>
<option value=13 >上海	13</option>
<option value=28 >苏州	28</option>
<option value=33 >云南	33</option>
<option value=38 >浙江	38</option>
<option value=15 >黑龙江	15</option>
<option value=10 >安徽	10</option>
<option value=32 >新疆	32</option>
<option value=26 >西藏	26</option>
<option value=31 >四川	31</option>
<option value=2 >贵州	2</option>
<option value=43 >河北	43</option>
<option value=42 >山东	42</option>
<option value=12 >江西	12</option>
<option value=6 >广州通路	6</option>
<option value=14 >吉林	14</option>
<option value=45 >北京	45</option>
<option value=9 >福建	9</option>
<option value=5 >重庆	5</option>
<option value=18 >青海	18</option>
<option value=44 >天津	44</option>
<option value=41 >河南	41</option>
<option value=4 >广西	4</option>
<option value=37 >山西	37</option>
<option value=47 >西安	47</option>
<option value=7 >宁夏	7</option>
<option value=27 >湖南	27</option>
<option value=19 >甘肃	19</option>
<option value=8 >海南	8</option>
<option value=39 >武汉	39</option>
		</select><br/><br/><br/>
		<!-- &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;ip:<input name="ip" id="ip"/><br/>
		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;端口号:<input name="port" id="port" value="3306"/><br/>
		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;数据库名称:<input name="name"  id="name" /><br/>
		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;数据库密码:<input name="password" id="password" /><br/>-->
		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<input  type="button" value="提交" id="_check_submit"   onclick="javascript:_submit();"/> 
	</form>
	<div id="msg" style="font-family: 微软雅黑;color: red"></div>
	<div id="compare">对比结果</div>
</body>
</html>