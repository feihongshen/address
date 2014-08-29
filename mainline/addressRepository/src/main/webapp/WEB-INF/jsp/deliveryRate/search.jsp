<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@page import="cn.explink.domain.logdto.*"%>
<%@page import="cn.explink.domain.*"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="cn.explink.util.Page"%>
<%@page import="cn.explink.enumutil.CwbOrderTypeIdEnum"%>
<%@page import="cn.explink.enumutil.PaytypeEnum"%>
<%@page import="cn.explink.enumutil.FlowOrderTypeEnum"%>
<%@page import="cn.explink.enumutil.DeliveryStateEnum"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<html>

<head>
<div></div>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<link rel="stylesheet" href="<%=request.getContextPath()%>/css/2.css" type="text/css"/>
<link rel="stylesheet" href="<%=request.getContextPath()%>/css/index.css" type="text/css"/>
<link rel="stylesheet" href="<%=request.getContextPath()%>/css/reset.css" type="text/css"/>
<link rel="stylesheet" href="<%=request.getContextPath()%>/css/redmond/jquery-ui-1.8.18.custom.css" type="text/css" media="all" />

<script src="<%=request.getContextPath()%>/js/jquery-1.7.1.min.js" type="text/javascript"></script>
<script src="<%=request.getContextPath()%>/js/multiSelcet/jquery.multiSelect.js" type="text/javascript"></script>
<script src="<%=request.getContextPath()%>/js/multiSelcet/jquery.bgiframe.min.js" type="text/javascript"></script>
<link href="<%=request.getContextPath()%>/js/multiSelcet/jquery.multiSelect.css" rel="stylesheet" type="text/css" />

<script src="<%=request.getContextPath()%>/js/jquery-ui-1.8.18.custom.min.js" type="text/javascript"></script>
<script src="<%=request.getContextPath()%>/js/jquery.ui.datepicker-zh-CN.js" type="text/javascript"></script>
<script src="<%=request.getContextPath()%>/js/jquery-ui-timepicker-addon.js" type="text/javascript"></script>
<script src="<%=request.getContextPath()%>/js/jquery.ui.message.min.js" type="text/javascript"></script>
<script src="<%=request.getContextPath()%>/js/multiSelcet/MyMultiSelect.js" type="text/javascript"></script>
<script src="<%=request.getContextPath()%>/js/js.js" type="text/javascript"></script>
<script src="<%=request.getContextPath()%>/js/jquery.form.js" type="text/javascript"></script>
<script language="javascript">
	$(function() {
		var $menuli = $(".cg_tabbtn ul li");
		$menuli.click(function() {
			if($(this).children().attr("class") == "light") {
				return;
			}
			$(this).children().addClass("light");
			$(this).siblings().children().removeClass("light");
			var index = $menuli.index(this);
			$(".tabbox li").eq(index).show().siblings().hide();
			
			$(".downloadRequestTr").each(function(){
				if ($(this).is(':hidden')) {
					$(this).show();
				} else {
					$(this).hide();
				}
			});
		});
	})

	$(function() {
		$("#branchId").multiSelect({oneOrMoreSelected:'*', noneSelected:'请选择'});
		$("#customerId").multiSelect({oneOrMoreSelected:'*', noneSelected:'请选择'});
		$("#timeType").multiSelect({oneOrMoreSelected:'*', noneSelected:'请选择'});
		$("#timeType2").multiSelect({oneOrMoreSelected:'*', noneSelected:'请选择'});
		
		$("[name='startDate']").datepicker({
			maxDate: 0
		});
		$("[name='startDateHidden']").datepicker({
			maxDate: 0
		});
		$("[name='endDate']").datepicker({
			maxDate: 0
		});
		$("[name='endDateHidden']").datepicker({
			maxDate: 0
		});
		$("#startDate").datepicker();
		$("#endDate").datepicker();
		$("#startDate2").datepicker();
		$("#endDate2").datepicker();
		
		$("[name='startTime']").timepicker({
			timeOnlyTitle: "选择时间",
			timeFormat: 'hh:mm:ss'
		});
		$("[name='endTime']").timepicker({
			timeOnlyTitle: "选择时间",
			timeFormat: 'hh:mm:ss'
		});
		$("[name='cdTime']").timepicker({
			timeOnlyTitle: "选择时间",
			timeFormat: 'hh:mm:ss'
		});
	})
	$(function(){
		$("input[name='customization']").click(function(){
			$(this).siblings(".switchTab").each(function(){
				if ($(this).is(':hidden')) {
					$(this).show();
				} else {
					$(this).hide();
				}
			});
		})
	})
	$(function(){
		list(1);
	})
	
	function refreshPage(tabId) {
		list(tabId);
	}
	function list(tabId) {
		$.ajax({
			type: "POST",
			url: "listDeliveryRateRequests",
			data: {
				tabId: tabId
			},
			dataType:"html",
			success : function(data) {
				$("#downloadRequestDiv").html(data);
			},
			error: function(XMLHttpRequest, textStatus, errorThrown) {
				alert(XMLHttpRequest.status);
				alert(XMLHttpRequest.readyState);
				alert(textStatus);
			}
		});
	}
	
	function getCheckedValue(name) {
		var checkedValue = "";
		$("label[class=checked]>input[name=" + name + "]").each(
			function(index){
				if (index != 0) {
					checkedValue += ",";
				}
				//找到被选中的项
				checkedValue += $(this).val();
		});
		return checkedValue;
	}
	
	function submitForm(formId) {
		if (formId == 1) {
			if (getCheckedValue("branchId") == "") {
				alert("请选择站点");
				return;
			}
			if ($("#startDate").attr("value") == null || $("#startDate").attr("value") == ""
				|| $("#endDate").attr("value") == null || $("#endDate").attr("value") == "") {
				alert("请选择统计日期");
				return;
			}
			if($("#startDate").val() > $("#endDate").val()){
				alert("开始时间不能大于结束时间");
				return;
			}
			if ($("#customization1").attr("checked")) {
				if ($("#startTime").attr("value") == null || $("#startTime").attr("value") == ""
						|| $("#endTime").attr("value") == null || $("#endTime").attr("value") == "") {
					alert("请选择统计时间");
					return;
				}
				if ($("#cdTime").attr("value") == null || $("#cdTime").attr("value") == "") {
					alert("请选择反馈时间");
					return;
				}
			} else {
				if (getCheckedValue("timeType") == "") {
					alert("请选择时效");
					return;
				}
			}
			$("#form1").ajaxSubmit({
				type: "POST",
				url: "listDeliveryRateRequests",
				dataType:"html",
				success : function(data) {
					$("#downloadRequestDiv").html(data);
				},
				error: function(XMLHttpRequest, textStatus, errorThrown) {
					alert(XMLHttpRequest.status);
					alert(XMLHttpRequest.readyState);
					alert(textStatus);
				}
			});
		} else {
			if (getCheckedValue("customerId") == "") {
				alert("请选择供货商");
				return;
			}
			if ($("#startDate2").attr("value") == null || $("#startDate2").attr("value") == ""
					|| $("#endDate2").attr("value") == null || $("#endDate2").attr("value") == "") {
				alert("请选择统计日期");
				return;
			}
			if($("#startDate2").val() > $("#endDate2").val()){
				alert("开始时间不能大于结束时间");
				return;
			}
			if ($("#customization2").attr("checked")) {
				if ($("#startTime").attr("value") == null || $("#startTime").attr("value") == ""
						|| $("#endTime").attr("value") == null || $("#endTime").attr("value") == "") {
					alert("请选择统计时间");
					return;
				}
				if ($("#cdTime").attr("value") == null || $("#cdTime").attr("value") == "") {
					alert("请选择反馈时间");
					return;
				}
			} else {
				if (getCheckedValue("timeType2") == "") {
					alert("请选择时效");
					return;
				}
			}
			$("#form2").ajaxSubmit({
				type: "POST",
				url: "listDeliveryRateRequests",
				dataType:"html",
				success : function(data) {
					$("#downloadRequestDiv").html(data);
				},
				error: function(XMLHttpRequest, textStatus, errorThrown) {
					alert(XMLHttpRequest.status);
					alert(XMLHttpRequest.readyState);
					alert(textStatus);
				}
			});
		}
	}
	
	function deleteDownloadRequest(downloadRequestId, tabId) {
		$.ajax({
			type: "POST",
			url: "listDeliveryRateRequests",
			data: {
				action: "delete",
				downloadRequestId: downloadRequestId,
				tabId: tabId
			},
			dataType:"html",
			success : function(data) {
				$("#downloadRequestDiv").html(data);
			},
			error: function(XMLHttpRequest, textStatus, errorThrown) {
				alert(XMLHttpRequest.status);
				alert(XMLHttpRequest.readyState);
				alert(textStatus);
			}
		});
	}
</script>

</head>
<script type="text/javascript">
	
</script>
<body style="background: #eef9ff" marginwidth="0" marginheight="0">
	<div class="saomiao_box2">
		<div class="cg_tabbtn">
			<ul>
				<li><a class="light">站点</a></li>
				<li><a >供货商</a></li>
			</ul>
		</div>
		<div class="tabbox">
			<!-- 站点tab -->
			<li>
				<div class="saomiao_inbox2">
					<form id="form1" action="deliveryRateList" method="post">
						<input name="formId" value="1" type="hidden" />
						<input name="tabId" value="1" type="hidden" />
						<div class="saomiao_selet2">
							<input id="customization1" name="customization" type="checkbox" checked="checked" formId="form1">自行指定时效</input>
							<p style="line-height:5px">&nbsp;</p>
							站点： <select name="branchId" id="branchId" multiple="multiple" style="width: 200px;">
								<c:forEach items="${branchList}" varStatus="i" var="branch">
									<option value="${branch.branchid}">${branch.branchname}</option>
								</c:forEach>
							</select> [<a href="javascript:multiSelectAll('branchId',1,'请选择');">全选</a>]
							[<a href="javascript:multiSelectAll('branchId',0,'请选择');">取消全选</a>]
							查询日期：<input id="startDate" name="startDate" type="text" value="${startDate}" />
							到 <input id="endDate" name="endDate" type="text" value="${endDate}" />
							<p style="line-height:5px">&nbsp;</p>
							<font class="switchTab" style="display:none">
								从
							</font>
							<select name="queryType">
								<option value="byBranch">站点到货时间</option>
								<option value="byUser">小件员领货时间</option>
							</select>
							<font class="switchTab" style="display:inline">
								在
								<input id="startTime" name="startTime" type="text" value="00:00:00" />
								到 <input id="endTime" name="endTime" type="text" value="00:00:00" />
								且
							</font>
							<font class="switchTab" style="display:none">
								到
							</font>
							<select name="computeType">
								<option value="bySubmitTime">反馈时间</option>
								<option value="byApproveTime">审核时间</option>
							</select>
							<font class="switchTab" style="display:inline">
								在
								<select name="cdDateType">
									<c:forEach items="${allCdDateTypes}" varStatus="i" var="cdDateType">
										<option value="${cdDateType}">${cdDateType.desc}</option>
									</c:forEach>
								</select>
								<input id="cdTime" name="cdTime" type="text" value="20:00:00" />
							</font>
							<font class="switchTab" style="display: none">
								时效在
								<select name="timeType" id="timeType" multiple="multiple" style="width: 100px;">
									<c:forEach items="${allTimeTypes}" varStatus="i" var="timeType">
										<option value="${timeType.name}">${timeType.desc}</option>
									</c:forEach>
								</select>
								内
								[<a href="javascript:multiSelectAll('timeType',1,'请选择');">全选</a>]
								[<a href="javascript:multiSelectAll('timeType',0,'请选择');">取消全选</a>]
							</font>
							<input type="button" class="input_button2" value="查询" onclick="submitForm(1)"/>
							<input type="button" class="input_button2" value="刷新" onclick="refreshPage(1)"/>
						</div>
					</form>
				</div>
			</li>
			
			<!-- 供货商tab -->
			<li style="display: none">
				<div class="saomiao_inbox2">
					<form id="form2" action="deliveryRateList" method="post">
						<input name="formId" value="2" type="hidden" />
						<input name="tabId" value="2" type="hidden" />
						<input name="timeType" value="" type="hidden" />
						<div class="saomiao_selet2">
							<input id="customization2" name="customization" type="checkbox" checked="checked" formId="form2">自行指定时效</input>
							<p style="line-height:5px">&nbsp;</p>
							供货商： <select name="customerId" id="customerId" multiple="multiple" style="width: 200px;">
								<c:forEach items="${customerList}" varStatus="i" var="customer">
									<option value="${customer.customerid}">${customer.customername}</option>
								</c:forEach>
							</select> [<a href="javascript:multiSelectAll('customerId',1,'请选择');">全选</a>]
							[<a href="javascript:multiSelectAll('customerId',0,'请选择');">取消全选</a>]
							查询日期： <input id="startDate2" name="startDate" type="text" value="${startDate}">
							至 <input id="endDate2" name="endDate" type="text" value="${endDate}">
							<p style="line-height:5px">&nbsp;</p>
							<font class="switchTab" style="display: none">
								从
							</font>
							<select name="queryType">
								<option value="byWarehouse">库房入库时间</option>
								<option value="byVendor">供货商发货时间</option>
							</select>
							<font class="switchTab" style="display: inline">
								在 <input id="startTime" name="startTime" type="text" value="00:00:00" />
								到 <input id="endTime" name="endTime" type="text" value="00:00:00" />
								且 
							</font>
							<font class="switchTab" style="display: none">
								到
							</font>
							<select name="computeType">
								<option value="bySubmitTime">反馈时间</option>
								<option value="byApproveTime">审核时间</option>
							</select>
							在
							<font class="switchTab" style="display: inline">
								<select name="cdDateType">
									<c:forEach items="${allCdDateTypes}" varStatus="i" var="cdDateType">
										<option value="${cdDateType}">${cdDateType.desc}</option>
									</c:forEach>
								</select>
							</font>
							<font class="switchTab" style="display: inline">
								<input id="cdTime" name="cdTime" type="text" value="20:00:00" />
							</font>
							<font class="switchTab" style="display: none">
							时效在  <select name="timeType2" id="timeType2" multiple="multiple" style="width: 100px;">
								<c:forEach items="${allTimeTypes}" varStatus="i" var="timeType">
									<option value="${timeType.name}">${timeType.desc}</option>
								</c:forEach>
							</select>
							内
							[<a href="javascript:multiSelectAll('timeType2',1,'请选择');">全选</a>]
							[<a href="javascript:multiSelectAll('timeType2',0,'请选择');">取消全选</a>]
							</font>
							<input type="button" class="input_button2" value="查询" onclick="submitForm(2)"/>
							<input type="button" class="input_button2" value="刷新" onclick="refreshPage(2)"/>
						</div>
					</form>
				</div>
			</li>
		</div>
		
		<div id="downloadRequestDiv" class="saomiao_inwrith2">
		</div>
	</div>
</body>
</html>

