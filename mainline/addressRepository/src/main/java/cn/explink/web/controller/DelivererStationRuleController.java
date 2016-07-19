
package cn.explink.web.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.explink.modle.AjaxJson;
import cn.explink.service.DelivererRuleService;
import cn.explink.web.vo.DelivererStationRuleVo;

/**
 * 小件员关联维护
 * <p>
 * 类详细描述
 * </p>
 * @author yuxin.he
 * @since 1.0
 */

@RequestMapping("/delivererStationRule")
@Controller
public class DelivererStationRuleController extends BaseController {

    @RequestMapping("/delivererStationRule")
    public String index(Model model) {
        return "/address/delivererStationRulePage";
    }

    @RequestMapping("/getStations")
    public @ResponseBody
    AjaxJson getStations(HttpServletRequest request, HttpServletResponse response) {
        AjaxJson aj = new AjaxJson();
        Long customerId = this.getCustomerId();
        return aj;
    }

    @Autowired
    DelivererRuleService delivererRuleService;

    @RequestMapping("/getAddressByDeliverer")
    @ResponseBody
    public List<DelivererStationRuleVo> getAddressByDeliverer(Long stationId, Long delivererId) {

        List<DelivererStationRuleVo> ls = this.delivererRuleService.getAddressByDeliverer(this.getCustomerId(),
                stationId, delivererId);

        return ls;
    }

    @RequestMapping("/saveDelivererRule")
    @ResponseBody
    public AjaxJson saveDelivererRule(String jsonStr) {
        AjaxJson aj = new AjaxJson();
        try {
            DelivererStationRuleVo ls = (DelivererStationRuleVo) JSONObject.toBean(JSONObject.fromObject(jsonStr),
                    new DelivererStationRuleVo(), new JsonConfig());
            this.delivererRuleService.saveDelivererRule(ls, this.getCustomerId());
            aj.setSuccess(true);
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("ruleId", ls.getRuleId());
            map.put("addressId", ls.getAddressId());
            map.put("rule", ls.getRule());
            aj.setAttributes(map);

        } catch (Exception ex) {
            aj.setSuccess(false);
            aj.setMsg("保存错误,错误内容:" + ex.getMessage());
        }
        return aj;
    }

    @RequestMapping("/checkDelivererRule")
    @ResponseBody
    public AjaxJson checkDelivererRule(Long delivererId, Long stationId, Long addressId, Long ruleId) {
        AjaxJson aj = new AjaxJson();

        return aj;

    }

}
