package cn.explink.web.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.explink.modle.AjaxJson;
import cn.explink.service.UserService;


@RequestMapping("/user")
@Controller
public class UserController {

	private static Logger logger = LoggerFactory.getLogger(UserController.class);

	@Autowired
	private UserService userService;
	
	
	@RequestMapping("/resetPsd")
	@ResponseBody
	public AjaxJson login(Model model, @RequestParam(value = "oldpass", required = true) String oldpass
			, @RequestParam(value = "password", required = true) String password,
			@RequestParam(value = "cfmpass", required = true) String cfmpass) {
		AjaxJson aj = new AjaxJson();
		try{
			userService.resetPsd(oldpass,password);
			aj.setSuccess(true);
			aj.setMsg("密码修改成功！");
		}catch(Exception e){
			aj.setSuccess(false);
			aj.setMsg(e.getMessage());
		}
		return  aj;
	}
}
