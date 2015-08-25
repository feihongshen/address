package cn.explink.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RequestMapping("/login")
@Controller
public class LoginController {

	@RequestMapping("/login")
	public String login(Model model, @RequestParam(value = "name", required = false) String name, @RequestParam(value = "password", required = false) String password) {
		return "/common/login";
	}

}
