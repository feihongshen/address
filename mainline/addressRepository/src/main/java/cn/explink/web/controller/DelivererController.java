
package cn.explink.web.controller;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.explink.modle.ComboBox;
import cn.explink.service.AddressService;
import cn.explink.service.DelivererRuleService;
import cn.explink.service.DelivererService;
import cn.explink.tree.ZTreeNode;

@RequestMapping("/deliverer")
@Controller
public class DelivererController extends BaseController {

    @Autowired
    private DelivererService delivererService;

    @Autowired
    private DelivererRuleService delivererRuleService;

    @Autowired
    private AddressService addressService;

    @RequestMapping("/getDelivererByStation")
    @ResponseBody
    public List<ComboBox> getDelivererByStation(String stationId) {
        Long customerId = this.getCustomerId();
        return this.delivererService.getDelivererComBoxByStation(stationId, customerId);
    }

    
    

    @RequestMapping("/getDelivererByStation2")
    @ResponseBody
    public List<ZTreeNode> getDelivererByStation2(String stationId) {
        Long customerId = this.getCustomerId();
        return this.delivererService.getDelivererZTreeNodeByStation(stationId, customerId);
    }
    
    @RequestMapping("/getAddressByDeliverer")
    @ResponseBody
    public List<ZTreeNode> getAddressByDeliverer(String stationId, String delivererId) {
        Long customerId = this.getCustomerId();
        if (StringUtils.isEmpty(stationId) || StringUtils.isEmpty(delivererId)) {
            return new ArrayList<ZTreeNode>();
        }
        return this.addressService.getAdressByDeliverer(customerId, stationId, delivererId);
    }
    
    
    
 
    
}
