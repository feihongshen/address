<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<script lang="javascript">
	//修改规则相关
	$(function() {
		$("#branchId").multiSelect({
			oneOrMoreSelected : '*',
			noneSelected : '请选择'
		});
		$("#customerId").multiSelect({
			oneOrMoreSelected : '*',
			noneSelected : '请选择'
		});
		$("#timeType").multiSelect({
			oneOrMoreSelected : '*',
			noneSelected : '请选择'
		});
		$("#timeType2").multiSelect({
			oneOrMoreSelected : '*',
			noneSelected : '请选择'
		});

		$("#startDate").datepicker();
		$("#endDate").datepicker();
		$("#startDate2").datepicker();
		$("#endDate2").datepicker();

		$("[name='startTime']").timepicker({
			timeOnlyTitle : "选择时间",
			timeFormat : 'hh:mm:ss'
		});
		$("[name='endTime']").timepicker({
			timeOnlyTitle : "选择时间",
			timeFormat : 'hh:mm:ss'
		});
		$("[name='cdTime']").timepicker({
			timeOnlyTitle : "选择时间",
			timeFormat : 'hh:mm:ss'
		});

		$("input[name='customization']").change(customizationChanged);
		$("[name='selectType']").change(selectTypeChanged);
	})

	function customizationChanged() {
		$(".switchTab").each(function() {
			if ($(this).is(':hidden')) {
				$(this).show();
			} else {
				$(this).hide();
			}
		});
	}

	function selectTypeChanged() {
		if ($("#selectType").val() == "1") {
			$("#bocNameTd").html("站点");
			$("#queryType")
					.html(
							'<option value="byBranch">站点到货时间</option><option value="byUser">小件员领货时间</option>');
		} else if ($("#selectType").val() == "2") {
			$("#bocNameTd").html("供货商");
			$("#queryType")
					.html(
							'<option value="byWarehouse">库房入库时间</option><option value="byVendor">供货商发货时间</option>');
		}
		$(".bocSwitchTab").each(function() {
			if ($(this).is(':hidden')) {
				$(this).show();
			} else {
				$(this).hide();
			}
		});

		// $("input[name='customization']").attr("checked", "checked");
		// customizationChanged();
	}

	// 保存
	function saveDeliveryRateCondition() {
		if ($("#selectType").val() == 1) {
			if (getCheckedValue("branchId") == ""
					|| getCheckedValue("branchId") == null) {
				alert("请选择站点");
				return;
			}
		} else {
			if (getCheckedValue("customerId") == ""
				|| getCheckedValue("customerId") == null) {
			alert("请选择供货商");
			return;
		}
		}
		if ($("#customization").attr("checked")) {

			if ($("#startTime").val() == null || $("#startTime").val() == ""
					|| $("#endTime").val() == null || $("#endTime").val() == "") {
				alert("请选择统计时间");
				return;
			}

			if ($("#startTime").val() > $("#endTime").val()) {
				alert("开始时间不能大于结束时间");
				return;
			}
		} else {

			if (getCheckedValue("timeType") == "") {
				alert("请选择时效");
				return;
			}
		}

		$("#saveDeliveryRateConditionForm").ajaxSubmit({
			type : "POST",
			url : "saveDeliveryRateCondition",
			dataType : "html",
			success : function(data) {
				closeBox();
				$("#alert_box").html("");
				listDeliveryRateCondition();
			},
			error : function(XMLHttpRequest, textStatus, errorThrown) {
				// alert(XMLHttpRequest.status);
				// alert(XMLHttpRequest.responseText, "Failed");
				alert(textStatus);
			}
		});
	}

	function backButtonClicked() {
		closeBox();
		$("#alert_box").html("");
	}
</script>

<div id="box_bg"></div>
<div id="box_contant">
	<div id="box_top_bg"></div>
	<div id="box_in_bg">
		<h1>
			<div id="close_box" onclick="backButtonClicked()"></div>
			设置查询条件
		</h1>
		<form id="saveDeliveryRateConditionForm" method="post">
			<input name="id" type="hidden" value="${deliveryRateCondition.id}" />
			<div class="right_title" style="padding: 10px">
				<table width="500" border="0" cellpadding="5" cellspacing="1"
					class="table_2">
					<tr>
						<td width="80" align="left">查询类型：</td>
						<td align="left"><select id="selectType" name="selectType">
								<option value="1"
									<c:if test="${deliveryRateCondition.selectType == '1'}">
										selected="selected"
									</c:if>>站点</option>
								<option value="2"
									<c:if test="${deliveryRateCondition.selectType == '2'}">
										selected="selected"
									</c:if>>供货商</option>
						</select></td>
					</tr>
					<tr>
						<td align="left">条件名：</td>
						<td align="left"><input type="text" name="name"
							value="${deliveryRateCondition.name}" /></td>
					</tr>
					<tr>
						<td colspan="2" align="left"><input id="customization"
							name="customization" type="checkbox"
							<c:if test="${deliveryRateRequest == null ||  deliveryRateRequest.customization == true}">
									checked="checked"
								</c:if>>自行指定时效</input>
						</td>
					</tr>
					<tr>
						<td align="left" id="bocNameTd"><c:choose>
								<c:when test="${deliveryRateCondition.selectType == 2}">
									供货商：
								</c:when>
								<c:otherwise>
									站点：
								</c:otherwise>
							</c:choose></td>
						<td align="left"><font class="bocSwitchTab"
							<c:choose>
									<c:when test="${deliveryRateCondition == null}">
										style="display:inline"
									</c:when>
									<c:otherwise>
										<c:choose>
											<c:when test="${deliveryRateCondition.selectType == '1'}">
												style="display:inline"
											</c:when>
											<c:otherwise>
												style="display:none"
											</c:otherwise>
										</c:choose></c:otherwise>
								</c:choose> > <select name="branchId" id="branchId" multiple="multiple"
								style="width: 200px;">
									<c:forEach items="${branchList}" varStatus="i" var="branch">
										<option value="${branch.branchid}"
											<c:if test="${fn:contains(deliveryRateRequest.branchIds, branch.branchid)}">
												selected="selected"
											</c:if>>${branch.branchname}</option>
									</c:forEach>
							</select> [<a href="javascript:multiSelectAll('branchId',1,'请选择');">全选</a>]
								[<a href="javascript:multiSelectAll('branchId',0,'请选择');">取消全选</a>]
						</font> <font class="bocSwitchTab"
							<c:choose>
									<c:when test="${deliveryRateCondition == null}">
										style="display:none"
									</c:when>
									<c:otherwise>
										<c:choose>
											<c:when test="${deliveryRateCondition.selectType == '2'}">
												style="display:inline"
											</c:when>
											<c:otherwise>
												style="display:none"
											</c:otherwise>
										</c:choose></c:otherwise>
								</c:choose> > <select name="customerId" id="customerId" multiple="multiple"
								style="width: 200px;">
									<c:forEach items="${customerList}" varStatus="i" var="customer">
										<option value="${customer.customerid}"
											<c:if test="${fn:contains(deliveryRateRequest.customerIds, customer.customerid)}">
												selected="selected"
											</c:if>>${customer.customername}</option>
									</c:forEach>
							</select> [<a href="javascript:multiSelectAll('customerId',1,'请选择');">全选</a>]
								[<a href="javascript:multiSelectAll('customerId',0,'请选择');">取消全选</a>]
						</font></td>
					</tr>
					<tr>
						<td colspan="2" align="left"><font class="switchTab"
							<c:choose>
									<c:when test="${deliveryRateRequest == null ||  deliveryRateRequest.customization == true}">
										style="display:none"
									</c:when>
									<c:otherwise>
										style="display:inline"
									</c:otherwise>
								</c:choose>>
								从 </font> <select id="queryType" name="queryType">
								<c:if
									test="${deliveryRateCondition == null || deliveryRateCondition.selectType == '1'}">
									<option value="byBranch"
										<c:if test="${deliveryRateRequest.queryType == 'byBranch'}">selected="selected"</c:if>>站点到货时间</option>
									<option value="byUser"
										<c:if test="${deliveryRateRequest.queryType == 'byUser'}">selected="selected"</c:if>>小件员领货时间</option>
								</c:if>
								<c:if test="${deliveryRateCondition.selectType == '2'}">
									<option value="byWarehouse"
										<c:if test="${deliveryRateRequest.queryType == 'byWarehouse'}">selected="selected"</c:if>>库房入库时间</option>
									<option value="byVendor"
										<c:if test="${deliveryRateRequest.queryType == 'byVendor'}">selected="selected"</c:if>>供货商发货时间</option>
								</c:if>
						</select> <font class="switchTab"
							<c:choose>
									<c:when test="${deliveryRateRequest == null ||  deliveryRateRequest.customization == true}">
										style="display:inline"
									</c:when>
									<c:otherwise>
										style="display:none"
									</c:otherwise>
								</c:choose>>
								在 <input id="startTime" name="startTime" type="text"
								value="${deliveryRateRequest.startTime == null ? '00:00:00' : deliveryRateRequest.startTime}" /> 到 <input
								id="endTime" name="endTime" type="text"
								value="${deliveryRateRequest.endTime == null ? '00:00:00' : deliveryRateRequest.endTime}" />
						</font></td>
					</tr>
					<tr>
						<td colspan="2" align="left"><font class="switchTab"
							<c:choose>
									<c:when test="${deliveryRateRequest == null ||  deliveryRateRequest.customization == true}">
										style="display:none"
									</c:when>
									<c:otherwise>
										style="display:inline"
									</c:otherwise>
								</c:choose>>
								到 </font> <select id="computeType" name="computeType">
								<option value="bySubmitTime"
									<c:if test="${deliveryRateRequest.computeType == 'bySubmitTime'}">selected="selected"</c:if>>反馈时间</option>
								<option value="byApproveTime"
									<c:if test="${deliveryRateRequest.computeType == 'byApproveTime'}">selected="selected"</c:if>>审核时间</option>
						</select> <font class="switchTab"
							<c:choose>
									<c:when test="${deliveryRateRequest == null ||  deliveryRateRequest.customization == true}">
										style="display:inline"
									</c:when>
									<c:otherwise>
										style="display:none"
									</c:otherwise>
								</c:choose>>
							
								在 <select name="cdDateType">
									<c:forEach items="${allCdDateTypes}" varStatus="i"
										 var="cdDateType">
										<option value="${cdDateType}" 
											<c:forEach items="${deliveryRateRequest.timeTypes}" var="dtimeType">
											  <c:if test="${dtimeType.cdDateType.desc == cdDateType.desc}">
											     <c:set value="${dtimeType.cdTime}" var="cdTime"/>
											     selected="selected"
											  </c:if>
										    </c:forEach>
										>${cdDateType.desc}</option>
									</c:forEach>
							</select> 
							<input id="cdTime" name="cdTime" type="text"  value="${cdTime == null ? '20:00:00' : cdTime}"/>
						</font> <font class="switchTab"
							<c:choose>
									<c:when test="${deliveryRateRequest == null || deliveryRateRequest.customization == true}">
										style="display:none"
									</c:when>
									<c:otherwise>
										style="display:inline"
									</c:otherwise>
								</c:choose>>
								
								时效在 <select name="timeType" id="timeType" multiple="multiple"
								style="width: 100px;">
									<c:forEach items="${allTimeTypes}" varStatus="i" var="timeType">
										<option value="${timeType.name}"
										   <c:forEach items="${deliveryRateRequest.timeTypes}" var="dtimeType">
											  <c:if test="${timeType.name==dtimeType.name }"> selected="selected" </c:if>
										   </c:forEach>
										>${timeType.desc}</option>
									</c:forEach>
							</select> 内 [<a href="javascript:multiSelectAll('timeType',1,'请选择');">全选</a>]
								[<a href="javascript:multiSelectAll('timeType',0,'请选择');">取消全选</a>]
						</font></td>
					</tr>
				</table>
			</div>
			<table width="100%" border="0" cellspacing="0" cellpadding="5">
				<tr>
					<td align="center"><input type="button" class="input_button1"
						value="保存" onclick="saveDeliveryRateCondition()" />&nbsp;&nbsp; <input
						type="button" class="input_button1" value="返回"
						onclick="backButtonClicked()" /></td>
				</tr>
			</table>
		</form>
	</div>
</div>
<div id="box_yy"></div>

