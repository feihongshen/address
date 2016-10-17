package cn.explink.util;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import cn.explink.dao.BizLogDAO;
import cn.explink.domain.Address;
import cn.explink.domain.Alias;
import cn.explink.domain.BizLog;
import cn.explink.domain.DeliveryStation;
import cn.explink.domain.DeliveryStationRule;
import cn.explink.domain.enums.LogTypeEnum;
import cn.explink.service.AddressService;
import cn.explink.service.BizLogService;
import cn.explink.service.DeliveryStationService;

public class SynInsertBizLogThread implements Runnable {
    private Class clazz;
    private List<BizLog> bizLogList;
    private BizLogDAO bizLogDAO;
    private BizLogService bizLogService;
    private Long customerId;
    private int operationType;
    private String operationIP;
    private AddressService addressService;
    private DeliveryStationService deliverySationtService;
    private Object obj;

    public SynInsertBizLogThread(Class clazz, Long customerId, int operationType, String operationIP, Object obj, BizLogDAO bizLogDAO, BizLogService bizLogService, AddressService addressService, DeliveryStationService deliverySationtService) {
        super();
        this.clazz = clazz;
        this.bizLogList = this.bizLogList;
        this.bizLogDAO = bizLogDAO;
        this.bizLogService = bizLogService;
        this.customerId = customerId;
        this.operationType = operationType;
        this.operationIP = operationIP;
        this.addressService = addressService;
        this.deliverySationtService = deliverySationtService;
        this.obj = obj;
    }

    @Override
    public void run() {
        this.initBizLog();
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

    public void initBizLog() {
        List<BizLog> bizLogList = new ArrayList<BizLog>();
        Date date = new Date();
        //新增关键词的处理--把关键词名称集合先转化成关键词对象，然后转化为日志对象，然后保存
        if (this.operationType == LogTypeEnum.addAddress.getValue()) {
            Map<String, Object> map = (Map<String, Object>) this.obj;

            String addresses = (String) map.get("addresses");
            Long parentId = (Long) map.get("parentId");
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
                addressList = this.addressService.getAddressByNameAndPid(addressNameList, parentId);

                for (Address temp : addressList) {
                    BizLog bizLog = new BizLog();
                    bizLog.setOperationType(LogTypeEnum.addAddress.getValue());
                    bizLog.setCustomerId(this.customerId);
                    bizLog.setAddressId(temp.getId());
                    bizLog.setAddressName(temp.getName());
                    bizLog.setOperationIP(this.operationIP);
                    bizLog.setOperationTime(date);
                    bizLogList.add(bizLog);
                }
            }
        } else if (this.operationType == LogTypeEnum.deleteAddress.getValue()) {
            Long addressId = (Long) this.obj;
            Address address = this.addressService.getAddressById(addressId);
            BizLog bizLog = new BizLog();
            bizLog.setOperationType(LogTypeEnum.deleteAddress.getValue());
            bizLog.setCustomerId(this.customerId);
            bizLog.setAddressId(address.getId());
            bizLog.setAddressName(address.getName());
            bizLog.setOperationIP(this.operationIP);
            bizLog.setOperationTime(date);
            bizLogList.add(bizLog);
        } else if (this.operationType == LogTypeEnum.addAlias.getValue()) {
            Alias alias = (Alias) this.obj;
            BizLog bizLog = new BizLog();
            bizLog.setOperationType(LogTypeEnum.addAlias.getValue());
            bizLog.setCustomerId(this.customerId);
            bizLog.setAliasId(alias.getId());
            bizLog.setAliasName(alias.getName());
            bizLog.setOperationIP(this.operationIP);
            bizLog.setOperationTime(date);
            bizLogList.add(bizLog);
        } else if (this.operationType == LogTypeEnum.deleteAlias.getValue()) {
            Alias alias = (Alias) this.obj;
            BizLog bizLog = new BizLog();
            bizLog.setOperationType(LogTypeEnum.deleteAlias.getValue());
            bizLog.setCustomerId(this.customerId);
            bizLog.setAliasId(alias.getId());
            bizLog.setAliasName(alias.getName());
            bizLog.setOperationIP(this.operationIP);
            bizLog.setOperationTime(date);
            bizLogList.add(bizLog);
        } else if (this.operationType == LogTypeEnum.addRule.getValue()) {
            DeliveryStationRule deliveryStationRule = (DeliveryStationRule) this.obj;
            BizLog bizLog = new BizLog();
            bizLog.setOperationType(LogTypeEnum.addRule.getValue());
            bizLog.setCustomerId(this.customerId);
            bizLog.setAddressId(deliveryStationRule.getAddress().getId());
            bizLog.setAddressName(deliveryStationRule.getAddress().getName());
            bizLog.setOriginStationId(deliveryStationRule.getDeliveryStation().getId());
            bizLog.setOriginStationName(deliveryStationRule.getDeliveryStation().getName());
            bizLog.setDeliveryStationRuleId(deliveryStationRule.getId());
            bizLog.setRuleExpression(deliveryStationRule.getRule());
            bizLog.setOperationIP(this.operationIP);
            bizLog.setOperationTime(date);
            bizLogList.add(bizLog);
        } else if (this.operationType == LogTypeEnum.deleteRule.getValue()) {
            DeliveryStationRule deliveryStationRule = (DeliveryStationRule) this.obj;
            BizLog bizLog = new BizLog();
            bizLog.setOperationType(LogTypeEnum.deleteRule.getValue());
            bizLog.setCustomerId(this.customerId);
            bizLog.setAddressId(deliveryStationRule.getAddress().getId());
            bizLog.setAddressName(deliveryStationRule.getAddress().getName());
            bizLog.setOriginStationId(deliveryStationRule.getDeliveryStation().getId());
            bizLog.setOriginStationName(deliveryStationRule.getDeliveryStation().getName());
            bizLog.setDeliveryStationRuleId(deliveryStationRule.getId());
            bizLog.setRuleExpression(deliveryStationRule.getRule());
            bizLog.setOperationIP(this.operationIP);
            bizLog.setOperationTime(date);
            bizLogList.add(bizLog);
        } else if (this.operationType == LogTypeEnum.addStation.getValue()) {
            DeliveryStation deliveryStation = (DeliveryStation) this.obj;

            BizLog bizLog = new BizLog();
            bizLog.setOperationType(LogTypeEnum.addStation.getValue());
            bizLog.setCustomerId(this.customerId);
            bizLog.setOriginStationId(deliveryStation.getId());
            bizLog.setOriginStationName(deliveryStation.getName());
            bizLog.setOperationIP(this.operationIP);
            bizLog.setOperationTime(date);
            bizLogList.add(bizLog);
        } else if (this.operationType == LogTypeEnum.updateStation.getValue()) {
            DeliveryStation deliveryStation = (DeliveryStation) this.obj;
            BizLog bizLog = new BizLog();
            bizLog.setOperationType(LogTypeEnum.updateStation.getValue());
            bizLog.setCustomerId(this.customerId);
            bizLog.setOperationIP(this.operationIP);
            bizLog.setOperationTime(date);
            bizLogList.add(bizLog);
        } else if (this.operationType == LogTypeEnum.deleteStation.getValue()) {
            DeliveryStation deliveryStation = (DeliveryStation) this.obj;
            BizLog bizLog = new BizLog();
            bizLog.setOperationType(LogTypeEnum.deleteStation.getValue());
            bizLog.setCustomerId(this.customerId);
            bizLog.setModifideStationId(deliveryStation.getId());
            bizLog.setModifideStationName(deliveryStation.getName());
            bizLog.setOperationIP(this.operationIP);
            bizLog.setOperationTime(date);
            bizLogList.add(bizLog);
        } else if (this.operationType == LogTypeEnum.changeStationRelation.getValue()) {
            BizLog bizLog = (BizLog) this.obj;
            bizLog.setOperationType(LogTypeEnum.changeStationRelation.getValue());
            DeliveryStation SourceStation = this.deliverySationtService.getDeliveryStationById(bizLog.getSourceStationId());
            DeliveryStation DestStation = this.deliverySationtService.getDeliveryStationById(bizLog.getDestStationId());
            bizLog.setSourceStationName(SourceStation.getName());
            bizLog.setDestStationName(DestStation.getName());
            bizLog.setCustomerId(this.customerId);
            bizLog.setOperationIP(this.operationIP);
            bizLog.setOperationTime(date);
            bizLogList.add(bizLog);
        }
        this.bizLogList = bizLogList;
    }

}