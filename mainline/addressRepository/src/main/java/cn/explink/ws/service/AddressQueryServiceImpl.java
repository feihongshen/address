package cn.explink.ws.service;

import java.util.List;

import javax.jws.WebService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.explink.domain.Address;
import cn.explink.domain.ClientApplication;
import cn.explink.service.AddressService;
import cn.explink.util.AddressUtil;
import cn.explink.util.ApplicationContextUtil;
import cn.explink.ws.vo.AddressQueryResult;
import cn.explink.ws.vo.AddressVo;
import cn.explink.ws.vo.ApplicationVo;
import cn.explink.ws.vo.ResultCodeEnum;

@WebService(endpointInterface = "cn.explink.ws.service.AddressQueryService")
public class AddressQueryServiceImpl extends BaseWebserviceImpl implements AddressQueryService {

	private static Logger logger = LoggerFactory.getLogger(AddressQueryServiceImpl.class);
	
	@Override
	public AddressQueryResult getAddressByParent(ApplicationVo applicationVo, Long parentAddressId) {
		logger.info("getAddressByParent for parentAddressId : {}", parentAddressId);
		AddressQueryResult result = new AddressQueryResult();
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
			List<Address> addressList = addressService.getChildAddressTree(clientApplication.getCustomerId(), parentAddressId);
			List<AddressVo> addressVoList = AddressUtil.cloneToAddressVoList(addressList);
			result.setAddressVoList(addressVoList);
			result.setResultCode(ResultCodeEnum.success);
		} catch (Exception e) {
			logger.error("mappingAddress failed for customerId = {}", clientApplication.getCustomerId(), e);
			result.setResultCode(ResultCodeEnum.failure);
			result.setMessage(e.getMessage());
		}
		return result;
	}

}