//初始数据
var zNodes ;
var leftDivStr ="[]";
var rightDivStr ="[]";

function initStations(){
	 $.ajax({
		 type: "POST",
			url:cxt+"/deliveryStationRule/station4combobox",
			success:function(optionData){
				for(var i=0;i<optionData.length;i++){
					var option=$("  <option value="+optionData[i]['id']+">"+optionData[i]['text']+"</option>");
					$("#sourceStation").append(option);
					var temp=option.clone(true);
					$("#targetStation").append(temp);
					
				}
				//楷书初始化TREE和相关数据
				initDemoTree('sourceStation');
				
			}
		});
}
function initDemoTree(stationName){
	var stationId=$("#"+stationName).val();
	$.ajax({
		 type: "POST",
			url:cxt+"/address/getAdressByStation",
			data:{"stationId":stationId},
			success:function(optionData){
				zNodes=optionData;
				if('sourceStation'==stationName){
					
					zTreeObj1 = $.fn.zTree.init($("#"+stationName+"tree"), demosetting, zNodes);
					
					leftDivStr = "[ ";
					for(var i=0;i<zNodes.length;i++){
						leftDivStr+="{id:"+zNodes[i].id+",pId:"+zNodes[i].pId+",name:\""+zNodes[i].name+"\",open:"+zNodes[i].open+"},";
					}
					leftDivStr = leftDivStr.substring(0,leftDivStr.length-1);
					leftDivStr+="]";
				}else{
					zTreeObj2 = $.fn.zTree.init($("#"+stationName+"tree"), demosetting, zNodes);
					
					rightDivStr = "[ ";
					for(var i=0;i<zNodes.length;i++){
						rightDivStr+="{id:"+zNodes[i].id+",pId:"+zNodes[i].pId+",name:\""+zNodes[i].name+"\",open:"+zNodes[i].open+"},";
					}
					rightDivStr = rightDivStr.substring(0,rightDivStr.length-1);
					rightDivStr+="]";
				}
				
				
				
			}
		});
}

function saveRelation(){
	
	var sourceStationId=$("#sourceStation").val();
	var targetStationId=$("#targetStation").val();
	var leftDiv="";
    var leftDivStrArray = eval(leftDivStr);
    for(var i = 0;i<leftDivStrArray.length;i++){
        leftDiv+= leftDivStrArray[i].id+",";
    }
    var rightDivStrArray = eval(rightDivStr);
    var rightDiv="";
    for(var i = 0;i<rightDivStrArray.length;i++){
        rightDiv+= rightDivStrArray[i].id+",";
    }
    leftDiv = leftDiv.substring(0,leftDiv.length-1);
    rightDiv = rightDiv.substring(0,rightDiv.length-1);
    $.ajax({
		 type: "POST",
			url:cxt+"/deliveryStationRule/changeStationRelation",
			data:{"sourceStationId":sourceStationId,"targetStationId":targetStationId,"sourceAddressId":leftDiv,"targetAddressId":rightDiv},
			success:function(optionData){
				if(optionData.success){
					$('.easyui-layout').layout('expand','north');
				}else{
					alert(optionData.msg);
				}
			}
		});
    
        
}



