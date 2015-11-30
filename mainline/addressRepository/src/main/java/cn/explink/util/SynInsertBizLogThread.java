package cn.explink.util;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cn.explink.dao.BizLogDAO;
import cn.explink.dao.DeliveryStationDao;
import cn.explink.domain.Address;
import cn.explink.domain.Alias;
import cn.explink.domain.BizLog;
import cn.explink.domain.DeliveryStation;
import cn.explink.domain.DeliveryStationRule;
import cn.explink.domain.enums.LogTypeEnum;
import cn.explink.service.AddressService;
import cn.explink.service.BizLogService;
import cn.explink.ws.vo.DeliveryStationVo;

public class SynInsertBizLogThread implements Runnable {
	private Class clazz;
	private List<BizLog> bizLogList;
	private BizLogDAO bizLogDAO;
	private BizLogService bizLogService;

	public SynInsertBizLogThread(Class clazz, Long customerId, int operationType, String operationIP, Object obj, BizLogDAO bizLogDAO, BizLogService bizLogService, AddressService addressService, DeliveryStationDao deliveryStationDao) {
		super();
		List<BizLog> bizLogList = new ArrayList<BizLog>();
		Date date = new Date();
		//新增关键词的处理--把关键词名称集合先转化成关键词对象，然后转化为日志对象，然后保存
		if (operationType == LogTypeEnum.addAddress.getValue()) {
			String addresses = (String) obj;
			List<Address> addressList = new ArrayList<Address>();
			List<String> addressNameList = new ArrayList<String>();
			for (String addressLine : addresses.split("\n")) {
				String addressName = addressLine.trim();
				if (addressName.length() == 0) {
					continue;
				}
				addressNameList.add(addressName);
			}
			if (addressNameList.size() > 0) {
				addressList = addressService.getAddressByNameList(addressNameList);

				for (Address temp : addressList) {
					BizLog bizLog = new BizLog();
					bizLog.setOperationType(LogTypeEnum.addAddress.getValue());
					bizLog.setCustomerId(customerId);
					bizLog.setAddressId(temp.getId());
					bizLog.setAddressName(temp.getName());
					bizLog.setOperationIP(operationIP);
					bizLog.setOperationTime(date);
					bizLogList.add(bizLog);
				}
			}
		} else if (operationType == LogTypeEnum.deleteAddress.getValue()) {
			Long addressId = (Long) obj;
			Address address = addressService.getAddressById(addressId);
			BizLog bizLog = new BizLog();
			bizLog.setOperationType(LogTypeEnum.deleteAddress.getValue());
			bizLog.setCustomerId(customerId);
			bizLog.setAddressId(address.getId());
			bizLog.setAddressName(address.getName());
			bizLog.setOperationIP(operationIP);
			bizLog.setOperationTime(date);
			bizLogList.add(bizLog);
		} else if (operationType == LogTypeEnum.addAlias.getValue()) {
			Alias alias = (Alias) obj;
			BizLog bizLog = new BizLog();
			bizLog.setOperationType(LogTypeEnum.addAlias.getValue());
			bizLog.setCustomerId(customerId);
			bizLog.setAliasId(alias.getId());
			bizLog.setAliasName(alias.getName());
			bizLog.setOperationIP(operationIP);
			bizLog.setOperationTime(date);
			bizLogList.add(bizLog);
		} else if (operationType == LogTypeEnum.deleteAlias.getValue()) {
			Alias alias = (Alias) obj;
			BizLog bizLog = new BizLog();
			bizLog.setOperationType(LogTypeEnum.deleteAlias.getValue());
			bizLog.setCustomerId(customerId);
			bizLog.setAliasId(alias.getId());
			bizLog.setAliasName(alias.getName());
			bizLog.setOperationIP(operationIP);
			bizLog.setOperationTime(date);
			bizLogList.add(bizLog);
		} else if (operationType == LogTypeEnum.addRule.getValue()) {
			DeliveryStationRule deliveryStationRule = (DeliveryStationRule) obj;
			BizLog bizLog = new BizLog();
			bizLog.setOperationType(LogTypeEnum.addRule.getValue());
			bizLog.setCustomerId(customerId);
			bizLog.setAddressId(deliveryStationRule.getAddress().getId());
			bizLog.setAddressName(deliveryStationRule.getAddress().getName());
			bizLog.setOriginStationId(deliveryStationRule.getDeliveryStation().getId());
			bizLog.setOriginStationNAME(deliveryStationRule.getDeliveryStation().getName());
			bizLog.setDeliveryStationRuleId(deliveryStationRule.getId());
			bizLog.setRuleExpression(deliveryStationRule.getRuleExpression());
			bizLog.setOperationIP(operationIP);
			bizLog.setOperationTime(date);
			bizLogList.add(bizLog);
		} else if (operationType == LogTypeEnum.deleteRule.getValue()) {
			DeliveryStationRule deliveryStationRule = (DeliveryStationRule) obj;
			BizLog bizLog = new BizLog();
			bizLog.setOperationType(LogTypeEnum.deleteRule.getValue());
			bizLog.setCustomerId(customerId);
			bizLog.setAddressId(deliveryStationRule.getAddress().getId());
			bizLog.setAddressName(deliveryStationRule.getAddress().getName());
			bizLog.setOriginStationId(deliveryStationRule.getDeliveryStation().getId());
			bizLog.setOriginStationNAME(deliveryStationRule.getDeliveryStation().getName());
			bizLog.setDeliveryStationRuleId(deliveryStationRule.getId());
			bizLog.setRuleExpression(deliveryStationRule.getRuleExpression());
			bizLog.setOperationIP(operationIP);
			bizLog.setOperationTime(date);
			bizLogList.add(bizLog);
		} else if (operationType == LogTypeEnum.addStation.getValue()) {
			DeliveryStationVo deliveryStationVo = (DeliveryStationVo) obj;
			DeliveryStation deliveryStation = deliveryStationDao.getDeliveryStation(deliveryStationVo.getCustomerId(), deliveryStationVo.getExternalId());

			BizLog bizLog = new BizLog();
			bizLog.setOperationType(LogTypeEnum.addStation.getValue());
			bizLog.setCustomerId(customerId);
			bizLog.setOriginStationId(deliveryStation.getId());
			bizLog.setOriginStationNAME(deliveryStation.getName());
			bizLog.setOperationIP(operationIP);
			bizLog.setOperationTime(date);
			bizLogList.add(bizLog);
		} else if (operationType == LogTypeEnum.updateStation.getValue()) {
			DeliveryStationVo deliveryStationVo = (DeliveryStationVo) obj;
			DeliveryStation deliveryStation = deliveryStationDao.getDeliveryStation(deliveryStationVo.getCustomerId(), deliveryStationVo.getExternalId());
			BizLog bizLog = new BizLog();
			bizLog.setOperationType(LogTypeEnum.updateStation.getValue());
			bizLog.setCustomerId(customerId);
			bizLog.setOriginStationId(deliveryStation.getId());
			bizLog.setOriginStationNAME(deliveryStation.getName());
			bizLog.setDestStationId(deliveryStation.getId());
			bizLog.setDestStationName(deliveryStationVo.getName());
			bizLog.setOperationIP(operationIP);
			bizLog.setOperationTime(date);
			bizLogList.add(bizLog);
		} else if (operationType == LogTypeEnum.deleteStation.getValue()) {
			DeliveryStation deliveryStation = (DeliveryStation) obj;
			BizLog bizLog = new BizLog();
			bizLog.setOperationType(LogTypeEnum.deleteStation.getValue());
			bizLog.setCustomerId(customerId);
			bizLog.setDestStationId(deliveryStation.getId());
			bizLog.setDestStationName(deliveryStation.getName());
			bizLog.setOperationIP(operationIP);
			bizLog.setOperationTime(date);
			bizLogList.add(bizLog);
		} else if (operationType == LogTypeEnum.changeStationRelation.getValue()) {
			BizLog bizLog = (BizLog) obj;
			bizLog.setOperationType(LogTypeEnum.changeStationRelation.getValue());
			bizLog.setCustomerId(customerId);
			bizLog.setOperationIP(operationIP);
			bizLog.setOperationTime(date);
			bizLogList.add(bizLog);
		}

		this.clazz = clazz;
		this.bizLogList = bizLogList;
		this.bizLogDAO = bizLogDAO;
		this.bizLogService = bizLogService;
	}

	@Override
	public void run() {
		this.bizLogService.loggerInfo(this.clazz, this.bizLogList, this.bizLogDAO, this.bizLogService);
	}

	public List<BizLog> getBizLogList() {
		return this.bizLogList;
	}

	public void setBizLogList(List<BizLog> bizLogList) {
		this.bizLogList = bizLogList;
	}

	public BizLogDAO getBizLogDAO() {
		return this.bizLogDAO;
	}

	public void setBizLogDAO(BizLogDAO bizLogDAO) {
		this.bizLogDAO = bizLogDAO;
	}

	public Class getClazz() {
		return this.clazz;
	}

	public void setClazz(Class clazz) {
		this.clazz = clazz;
	}

	public BizLogService getBizLogService() {
		return this.bizLogService;
	}

	public void setBizLogService(BizLogService bizLogService) {
		this.bizLogService = bizLogService;
	}

}