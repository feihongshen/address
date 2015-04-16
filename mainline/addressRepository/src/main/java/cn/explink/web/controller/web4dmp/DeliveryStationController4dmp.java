package cn.explink.web.controller.web4dmp;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.explink.domain.DeliveryStation;
import cn.explink.web.controller.DeliveryStationController;

@RequestMapping("/station4dmp")
@Controller
public class DeliveryStationController4dmp extends DeliveryStationController {

	@RequestMapping("/getAllDeliveryStation")
	public @ResponseBody String getAllDeliveryStation(HttpServletRequest request, HttpServletResponse response) {
		List<DeliveryStation> deliveryStationList = this.getDeliveryStationService().listAll(this.getCustomerId());
		String callback = request.getParameter("callback");

		ObjectMapper objectMapper = new ObjectMapper();
		String deliveryStationListJson = "";

		try {
			deliveryStationListJson = objectMapper.writeValueAsString(deliveryStationList);
		} catch (JsonGenerationException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return callback + "(" + deliveryStationListJson + ");";
	}
}
