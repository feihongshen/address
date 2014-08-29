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
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<html>

<head>
<div></div>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<link rel="stylesheet" href="<%=request.getContextPath()%>/css/2.css"
	type="text/css" />
<link rel="stylesheet"
	href="<%=request.getContextPath()%>/css/index.css" type="text/css" />
<link rel="stylesheet"
	href="<%=request.getContextPath()%>/css/reset.css" type="text/css" />
<script src="<%=request.getContextPath()%>/js/jquery-1.7.1.min.js"
	type="text/javascript"></script>
<script src="<%=request.getContextPath()%>/js/highcharts/highcharts.js"></script>
<script src="<%=request.getContextPath()%>/js/highcharts/exporting.js"></script>
<script
	src="<%=request.getContextPath()%>/js/multiSelcet/jquery.multiSelect.js"
	type="text/javascript"></script>
<script
	src="<%=request.getContextPath()%>/js/multiSelcet/jquery.bgiframe.min.js"
	type="text/javascript"></script>
<link
	href="<%=request.getContextPath()%>/js/multiSelcet/jquery.multiSelect.css"
	rel="stylesheet" type="text/css" />

<link rel="stylesheet"
	href="<%=request.getContextPath()%>/css/smoothness/jquery-ui-1.8.18.custom.css"
	type="text/css" media="all" />
<script
	src="<%=request.getContextPath()%>/js/jquery-ui-1.8.18.custom.min.js"
	type="text/javascript"></script>
<script
	src="<%=request.getContextPath()%>/js/jquery.ui.datepicker-zh-CN.js"
	type="text/javascript"></script>
<script
	src="<%=request.getContextPath()%>/js/jquery-ui-timepicker-addon.js"
	type="text/javascript"></script>
<script src="<%=request.getContextPath()%>/js/jquery.ui.message.min.js"
	type="text/javascript"></script>
<script
	src="<%=request.getContextPath()%>/js/multiSelcet/MyMultiSelect.js"
	type="text/javascript"></script>
<script src="<%=request.getContextPath()%>/js/js.js"
	type="text/javascript"></script>
<script language="javascript">
	<c:set var="dateSize" value="${fn:length(drAgg.allDate)}" />
	<c:set var="timeTypeSize" value="${fn:length(drAgg.timeTypes)}" />
	
	var categories = [];
	<c:choose>
		<c:when test="${bocId == null}">
			<c:forEach items="${drAgg.timeTypes}" varStatus="i" var="timeType">
				categories[${i.index}] = '${timeType.desc}';
			</c:forEach>			
		</c:when>
		<c:otherwise>
			<c:forEach items="${drAgg.allDate}" varStatus="i" var="date">
				categories[${i.index}] = '${date}';
			</c:forEach>
			categories[${dateSize}] = '整体';
		</c:otherwise>
	</c:choose>
	
	
	var chart; // 全局变量
	$(document).ready(function() {
		chart = new Highcharts.Chart({
			chart : {
				renderTo : 'container',
				//type : 'bar'
				//type: 'column'//竖柱形图
				//type: 'line'	//折线图
				type: '${type}'
			},
			plotOptions: {
	            series: {
	                dataLabels: {
	                	enabled: true,
	    				format: '{y}%',
	    				<c:if test="${type == 'bar'}">
		    				x: 10,
	    					y: 0	
	    				</c:if>
		                <c:if test="${type == 'column'}">
		    				x: 0,
							y: -10	
						</c:if>
		                <c:if test="${type == 'line'}">
		    				x: 0,
							y: -10	
						</c:if>
	                }
	            }
	        },
			title : {
				text : '${title}'
			},
			xAxis : {
				categories : categories
			},
			yAxis : {
				title : {
					text : ''
				}
			},
			series : [
				<c:choose>
					<c:when test="${bocId == null}">
						<c:forEach items="${dateAggMap}" varStatus="i" var="dateAggPair">
							<c:if test="${i.index != '0'}">,</c:if>
							{
								name: '${dateAggPair.key}',
								data: [
									<c:forEach items="${drAgg.timeTypes}" varStatus="j" var="timeType">
										<c:if test="${j.index != '0'}">,</c:if>
										<c:choose>
											<c:when test="${dateAggPair.value != null}">
												${fn:replace(dateAggPair.value.timeTypeMap[timeType].rate, '%', '')}
											</c:when>
											<c:otherwise>
												0
											</c:otherwise>
										</c:choose>
									</c:forEach>
								]
							}
						</c:forEach>
					</c:when>
					<c:otherwise>
				  		<c:forEach items="${drAgg.timeTypes}" varStatus="i" var="timeType">
							<c:if test="${i.index != '0'}">,</c:if>
							{
								name: '${timeType.desc}',
								data: [
									<c:forEach items="${drAgg.allDate}" varStatus="j" var="date">
										<c:if test="${j.index != '0'}">,</c:if>
										<c:set var="dateAggMap" value="${bocAgg.dateAggMap}" />
										<c:choose>
											<c:when test="${bocAgg.dateAggMap[date] != null}">
												${fn:replace(bocAgg.dateAggMap[date].timeTypeMap[timeType].rate, '%', '')}
											</c:when>
											<c:otherwise>
												0
											</c:otherwise>
										</c:choose>
									</c:forEach>
									, ${fn:replace(bocAgg.total.timeTypeMap[timeType].rate, '%', '')}
								]
							}
						</c:forEach>
					</c:otherwise>
				</c:choose>
				
			]
		});
		
	});
</script>

</head>
<script type="text/javascript">
	
</script>
<body style="background: #eef9ff" marginwidth="0" marginheight="0">
	<div style="position:relative;overflow-x:auto;overflow-y:auto" >
		<div id="container" style="width: 100%; height: 100%"></div>
	</div>
</body>
</html>

