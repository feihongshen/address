package cn.explink.web.vo;

import java.util.List;

public class DelivererRuleShowVo {
	
	Long delivererId;
	String delivererName;
	Long delivererStationId;
	
 

	public String getDelivererName() {
		return delivererName;
	}

	public void setDelivererName(String delivererName) {
		this.delivererName = delivererName;
	}

 
 
	public Long getDelivererId() {
		return delivererId;
	}

	public void setDelivererId(Long delivererId) {
		this.delivererId = delivererId;
	}

	public Long getDelivererStationId() {
		return delivererStationId;
	}

	public void setDelivererStationId(Long delivererStationId) {
		this.delivererStationId = delivererStationId;
	}



	private List<DelivererStationRuleVo> delivererStationRuleVo;

	public List<DelivererStationRuleVo> getDelivererStationRuleVo() {
		return delivererStationRuleVo;
	}

	public void setDelivererStationRuleVo(
			List<DelivererStationRuleVo> delivererStationRuleVo) {
		this.delivererStationRuleVo = delivererStationRuleVo;
	}
}
