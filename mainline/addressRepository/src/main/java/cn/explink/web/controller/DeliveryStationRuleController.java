package cn.explink.web.controller;

import java.util.ArrayList;
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
import org.springframework.web.bind.annotation.ResponseBody;

import cn.explink.modle.AjaxJson;
import cn.explink.service.AddressService;
import cn.explink.service.DeliveryStationRuleService;
import cn.explink.ws.vo.OrderVo;

@RequestMapping("/deliveryStationRule")
@Controller
public class DeliveryStationRuleController extends BaseController {

	private static Logger logger = LoggerFactory.getLogger(DeliveryStationRuleController.class);

	@Autowired
	private AddressService addressService;

	@Autowired
	private DeliveryStationRuleService deliveryStationRuleService;

	@RequestMapping("/deliveryStationRule")
	public String index(Model model) {
		return "/address/deliveryStationRule";
	}

	

	
	
	@RequestMapping("/parseAdress")
	public @ResponseBody AjaxJson parseAdress(String needMatched,HttpServletRequest request, HttpServletResponse response) {
		AjaxJson aj=new AjaxJson();
		//TODO GET CUSTOMER FROM USER
		Long customerId=getCustomerId();
		List<OrderVo> list=new ArrayList<OrderVo>();
		for(String addressLine : needMatched.split("\n")){
			if(addressLine.trim().length()==0){
				continue;
			}
			OrderVo order=new OrderVo();
			order.setCustomerId(customerId);
			order.setAddressLine(addressLine);
			list.add(order);
		}
		try {
			Map<String, Object> attributes = addressService.match(customerId, list);
			attributes.put("insum", list.size());
			aj.setAttributes(attributes);
			
		} catch (Exception e) {
			aj.setSuccess(false);
			aj.setMsg("匹配异常"+e.getMessage());
		} 
		aj.setSuccess(true);
		aj.setMsg("完成匹配");
		return aj;
		
	}
	
	
	
}
