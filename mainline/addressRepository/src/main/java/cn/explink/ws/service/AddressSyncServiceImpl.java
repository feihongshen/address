package cn.explink.ws.service;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.jws.WebService;

import org.springframework.beans.factory.annotation.Autowired;

import cn.explink.dao.BizLogDAO;
import cn.explink.dao.DeliveryStationDao;
import cn.explink.domain.ClientApplication;
import cn.explink.domain.Deliverer;
import cn.explink.domain.DeliveryStation;
import cn.explink.domain.Vendor;
import cn.explink.domain.enums.LogTypeEnum;
import cn.explink.service.BizLogService;
import cn.explink.service.DelivererRuleService;
import cn.explink.service.DelivererService;
import cn.explink.service.DeliveryStationService;
import cn.explink.service.VendorService;
import cn.explink.util.ApplicationContextUtil;
import cn.explink.util.SynInsertBizLogThread;
import cn.explink.ws.vo.AddressSyncServiceResult;
import cn.explink.ws.vo.ApplicationVo;
import cn.explink.ws.vo.DelivererRuleVo;
import cn.explink.ws.vo.DelivererVo;
import cn.explink.ws.vo.DeliveryStationVo;
import cn.explink.ws.vo.ResultCodeEnum;
import cn.explink.ws.vo.VendorVo;

@WebService(endpointInterface = "cn.explink.ws.service.AddressSyncService")
public class AddressSyncServiceImpl extends BaseWebserviceImpl implements AddressSyncService {

	@Autowired
	private BizLogService bizLogService;

	@Autowired
	private BizLogDAO bizLogDAO;

	@Autowired
	private DeliveryStationDao deliveryStationDao;

	@Override
	public AddressSyncServiceResult createDeliveryStation(ApplicationVo applicationVo, DeliveryStationVo deliveryStationVo) {
		AddressSyncServiceResult result = new AddressSyncServiceResult();
		ClientApplication clientApplication = null;
		try {
			clientApplication = this.validateApplication(applicationVo);
		} catch (Exception e) {
			result.setResultCode(ResultCodeEnum.failure);
			result.setMessage(e.getMessage());
			return result;
		}
		deliveryStationVo.setCustomerId(clientApplication.getCustomerId());

		DeliveryStationService deliveryStationService = ApplicationContextUtil.getBean("deliveryStationService");
		try {
			DeliveryStation deliveryStation = deliveryStationService.createDeliveryStation(deliveryStationVo);
			ExecutorService service = Executors.newCachedThreadPool();
			service.execute(new SynInsertBizLogThread(AddressSyncServiceImpl.class, deliveryStationVo.getCustomerId(), LogTypeEnum.addStation.getValue(), null, deliveryStation, this.bizLogDAO, this.bizLogService, null, this.deliveryStationDao));
			service.shutdown();
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
			clientApplication = this.validateApplication(applicationVo);
		} catch (Exception e) {
			result.setResultCode(ResultCodeEnum.failure);
			result.setMessage(e.getMessage());
			return result;
		}
		deliveryStationVo.setCustomerId(clientApplication.getCustomerId());

		DeliveryStationService deliveryStationService = ApplicationContextUtil.getBean("deliveryStationService");
		try {
			DeliveryStation deliveryStation = deliveryStationService.updateDeliveryStation(deliveryStationVo);
			ExecutorService service = Executors.newCachedThreadPool();
			service.execute(new SynInsertBizLogThread(AddressSyncServiceImpl.class, deliveryStationVo.getCustomerId(), LogTypeEnum.updateStation.getValue(), null, deliveryStation, this.bizLogDAO, this.bizLogService, null, this.deliveryStationDao));
			service.shutdown();
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
			clientApplication = this.validateApplication(applicationVo);
		} catch (Exception e) {
			result.setResultCode(ResultCodeEnum.failure);
			result.setMessage(e.getMessage());
			return result;
		}
		deliveryStationVo.setCustomerId(clientApplication.getCustomerId());

		DeliveryStationService deliveryStationService = ApplicationContextUtil.getBean("deliveryStationService");
		try {
			DeliveryStation deliveryStation = deliveryStationService.deleteDeliveryStation(deliveryStationVo);
			ExecutorService service = Executors.newCachedThreadPool();
			service.execute(new SynInsertBizLogThread(AddressSyncServiceImpl.class, deliveryStationVo.getCustomerId(), LogTypeEnum.deleteStation.getValue(), null, deliveryStation, this.bizLogDAO, this.bizLogService, null, null));
			service.shutdown();
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
			clientApplication = this.validateApplication(applicationVo);
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
			clientApplication = this.validateApplication(applicationVo);
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
			clientApplication = this.validateApplication(applicationVo);
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
			clientApplication = this.validateApplication(applicationVo);
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
			clientApplication = this.validateApplication(applicationVo);
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
			clientApplication = this.validateApplication(applicationVo);
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
			clientApplication = this.validateApplication(applicationVo);
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
	public AddressSyncServiceResult deleteDelivererRule(ApplicationVo applicationVo, Long ruleId) {
		AddressSyncServiceResult result = new AddressSyncServiceResult();
		ClientApplication clientApplication = null;
		try {
			clientApplication = this.validateApplication(applicationVo);
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