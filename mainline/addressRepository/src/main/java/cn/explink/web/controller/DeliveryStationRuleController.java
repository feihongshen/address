package cn.explink.web.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.ListUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.explink.domain.Address;
import cn.explink.domain.DeliveryStation;
import cn.explink.domain.DeliveryStationRule;
import cn.explink.modle.AjaxJson;
import cn.explink.modle.ComboBox;
import cn.explink.service.AddressService;
import cn.explink.service.DeliveryStationRuleService;
import cn.explink.service.DeliveryStationService;
import cn.explink.ws.vo.OrderVo;

@RequestMapping("/deliveryStationRule")
@Controller
public class DeliveryStationRuleController extends BaseController {

	private static Logger logger = LoggerFactory.getLogger(DeliveryStationRuleController.class);

	@Autowired
	private AddressService addressService;

	@Autowired
	private DeliveryStationRuleService deliveryStationRuleService;
	
	@Autowired
	private DeliveryStationService deliverySationtService;

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
	
	@RequestMapping("/saveDeliveryStationRule")
	public @ResponseBody AjaxJson saveDeliveryStationRule(Long addressId,String deliveryStationRule,HttpServletRequest request, HttpServletResponse response) {
		AjaxJson aj=new AjaxJson();
		//TODO GET CUSTOMER FROM USER
		
		try {
		Long customerId=getCustomerId();
		//前台用，拼接参数字段
		String[] dsrkey=deliveryStationRule.split(",");
		for (String key : dsrkey) {
			String[] deliveryStationKey=key.split("#");
			//TODO 批量创建 规则冲突判断
			deliveryStationRuleService.createDeliveryStationRule(addressId, Long.parseLong(deliveryStationKey[0]), customerId, deliveryStationKey[1]);
		}
			
		} catch (Exception e) {
			aj.setSuccess(false);
		} 
		aj.setSuccess(true);
		return aj;
		
	}
	
	@RequestMapping("/station4combobox")
	@ResponseBody
	public List<ComboBox> station4combobox() {
		Long customerId=getCustomerId();
		return deliverySationtService.getAllSationt(customerId);
	}
	
	
	
	
	
	
	
	
}
