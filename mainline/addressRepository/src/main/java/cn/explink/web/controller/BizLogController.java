package cn.explink.web.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.explink.service.BizLogService;

@RequestMapping("/bizLogController")
@Controller
public class BizLogController {
	@Autowired
	private BizLogService bizLogService;

	/**
	 *
	 * @Title: findLog
	 * @description 日志查询的初始化界面
	 * @author 刘武强
	 * @date  2015年11月25日下午5:04:48
	 * @param  @param model
	 * @param  @return
	 * @return  String
	 * @throws
	 */
	@RequestMapping("/findLog")
	public String findLog(Model model) {
		return "/address/bizlogSearch";
	}

	/**
	 *
	 * @Title: list
	 * @description 根据参数，查询日志信息方法
	 * @author 刘武强
	 * @date  2015年11月25日下午5:05:10
	 * @param  @param model
	 * @param  @param operationType
	 * @param  @param beginTime
	 * @param  @param endTime
	 * @param  @param operationIP
	 * @param  @param page
	 * @param  @param pageNum
	 * @param  @return
	 * @return  Map<String,Object>
	 * @throws
	 */
	@RequestMapping("/list")
	@ResponseBody
	public Map<String, Object> list(Model model, int operationType, String beginTime, String endTime, String operationIP, int pageNum, int pageSize) {
		Map<String, Object> map = this.bizLogService.findInfo(operationType, beginTime, endTime, operationIP, pageNum, pageSize);
		return map;
	}
}
