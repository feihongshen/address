package cn.explink.web.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import cn.explink.service.AddressService;
import cn.explink.service.DeliveryStationRuleService;
import cn.explink.service.DeliveryStationService;
import cn.explink.service.VendorService;

@RequestMapping("/stationAdressRelation")
@Controller
public class StationAdressRelationController extends BaseController {

	private static Logger logger = LoggerFactory.getLogger(StationAdressRelationController.class);

	@Autowired
	private AddressService addressService;

	@Autowired
	private DeliveryStationRuleService deliveryStationRuleService;

	@Autowired
	private DeliveryStationService deliverySationtService;

	@Autowired
	private VendorService vendorService;

	@RequestMapping("/stationAdressRelation")
	public String index(Model model) {
		return "/address/stationAdressRelation";
	}

}
