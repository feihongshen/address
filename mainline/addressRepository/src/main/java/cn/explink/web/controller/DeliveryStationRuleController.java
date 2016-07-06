
package cn.explink.web.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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

import cn.explink.dao.BizLogDAO;
import cn.explink.dao.DeliveryStationDao;
import cn.explink.domain.BizLog;
import cn.explink.domain.DeliveryStationRule;
import cn.explink.domain.VendorsAging;
import cn.explink.domain.enums.LogTypeEnum;
import cn.explink.modle.AjaxJson;
import cn.explink.modle.ComboBox;
import cn.explink.modle.DataGrid;
import cn.explink.modle.DataGridReturn;
import cn.explink.service.AddressService;
import cn.explink.service.BizLogService;
import cn.explink.service.DeliveryStationRuleService;
import cn.explink.service.DeliveryStationService;
import cn.explink.service.VendorService;
import cn.explink.util.SynInsertBizLogThread;
import cn.explink.web.vo.DeliveryStationRuleVo;
import cn.explink.web.vo.VendorsAgingVo;
import cn.explink.ws.vo.OrderVo;

@RequestMapping("/deliveryStationRule")
@Controller
public class DeliveryStationRuleController extends BaseController {

    private static final Logger LOGGER = LoggerFactory.getLogger(DeliveryStationRuleController.class);

    @Autowired
    private AddressService addressService;

    @Autowired
    private DeliveryStationRuleService deliveryStationRuleService;

    @Autowired
    private DeliveryStationService deliverySationtService;

    @Autowired
    private VendorService vendorService;

    @Autowired
    private BizLogService bizLogService;

    @Autowired
    private BizLogDAO bizLogDAO;

    @Autowired
    private DeliveryStationDao deliveryStationDao;

    @RequestMapping("/deliveryStationRule")
    public String index(Model model) {
        return "/address/deliveryStationPage";
    }

    @RequestMapping("/parseAdress")
    public @ResponseBody AjaxJson parseAdress(String needMatched, HttpServletRequest request,
            HttpServletResponse response) {
        AjaxJson aj = new AjaxJson();
        // TODO GET CUSTOMER FROM USER
        Long customerId = this.getCustomerId();
        List<OrderVo> list = new ArrayList<OrderVo>();
        for (String addressLine : needMatched.split("\n")) {
            if (addressLine.trim().length() == 0) {
                continue;
            }
            OrderVo order = new OrderVo();
            order.setCustomerId(customerId);
            order.setAddressLine(addressLine);
            list.add(order);
        }
        try {
            Map<String, Object> attributes = this.addressService.txNoneMatch(customerId, list);
            attributes.put("insum", list.size());
            aj.setAttributes(attributes);

        } catch (Exception e) {
            aj.setSuccess(false);
            aj.setMsg("匹配异常" + e.getMessage());
        }
        aj.setSuccess(true);
        aj.setMsg("完成匹配");
        return aj;

    }

    @RequestMapping("/saveDeliveryStationRule")
    public @ResponseBody AjaxJson saveDeliveryStationRule(Long addressId, String deliveryStationRule,
            HttpServletRequest request, HttpServletResponse response) {
        AjaxJson aj = new AjaxJson();
        // TODO GET CUSTOMER FROM USER
        Map map = new HashMap();
        try {
            Long customerId = this.getCustomerId();
            // 前台用，拼接参数字段
            String[] dsrkey = deliveryStationRule.split(",");
            for (String key : dsrkey) {
                String[] deliveryStationKey = key.split("#");
                // TODO 批量创建 规则冲突判断
                try {
                    this.deliveryStationRuleService.createDeliveryStationRule(addressId,
                            Long.parseLong(deliveryStationKey[0]), customerId, deliveryStationKey[1],
                            this.getUserIp(request));
                } catch (Exception e) {
                    map.put(deliveryStationKey[1], e.getMessage());
                    aj.setSuccess(false);
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            aj.setSuccess(false);
        }
        if (!aj.isSuccess()) {
            aj.setAttributes(map);
            return aj;
        }
        aj.setSuccess(true);
        return aj;

    }

    @RequestMapping("/saveDeliveryStationRuleJson")
    public @ResponseBody AjaxJson saveDeliveryStationRuleJson(String jsonStr, HttpServletRequest request,
            HttpServletResponse response) {
        AjaxJson aj = new AjaxJson();
        try {
            JSONArray array = JSONArray.fromObject(jsonStr);
            List<DeliveryStationRuleVo> list = JSONArray.toList(array, new DeliveryStationRuleVo(), new JsonConfig());
            Long customerId = this.getCustomerId();
            if (list != null) {
                this.deliveryStationRuleService
                        .createDeliveryStationRuleList(list, customerId, this.getUserIp(request));
            }
        } catch (Exception e) {
            aj.setSuccess(false);
            aj.setMsg("无效规则");
        }
        return aj;
    }

    @RequestMapping("/saveVendorAge")
    public @ResponseBody AjaxJson saveVendorAge(String jsonStr, HttpServletRequest request, HttpServletResponse response) {
        AjaxJson aj = new AjaxJson();
        aj.setSuccess(true);
        try {
            JSONArray array = JSONArray.fromObject(jsonStr);
            List<VendorsAgingVo> list = JSONArray.toList(array, new VendorsAgingVo(), new JsonConfig());
            ;
            Long customerId = this.getCustomerId();
            if (list != null) {
                this.deliveryStationRuleService.saveVendorAge(list, customerId);
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
        Long customerId = this.getCustomerId();
        return this.deliverySationtService.getAllSationt(customerId);
    }

    @RequestMapping("/vendors4combobox")
    @ResponseBody
    public List<ComboBox> vendors4combobox() {
        Long customerId = this.getCustomerId();
        return this.vendorService.getAllvendor(customerId);
    }

    @RequestMapping("/datagrid")
    public @ResponseBody DataGridReturn datagrid(HttpServletRequest request, HttpServletResponse response,
            DataGrid dataGrid) {
        String addressId = request.getParameter("addressId");
        return this.deliveryStationRuleService.getDataGridReturnView(addressId);

    }

    @RequestMapping("/getAllStationRule")
    public @ResponseBody List<DeliveryStationRuleVo> getAllStationRule(HttpServletRequest request,
            HttpServletResponse response, DataGrid dataGrid) {
        String addressId = request.getParameter("addressId");
        Long custmerId = this.getCustomerId();
        return this.deliveryStationRuleService.getAllStationRule(addressId, custmerId);

    }

    @RequestMapping("/getAges")
    public @ResponseBody List<VendorsAging> getAges(HttpServletRequest request, HttpServletResponse response,
            DataGrid dataGrid) {
        String addressId = request.getParameter("addressId");
        Long custmerId = this.getCustomerId();
        return this.deliveryStationRuleService.getAllVendorAging(addressId, custmerId);
    }

    @RequestMapping("/getMatchTree")
    public @ResponseBody List<Long> getMatchTree(@RequestParam(value = "id", required = false) Long parentId) {
        Long customerId = this.getCustomerId();
        if (parentId == null) {
            parentId = 1L;
        }
        return this.deliveryStationRuleService.getAddressIds(parentId, customerId);
    }

    @RequestMapping("/delete")
    public @ResponseBody AjaxJson delete(Long deliveryStationRuleId, HttpServletRequest request,
            HttpServletResponse response) {
        AjaxJson aj = new AjaxJson();
        DeliveryStationRule deliveryStationRule = this.deliveryStationRuleService.getRuleById(deliveryStationRuleId);
        deliveryStationRule.setAddress(deliveryStationRule.getAddress());
        this.deliveryStationRuleService.delete(deliveryStationRuleId);

        System.out.println(deliveryStationRule.getAddress().getName());
        ExecutorService service = Executors.newCachedThreadPool();
        service.execute(new SynInsertBizLogThread(AddressController.class, this.getCustomerId(), LogTypeEnum.deleteRule
                .getValue(), this.getUserIp(request), deliveryStationRule, this.bizLogDAO, this.bizLogService, null,
                null));
        service.shutdown();
        // DeliveryStationRuleController.LOGGER.info("删除规则结果：{}", "IP:" + this.getUserIp(request) +
        // " deliveryStationRuleId=" + deliveryStationRuleId);
        aj.setSuccess(true);
        return aj;

    }

    @RequestMapping("/deleteVendorAge")
    public @ResponseBody AjaxJson deleteVendorAge(Long id, HttpServletRequest request, HttpServletResponse response) {
        AjaxJson aj = new AjaxJson();
        this.deliveryStationRuleService.deleteVendorAge(id);
        aj.setSuccess(true);
        return aj;

    }

    @RequestMapping("/changeStationRelation")
    public @ResponseBody AjaxJson changeStationRelation(Long sourceStationId, Long targetStationId,
            Long sourceDelivererId, Long targetDelivererId, String sourceAddressId, String targetAddressId,
            HttpServletRequest request, HttpServletResponse response) {
        AjaxJson aj = new AjaxJson();
        // TODO GET CUSTOMER FROM USER
        Long customerId = this.getCustomerId();

        try {
            this.deliveryStationRuleService.changeStationRelation(sourceStationId, targetStationId, sourceDelivererId,
                    targetDelivererId, sourceAddressId, targetAddressId);

            BizLog bizlog = new BizLog();
            bizlog.setSourceStationId(sourceStationId);
            bizlog.setDestStationId(targetStationId);
            bizlog.setSplitCombineAddress(sourceAddressId);
            ExecutorService service = Executors.newCachedThreadPool();
            service.execute(new SynInsertBizLogThread(DeliveryStationRuleController.class, customerId,
                    LogTypeEnum.changeStationRelation.getValue(), this.getUserIp(request), bizlog, this.bizLogDAO,
                    this.bizLogService, null, this.deliverySationtService));
            service.shutdown();
            // DeliveryStationRuleController.LOGGER
            // .info("拆合站：{}", "IP:" + this.getUserIp(request) + "  sourceStationId=" + sourceStationId +
            // "  targetStationId=" + targetStationId + "  sourceAddressId=" + sourceAddressId + "  targetAddressId=" +
            // targetAddressId);
        } catch (Exception e) {
            aj.setSuccess(false);
            aj.setMsg("异常" + e.getMessage());
        }
        aj.setSuccess(true);
        aj.setMsg("完成匹配");
        return aj;

    }
}
