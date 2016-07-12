
package cn.explink.web.controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.explink.dao.BizLogDAO;
import cn.explink.domain.Address;
import cn.explink.domain.DeliveryStation;
import cn.explink.domain.Vendor;
import cn.explink.domain.enums.DeliveryStationStausEnmu;
import cn.explink.domain.enums.LogTypeEnum;
import cn.explink.domain.fields.AddressIdAndAddressLinePair;
import cn.explink.modle.AjaxJson;
import cn.explink.modle.DataGrid;
import cn.explink.modle.DataGridReturn;
import cn.explink.modle.SortDirection;
import cn.explink.qbc.CriteriaQuery;
import cn.explink.quick.QuickSerivce;
import cn.explink.service.AddressService;
import cn.explink.service.BizLogService;
import cn.explink.service.DeliveryStationService;
import cn.explink.tree.ZTreeNode;
import cn.explink.util.HqlGenerateUtil;
import cn.explink.util.StringUtil;
import cn.explink.util.SynInsertBizLogThread;

@RequestMapping("/station")
@Controller
public class DeliveryStationController extends BaseController {

    @Autowired
    private DeliveryStationService deliveryStationService;

    @Autowired
    private QuickSerivce quickSerivce;

    @Autowired
    private AddressService addressService;

    @Autowired
    private BizLogService bizLogService;

    @Autowired
    private BizLogDAO bizLogDAO;

    @RequestMapping("/list")
    public @ResponseBody
    DataGridReturn list(DeliveryStation deliveryStation, HttpServletRequest request, HttpServletResponse response,
            DataGrid dataGrid) {
        CriteriaQuery cq = new CriteriaQuery(DeliveryStation.class, dataGrid);
        cq.addOrder("name", SortDirection.asc);
        cq.add(Restrictions.eq("customer.id", this.getCustomerId()));
        cq.add(Restrictions.eq("status", DeliveryStationStausEnmu.valid.getValue()));
        HqlGenerateUtil.installHql(cq, deliveryStation, request.getParameterMap());
        return this.deliveryStationService.getDataGridReturn(cq, true);
    }

    /**
     * 根据站点ID导出所有关键词库
     * @param id
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("/downloadStationAddress")
    public String downloadStationAddress(@RequestParam(value = "id", required = false) Long id,
            HttpServletRequest request, HttpServletResponse response) {
        List<String> headerNameList = new ArrayList<String>();
        headerNameList.add("省/直辖市");
        headerNameList.add("市");
        headerNameList.add("区");
        headerNameList.add("地址1");
        headerNameList.add("地址2");
        headerNameList.add("地址3");
        headerNameList.add("站点");
        DeliveryStation station = this.deliveryStationService.getById(id);
        List<List<String>> addressList = this.deliveryStationService.getAddressById(id, station.getName());
        XSSFWorkbook wb = this.deliveryStationService.createAddressFile(headerNameList, addressList);
        String fileName = station.getName() + "关键字.xlsx";
        this.setDownloadFileName(response, fileName);
        try {
            ServletOutputStream out = response.getOutputStream();
            wb.write(out);
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @RequestMapping("/listAll")
    public @ResponseBody
    List<DeliveryStation> listAll(HttpServletRequest request, HttpServletResponse response, DataGrid dataGrid) {
        return this.deliveryStationService.listAll(this.getCustomerId());
    }

    @RequestMapping("/listAllToTreeNode")
    public @ResponseBody
    List<ZTreeNode> listAllToTreeNode(@RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "pageSize", required = false) Integer pageSize) {
        return this.deliveryStationService.listAllToTreeNode(this.getCustomerId(), page, pageSize);
    }

    @RequestMapping("/listAllVendor")
    public @ResponseBody
    List<Vendor> listAllVendor(HttpServletRequest request, HttpServletResponse response, DataGrid dataGrid) {
        return this.deliveryStationService.listAllVendor(this.getCustomerId());
    }

    /**
     * 根据站点IDs导出所有关键词库
     * @param id
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("/downloadStationAddresses")
    public String downloadStationAddresses(@RequestParam(value = "ids", required = false) String ids,
            HttpServletRequest request, HttpServletResponse response) {
        List<String> headerNameList = new ArrayList<String>();
        headerNameList.add("省/直辖市");
        headerNameList.add("市");
        headerNameList.add("区");
        headerNameList.add("地址1");
        headerNameList.add("地址2");
        headerNameList.add("地址3");
        headerNameList.add("原站点");
        headerNameList.add("新站点");
        List<List<String>> addressList = new ArrayList<List<String>>();
        String fileName = "";
        if (StringUtils.isNotEmpty(ids)) {
            String[] id = ids.split(",");
            for (String element : id) {
                Long tid = Long.parseLong(element);
                DeliveryStation station = this.deliveryStationService.getById(tid);
                addressList.addAll(this.deliveryStationService.getAddressById(tid, station.getName()));
                fileName += station.getName() + "-";
            }
        }
        XSSFWorkbook wb = this.deliveryStationService.createAddressFile(headerNameList, addressList);
        fileName = fileName + "关键字.xlsx";
        this.setDownloadFileName(response, fileName);
        try {
            ServletOutputStream out = response.getOutputStream();
            wb.write(out);
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @RequestMapping("/getDeliveryStationByUid")
    @ResponseBody
    public DeliveryStation getDeliveryStationByUid(String uid) {
        return this.deliveryStationService.getDeliveryStationByUid(uid);
    }

    @RequestMapping("/modifyByUid")
    @ResponseBody
    public AjaxJson modifyByUid(String uid, String coordinate, BigDecimal mapcenterLat, BigDecimal mapcenterLng,
            HttpServletRequest request) {
        AjaxJson aj = new AjaxJson();

        Long customerId = this.getCustomerId();
        DeliveryStation oldDeliveryStation = this.deliveryStationService.getDeliveryStationByUid(uid);
        String oldCoordinate = oldDeliveryStation.getCoordinate();

        if (StringUtil.isEmpty(uid) || StringUtil.isEmpty(coordinate)) {
            aj.setSuccess(false);
            return aj;
        }

        DeliveryStation deliveryStation = new DeliveryStation();
        deliveryStation.setUid(uid);
        deliveryStation.setCoordinate(coordinate);
        deliveryStation.setMapcenterLat(mapcenterLat);
        deliveryStation.setMapcenterLng(mapcenterLng);
        DeliveryStation deliveryStationResult = this.deliveryStationService.updateDeliveryStationByUid(deliveryStation);
        ExecutorService service = Executors.newCachedThreadPool();
        service.execute(new SynInsertBizLogThread(DeliveryStationController.class, this.getCustomerId(),
                LogTypeEnum.updateStation.getValue(), this.getUserIp(request), deliveryStation, this.bizLogDAO,
                this.bizLogService, null, null));
        service.shutdown();
        if (null != deliveryStationResult) {
            aj.setSuccess(true);
        }
        ExecutorService Synservice = Executors.newCachedThreadPool();
        Synservice.execute(new SynUpdateDeliveryStationRuleThread(customerId, oldDeliveryStation.getId(),
                oldCoordinate, coordinate, this.deliveryStationService, this.addressService));
        Synservice.shutdown();

        return aj;
    }

    @RequestMapping("/modifyById")
    @ResponseBody
    public AjaxJson modifyById(Long id, String uid, String coordinate, BigDecimal mapcenterLat,
            BigDecimal mapcenterLng, HttpServletRequest request) {
        AjaxJson aj = new AjaxJson();

        Long customerId = this.getCustomerId();

        DeliveryStation oldDeliveryStation = this.deliveryStationService.getDeliveryStationById(id);
        String oldCoordinate = oldDeliveryStation.getCoordinate();

        DeliveryStation deliveryStation = new DeliveryStation();
        deliveryStation.setId(id);
        deliveryStation.setName(oldDeliveryStation.getName());
        deliveryStation.setUid(uid);
        deliveryStation.setCoordinate(coordinate);
        deliveryStation.setMapcenterLat(mapcenterLat);
        deliveryStation.setMapcenterLng(mapcenterLng);
        DeliveryStation deliveryStationResult = this.deliveryStationService.updateDeliveryStationById(deliveryStation);
        ExecutorService service = Executors.newCachedThreadPool();
        service.execute(new SynInsertBizLogThread(AddressController.class, this.getCustomerId(),
                LogTypeEnum.updateStation.getValue(), this.getUserIp(request), deliveryStation, this.bizLogDAO,
                this.bizLogService, null, null));
        service.shutdown();
        if (null != deliveryStationResult) {
            aj.setSuccess(true);
        }
        ExecutorService Synservice = Executors.newCachedThreadPool();
        Synservice.execute(new SynUpdateDeliveryStationRuleThread(customerId, id, oldCoordinate, coordinate,
                this.deliveryStationService, this.addressService));
        Synservice.shutdown();

        return aj;
    }

    public DeliveryStationService getDeliveryStationService() {
        return this.deliveryStationService;
    }

    public void setDeliveryStationService(DeliveryStationService deliveryStationService) {
        this.deliveryStationService = deliveryStationService;
    }

    public class SynUpdateDeliveryStationRuleThread implements Runnable {

        private Long customerId;

        private Long stationId;

        private String oldCoordinate;

        private String newCoordinate;

        private DeliveryStationService deliveryStationService;

        private AddressService addressService;

        public SynUpdateDeliveryStationRuleThread(Long customerId, Long stationId, String oldCoordinate,
                String newCoordinate, DeliveryStationService deliveryStationService, AddressService addressService) {
            super();
            this.customerId = customerId;
            this.stationId = stationId;
            this.oldCoordinate = oldCoordinate;
            this.newCoordinate = newCoordinate;
            this.deliveryStationService = deliveryStationService;
            this.addressService = addressService;
        }

        @Override
        public void run() {
            List<AddressIdAndAddressLinePair> addressIdAndAddressLinePairList = this
                    .getAllAddressByCustomerId(this.customerId);
            this.deliveryStationService.synUpdateDeliveryStationRule(addressIdAndAddressLinePairList,
                    this.oldCoordinate, this.newCoordinate, this.customerId, this.stationId);
        }

        private List<AddressIdAndAddressLinePair> getAllAddressByCustomerId(Long customerId) {
            List<AddressIdAndAddressLinePair> pairList = new ArrayList<AddressIdAndAddressLinePair>();

            List<Address> customerAddressList = this.addressService.getAllBands(customerId);
            Set<Long> pathIdSet = new HashSet<Long>();
            for (Address customerAddress : customerAddressList) {
                String path = customerAddress.getPath();
                if ((path == null) || path.isEmpty()) {
                    continue;
                }
                String[] parts = path.split("-");
                for (String part : parts) {
                    pathIdSet.add(Long.valueOf(part));
                }
            }

            Map<Long, Address> addressMap = this.addressService.getAddressMapByIdSet(pathIdSet);
            for (Address customerAddress : customerAddressList) {
                AddressIdAndAddressLinePair pair = new AddressIdAndAddressLinePair();
                Stack<String> stack = new Stack<String>();
                stack.push(customerAddress.getName());
                pair.setAddressId(customerAddress.getId());
                Long parentId = customerAddress.getParentId();
                while (parentId != -1) {
                    Address address = addressMap.get(parentId);
                    stack.push(address.getName());
                    parentId = address.getParentId();
                }
                StringBuffer addressLine = new StringBuffer();
                while (!stack.isEmpty()) {
                    addressLine.append(stack.pop());
                }
                pair.setAddressLine(addressLine.toString());

                pairList.add(pair);
            }
            return pairList;
        }

        public Long getCustomerId() {
            return this.customerId;
        }

        public void setCustomerId(Long customerId) {
            this.customerId = customerId;
        }

        public Long getStationId() {
            return this.stationId;
        }

        public void setStationId(Long stationId) {
            this.stationId = stationId;
        }

        public String getOldCoordinate() {
            return this.oldCoordinate;
        }

        public void setOldCoordinate(String oldCoordinate) {
            this.oldCoordinate = oldCoordinate;
        }

        public String getNewCoordinate() {
            return this.newCoordinate;
        }

        public void setNewCoordinate(String newCoordinate) {
            this.newCoordinate = newCoordinate;
        }

        public DeliveryStationService getDeliveryStationService() {
            return this.deliveryStationService;
        }

        public void setDeliveryStationService(DeliveryStationService deliveryStationService) {
            this.deliveryStationService = deliveryStationService;
        }

    }
}
