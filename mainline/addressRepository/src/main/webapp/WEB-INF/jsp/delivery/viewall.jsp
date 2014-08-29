<%@page import="java.util.Map"%>
<%@page import="java.util.List"%>
<%@page import="cn.explink.web.controller.DeliveryDTO"%>
<%@page import="cn.explink.web.controller.EmaildateTDO"%>
<%@page import="cn.explink.domain.Customer"%>
<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@page import="cn.explink.util.StringUtil"%>
<%
Map deliveryAllMap =(Map) request.getAttribute("deliveryAllMap");
Map deliverySuccessMap =(Map) request.getAttribute("deliverySuccessMap");
String dayStr =(String) request.getAttribute("dayStr");
String dayStrlist =(String) request.getAttribute("dayStrList");
List<EmaildateTDO> emailList =(List<EmaildateTDO>) request.getAttribute("emailList");
List<Customer> cumstrList =(List<Customer>) request.getAttribute("cumstrList");
List cumstrList1 =(List) request.getAttribute("cumstrList1");
List<Customer> cumstrListAll =(List<Customer>) request.getAttribute("cumstrListAll");
String[] dayS = dayStrlist.split(",");
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<HTML>
<HEAD>
<TITLE></TITLE>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" href="<%=request.getContextPath()%>/css/explink.css" type="text/css">
<link href="<%=request.getContextPath()%>/js/multiSelcet/jquery.multiSelect.css" rel="stylesheet" type="text/css" />
<script src="<%=request.getContextPath()%>/js/jquery-1.7.1.min.js" type="text/javascript"></script>
<script src="<%=request.getContextPath()%>/js/multiSelcet/jquery.multiSelect.js" type="text/javascript"></script>
<script src="<%=request.getContextPath()%>/js/multiSelcet/jquery.bgiframe.min.js" type="text/javascript"></script>
<script src="<%=request.getContextPath()%>/js/highcharts/highcharts.js"></script>
<script src="<%=request.getContextPath()%>/js/highcharts/exporting.js"></script>

<link rel="stylesheet" href="<%=request.getContextPath()%>/css/smoothness/jquery-ui-1.8.18.custom.css" type="text/css" media="all" />
<script src="<%=request.getContextPath()%>/js/jquery-ui-1.8.18.custom.min.js" type="text/javascript"></script>
<script src="<%=request.getContextPath()%>/js/jquery.ui.datepicker-zh-CN.js" type="text/javascript"></script>
<script src="<%=request.getContextPath()%>/js/jquery-ui-timepicker-addon.js" type="text/javascript"></script>
<script src="<%=request.getContextPath()%>/js/jquery.ui.message.min.js" type="text/javascript"></script>
<script src="<%=request.getContextPath()%>/js/multiSelcet/MyMultiSelect.js" type="text/javascript"></script>


<script>
$(function() {
	$("#strtime").datepicker();
	$("#endtime").datepicker();
	
});
</script>
<script type="text/javascript">

        $(document).ready(function() {
            // Options displayed in comma-separated list
            $("#control_7").multiSelect({ oneOrMoreSelected: '*' });
            $("#control_8").multiSelect({ oneOrMoreSelected: '*' });
   
   //获取下拉框的值
   $("#btnval").click(function(){
        var checkval="";
        $("label").each(function(index){
     //找到被选中的项
           if($(this).attr("class")=="checked"){
        checkval+=$(this).children().attr("value")+",";
     }
        });
        $("#controlStr").val(checkval);
        if(check()){
        $("#searchForm").submit();
        }
   });
        });
   
    </script>
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
                text: '投递率统计'
            },
            subtitle: {
                text: '按天统计'
            },
            xAxis: {
                categories: [
                    
                    <%=dayStr %>
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
                     
				<% if(!deliverySuccessMap.isEmpty()){
					if(cumstrList != null && cumstrList.size()>0){%>
				<% for (int i=0;i<cumstrList.size();i++) {
				if(i != cumstrList.size()-1){
				%>
				 {
				name: '<%=((Customer)cumstrList.get(i)).getCustomername()%>',
				data: [
				<%for(int j=0;j<dayS.length;j++){
				if(j!=dayS.length-1){%><%= ((Map)deliverySuccessMap.get(dayS[j])).get(((Customer)cumstrList.get(i)).getCustomerid())%>,<%}
				else{%><%= ((Map)deliverySuccessMap.get(dayS[j])).get(((Customer)cumstrList.get(i)).getCustomerid())%><%}}%>]
				},
				 <%}
				else{%>
				{
				    name: '<%=((Customer)cumstrList.get(i)).getCustomername()%>',
				    data: [
				    <%for(int j=0;j<dayS.length;j++){
				    if(j!=dayS.length-1){%><%= ((Map)deliverySuccessMap.get(dayS[j])).get(((Customer)cumstrList.get(i)).getCustomerid())%>,<%}
				    else{%><%= ((Map)deliverySuccessMap.get(dayS[j])).get(((Customer)cumstrList.get(i)).getCustomerid())%><%}}%>]
					}
				<%}}%>
				<%}}%>
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
                text: '入库数据量'
            },
            subtitle: {
                text: '按天统计（默认统计当天）'
            },
            xAxis: {
                categories: [
                    
                    <%=dayStr %>
                ]
            },
            yAxis: {
                min: 0,
                title: {
                    text: '入库总数据'
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
                     
					<% if(!deliveryAllMap.isEmpty()){
						if(cumstrList != null && cumstrList.size()>0){%>
					<% for (int i=0;i<cumstrList.size();i++) {
					if(i != cumstrList.size()-1){
					%>
					 {
					name: '<%=((Customer)cumstrList.get(i)).getCustomername()%>',
					data: [
					<%for(int j=0;j<dayS.length;j++){
					if(j!=dayS.length-1){%><%= ((Map)deliveryAllMap.get(dayS[j])).get(((Customer)cumstrList.get(i)).getCustomerid())%>,<%}
					else{%><%= ((Map)deliveryAllMap.get(dayS[j])).get(((Customer)cumstrList.get(i)).getCustomerid())%><%}}%>]
					},
					 <%}
					else{%>
					{
					    name: '<%=((Customer)cumstrList.get(i)).getCustomername()%>',
					    data: [
					    <%for(int j=0;j<dayS.length;j++){
					    if(j!=dayS.length-1){%><%= ((Map)deliveryAllMap.get(dayS[j])).get(((Customer)cumstrList.get(i)).getCustomerid())%>,<%}
					    else{%><%= ((Map)deliveryAllMap.get(dayS[j])).get(((Customer)cumstrList.get(i)).getCustomerid())%><%}}%>]
						}
					<%}}}%>
					<%}%>
                   ]
        });
    });
    
});

</script>
<script type="text/javascript">
//根据滚动条滚动时间刷新漂浮框的xy轴
function reIframeTopAndBottomXY(){
	var st = document.documentElement.scrollTop;
	if(st==0){st=document.body.scrollTop;}
	var ch =  document.documentElement.clientHeight;
	if(ch==0){ch=document.body.clientHeight;}
	if($(".iframe_bottom")){
		$(".iframe_bottom").css("top", st+ch-40);
	}
	if($("#scroll")){
		$("#scroll").css("height", st+ch-130);
	}
	if($(".form_btnbox")){
		$(".form_btnbox").css("top", st+ch-75);
	}
	if($(".inputselect_box")){
		$(".inputselect_box").css("top", st);
	}
	if($(".list_topbar")){
		$(".list_topbar").css("top", st);
	}
	
}
$(window).resize(function() {
	centerBox();
	reIframeTopAndBottomXY();
	});
$(window).scroll(function(event){
	reIframeTopAndBottomXY();
});
$(window).load(function(event){
	reIframeTopAndBottomXY();
});

</script>
    <style type="text/css">
        HTML
        {
            font-family: Arial, Helvetica, sans-serif;
            font-size: 12px;
        }
        H2
        {
            font-size: 14px;
            font-weight: bold;
            margin: 1em 0em .25em 0em;
        }
        P
        {
         margin-top:20px;
        }
		.inputselect_box {
	padding: 5px 10px;
	position: absolute;
	z-index: 9;
	width: 100%;
	height:60px;
	background: url(../images/repeatx.png) repeat-x 0 -485px;
	border: 1px solid #86AFD5
}
    </style>
</HEAD>
<BODY>
    <div class="inputselect_box">
	<form id="searchForm" action ="<%=request.getContextPath()%>/delivery/selectAll" method = "post">
     供货商（可多选）：<select id="control_7" name="control_7[]" multiple="multiple" style="width: 320px;">
           <%if(cumstrListAll != null && cumstrListAll.size()>0){ %>
           <%for( Customer c:cumstrListAll){ %>
            <option value="<%= c.getCustomerid()%>" <%if(!cumstrList1.isEmpty()) 
            {for(int i=0;i<cumstrList1.size();i++){
            	if(c.getCustomerid() == new Long(cumstrList1.get(i).toString())){
            		%>selected="selected"<%
            	 break;
            	}
            }
             }%>><%=c.getCustomername()%></option>
            <%} }%>
        </select>
        [<a href="javascript:multiSelectAll('control_7[]',1,'请选择');">全选</a>]
    	[<a href="javascript:multiSelectAll('control_7[]',0,'请选择');">取消全选</a>]
        
          投递率：<select name ="timeLong" id="timeLong">
			<option value ="-1" <%if( new Long(request.getParameter("timeLong")==null?"-1":request.getParameter("timeLong"))==-1) {%>selected="selected"<%} %>>不限</option>
			<option value ="24" <%if( new Long(request.getParameter("timeLong")==null?"-1":request.getParameter("timeLong"))==24) {%>selected="selected"<%} %> >24小时内</option>
			<option value ="36" <%if( new Long(request.getParameter("timeLong")==null?"-1":request.getParameter("timeLong"))==36) {%>selected="selected"<%} %>>36小时内</option>
			<option value ="48" <%if( new Long(request.getParameter("timeLong")==null?"-1":request.getParameter("timeLong"))==48) {%>selected="selected"<%} %> >48小时内</option>
			<option value ="60" <%if( new Long(request.getParameter("timeLong")==null?"-1":request.getParameter("timeLong"))==60) {%>selected="selected"<%} %>>60小时内</option>
           </select>
          <select name ="flowordertype" id="flowordertypeid">
			<option value ="1" <%if( new Long(request.getParameter("flowordertype")==null?"1":request.getParameter("flowordertype"))==1) {%>selected="selected"<%} %> >妥投率</option>
			<option value ="2" <%if( new Long(request.getParameter("flowordertype")==null?"1":request.getParameter("flowordertype"))==2) {%>selected="selected"<%} %>>有结果率</option>
           </select>
<br>
   
   <select id="datetype" name ="datetype">
            <option value="1" <%if( new Long(request.getParameter("datetype")==null?"1":request.getParameter("datetype"))==1) {%>selected="selected"<%} %>>发货时间</option>
            <option value="2" <%if( new Long(request.getParameter("datetype")==null?"1":request.getParameter("datetype"))==2) {%>selected="selected"<%} %>>入库时间</option>
            <option value="3" <%if( new Long(request.getParameter("datetype")==null?"1":request.getParameter("datetype"))==3) {%>selected="selected"<%} %>>到站时间</option>
		</select>
   <input type ="text" name ="stratedate" id="strtime" value ="<%=StringUtil.nullConvertToEmptyString(request.getAttribute("stratedate")) %>">
    到<input type ="text" name ="enddate" id="endtime"  value ="<%=StringUtil.nullConvertToEmptyString(request.getAttribute("enddate")) %>">
           
    批次号： <select id="control_8" name="control_8[]" multiple="multiple" style="width: 320px;">
            <%if(emailList != null &&emailList.size()>0){ %>
            <%for( EmaildateTDO e:emailList){ %>
            <option value="#-<%= e.getEmaildateid()%>"
           <%if((request.getParameter("controlStr")==null?"":request.getParameter("controlStr")).indexOf("#-"+e.getEmaildateid()+",") > -1 ){ %> selected="selected"<%} %>
            ><%=e.getEmaildate()%></option>
            <%}} %>
        </select>
         <input type ="button"  value ="统计" id="btnval">

   
   <input type="hidden" id="controlStr" name="controlStr" value=""/>

 </form>
 </div>
<div style="height:70px; "></div>
<div id="container" style="min-width: 400px; height: 280px; margin: 0 auto" ></div>
<div id="container2" style="min-width: 400px; height: 280px; margin: 0 auto"></div>
<script type="text/javascript">
function check(){
	if($("#strtime").val()>$("#endtime").val()){
		alert("开始时间不能大于结束时间");
		return false;
	}
	else{
		return true;
	}
}


</script>


</BODY>
</HTML>
