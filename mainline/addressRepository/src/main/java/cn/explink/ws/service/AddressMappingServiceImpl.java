package cn.explink.ws.service;

import java.util.List;

import javax.jws.WebService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.explink.domain.ClientApplication;
import cn.explink.service.AddressService;
import cn.explink.util.ApplicationContextUtil;
import cn.explink.ws.vo.AddressMappingResult;
import cn.explink.ws.vo.ApplicationVo;
import cn.explink.ws.vo.OrderVo;
import cn.explink.ws.vo.ResultCodeEnum;
import cn.explink.ws.vo.SingleAddressMappingResult;

@WebService(endpointInterface = "cn.explink.ws.service.AddressMappingService")
public class AddressMappingServiceImpl extends BaseWebserviceImpl implements AddressMappingService {

	private static Logger logger = LoggerFactory.getLogger(AddressMappingServiceImpl.class);
	
	@Override
	public AddressMappingResult mappingAddress(ApplicationVo applicationVo, List<OrderVo> orderList) {
		AddressMappingResult result = new AddressMappingResult();
		ClientApplication clientApplication = null;
		try {
			clientApplication = validateApplication(applicationVo);
		} catch (Exception e) {
			result.setResultCode(ResultCodeEnum.failure);
			result.setMessage(e.getMessage());
			return result;
		}
		AddressService addressService = ApplicationContextUtil.getBean("addressService");
		try {
			List<SingleAddressMappingResult> singleResultList = addressService.search(clientApplication.getCustomerId(), orderList);
			result.setSingleResultList(singleResultList);
			result.setResultCode(ResultCodeEnum.success);
		} catch (Exception e) {
			logger.error("mappingAddress failed for customerId = {}", clientApplication.getCustomerId(), e);
			result.setResultCode(ResultCodeEnum.failure);
			result.setMessage(e.getMessage());
		}
		return result;
	}

}