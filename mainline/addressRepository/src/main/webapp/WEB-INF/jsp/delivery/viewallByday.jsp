<%@page import="java.util.Map"%>
<%@page import="java.util.List"%>
<%@page import="cn.explink.web.controller.DeliveryDTO"%>
<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@page import="cn.explink.util.StringUtil"%>
<%
Map deliveryAllMap =(Map) request.getAttribute("deliveryAllMap");
Map deliverySuccessMap =(Map) request.getAttribute("deliverySuccessMap");

%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<HTML>
<HEAD>
<TITLE></TITLE>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" href="<%=request.getContextPath()%>/css/explink.css" type="text/css">
<link rel="stylesheet" href="<%=request.getContextPath()%>/css/redmond/jquery-ui-1.8.18.custom.css" type="text/css" media="all" />
<style type="text/css">

li{ float:left; padding:0px 5px; list-style:none; width: 100px; border:1px dashed blue;float:left;}
.Tab{ border-collapse:collapse;}
.Tab td{ border:solid 1px #000}
</style>
<script src="<%=request.getContextPath()%>/js/jquery-1.7.1.min.js" type="text/javascript"></script>
<script type="text/javascript">

$(function () {
    var chart;
    $(document).ready(function() {
        chart = new Highcharts.Chart({
            chart: {
                renderTo: 'container',
                type: 'column'
            },
            title: {
                text: '各站点投递率统计'
            },
            subtitle: {
                text: '按月统计'
            },
            xAxis: {
                categories: [
                    '一月',
                    '二月',
                    '三月',
                    '四月',
                    '五月',
                    '六月',
                    '七月',
                    '八月',
                    '九月',
                    '十月',
                    '十一月',
                    '十二月'
                ]
            },
            yAxis: {
                min: 0,
                title: {
                    text: '投递率 (%)'
                }
            },
            legend: {
                layout: 'vertical',
                backgroundColor: '#FFFFFF',
                align: 'left',
                verticalAlign: 'top',
                x: 100,
                y: 7,
                floating: true,
                shadow: true
            },
            tooltip: {
                formatter: function() {
                    return ''+
                        this.x +': '+ this.y +' %';
                }
            },
            plotOptions: {
                column: {
                    pointPadding: 0.2,
                    borderWidth: 0
                }
            },
            series: [
                     
                     <% if(!deliverySuccessMap.isEmpty()){%> 
                     
                      {
                    name: '公司',
                     data: [<%for(int j=1;j<13;j++){
                     if(j!=12){%><%= deliverySuccessMap.get(j)%>,<%}
                     else{%><%= deliverySuccessMap.get(j)%><%}}%>]
                 	}
                      <%}%>
                   ]
        });
    });
    
});
$(function () {
    var chart;
    $(document).ready(function() {
        chart = new Highcharts.Chart({
            chart: {
                renderTo: 'container2',
                type: 'column'
            },
            title: {
                text: '各站点投递总数统计'
            },
            subtitle: {
                text: '按月统计'
            },
            xAxis: {
                categories: [
                    '一月',
                    '二月',
                    '三月',
                    '四月',
                    '五月',
                    '六月',
                    '七月',
                    '八月',
                    '九月',
                    '十月',
                    '十一月',
                    '十二月'
                ]
            },
            yAxis: {
                min: 0,
                title: {
                    text: '投递数量'
                }
            },
            legend: {
                layout: 'vertical',
                backgroundColor: '#FFFFFF',
                align: 'left',
                verticalAlign: 'top',
                x: 100,
                y: 7,
                floating: true,
                shadow: true
            },
            tooltip: {
                formatter: function() {
                    return ''+
                        this.x +': '+ this.y;
                }
            },
            plotOptions: {
                column: {
                    pointPadding: 0.2,
                    borderWidth: 0
                }
            },
series: [
                     
                     <% if(!deliveryAllMap.isEmpty()){%> 
                     
                      {
                    name: '公司',
                     data: [<%for(int j=1;j<13;j++){
                     if(j!=12){%><%= deliveryAllMap.get(j)%>,<%}
                     else{%><%= deliveryAllMap.get(j)%><%}}%>]
                 	}
                      <%}%>
                   ]
        });
    });
    
});

</script>
</HEAD>
<BODY>
<body>
<script src="<%=request.getContextPath()%>/js/highcharts/highcharts.js"></script>
<script src="<%=request.getContextPath()%>/js/highcharts/exporting.js"></script>
<script type="text/javascript"	src="<%=request.getContextPath()%>/js/My97DatePicker/WdatePicker.js"></script>
<form id="searchForm" action ="<%=request.getContextPath()%>/delivery/selectAll" method = "post">
查询年份：<input type ="text" name ="date" id="strtime" class="Wdate"  onClick="WdatePicker()" value ="<%=StringUtil.nullConvertToEmptyString(request.getParameter("date")) %>">
<input type ="submit"  value ="查询">
</form>
<div id="container" style="min-width: 400px; height: 280px; margin: 0 auto"></div>
<div id="container2" style="min-width: 400px; height: 280px; margin: 0 auto"></div>

</body>

</BODY>
</HTML>
