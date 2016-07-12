
package cn.explink.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import net.sf.json.JSONArray;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.explink.dao.AddressDao;
import cn.explink.dao.BizLogDAO;
import cn.explink.dao.CustomerDao;
import cn.explink.dao.DeliveryStationDao;
import cn.explink.dao.DeliveryStationRuleDao;
import cn.explink.domain.Address;
import cn.explink.domain.BizLog;
import cn.explink.domain.Customer;
import cn.explink.domain.DeliveryStation;
import cn.explink.domain.DeliveryStationRule;
import cn.explink.domain.Vendor;
import cn.explink.domain.enums.DeliveryStationRuleTypeEnum;
import cn.explink.domain.enums.DeliveryStationStausEnmu;
import cn.explink.domain.enums.LogTypeEnum;
import cn.explink.domain.fields.AddressIdAndAddressLinePair;
import cn.explink.gis.GeoCoder;
import cn.explink.gis.GeoPoint;
import cn.explink.gis.GeoUtility;
import cn.explink.modle.ComboBox;
import cn.explink.tree.ZTreeNode;
import cn.explink.util.SynInsertBizLogThread;
import cn.explink.ws.service.AddressSyncServiceImpl;
import cn.explink.ws.vo.DeliveryStationVo;

@Service("deliveryStationService")
public class DeliveryStationService extends CommonServiceImpl<DeliveryStation, Long> {

    public DeliveryStationService() {
        super(DeliveryStation.class);
        // TODO Auto-generated constructor stub
    }

    @Autowired
    private DeliveryStationDao deliveryStationDao;

    @Autowired
    private CustomerDao customerDao;

    @Autowired
    private DeliveryStationRuleDao deliveryStationRuleDao;

    @Autowired
    private AddressDao addressDao;

    @Autowired
    private BizLogService bizLogService;

    @Autowired
    private BizLogDAO bizLogDAO;

    /**
     * 新增配送站点方法
     * <p>
     * 方法详细描述
     * </p>
     * @param deliveryStationVo
     * @return
     * @since 1.0
     */
    public DeliveryStation createDeliveryStation(DeliveryStationVo deliveryStationVo) {
        Customer customer = this.customerDao.get(deliveryStationVo.getCustomerId());
        if (customer == null) {
            throw new RuntimeException("customer is not exist");
        }

        DeliveryStation deliveryStation = this.deliveryStationDao.getDeliveryStation(deliveryStationVo.getCustomerId(),
                deliveryStationVo.getExternalId());
        if (deliveryStation == null) {
            deliveryStation = new DeliveryStation();
        }
        deliveryStation.setName(deliveryStationVo.getName());
        deliveryStation.setStatus(DeliveryStationStausEnmu.valid.getValue());
        deliveryStation.setCustomer(customer);
        deliveryStation.setExternalId(deliveryStationVo.getExternalId());
        deliveryStation.setStationCode(deliveryStationVo.getStationCode());
        this.deliveryStationDao.save(deliveryStation);
        // 新开线程执行业务日志记录
        ExecutorService service = Executors.newCachedThreadPool();
        service.execute(new SynInsertBizLogThread(AddressSyncServiceImpl.class, deliveryStationVo.getCustomerId(),
                LogTypeEnum.addStation.getValue(), null, deliveryStation, this.bizLogDAO, this.bizLogService, null,
                null));
        service.shutdown();
        return deliveryStation;
    }

    /**
     * 更新配送站点
     * <p>
     * 方法详细描述
     * </p>
     * @param deliveryStationVo
     * @return
     * @since 1.0
     */
    public DeliveryStation updateDeliveryStation(DeliveryStationVo deliveryStationVo) {
        DeliveryStation deliveryStation = this.deliveryStationDao.getDeliveryStation(deliveryStationVo.getCustomerId(),
                deliveryStationVo.getExternalId());
        // 暂时不加stationCode的更新记录
        BizLog bizlog = new BizLog();
        bizlog.setOriginStationId(deliveryStation.getId());
        bizlog.setOriginStationName(deliveryStation.getName());
        bizlog.setModifideStationId(deliveryStation.getId());
        bizlog.setModifideStationName(deliveryStationVo.getName());
        ExecutorService service = Executors.newCachedThreadPool();
        service.execute(new SynInsertBizLogThread(AddressSyncServiceImpl.class, deliveryStationVo.getCustomerId(),
                LogTypeEnum.updateStation.getValue(), null, deliveryStation, this.bizLogDAO, this.bizLogService, null,
                null));
        service.shutdown();
        deliveryStation.setStationCode(deliveryStationVo.getStationCode());
        deliveryStation.setName(deliveryStationVo.getName());
        deliveryStation.setStatus(DeliveryStationStausEnmu.valid.getValue());
        this.deliveryStationDao.save(deliveryStation);
        return deliveryStation;
    }

    public DeliveryStation updateDeliveryStationByUid(DeliveryStation deliveryStationParam) {
        DeliveryStation deliveryStation = this.deliveryStationDao
                .getDeliveryStationByUid(deliveryStationParam.getUid());
        deliveryStation.setCoordinate(deliveryStationParam.getCoordinate());
        deliveryStation.setMapcenterLng(deliveryStationParam.getMapcenterLng());
        deliveryStation.setMapcenterLat(deliveryStationParam.getMapcenterLat());

        return this.deliveryStationDao.save(deliveryStation);
    }

    public DeliveryStation getDeliveryStationByUid(String uid) {
        return this.deliveryStationDao.getDeliveryStationByUid(uid);
    }

    public DeliveryStation getDeliveryStationById(Long id) {
        return this.deliveryStationDao.getDeliveryStationById(id);
    }

    public DeliveryStation updateDeliveryStationById(DeliveryStation deliveryStationParam) {
        DeliveryStation deliveryStation = this.deliveryStationDao.getDeliveryStationById(deliveryStationParam.getId());
        deliveryStation.setName(deliveryStationParam.getName());
        deliveryStation.setCoordinate(deliveryStationParam.getCoordinate());
        deliveryStation.setMapcenterLng(deliveryStationParam.getMapcenterLng());
        deliveryStation.setMapcenterLat(deliveryStationParam.getMapcenterLat());
        deliveryStation.setUid(deliveryStationParam.getUid());

        return this.deliveryStationDao.save(deliveryStation);
    }

    /**
     * 同步更新关键字-站点关联关系
     */
    public void synUpdateDeliveryStationRule(List<AddressIdAndAddressLinePair> addressIdAndAddressLinePairList,
            String oldCoordinate, String newCoordinate, Long customerId, Long stationId) {
        List<Long> toDeleteAddressIdList = new ArrayList<Long>();
        for (AddressIdAndAddressLinePair addressIdAndAddressLinePair : addressIdAndAddressLinePairList) {
            // AddressIdAndAddressLinePair idAndLinePair =
            // this.combineAddressLine(fullAddrStationPair.getAddrList());
            boolean inOldArea = this.containsAddress(addressIdAndAddressLinePair.getAddressLine(), oldCoordinate);
            boolean inNewArea = this.containsAddress(addressIdAndAddressLinePair.getAddressLine(), newCoordinate);
            // 在修改前区域，不在修改后区域，则解除绑定
            if (inOldArea && !inNewArea) {
                toDeleteAddressIdList.add(addressIdAndAddressLinePair.getAddressId());
            }
            // 不在修改前区域，在修改后区域，则新增绑定
            if (!inOldArea && inNewArea) {
                this.createDeliveryStationRule(addressIdAndAddressLinePair.getAddressId(), stationId, customerId);
            }
            // 其他情况不需要处理
        }
        if (toDeleteAddressIdList.size() > 0) {
            this.deliveryStationRuleDao.deleteRuleByIds(toDeleteAddressIdList, customerId);
        }
    }

    private DeliveryStationRule createDeliveryStationRule(Long addressId, Long deliveryStationId, Long customerId) {
        Address address = this.addressDao.get(addressId);
        DeliveryStation deliveryStation = this.deliveryStationDao.get(deliveryStationId);

        DeliveryStationRule deliveryStationRule = new DeliveryStationRule();
        address.getDeliveryStationRules().add(deliveryStationRule);
        deliveryStationRule.setAddress(address);
        deliveryStationRule.setDeliveryStation(deliveryStation);
        deliveryStationRule.setCreationTime(new Date());
        deliveryStationRule.setRuleType(DeliveryStationRuleTypeEnum.fallback.getValue());
        this.deliveryStationRuleDao.save(deliveryStationRule);

        return deliveryStationRule;
    }

    private List<GeoPoint> getGeoPointListByCoordinate(String coorString) {
        @SuppressWarnings("unchecked")
        List<GeoPoint> pts = (List<GeoPoint>) JSONArray.toCollection(JSONArray.fromObject(coorString), GeoPoint.class);
        return pts;
    }

    private String getPathFromCoordinate(String coorString) {
        coorString = coorString.substring(coorString.indexOf("path") + 6, coorString.indexOf("}]") + 2);
        return coorString;
    }

    private boolean containsAddress(String addressLine, String coordinate) {
        GeoPoint position = GeoCoder.getInstance().getGeoCoder().GetLocationDetails(addressLine);
        // 地理编码+POI检索失败
        if (position == null) {
            return false;
        }
        coordinate = this.getPathFromCoordinate(coordinate);
        List<GeoPoint> geoPointList = this.getGeoPointListByCoordinate(coordinate);
        if (geoPointList == null) {
            return false;
        }

        // 空间检索
        GeoPoint[] polygon = new GeoPoint[geoPointList.size()];
        if (GeoUtility.isInPolygon(position, geoPointList.toArray(polygon))) {
            return true;
        }
        return false;
    }

    public DeliveryStation deleteDeliveryStation(DeliveryStationVo deliveryStationVo) {
        DeliveryStation deliveryStation = this.deliveryStationDao.getDeliveryStation(deliveryStationVo.getCustomerId(),
                deliveryStationVo.getExternalId());
        deliveryStation.setStatus(DeliveryStationStausEnmu.invalid.getValue());
        this.deliveryStationDao.save(deliveryStation);
        ExecutorService service = Executors.newCachedThreadPool();
        service.execute(new SynInsertBizLogThread(AddressSyncServiceImpl.class, deliveryStationVo.getCustomerId(),
                LogTypeEnum.deleteStation.getValue(), null, deliveryStation, this.bizLogDAO, this.bizLogService, null,
                null));
        service.shutdown();
        return deliveryStation;
    }

    /**
     * 生成关键词列表
     * @param headerNameList
     * @return
     */
    public XSSFWorkbook createAddressFile(List<String> headerNameList, List<List<String>> address) {
        XSSFWorkbook wookbook = new XSSFWorkbook();
        XSSFSheet sheet = wookbook.createSheet();
        XSSFRow row = sheet.createRow(0);
        int columnIndex = 0;
        for (String headerName : headerNameList) {
            XSSFCell cell = row.createCell(columnIndex);
            cell.setCellValue(headerName);
            columnIndex++;
        }
        for (int i = 0; i < address.size(); i++) {
            columnIndex = 0;
            row = sheet.createRow(i + 1);
            for (int j = 1; j < address.get(i).size(); j++) {
                XSSFCell cell = row.createCell(columnIndex);
                cell.setCellValue(address.get(i).get(j));
                columnIndex++;
            }
        }

        return wookbook;
    }

    /**
     * 查询站点
     * @param id
     * @return
     */
    public DeliveryStation getById(Long id) {
        return this.deliveryStationDao.get(id);
    }

    /**
     * 根据站点ID查询所有关键词
     * @param id
     * @return
     */
    public List<List<String>> getAddressById(Long id, String stationName) {
        List<Address> list = this.deliveryStationDao.getAddress(id);
        Map<Long, String> addMap = new HashMap<Long, String>();
        Set<Long> addIds = new HashSet<Long>();
        for (Address a : list) {
            if (a.getPath() != null) {
                String[] ids = a.getPath().split("-");
                for (String id2 : ids) {
                    if (StringUtils.isNotBlank(id2)) {
                        addIds.add(Long.parseLong(id2));
                    }
                }
            }
        }
        List<Address> addressList = this.deliveryStationDao.getAddressByIds(addIds);
        for (Address a : addressList) {
            addMap.put(a.getId(), a.getName());
        }
        List<List<String>> result = new ArrayList<List<String>>();
        for (Address a : list) {
            List<String> row = new ArrayList<String>();
            if (a.getPath() != null) {
                String[] ids = a.getPath().split("-");
                for (String id2 : ids) {
                    if (StringUtils.isNotBlank(id2)) {
                        row.add(addMap.get(Long.parseLong(id2)));
                    }
                }
                row.add(a.getName());
                for (int i = ids.length + 1; i < 7; i++) {
                    row.add(" ");
                }
                row.add(stationName);
            }
            result.add(row);
        }
        return result;
    }

    public List<DeliveryStation> listAll(Long customerId) {
        return this.deliveryStationDao.listAll(customerId);
    }

    public List<ComboBox> getAllSationt(Long customerId) {
        return this.deliveryStationDao.getComBoxDeliveryStation(customerId);
    }

    public DeliveryStation getByNameAndCustomerId(String deliveryStationName, Long customerId) {
        return this.deliveryStationDao.getByNameAndCustomerId(deliveryStationName, customerId);
    }

    public List<Vendor> listAllVendor(Long customerId) {
        return this.deliveryStationDao.listAllVendor(customerId);
    }

    public List<ZTreeNode> listAllToTreeNode(Long customerId, Integer page, Integer pageSize) {
        return this.deliveryStationDao.listAllToTreeNode(customerId, page, pageSize);
    }
}
