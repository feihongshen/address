package cn.explink.web.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import cn.explink.domain.User;

@RequestMapping("/common")
@Controller
public class CommonController extends BaseController {

	private static Logger logger = LoggerFactory.getLogger(CommonController.class);

	@RequestMapping("/index")
	public String hello(Model model) {
		User user = getLogginedUser();
		logger.info("user = " + user.toString());
		return "/common/index";
	}

}
