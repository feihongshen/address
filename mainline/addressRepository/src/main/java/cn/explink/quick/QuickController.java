package cn.explink.quick;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.explink.domain.DeliveryStation;

@Controller
@RequestMapping("/quick")
public class QuickController {

	@Autowired
	private QuickSerivce quickService = null;

	@ResponseBody
	@RequestMapping("/getfulladdrstatpair")
	public List<FullAddrStationPair> getFullAddrStationPair(int page, int pageSize) {
		return this.getQuickService().getFullAddrStationPair(page, pageSize);
	}

	@ResponseBody
	@RequestMapping("/getalldelstat")
	public List<DeliveryStation> getAllDeliverStation() {
		return this.getQuickService().getAllDeliverStation();
	}

	@RequestMapping("/updateaddrstat")
	public void updateStation(Long addressId, Long stationId) {
		this.getQuickService().updateAddressStation(addressId, stationId);
	}

	private QuickSerivce getQuickService() {
		return this.quickService;
	}

}
