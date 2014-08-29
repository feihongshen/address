package cn.explink.web.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import cn.explink.service.ScheduledTaskService;

@RequestMapping("/login")
@Controller
public class LoginController {

	private static Logger logger = LoggerFactory.getLogger(LoginController.class);

	@RequestMapping("/login")
	public String login(Model model, @RequestParam(value = "name", required = false) String name
			, @RequestParam(value = "password", required = false) String password) {
		return "/common/login";
	}
	
}
