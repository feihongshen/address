function initMap(mapLocation,needMatchedWords)
{
	var mapManager=new AR.ExpdopMap();
    mapManager.initializeMap({map:mapLocation});
    mapManager.initializeDeliveryStation();
    var deliverySta=mapManager.getDeliveryStation();
    
	$.ajax({
		 type: "POST",
			url:ctx+"/station/listAll",
			data:{},
			success:function(data){
   				 // 站点数据
   				 deliverySta.setDeliveryStationItems(data);
   				 setTimeout(function(){deliverySta.setViewportToAllStationRegion();},300); 
			}
		});
	if(needMatchedWords==undefined||needMatchedWords==null||needMatchedWords.length==0){
		return;
	}
	$.ajax({
		 type: "POST",
			url:ctx+"/address/getPointByAddress",
			data : {
				needMatched : needMatchedWords
			},
			success : function(returnData) {
				// 地址点 
				var pointLabelArray = new Array(returnData.length);
				for (var i = 0; i < returnData.length; i++) {
					var pointLabel = new Object();
					pointLabel.point = new BMap.Point(returnData[i].lng,
							returnData[i].lat);
					pointLabel.label = returnData[i].addressLine;
					pointLabelArray[i] = pointLabel;
				}
				deliverySta.addAddressMarker(pointLabelArray);
			}
		});
	}