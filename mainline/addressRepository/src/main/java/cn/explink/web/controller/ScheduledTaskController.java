package cn.explink.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import cn.explink.service.ScheduledTaskService;

@RequestMapping("/schedule")
@Controller
public class ScheduledTaskController {

	@Autowired
	private ScheduledTaskService scheduledTaskService;

	@RequestMapping("/processTask")
	public String getAddress(Model model, @RequestParam(value = "taskType", required = false) String taskType) {
		this.scheduledTaskService.scheduleTasks(taskType);
		return "/address/getAddress";
	}

}
