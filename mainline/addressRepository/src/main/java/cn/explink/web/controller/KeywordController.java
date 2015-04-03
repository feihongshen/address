package cn.explink.web.controller;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.explink.domain.RawAddress;
import cn.explink.service.RawAddressService;
import cn.explink.spliter.vo.FullRawAddressStationPair;

@RequestMapping("/keyword")
@Controller
public class KeywordController extends BaseController {

	private static Logger logger = LoggerFactory.getLogger(KeywordController.class);

	@Autowired
	private RawAddressService rawAddressService;

	@RequestMapping("/keywordMaintain")
	public String addressMapping(Model model) {
		return "address/keywordMaintain";
	}

	@RequestMapping("/loadData")
	@ResponseBody
	public List<RawAddress> loadData(int pageNum, int pageSize, HttpServletRequest request) {
		// try {
		// Map<String, Object> map = this.orderService.getListDataByPage(new
		// OrderVO(), pageNum, pageSize);
		// return (List<OrderVO>) map.get("list");
		// } catch (Exception e) {
		// e.printStackTrace();
		// return new ArrayList<OrderVO>();
		// }
		return null;
	}

	@RequestMapping("/query")
	@ResponseBody
	public Map<String, Object> queryByPage(String address, String station, int pageNum, int pageSize, HttpServletRequest request) {
		Long customerId = this.getCustomerId();
		List<FullRawAddressStationPair> fullRawAddressStationPair = this.rawAddressService.getFullRawAddressStationPair(customerId, address, station, pageNum, pageSize);
		// return this.orderService.getListDataByPage(orderVO, pageNum,
		// pageSize);
		return null;
	}
}
