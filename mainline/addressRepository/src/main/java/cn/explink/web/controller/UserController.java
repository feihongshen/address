
package cn.explink.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.explink.domain.Customer;
import cn.explink.modle.AjaxJson;
import cn.explink.service.UserService;

@RequestMapping("/user")
@Controller
public class UserController extends BaseController {

    @Autowired
    private UserService userService;

    @RequestMapping("/resetPsd")
    @ResponseBody
    public AjaxJson login(Model model, @RequestParam(value = "oldpass", required = true) String oldpass,
            @RequestParam(value = "password", required = true) String password,
            @RequestParam(value = "cfmpass", required = true) String cfmpass) {
        AjaxJson aj = new AjaxJson();
        try {
            this.userService.resetPsd(oldpass, password);
            aj.setSuccess(true);
            aj.setMsg("密码修改成功！");
        } catch (Exception e) {
            aj.setSuccess(false);
            aj.setMsg(e.getMessage());
        }
        return aj;
    }

    @RequestMapping("/getLogginedCustomer")
    @ResponseBody
    public String getLogginedCustomer() {
        Customer customer = this.getLogginedUser().getCustomer();
        return customer == null ? null : customer.getName();
    }
}
