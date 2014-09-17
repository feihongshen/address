package cn.explink.web.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.explink.modle.AjaxJson;
import cn.explink.modle.ComboBox;
import cn.explink.modle.DataGrid;
import cn.explink.modle.DataGridReturn;
import cn.explink.service.AddressService;
import cn.explink.service.DeliveryStationRuleService;
import cn.explink.service.DeliveryStationService;
import cn.explink.service.VendorService;
import cn.explink.ws.vo.OrderVo;

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
