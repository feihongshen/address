package cn.explink.ws.service;

import java.util.List;

import javax.jws.WebService;

import cn.explink.domain.ClientApplication;
import cn.explink.domain.Deliverer;
import cn.explink.domain.DelivererRule;
import cn.explink.domain.DeliveryStation;
import cn.explink.domain.Vendor;
import cn.explink.service.DelivererRuleService;
import cn.explink.service.DelivererService;
import cn.explink.service.DeliveryStationService;
import cn.explink.service.VendorService;
import cn.explink.util.ApplicationContextUtil;
import cn.explink.ws.vo.AddressSyncServiceResult;
import cn.explink.ws.vo.ApplicationVo;
import cn.explink.ws.vo.DelivererRuleVo;
import cn.explink.ws.vo.DelivererVo;
import cn.explink.ws.vo.DeliveryStationVo;
import cn.explink.ws.vo.ResultCodeEnum;
import cn.explink.ws.vo.VendorVo;

@WebService(endpointInterface = "cn.explink.ws.service.AddressSyncService")
public class AddressSyncServiceImpl extends BaseWebserviceImpl implements AddressSyncService {

	@Override
	public AddressSyncServiceResult createDeliveryStation(ApplicationVo applicationVo, DeliveryStationVo deliveryStationVo) {
		AddressSyncServiceResult result = new AddressSyncServiceResult();
		ClientApplication clientApplication = null;
		try {
			clientApplication = validateApplication(applicationVo);
		} catch (Exception e) {
			result.setResultCode(ResultCodeEnum.failure);
			result.setMessage(e.getMessage());
			return result;
		}
		deliveryStationVo.setCustomerId(clientApplication.getCustomerId());

		DeliveryStationService deliveryStationService = ApplicationContextUtil.getBean("deliveryStationService");
		try {
			DeliveryStation deliveryStation = deliveryStationService.createDeliveryStation(deliveryStationVo);
			result.setResultCode(ResultCodeEnum.success);
			result.setReferenceId(deliveryStation.getId());
		} catch (Exception e) {
			result.setResultCode(ResultCodeEnum.failure);
			result.setMessage(e.getMessage());
		}
		return result;
	}

	@Override
	public AddressSyncServiceResult updateDeliveryStation(ApplicationVo applicationVo, DeliveryStationVo deliveryStationVo) {
		AddressSyncServiceResult result = new AddressSyncServiceResult();
		ClientApplication clientApplication = null;
		try {
			clientApplication = validateApplication(applicationVo);
		} catch (Exception e) {
			result.setResultCode(ResultCodeEnum.failure);
			result.setMessage(e.getMessage());
			return result;
		}
		deliveryStationVo.setCustomerId(clientApplication.getCustomerId());

		DeliveryStationService deliveryStationService = ApplicationContextUtil.getBean("deliveryStationService");
		try {
			DeliveryStation deliveryStation = deliveryStationService.updateDeliveryStation(deliveryStationVo);
			result.setResultCode(ResultCodeEnum.success);
			result.setReferenceId(deliveryStation.getId());
		} catch (Exception e) {
			result.setResultCode(ResultCodeEnum.failure);
			result.setMessage(e.getMessage());
		}

		return result;
	}

	@Override
	public AddressSyncServiceResult deleteDeliveryStation(ApplicationVo applicationVo, DeliveryStationVo deliveryStationVo) {
		AddressSyncServiceResult result = new AddressSyncServiceResult();
		ClientApplication clientApplication = null;
		try {
			clientApplication = validateApplication(applicationVo);
		} catch (Exception e) {
			result.setResultCode(ResultCodeEnum.failure);
			result.setMessage(e.getMessage());
			return result;
		}
		deliveryStationVo.setCustomerId(clientApplication.getCustomerId());

		DeliveryStationService deliveryStationService = ApplicationContextUtil.getBean("deliveryStationService");
		try {
			DeliveryStation deliveryStation = deliveryStationService.deleteDeliveryStation(deliveryStationVo);
			result.setResultCode(ResultCodeEnum.success);
			result.setReferenceId(deliveryStation.getId());
		} catch (Exception e) {
			result.setResultCode(ResultCodeEnum.failure);
			result.setMessage(e.getMessage());
		}

		return result;
	}

	@Override
	public AddressSyncServiceResult createVendor(ApplicationVo applicationVo, VendorVo vendorVo) {
		AddressSyncServiceResult result = new AddressSyncServiceResult();
		ClientApplication clientApplication = null;
		try {
			clientApplication = validateApplication(applicationVo);
		} catch (Exception e) {
			result.setResultCode(ResultCodeEnum.failure);
			result.setMessage(e.getMessage());
			return result;
		}
		vendorVo.setCustomerId(clientApplication.getCustomerId());

		VendorService vendorService = ApplicationContextUtil.getBean("vendorService");
		try {
			Vendor vendor = vendorService.createVendor(vendorVo);
			result.setResultCode(ResultCodeEnum.success);
			result.setReferenceId(vendor.getId());
		} catch (Exception e) {
			result.setResultCode(ResultCodeEnum.failure);
			result.setMessage(e.getMessage());
		}

		return result;
	}

	@Override
	public AddressSyncServiceResult updateVendor(ApplicationVo applicationVo, VendorVo vendorVo) {
		AddressSyncServiceResult result = new AddressSyncServiceResult();
		ClientApplication clientApplication = null;
		try {
			clientApplication = validateApplication(applicationVo);
		} catch (Exception e) {
			result.setResultCode(ResultCodeEnum.failure);
			result.setMessage(e.getMessage());
			return result;
		}
		vendorVo.setCustomerId(clientApplication.getCustomerId());

		VendorService vendorService = ApplicationContextUtil.getBean("vendorService");
		try {
			Vendor vendor = vendorService.updateVendor(vendorVo);
			result.setResultCode(ResultCodeEnum.success);
			result.setReferenceId(vendor.getId());
		} catch (Exception e) {
			result.setResultCode(ResultCodeEnum.failure);
			result.setMessage(e.getMessage());
		}

		return result;
	}

	@Override
	public AddressSyncServiceResult deleteVendor(ApplicationVo applicationVo, VendorVo vendorVo) {
		AddressSyncServiceResult result = new AddressSyncServiceResult();
		ClientApplication clientApplication = null;
		try {
			clientApplication = validateApplication(applicationVo);
		} catch (Exception e) {
			result.setResultCode(ResultCodeEnum.failure);
			result.setMessage(e.getMessage());
			return result;
		}
		vendorVo.setCustomerId(clientApplication.getCustomerId());

		VendorService vendorService = ApplicationContextUtil.getBean("vendorService");
		try {
			Vendor vendor = vendorService.deleteVendor(vendorVo);
			result.setResultCode(ResultCodeEnum.success);
			result.setReferenceId(vendor.getId());
		} catch (Exception e) {
			result.setResultCode(ResultCodeEnum.failure);
			result.setMessage(e.getMessage());
		}

		return result;
	}

	@Override
	public AddressSyncServiceResult createDeliverer(ApplicationVo applicationVo, DelivererVo delivererVo) {
		AddressSyncServiceResult result = new AddressSyncServiceResult();
		ClientApplication clientApplication = null;
		try {
			clientApplication = validateApplication(applicationVo);
		} catch (Exception e) {
			result.setResultCode(ResultCodeEnum.failure);
			result.setMessage(e.getMessage());
			return result;
		}
		delivererVo.setCustomerId(clientApplication.getCustomerId());

		DelivererService delivererService = ApplicationContextUtil.getBean("delivererService");
		try {
			Deliverer deliverer = delivererService.createDeliverer(delivererVo);
			result.setResultCode(ResultCodeEnum.success);
			result.setReferenceId(deliverer.getId());
		} catch (Exception e) {
			result.setResultCode(ResultCodeEnum.failure);
			result.setMessage(e.getMessage());
		}

		return result;
	}

	@Override
	public AddressSyncServiceResult updateDeliverer(ApplicationVo applicationVo, DelivererVo delivererVo) {
		AddressSyncServiceResult result = new AddressSyncServiceResult();
		ClientApplication clientApplication = null;
		try {
			clientApplication = validateApplication(applicationVo);
		} catch (Exception e) {
			result.setResultCode(ResultCodeEnum.failure);
			result.setMessage(e.getMessage());
			return result;
		}
		delivererVo.setCustomerId(clientApplication.getCustomerId());

		DelivererService delivererService = ApplicationContextUtil.getBean("delivererService");
		try {
			Deliverer deliverer = delivererService.updateDeliverer(delivererVo);
			result.setResultCode(ResultCodeEnum.success);
			result.setReferenceId(deliverer.getId());
		} catch (Exception e) {
			result.setResultCode(ResultCodeEnum.failure);
			result.setMessage(e.getMessage());
		}

		return result;
	}

	@Override
	public AddressSyncServiceResult deleteDeliverer(ApplicationVo applicationVo, DelivererVo delivererVo) {
		AddressSyncServiceResult result = new AddressSyncServiceResult();
		ClientApplication clientApplication = null;
		try {
			clientApplication = validateApplication(applicationVo);
		} catch (Exception e) {
			result.setResultCode(ResultCodeEnum.failure);
			result.setMessage(e.getMessage());
			return result;
		}
		delivererVo.setCustomerId(clientApplication.getCustomerId());

		DelivererService delivererService = ApplicationContextUtil.getBean("delivererService");
		try {
			Deliverer deliverer = delivererService.deleteDeliverer(delivererVo);
			result.setResultCode(ResultCodeEnum.success);
			result.setReferenceId(deliverer.getId());
		} catch (Exception e) {
			result.setResultCode(ResultCodeEnum.failure);
			result.setMessage(e.getMessage());
		}

		return result;
	}

	@Override
	public AddressSyncServiceResult createDelivererRule(ApplicationVo applicationVo, List<DelivererRuleVo> delivererRuleVoList) {
		AddressSyncServiceResult result = new AddressSyncServiceResult();
		ClientApplication clientApplication = null;
		try {
			clientApplication = validateApplication(applicationVo);
		} catch (Exception e) {
			result.setResultCode(ResultCodeEnum.failure);
			result.setMessage(e.getMessage());
			return result;
		}

		DelivererRuleService delivererRuleService = ApplicationContextUtil.getBean("delivererRuleService");
		try {
			delivererRuleService.createDelivererRule(clientApplication.getCustomerId(), delivererRuleVoList);
			result.setResultCode(ResultCodeEnum.success);
		} catch (Exception e) {
			result.setResultCode(ResultCodeEnum.failure);
			result.setMessage(e.getMessage());
		}
		return result;
	}

	@Override
	public AddressSyncServiceResult deleteDelivererRule( ApplicationVo applicationVo, Long ruleId) {
		AddressSyncServiceResult result = new AddressSyncServiceResult();
		ClientApplication clientApplication = null;
		try {
			clientApplication = validateApplication(applicationVo);
		} catch (Exception e) {
			result.setResultCode(ResultCodeEnum.failure);
			result.setMessage(e.getMessage());
			return result;
		}
		DelivererRuleService delivererRuleService = ApplicationContextUtil.getBean("delivererRuleService");
		try {
			delivererRuleService.delete(ruleId);
			result.setResultCode(ResultCodeEnum.success);
		} catch (Exception e) {
			result.setResultCode(ResultCodeEnum.failure);
			result.setMessage(e.getMessage());
		}
		return result;
	}
}