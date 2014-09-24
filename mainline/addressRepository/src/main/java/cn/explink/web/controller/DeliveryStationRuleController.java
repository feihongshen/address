package cn.explink.web.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JsonConfig;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.explink.domain.VendorsAging;
import cn.explink.modle.AjaxJson;
import cn.explink.modle.ComboBox;
import cn.explink.modle.DataGrid;
import cn.explink.modle.DataGridReturn;
import cn.explink.service.AddressService;
import cn.explink.service.DeliveryStationRuleService;
import cn.explink.service.DeliveryStationService;
import cn.explink.service.VendorService;
import cn.explink.web.vo.DeliveryStationRuleVo;
import cn.explink.web.vo.VendorsAgingVo;
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
	
	@Autowired
	private VendorService vendorService;

	@RequestMapping("/deliveryStationRule")
	public String index(Model model) {
		return "/address/deliveryStationPage";
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
		Map map=new HashMap();
		try {
			Long customerId=getCustomerId();
			//前台用，拼接参数字段
			String[] dsrkey=deliveryStationRule.split(",");
			for (String key : dsrkey) {
				String[] deliveryStationKey=key.split("#");
				//TODO 批量创建 规则冲突判断
				try {
					deliveryStationRuleService.createDeliveryStationRule(addressId, Long.parseLong(deliveryStationKey[0]), customerId, deliveryStationKey[1]);
				} catch (Exception e) {
					map.put(deliveryStationKey[1], e.getMessage());
					aj.setSuccess(false);
					e.printStackTrace();
				}
			}
		} catch (Exception e) {
			aj.setSuccess(false);
		} 
		if(!aj.isSuccess()){
			aj.setAttributes(map);
			return aj;
		}
		aj.setSuccess(true);
		return aj;
		
	}
	@RequestMapping("/saveDeliveryStationRuleJson")
	public @ResponseBody AjaxJson saveDeliveryStationRuleJson(String jsonStr,HttpServletRequest request, HttpServletResponse response) {
		AjaxJson aj=new AjaxJson();
		//TODO GET CUSTOMER FROM USER
		String msg = "";
		try {
			JSONArray array =  JSONArray.fromObject(jsonStr);
			List<DeliveryStationRuleVo> list = JSONArray.toList(array, new DeliveryStationRuleVo(), new JsonConfig());;
			Long customerId=getCustomerId();
			if(list!=null){
				for(DeliveryStationRuleVo r:list){
					try {
						deliveryStationRuleService.createDeliveryStationRule(r.getAddressId(), r.getStationId(), customerId, r.getRuleExpression());
					} catch (Exception e) {
						msg+=r.getStationId()+":"+e.getMessage();
						aj.setSuccess(false);
						aj.setMsg(msg);
						e.printStackTrace();
					}
				}
			}
		} catch (Exception e) {
			aj.setSuccess(false);
			aj.setMsg(e.getMessage());
		} 
		return aj;
	}
	
	@RequestMapping("/saveVendorAge")
	public @ResponseBody AjaxJson saveVendorAge(String jsonStr,HttpServletRequest request, HttpServletResponse response) {
		AjaxJson aj=new AjaxJson();
		aj.setSuccess(true);
		try {
			JSONArray array =  JSONArray.fromObject(jsonStr);
			List<VendorsAgingVo> list = JSONArray.toList(array, new VendorsAgingVo(), new JsonConfig());;
			Long customerId=getCustomerId();
			if(list!=null){
				deliveryStationRuleService.saveVendorAge(list,customerId);
			}
		} catch (Exception e) {
			aj.setSuccess(false);
			aj.setMsg(e.getMessage());
		} 
		return aj;
	}
	
	@RequestMapping("/station4combobox")
	@ResponseBody
	public List<ComboBox> station4combobox() {
		Long customerId=getCustomerId();
		return deliverySationtService.getAllSationt(customerId);
	}
	@RequestMapping("/vendors4combobox")
	@ResponseBody
	public List<ComboBox> vendors4combobox() {
		Long customerId=getCustomerId();
		return vendorService.getAllvendor(customerId);
	}
	
	@RequestMapping("/datagrid")
	public @ResponseBody DataGridReturn datagrid(HttpServletRequest request, HttpServletResponse response, DataGrid dataGrid) {
		String addressId=request.getParameter("addressId");
		return this.deliveryStationRuleService.getDataGridReturnView(addressId);
		
	}
	@RequestMapping("/getAllStationRule")
	public @ResponseBody List<DeliveryStationRuleVo> getAllStationRule(HttpServletRequest request, HttpServletResponse response, DataGrid dataGrid) {
		String addressId=request.getParameter("addressId");
		Long custmerId = this.getCustomerId();
		return this.deliveryStationRuleService.getAllStationRule(addressId,custmerId);
		
	}
	@RequestMapping("/getAges")
	public @ResponseBody List<VendorsAging> getAges(HttpServletRequest request, HttpServletResponse response, DataGrid dataGrid) {
		String addressId=request.getParameter("addressId");
		Long custmerId = this.getCustomerId();
		return this.deliveryStationRuleService.getAllVendorAging(addressId,custmerId);
	}
	
	@RequestMapping("/getMatchTree")
	public @ResponseBody List<Long>  getMatchTree( @RequestParam(value = "id", required = false) Long parentId) {
		Long customerId=getCustomerId();
		if(parentId==null){
			parentId=1L;
		}
		return deliveryStationRuleService.getAddressIds(parentId,customerId);
	}
	
	@RequestMapping("/delete")
	public @ResponseBody AjaxJson delete(Long deliveryStationRuleId,HttpServletRequest request, HttpServletResponse response) {
		AjaxJson aj=new AjaxJson();
		deliveryStationRuleService.delete(deliveryStationRuleId);
		aj.setSuccess(true);
		return aj;
		
	}
	@RequestMapping("/deleteVendorAge")
	public @ResponseBody AjaxJson deleteVendorAge(Long id,HttpServletRequest request, HttpServletResponse response) {
		AjaxJson aj=new AjaxJson();
		deliveryStationRuleService.deleteVendorAge(id);
		aj.setSuccess(true);
		return aj;
		
	}
	@RequestMapping("/changeStationRelation")
	public @ResponseBody AjaxJson changeStationRelation(Long sourceStationId,Long targetStationId,
			String sourceAddressId,String targetAddressId,HttpServletRequest request, HttpServletResponse response) {
		AjaxJson aj=new AjaxJson();
		//TODO GET CUSTOMER FROM USER
		Long customerId=getCustomerId();
		
		try {
			deliveryStationRuleService.changeStationRelation(sourceStationId, targetStationId, sourceAddressId, targetAddressId);
		} catch (Exception e) {
			aj.setSuccess(false);
			aj.setMsg("异常"+e.getMessage());
		} 
		aj.setSuccess(true);
		aj.setMsg("完成匹配");
		return aj;
		
	}
	
	
	
	
	
	
}
