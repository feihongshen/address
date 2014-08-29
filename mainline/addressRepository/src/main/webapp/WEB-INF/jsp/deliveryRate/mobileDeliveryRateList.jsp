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

<table width="100%" border="0" cellspacing="1" cellpadding="2" class="table_2">
	<tr class="font_1">
		<td width="100" align="center" bgcolor="#F4F4F4">条件名</td>
		<td width="100" align="center" bgcolor="#F4F4F4">查询类型</td>
		<td bgcolor="#F4F4F4">条件内容</td>
		<td width="100" bgcolor="#F4F4F4">操作</td>
	</tr>
	<c:forEach items="${deliveryRateConditionList}" varStatus="i" var="deliveryRateConditionWrapper">
		<tr>
			<td width="100" align="center" bgcolor="#F4F4F4">${deliveryRateConditionWrapper.deliveryRateCondition.name}</td>
			<td width="100" align="center" bgcolor="#F4F4F4">
				<c:if test="${deliveryRateConditionWrapper.deliveryRateCondition.selectType == 1}">
					站点
				</c:if>
				<c:if test="${deliveryRateConditionWrapper.deliveryRateCondition.selectType == 2}">
					供货商
				</c:if>
			</td>
			<td bgcolor="#F4F4F4">${deliveryRateConditionWrapper.query}</td>
			<td width="100" bgcolor="#F4F4F4">
				<c:choose>
					<c:when test="${deliveryRateConditionWrapper.deliveryRateCondition.status == 1}">
						[<a href="javascript:deactiveCondition(${deliveryRateConditionWrapper.deliveryRateCondition.id});">停用</a>]
					</c:when>
					<c:otherwise>
						[<a href="javascript:activeCondition(${deliveryRateConditionWrapper.deliveryRateCondition.id});">启用</a>]
					</c:otherwise>
				</c:choose>
				[<a href="javascript:preSaveDeliveryRateCondition(${deliveryRateConditionWrapper.deliveryRateCondition.id});">设置</a>]
			</td>
		</tr>
	</c:forEach>
</table>

