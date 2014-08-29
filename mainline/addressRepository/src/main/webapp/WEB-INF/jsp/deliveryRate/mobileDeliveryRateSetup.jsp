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
<script src="<%=request.getContextPath()%>/js/common/multiSelectUtil.js" type="text/javascript"></script>

<script language="javascript">
	$(function() {
		var $menuli = $(".cg_tabbtn ul li");
		$menuli.click(function() {
			$(this).children().addClass("light");
			$(this).siblings().children().removeClass("light");
			var index = $menuli.index(this);
			$(".tabbox li").eq(index).show().siblings().hide();
		});
	})

	$(function() {
		centerBox();
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
	
	function refreshPage() {
		window.location="mobileDeliveryRateSetup";
	}
	
	function listDeliveryRateCondition() {
		$.ajax({
			type: "POST",
			url: "listDeliveryRateCondition",
			data: {
				name: $("#name").val(),
				selectType: $("#selectType").val()
			},
			dataType:"html",
			success : function(data) {
				$("#conditionListDiv").html(data);
			},
			error: function(XMLHttpRequest, textStatus, errorThrown) {
				alert(XMLHttpRequest.status);
			}
		});
	}
	
	function preSaveDeliveryRateCondition(id) {
		$.ajax({
			type: "POST",
			url: "preSaveDeliveryRateCondition",
			data: {
				id: id
			},
			dataType:"html",
			success : function(data) {
				$("#alert_box").html(data);
				$("#alert_box").show();
				centerBox();
			},
			error: function(XMLHttpRequest, textStatus, errorThrown) {
				alert(XMLHttpRequest.status);
			}
		});
	}
	
	function deactiveCondition(id) {
		$.ajax({
			type: "POST",
			url: "deactiveDeliveryRateCondition",
			data: {
				id: id
			},
			dataType:"html",
			success : function(data) {
				listDeliveryRateCondition();
			},
			error: function(XMLHttpRequest, textStatus, errorThrown) {
				alert(XMLHttpRequest.status);
			}
		});
	}
	
	function activeCondition(id) {
		$.ajax({
			type: "POST",
			url: "activeDeliveryRateCondition",
			data: {
				id: id
			},
			dataType:"html",
			success : function(data) {
				listDeliveryRateCondition();
			},
			error: function(XMLHttpRequest, textStatus, errorThrown) {
				alert(XMLHttpRequest.status);
			}
		});
	}
</script>

</head>
<body style="background: #eef9ff" marginwidth="0" marginheight="0">
	<!--弹窗开始 -->
	<div id="alert_box" style="display: none"></div>
	<!--弹窗结束-->
	<div class="saomiao_box2">
		<div class="cg_tabbtn">
			<ul>
				<li><a href="#" class="light">妥投率条件设置 </a></li>
			</ul>
		</div>
		<div class="tabbox">
			<li>
				<div class="green_searchbox">
					<form id="listForm" action="" method="post">
						<div class="green_searchbox">
							条件名： <label for="textarea"></label> 
							<input id="name" name="name" type="text" size="30" /> &nbsp;查询类型： 
							<select id="selectType" name="selectType">
								<option value="0">全部</option>
								<option value="1">站点</option>
								<option value="2">供货商</option>
							</select> &nbsp;&nbsp;&nbsp; 
							<input type="button" class="input_button2" value="查询" onclick="listDeliveryRateCondition()" />
							<input type="button" class="input_button2" value="返回" onclick="refreshPage()" />
							<input type="button" class="input_button3" style="width: 120px" value="设置查询条件" onclick="preSaveDeliveryRateCondition()"/>
						</div>
						<div id="conditionListDiv" class="saomiao_inwrith2">
							<table width="100%" border="0" cellspacing="1" cellpadding="2" class="table_2">
								<tr class="font_1">
									<td width="100" align="center" bgcolor="#F4F4F4">条件名</td>
									<td width="100" align="center" bgcolor="#F4F4F4">查询类型</td>
									<td bgcolor="#F4F4F4">条件内容</td>
									<td width="100" bgcolor="#F4F4F4">操作</td>
								</tr>
							</table>
						</div>
					</form>
				</div>
			</li>
		</div>
		<div></div>
	</div>
</body>
</html>

