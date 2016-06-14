
package cn.explink.web.controller;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import cn.explink.dao.BizLogDAO;
import cn.explink.domain.Address;
import cn.explink.domain.AddressImportDetail;
import cn.explink.domain.AddressImportResult;
import cn.explink.domain.Alias;
import cn.explink.domain.ClientApplication;
import cn.explink.domain.Deliverer;
import cn.explink.domain.DeliveryStation;
import cn.explink.domain.User;
import cn.explink.domain.enums.AddressImportDetailStatsEnum;
import cn.explink.domain.enums.LogTypeEnum;
import cn.explink.exception.ExplinkRuntimeException;
import cn.explink.gis.GeoCoder;
import cn.explink.gis.GeoPoint;
import cn.explink.modle.AjaxJson;
import cn.explink.modle.DataGrid;
import cn.explink.modle.DataGridReturn;
import cn.explink.modle.ImportProcessJson;
import cn.explink.modle.KeywordMatchedResult;
import cn.explink.qbc.CriteriaQuery;
import cn.explink.service.AddressImportResultService;
import cn.explink.service.AddressImportService;
import cn.explink.service.AddressService;
import cn.explink.service.ApplicationService;
import cn.explink.service.BizLogService;
import cn.explink.service.DelivererService;
import cn.explink.service.DeliveryStationService;
import cn.explink.service.LuceneService;
import cn.explink.tree.ZTreeNode;
import cn.explink.util.HqlGenerateUtil;
import cn.explink.util.StringUtil;
import cn.explink.util.SynInsertBizLogThread;
import cn.explink.web.vo.AddressImportTypeEnum;
import cn.explink.web.vo.AddressPosition;
import cn.explink.web.vo.CarrierMatchPointResponseHeadVO;
import cn.explink.web.vo.CarrierMatchPointResponseItemVO;
import cn.explink.web.vo.HeadVo;
import cn.explink.web.vo.ItemVo;
import cn.explink.web.vo.RequestVo;
import cn.explink.ws.vo.AddressMappingResult;
import cn.explink.ws.vo.AddressVo;
import cn.explink.ws.vo.ApplicationVo;
import cn.explink.ws.vo.DeliveryStationVo;
import cn.explink.ws.vo.OrderAddressMappingResult;
import cn.explink.ws.vo.OrderVo;
import cn.explink.ws.vo.ResultCodeEnum;

@RequestMapping("/address")
@Controller
public class AddressController extends BaseController {

    private static final Logger LOGGER = LoggerFactory.getLogger(AddressController.class);

    @Autowired
    private LuceneService luceneService;

    @Autowired
    private AddressService addressService;

    @Autowired
    private AddressImportService addressImportService;

    @Autowired
    private AddressImportResultService addressImportResultService;

    @Autowired
    private DeliveryStationService deliveryStationService;

    @Autowired
    private DelivererService delivererService;

    @Autowired
    private BizLogService bizLogService;

    @Autowired
    ApplicationService applicationService;

    @Autowired
    private BizLogDAO bizLogDAO;

    @RequestMapping("/index")
    public String index(Model model) {
        return "/address/index";
    }

    @RequestMapping("/getAddress")
    public String getAddress(Model model, @RequestParam(value = "addressId", required = false) Long addressId) {
        return "/address/getAddress";
    }

    @RequestMapping("/saveAddress")
    public String saveAddress(Model model, Address address) {
        this.addressService.createAndBindAddress(address, null, this.getCustomerId());
        model.addAttribute("address", address);
        return "/address/getAddress";
    }

    @RequestMapping("/searchAddress")
    public String searchAddress(Model model, @RequestParam(value = "addressLine", required = false) String addressLine)
            throws IOException, ParseException {
        if (!StringUtil.isEmpty(addressLine)) {
            this.luceneService.search(addressLine, this.getCustomerId());
        }
        return "/address/getAddress";
    }

    @RequestMapping("/createAlias")
    public String createAlias(Model model, Alias alias) throws IOException, ParseException {
        if (alias != null) {
            this.addressService.createAlias(alias);
        }
        return "/address/getAddress";
    }

    @RequestMapping("/getAddressTree")
    public @ResponseBody List<ZTreeNode> getAddressTree(@RequestParam(value = "id", required = false) Long parentId,
            @RequestParam(value = "ids", required = false) String ids,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "pageSize", required = false) Integer pageSize) {
        Long customerId = this.getCustomerId();
        List<ZTreeNode> list = this.addressService.getAsyncAddressPage(customerId, parentId, ids, page, pageSize);
        if (StringUtils.isNotBlank(ids)) {
            this.addressService.appendStation(customerId, list);
        }
        return list;
    }

    @RequestMapping("/getStationAddressTree")
    public @ResponseBody List<ZTreeNode> getStationAddressTree(
            @RequestParam(value = "id", required = false) Long parentId,
            @RequestParam(value = "level", required = false) Long level) {
        Long customerId = this.getCustomerId();
        return this.addressService.getStationAddressTree(customerId, parentId);
    }

    @RequestMapping("/getStationAddressTreePage")
    public @ResponseBody List<ZTreeNode> getStationAddressTree(
            @RequestParam(value = "id", required = false) Long parentId,
            @RequestParam(value = "level", required = false) Long level,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "pageSize", required = false) Integer pageSize) {
        Long customerId = this.getCustomerId();
        return this.addressService.getStationAddressTreePage(customerId, parentId, page, pageSize);
    }

    @RequestMapping("/getZTree")
    public @ResponseBody List<ZTreeNode> getZTree(String name, Integer band) {
        Long customerId = this.getCustomerId();
        return this.addressService.getZAddress(customerId, name, band);
    }

    @RequestMapping("/getAllAddress")
    public @ResponseBody List<ZTreeNode> getAllAddress() {
        Long customerId = this.getCustomerId();
        List<ZTreeNode> result = this.addressService.getAllAddress(customerId);
        this.addressService.appendStation(customerId, result);
        return result;
    }

    @RequestMapping("/getAdressByStation")
    public @ResponseBody List<ZTreeNode> getAdressByStation(String stationId) {
        Long customerId = this.getCustomerId();
        return this.addressService.getAdressByStation(customerId, stationId);
    }

    /**
     * @param model
     * @param parentId
     * @return
     */
    @RequestMapping("/addressImportPage")
    public String addressImportPage(Model model) {
        return "address/importDatagrid";
    }

    @RequestMapping("/addressMapping")
    public String addressMapping(Model model) {
        return "address/addressMapping";
    }

    @RequestMapping("/keywordMapping")
    public String keywordMapping(Model model) {
        return "address/keywordMapping";
    }

    @RequestMapping("/downloadAddressTemplate")
    public String downloadAddressTemplate(Model model, HttpServletRequest request, HttpServletResponse response) {
        List<String> headerNameList = new ArrayList<String>();
        headerNameList.add("省/直辖市");
        headerNameList.add("市");
        headerNameList.add("区");
        headerNameList.add("地址1");
        headerNameList.add("地址2");
        headerNameList.add("地址3");
        headerNameList.add("站点");
        headerNameList.add("配送员");
        XSSFWorkbook wb = this.addressImportService.createAddressTemplate(headerNameList);
        String fileName = "地址导入模板.xlsx";
        this.setDownloadFileName(response, fileName);
        try {
            ServletOutputStream out = response.getOutputStream();
            wb.write(out);
            out.close();
        } catch (IOException e) {
            AddressController.LOGGER.error(e.getMessage());
        }
        return null;
    }

    /**
     * Excel导入关键字
     * @param request
     * @param response
     * @param file
     * @param importType
     * @param stationId
     * @return
     */
    @RequestMapping("/importAddress")
    public @ResponseBody AjaxJson importAddress(HttpServletRequest request, HttpServletResponse response,
            MultipartFile file, Integer importType, Long stationId) {
        if (importType == null) {
            importType = AddressImportTypeEnum.init.getValue();
        }
        InputStream in = null;
        AjaxJson aj = new AjaxJson();
        try {
            in = file.getInputStream();
            AddressImportResult addressImportResult = this.importAddress(in, this.getLogginedUser(), importType,
                    stationId, request);
            if (null == addressImportResult) {
                aj.setSuccess(false);
                aj.setMsg("数据异常");
                return aj;
            }
            aj.setSuccess(true);
            aj.setMsg(addressImportResult.getId() + "");
            if (addressImportResult.getAddressImportDetails() != null) {
                for (AddressImportDetail a : addressImportResult.getAddressImportDetails()) {
                    a.setAddressImportResult(null);
                }
            }
            request.getSession().setAttribute("list", addressImportResult.getAddressImportDetails());
            aj.setInfo("导入成功：" + addressImportResult.getSuccessCount() + "个；导入失败："
                    + addressImportResult.getFailureCount() + "个");
        } catch (Exception e) {
            AddressController.LOGGER.info(e.getMessage());
            aj.setSuccess(false);
            aj.setInfo("导入失败！");
        }
        return aj;
    }

    /**
     * Excel导入关键字
     * @param request
     * @param response
     * @param file
     * @param importType
     * @param stationId
     * @return
     */
    @RequestMapping("/moveAddress")
    public @ResponseBody AjaxJson moveAddress(HttpServletRequest request, HttpServletResponse response,
            MultipartFile file) {
        InputStream in = null;
        AjaxJson aj = new AjaxJson();
        try {
            in = file.getInputStream();
            AddressImportResult addressImportResult = this.moveAddress(in, this.getLogginedUser());
            if (null == addressImportResult) {
                aj.setSuccess(false);
                aj.setMsg("数据异常");
                return aj;
            }
            aj.setSuccess(true);
            aj.setMsg(addressImportResult.getId() + "");
            request.getSession().setAttribute("list", addressImportResult.getAddressImportDetails());
            aj.setInfo("导入成功：" + addressImportResult.getSuccessCount() + "个；导入失败："
                    + addressImportResult.getFailureCount() + "个");
        } catch (Exception e) {
            AddressController.LOGGER.info(e.getMessage());
            aj.setSuccess(false);
            aj.setInfo(e.getMessage());
        }
        return aj;
    }

    @RequestMapping("/getPromtInfo")
    public @ResponseBody AjaxJson getPromtInfo(HttpServletRequest request, HttpServletResponse response) {
        AjaxJson aj = new AjaxJson();
        Long customerId = this.getCustomerId();
        Map map = this.addressService.getAdressPromtInfo(customerId);
        aj.setAttributes(map);
        aj.setSuccess(true);
        return aj;
    }

    @RequestMapping("/getImportDetail")
    public @ResponseBody List<AddressImportDetail> getImportDetail(HttpServletRequest request,
            HttpServletResponse response) {
        Set<AddressImportDetail> set = (HashSet<AddressImportDetail>) request.getSession().getAttribute("list");
        if (set != null) {
            return new ArrayList<AddressImportDetail>((HashSet) request.getSession().getAttribute("list"));
        } else {
            return new ArrayList<AddressImportDetail>();
        }
    }

    /**
     * 获取导入进度
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("/getImportProc")
    public @ResponseBody ImportProcessJson getImportProc(HttpServletRequest request, HttpServletResponse response) {
        if (request.getSession().getAttribute("proc") != null) {
            ImportProcessJson pr = (ImportProcessJson) request.getSession().getAttribute("proc");
            pr.cal();
            if (pr.isFinish()) {
                request.getSession().removeAttribute("proc");
            }
            return pr;
        } else {
            return null;
        }

    }

    @RequestMapping("/deleteImportAddressResult")
    public @ResponseBody AjaxJson deleteImportAddressResult(Model model,
            @RequestParam(value = "id", required = false) Long id, HttpServletRequest request) {
        AjaxJson aj = new AjaxJson();
        this.addressImportService.deleteImportAddressResult(id, this.getCustomerId());
        AddressController.LOGGER.info("删除导入结果：{}", "IP:" + this.getUserIp(request) + " id=" + id);
        aj.setSuccess(true);
        return aj;
    }

    @RequestMapping("/downloadFailureMsg")
    public String downloadFailureMsg(@RequestParam(value = "resultId", required = false) Long resultId,
            HttpServletRequest request, HttpServletResponse response) {
        List<String> headerNameList = new ArrayList<String>();
        headerNameList.add("信息");
        headerNameList.add("省/直辖市");
        headerNameList.add("市");
        headerNameList.add("区");
        headerNameList.add("地址1");
        headerNameList.add("地址2");
        headerNameList.add("地址3");
        headerNameList.add("站点");

        List<List<String>> importResultList = this.addressImportResultService.getImportDetailByResultId(resultId);
        XSSFWorkbook wb = this.addressImportResultService.createAddressImportResultFile(headerNameList,
                importResultList);
        String fileName = "导入错误信息.xlsx";
        this.setDownloadFileName(response, fileName);
        try {
            ServletOutputStream out = response.getOutputStream();
            wb.write(out);
            out.close();
        } catch (IOException e) {
            AddressController.LOGGER.error(e.getMessage());
        }
        return null;
    }

    @RequestMapping("/datagrid")
    public @ResponseBody DataGridReturn datagrid(AddressImportDetail addressImportDetail, HttpServletRequest request,
            HttpServletResponse response, DataGrid dataGrid) {
        CriteriaQuery cq = new CriteriaQuery(AddressImportDetail.class, dataGrid);
        AddressImportResult addressImportResult = new AddressImportResult();
        addressImportResult.setId(Long.parseLong(request.getParameter("resultId")));
        addressImportDetail.setAddressImportResult(addressImportResult);
        HqlGenerateUtil.installHql(cq, addressImportDetail, request.getParameterMap());
        return this.addressImportService.getDataGridReturn(cq, true);
    }

    @RequestMapping("/subdatagrid")
    public @ResponseBody DataGridReturn subdatagrid(AddressImportResult addressImportResult,
            HttpServletRequest request, HttpServletResponse response, DataGrid dataGrid) {
        if (StringUtils.isBlank(dataGrid.getSort())) {
            dataGrid.setSort("importDate");
        }

        CriteriaQuery cq = new CriteriaQuery(AddressImportResult.class, dataGrid);
        String begin = request.getParameter("importDate_begin");
        String end = request.getParameter("importDate_end");
        try {
            if (StringUtils.isNotBlank(begin)) {
                Date beginDate = DateUtils.parseDate(begin, "yyyy-MM-dd");
                cq.ge("importDate", beginDate);
            }
            if (StringUtils.isNotBlank(end)) {
                Date endDate = org.apache.commons.lang.time.DateUtils
                        .addDays(DateUtils.parseDate(end, "yyyy-MM-dd"), 1);
                cq.le("importDate", endDate);
            }
        } catch (java.text.ParseException e) {
            AddressController.LOGGER.error(e.getMessage());
        }
        addressImportResult.setUserId(this.getCustomerId());
        HqlGenerateUtil.installHql(cq, addressImportResult, request.getParameterMap());
        return this.addressImportResultService.getDataGridReturn(cq, true);

    }

    @RequestMapping("/del")
    public @ResponseBody AjaxJson del(AddressImportResult addressImportResult, HttpServletRequest request,
            HttpServletResponse response, DataGrid dataGrid) {
        AjaxJson aj = new AjaxJson();
        this.addressImportResultService.delete(addressImportResult.getId());
        AddressController.LOGGER.info("删除导入结果：{}",
                "IP:" + this.getUserIp(request) + " id=" + addressImportResult.getId());
        aj.setSuccess(true);
        return aj;

    }

    @RequestMapping("/parseAdress")
    public @ResponseBody AjaxJson parseAdress(String needMatched, HttpServletRequest request,
            HttpServletResponse response) {
        AjaxJson aj = new AjaxJson();
        Long customerId = this.getCustomerId();
        List<OrderVo> list = new ArrayList<OrderVo>();
        for (String addressLine : needMatched.split("\n")) {
            if (addressLine.trim().length() == 0) {
                continue;
            }
            OrderVo order = new OrderVo();
            order.setCustomerId(customerId);
            order.setAddressLine(addressLine);
            list.add(order);
        }
        try {
            Map<String, Object> attributes = this.addressService.txNoneMatch(customerId, list);
            attributes.put("insum", list.size());
            aj.setAttributes(attributes);

        } catch (Exception e) {
            aj.setSuccess(false);
            aj.setMsg("匹配异常" + e.getMessage());
        }
        aj.setSuccess(true);
        aj.setMsg("完成匹配");
        return aj;

    }

    /**
     * 关键字匹配.
     * @param needMatched
     * @param request
     * @param response
     * @return
     * @throws IOException
     * @throws ParseException
     */
    @RequestMapping("/matchKeyword")
    public @ResponseBody KeywordMatchedResult matchKeyword(String needMatched, HttpServletRequest request,
            HttpServletResponse response) throws IOException, ParseException {
        Long customerId = this.getCustomerId();
        if (StringUtil.isEmpty(needMatched)) {
            return null;
        }
        KeywordMatchedResult result = this.luceneService.getKeyWordMatchResult(needMatched, customerId);
        this.addressService.appendStation(customerId, result.getzTreeNodeList());
        return result;
    }

    @RequestMapping("/getPointByAddress")
    public @ResponseBody List<AddressPosition> getPointByAddress(String needMatched, HttpServletRequest request,
            HttpServletResponse response) throws IOException, ParseException {
        List<AddressPosition> addressPositionList = new ArrayList<AddressPosition>();
        if (StringUtil.isEmpty(needMatched)) {
            return null;
        }
        for (String addressLine : needMatched.split("\n")) {
            if (addressLine.trim().length() == 0) {
                continue;
            }
            GeoPoint position = GeoCoder.getInstance().getGeoCoder().GetLocationDetails(addressLine);
            if (position == null) {
                continue;
            }
            AddressPosition addressPosition = new AddressPosition();
            addressPosition.setAddressLine(addressLine);
            addressPosition.setLat(position.getLat());
            addressPosition.setLng(position.getLng());

            addressPositionList.add(addressPosition);
        }

        return addressPositionList;
    }

    @RequestMapping("/getUnbindInfo")
    public @ResponseBody KeywordMatchedResult getUnbindInfo(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ParseException {
        Long customerId = this.getCustomerId();

        // TODO
        KeywordMatchedResult result = this.luceneService.getKeyWordMatchResult("人民医院", customerId);
        return result;
    }

    /**
     * 地址库维护
     * @param model
     * @return
     */
    @RequestMapping("/addressEditPage")
    public String addressEditPage(Model model) {
        return "address/addressEditPage";
    }

    /**
     * 站点关键词导入
     * @param model
     * @return
     */
    @RequestMapping("/importStationAddress")
    public String importStationAddress(Model model) {
        return "address/importStationAddress";
    }

    /*
     * 新增关键词
     */
    @RequestMapping("/add")
    public @ResponseBody AjaxJson add(@RequestParam(value = "stationId") Long stationId,
            @RequestParam(value = "parentId", required = false) Long parentId,
            @RequestParam(value = "addresses", required = false) String addresses, HttpServletRequest request) {
        AjaxJson aj = new AjaxJson();
        aj.setSuccess(true);
        Long customerId = this.getCustomerId();
        List<Address> list = null;
        List<ZTreeNode> zList = null;
        try {
            if (stationId != null) {
                list = this.addressService.addAddressWithStation(parentId, addresses, stationId, customerId);
            } else {
                list = this.addressService.addAddress(parentId, addresses, customerId);
            }
            zList = this.transAddress(list);
            // 输出日志，并且收集保存日志 --刘武强 11.26
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("addresses", addresses);
            map.put("parentId", parentId);
            ExecutorService service = Executors.newCachedThreadPool();
            service.execute(new SynInsertBizLogThread(AddressController.class, customerId, LogTypeEnum.addAddress
                    .getValue(), this.getUserIp(request), map, this.bizLogDAO, this.bizLogService, this.addressService,
                    null));
            service.shutdown();

            // AddressController.LOGGER.info("添加关键词：{}", addresses);
        } catch (Exception e) {
            aj.setSuccess(false);
            aj.setMsg(e.getMessage());
        }
        aj.setObj(zList);
        return aj;
    }

    private List<ZTreeNode> transAddress(List<Address> list) {
        List<ZTreeNode> nlist = new ArrayList<ZTreeNode>();
        if (list != null) {
            for (Address a : list) {
                ZTreeNode node = new ZTreeNode(a.getName(), a.getId(), a.getParentId(), a.getAddressLevel());
                nlist.add(node);
            }
        }
        return nlist;
    }

    /**
     * 添加别名
     * @param addressId
     * @param alias
     * @return
     */
    @RequestMapping("/addAlias")
    public @ResponseBody AjaxJson addAlias(@RequestParam(value = "addressId") Long addressId,
            @RequestParam(value = "alias", required = false) String alias, HttpServletRequest request) {
        AjaxJson aj = null;
        Long customerId = this.getCustomerId();
        if ((alias != null) && (addressId != null)) {
            aj = this.addressService.addAlias(addressId, alias, customerId);
            // 输出日志，并且收集保存日志 --刘武强 11.26
            ExecutorService service = Executors.newCachedThreadPool();
            service.execute(new SynInsertBizLogThread(AddressController.class, customerId, LogTypeEnum.addAlias
                    .getValue(), this.getUserIp(request), aj.getObj(), this.bizLogDAO, this.bizLogService,
                    this.addressService, null));
            service.shutdown();
        }
        return aj;
    }

    /**
     * @Title: createAliasIndex
     * @description 给所有的别名生成索引（当客户的索引需要重新生成的时候，定时任务只能完成关键词的索引生成，而不能生成别名的索引）
     * @author 刘武强
     * @date 2016年1月4日下午4:44:41
     * @param @param request
     * @param @return
     * @return AjaxJson
     * @throws
     */
    @RequestMapping("/createAliasIndex")
    public @ResponseBody AjaxJson createAliasIndex(HttpServletRequest request) {
        AjaxJson aj = null;
        List<Alias> aliasList = new ArrayList<Alias>();
        aliasList = this.addressService.getListAliasByCustomerId(5L);
        for (Alias alias : aliasList) {
            try {
                this.addressService.updateIndexRightNow(null, alias.getId());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return aj;
    }

    /**
     * 获取别名
     * @param addressId
     * @param alias
     * @return
     */
    @RequestMapping("/getAlias")
    public @ResponseBody List<Alias> getAlias(@RequestParam(value = "addressId") Long addressId) {
        Long customerId = this.getCustomerId();
        return this.addressService.getAliasByAddressId(addressId, customerId);
    }

    /**
     * 删除别名
     * @param addressId
     * @return
     */
    @RequestMapping("/delAlias")
    public @ResponseBody AjaxJson delAlias(@RequestParam(value = "id") Long id, HttpServletRequest request) {
        AjaxJson aj = new AjaxJson();
        aj.setSuccess(true);
        // 获取别名对象 --刘武强 11.27
        Alias Alias = this.addressService.getAliasById(id);
        this.addressService.deleteAlias(id);
        // AddressController.LOGGER.info("删除别名：{}", "IP:" + this.getUserIp(request) + " id=" + id);
        // 输出日志，并且收集保存日志 --刘武强 11.26
        ExecutorService service = Executors.newCachedThreadPool();
        service.execute(new SynInsertBizLogThread(AddressController.class, this.getCustomerId(),
                LogTypeEnum.deleteAlias.getValue(), this.getUserIp(request), Alias, this.bizLogDAO, this.bizLogService,
                this.addressService, null));
        service.shutdown();
        return aj;
    }

    /**
     * 删除关键词
     * @param addressId
     * @return
     */
    @RequestMapping("/delAddress")
    public @ResponseBody AjaxJson delAddress(Long addressId, HttpServletRequest request) {
        AjaxJson aj = new AjaxJson();
        aj.setSuccess(true);
        try {
            this.addressService.deleteAddress(addressId, this.getCustomerId());
            ExecutorService service = Executors.newCachedThreadPool();
            service.execute(new SynInsertBizLogThread(AddressController.class, this.getCustomerId(),
                    LogTypeEnum.deleteAddress.getValue(), this.getUserIp(request), addressId, this.bizLogDAO,
                    this.bizLogService, this.addressService, null));
            service.shutdown();
            // AddressController.LOGGER.info("删除关键词：{}", "IP:" + this.getUserIp(request) + " customerId=" +
            // this.getCustomerId() + " addressId=" + addressId);
        } catch (Exception e) {
            AddressController.LOGGER.error(e.getMessage());
            aj.setSuccess(false);
        }
        return aj;
    }

    /**
     * @param in
     * @return
     * @throws IOException
     */
    public AddressImportResult importAddress(InputStream in, User user, Integer importType, Long stationId,
            HttpServletRequest req) throws Exception {
        Long customerId = user.getCustomer().getId();
        AddressImportResult result = new AddressImportResult();
        List<AddressImportDetail> details = new ArrayList<AddressImportDetail>();
        Map<String, Address> map = new HashMap<String, Address>();// 省市区地址MAP(Key:省-市-区)
        Map<String, Address> addressMap = new HashMap<String, Address>();// 关键字MAP(Key:父ID-名称)
        Map<String, DeliveryStation> stationMap = new HashMap<String, DeliveryStation>(); // 站点MAP(Key:客户ID-名称)
        Map<String, Deliverer> delivererMap = new HashMap<String, Deliverer>();// 小件员MAP(Key:客户ID-名称)
        Map<Long, Address> bindMap = new HashMap<Long, Address>();// 客户已经包含的地址列表
        Set<String> addressNames = new HashSet<String>();
        Set<String> adminNames = new HashSet<String>();
        XSSFWorkbook wb = new XSSFWorkbook(in);
        XSSFSheet sheet = wb.getSheetAt(0);
        int rowNum = 1;
        try {
            while (true) {
                XSSFRow row = sheet.getRow(rowNum);
                if (row == null) {
                    break;
                }
                rowNum++;
                String province = row.getCell(0) == null ? null : row.getCell(0).getStringCellValue();
                String city = row.getCell(1) == null ? null : row.getCell(1).getStringCellValue();
                String district = row.getCell(2) == null ? null : row.getCell(2).getStringCellValue();
                String address1 = row.getCell(3) == null ? null : row.getCell(3).getStringCellValue();
                String address2 = row.getCell(4) == null ? null : row.getCell(4).getStringCellValue();
                String address3 = row.getCell(5) == null ? null : row.getCell(5).getStringCellValue();
                String deliveryStationName = row.getCell(6) == null ? null : row.getCell(6).getStringCellValue();
                String delivererName = row.getCell(7) == null ? null : row.getCell(7).getStringCellValue();

                this.addressImportService.addNonNullValue(adminNames, province);
                this.addressImportService.addNonNullValue(adminNames, city);
                this.addressImportService.addNonNullValue(adminNames, district);
                this.addressImportService.addNonNullValue(addressNames, address1);
                this.addressImportService.addNonNullValue(addressNames, address2);
                this.addressImportService.addNonNullValue(addressNames, address3);

                AddressImportDetail detail = new AddressImportDetail();
                detail.setProvince(StringUtils.trim(province));
                detail.setCity(StringUtils.trim(city));
                detail.setDistrict(StringUtils.trim(district));
                detail.setAddress1(StringUtils.trim(address1));
                detail.setAddress2(StringUtils.trim(address2));
                detail.setAddress3(StringUtils.trim(address3));
                detail.setDeliveryStationName(StringUtils.trim(deliveryStationName));
                detail.setDelivererName(StringUtils.trim(delivererName));
                detail.setAddressImportResult(result);
                details.add(detail);
            }
        } catch (Exception e) {
            String msg = "关键词导入异常，异常行[" + rowNum + "],请检查该行格式是否正确！";
            throw new Exception(msg, e);
        }

        // 查找关键词并构造addressMap
        List<Address> addressList = this.addressService.getAddressByNames(addressNames);
        if ((addressList != null) && !addressList.isEmpty()) {
            for (Address a : addressList) {
                addressMap.put(a.getParentId() + "-" + a.getName(), a);
            }
        }
        // 查找所有行政关键词并构造map
        List<Address> list = this.addressService.getAdministrationAddress(adminNames, customerId);
        if ((list != null) && !list.isEmpty()) {
            Map<String, String> m = new HashMap<String, String>();
            for (Address a : list) {
                m.put(a.getId() + "", a.getName());
            }
            for (Address a : list) {
                if (Integer.valueOf(3).equals(a.getAddressLevel())) {
                    String path = a.getPath();
                    String[] ids = path.split("-");
                    map.put(m.get(ids[1]) + "-" + m.get(ids[2]) + "-" + a.getName(), a);
                }
            }
        }
        // 构造所有站点Map
        List<DeliveryStation> stationList = this.deliveryStationService.listAll(customerId);
        if ((stationList != null) && !stationList.isEmpty()) {
            for (DeliveryStation ds : stationList) {
                stationMap.put(customerId + "-" + ds.getName(), ds);
            }
        }

        // 构造所有小件员Map
        List<Deliverer> delivererList = this.delivererService.listAll(customerId);

        if ((delivererList != null) && !delivererList.isEmpty()) {
            for (Deliverer d : delivererList) {
                delivererMap.put(customerId + "-" + d.getName(), d);
            }
        }
        // 构造该客户的绑定地址
        List<Address> bandList = this.addressService.getAllBands(customerId);
        if (bandList != null) {
            for (Address a : bandList) {
                bindMap.put(a.getId(), a);
            }
        }
        ImportProcessJson proc = new ImportProcessJson();
        req.getSession().setAttribute("proc", proc);
        proc.setTotal(details.size());
        for (AddressImportDetail detail : details) {
            try {
                this.addressImportService.txNewImportDetail(map, detail, addressMap, stationMap, delivererMap, bindMap,
                        customerId, importType, stationId);
                if (new Integer(AddressImportDetailStatsEnum.failure.getValue()).equals(detail.getStatus())) {
                    proc.setFailure(proc.getFailure() + 1);
                } else {
                    proc.setSuccess(proc.getSuccess() + 1);
                }
                proc.setProcessed(proc.getProcessed() + 1);

            } catch (Exception e) {
                detail.setStatus(AddressImportDetailStatsEnum.failure.getValue());
                detail.setMessage(e.getMessage());
                AddressController.LOGGER.info(e.getMessage());
                proc.setFailure(proc.getFailure() + 1);
                proc.setProcessed(proc.getProcessed() + 1);
            }
        }
        proc.setFinish(true);
        Set<AddressImportDetail> detailSet = new HashSet<AddressImportDetail>();
        detailSet.addAll(details);
        result.setAddressImportDetails(detailSet);
        int successCount = 0;
        int failureCount = 0;
        for (AddressImportDetail detail : details) {
            if ((detail.getStatus() != null)
                    && (detail.getStatus().intValue() == AddressImportDetailStatsEnum.success.getValue())) {
                successCount++;
            } else {
                failureCount++;
            }
        }
        result.setSuccessCount(successCount);
        result.setFailureCount(failureCount);
        result.setImportDate(new Date());
        result.setUserId(user.getId());
        if (importType == AddressImportTypeEnum.init.getValue()) {
            this.addressImportResultService.save(result);
        }
        return result;
    }

    public AddressImportResult moveAddress(InputStream in, User user) throws Exception {
        Long customerId = user.getCustomer().getId();
        AddressImportResult result = new AddressImportResult();
        List<AddressImportDetail> details = new ArrayList<AddressImportDetail>();
        Map<String, Address> map = new HashMap<String, Address>();// 省市区地址MAP(Key:省-市-区)
        Map<String, Address> addressMap = new HashMap<String, Address>();// 关键字MAP(Key:父ID-名称)
        Map<String, DeliveryStation> stationMap = new HashMap<String, DeliveryStation>(); // 站点MAP(Key:客户ID-名称)
        Map<String, Deliverer> delivererMap = new HashMap<String, Deliverer>();// 小件员MAP(Key:客户ID-名称)
        Map<Long, Address> bindMap = new HashMap<Long, Address>();// 客户已经包含的地址列表
        Set<String> addressNames = new HashSet<String>();
        Set<String> adminNames = new HashSet<String>();
        XSSFWorkbook wb = new XSSFWorkbook(in);
        XSSFSheet sheet = wb.getSheetAt(0);
        int rowNum = 1;
        try {
            while (true) {
                XSSFRow row = sheet.getRow(rowNum);
                if (row == null) {
                    break;
                }
                rowNum++;
                String province = row.getCell(0) == null ? null : row.getCell(0).getStringCellValue();
                String city = row.getCell(1) == null ? null : row.getCell(1).getStringCellValue();
                String district = row.getCell(2) == null ? null : row.getCell(2).getStringCellValue();
                String address1 = row.getCell(3) == null ? null : row.getCell(3).getStringCellValue();
                String address2 = row.getCell(4) == null ? null : row.getCell(4).getStringCellValue();
                String address3 = row.getCell(5) == null ? null : row.getCell(5).getStringCellValue();
                String deliveryStationOldName = row.getCell(6) == null ? null : row.getCell(6).getStringCellValue();
                String deliveryStationName = row.getCell(7) == null ? null : row.getCell(7).getStringCellValue();

                this.addressImportService.addNonNullValue(adminNames, province);
                this.addressImportService.addNonNullValue(adminNames, city);
                this.addressImportService.addNonNullValue(adminNames, district);
                this.addressImportService.addNonNullValue(addressNames, address1);
                this.addressImportService.addNonNullValue(addressNames, address2);
                this.addressImportService.addNonNullValue(addressNames, address3);

                AddressImportDetail detail = new AddressImportDetail();
                detail.setProvince(province);
                detail.setCity(city);
                detail.setDistrict(district);
                detail.setAddress1(address1);
                detail.setAddress2(address2);
                detail.setAddress3(address3);
                detail.setDeliveryStationOldName(deliveryStationOldName);
                detail.setDeliveryStationName(deliveryStationName);
                detail.setAddressImportResult(result);
                details.add(detail);
            }
        } catch (Exception e) {
            String msg = "关键词导入异常，异常行[" + rowNum + "],请检查该行格式是否正确！";
            throw new Exception(msg, e);
        }

        // 查找客户已有关键词并构造addressMap
        List<Address> addressList = this.addressService.getAddressByNames(addressNames);
        if ((addressList != null) && !addressList.isEmpty()) {
            for (Address a : addressList) {
                addressMap.put(a.getParentId() + "-" + a.getName(), a);
            }
        }
        // 查找所有行政关键词并构造map
        List<Address> list = this.addressService.getAdministrationAddress(adminNames, customerId);
        if ((list != null) && !list.isEmpty()) {
            Map<String, String> m = new HashMap<String, String>();
            for (Address a : list) {
                m.put(a.getId() + "", a.getName());
            }
            for (Address a : list) {
                if (new Integer(3).equals(a.getAddressLevel())) {
                    String path = a.getPath();
                    String[] ids = path.split("-");
                    map.put(m.get(ids[1]) + "-" + m.get(ids[2]) + "-" + a.getName(), a);
                }
            }
        }
        // 构造所有站点Map
        List<DeliveryStation> stationList = this.deliveryStationService.listAll(customerId);
        if ((stationList != null) && !stationList.isEmpty()) {
            for (DeliveryStation ds : stationList) {
                stationMap.put(customerId + "-" + ds.getName(), ds);
            }
        }

        // 构造该客户的绑定地址
        List<Address> bandList = this.addressService.getAllBands(customerId);
        if (bandList != null) {
            for (Address a : bandList) {
                bindMap.put(a.getId(), a);
            }
        }
        for (AddressImportDetail detail : details) {
            try {
                this.addressImportService.txNewMoveDetail(map, detail, addressMap, stationMap, delivererMap, bindMap,
                        customerId);
            } catch (Exception e) {
                detail.setStatus(AddressImportDetailStatsEnum.failure.getValue());
                detail.setMessage(e.getMessage());
                AddressController.LOGGER.info(e.getMessage());
            }
        }
        Set<AddressImportDetail> detailSet = new HashSet<AddressImportDetail>();
        detailSet.addAll(details);
        result.setAddressImportDetails(detailSet);
        int successCount = 0;
        int failureCount = 0;
        for (AddressImportDetail detail : details) {
            if ((detail.getStatus() != null)
                    && (detail.getStatus().intValue() == AddressImportDetailStatsEnum.success.getValue())) {
                successCount++;
            } else {
                failureCount++;
            }
        }
        result.setSuccessCount(successCount);
        result.setFailureCount(failureCount);
        result.setImportDate(new Date());
        result.setUserId(user.getId());
        return result;
    }

    /**
     * 返回头部信息+站点匹配情况
     * @param expMsg
     * @param responseItems
     * @return
     */
    public String returnXml(CarrierMatchPointResponseHeadVO head, List<CarrierMatchPointResponseItemVO> responseItems) {
        StringBuilder sb = new StringBuilder();
        sb.append("<response><head>" + "<msg><![CDATA[" + head.getMsg() + "]]></msg>" + "</head>");
        if ((responseItems != null) && (responseItems.size() > 0)) {
            sb.append("<items>");

            for (CarrierMatchPointResponseItemVO responseItem : responseItems) {
                sb.append("<item>" + "<itemno>" + responseItem.getItemno() + "</itemno>" + "<netid>"
                        + responseItem.getNetid() + "</netid>" + "<netpoint><![CDATA[" + responseItem.getNetpoint()
                        + "]]></netpoint>" + "<tpsnetpoint><![CDATA[" + responseItem.getTpsnetpoint()
                        + "]]></tpsnetpoint>" + "<remark><![CDATA[" + responseItem.getRemark() + "]]></remark>"
                        + "</item>");

            }
            sb.append("</items>");
        }
        sb.append("</response>");
        String ret = sb.toString();
        AddressController.LOGGER.info("请求地址库 返回xml:{}", ret);
        return ret;
    }

    /**
     * 删除关键词
     * @param addressId
     * @return
     * @throws UnsupportedEncodingException
     */
    @RequestMapping("/matchAddress")
    @ResponseBody
    public String matchAddress(HttpServletRequest request, HttpServletResponse response)
            throws UnsupportedEncodingException {

        RequestVo requstVo = new RequestVo();
        CarrierMatchPointResponseHeadVO head = new CarrierMatchPointResponseHeadVO();
        List<CarrierMatchPointResponseItemVO> responItems = new ArrayList<CarrierMatchPointResponseItemVO>();
        if ((request.getParameter("data") == null) || StringUtils.isEmpty(request.getParameter("data"))) {
            // todo 请求参数不能为空
            head.setMsg("请求参数不能为空");
        } else {
            head.setMsg("");
            String xml = new String(request.getParameter("data"));
            request.setCharacterEncoding("UTF-8");
            LOGGER.info("請求站點匹配  matchAddress.request={} ", xml);
            try {

                requstVo = this.decodeRequest(xml);

                ApplicationVo applicationVo = new ApplicationVo();
                applicationVo.setId(Long.parseLong(requstVo.getHead().getUsercode()));

                ClientApplication application = this.applicationService.getClientApplicationById(applicationVo.getId());
                String md5Key = StringUtil.MD5(application.getId() + application.getPassword()
                        + requstVo.getHead().getBatchno().toLowerCase());
                if (!md5Key.toLowerCase().equals(requstVo.getHead().getKey().toLowerCase())) {
                    throw new ExplinkRuntimeException(" MD5校验失败,请求key:[" + requstVo.getHead().getKey() + "],解析:["
                            + md5Key + "]");
                }
                applicationVo.setCustomerId(application.getCustomerId());
                if (requstVo.getItems() != null) {
                    for (ItemVo itemVo : requstVo.getItems()) {
                        List<OrderVo> orderList = new ArrayList<OrderVo>();
                        CarrierMatchPointResponseItemVO responItem = new CarrierMatchPointResponseItemVO();
                        responItem.setNetid("");
                        responItem.setNetpoint("");
                        responItem.setRemark("");
                        responItem.setTpsnetpoint("");
                        OrderVo vo = new OrderVo();
                        vo.setAddressLine(itemVo.getProvince() + itemVo.getCity() + itemVo.getArea() + itemVo.getTown()
                                + itemVo.getAddress());
                        vo.setCustomerId(applicationVo.getCustomerId());
                        vo.setOrderId(itemVo.getItemno() + "");
                        vo.setVendorId(applicationVo.getCustomerId());
                        orderList.add(vo);
                        long start = System.currentTimeMillis();
                        LOGGER.info(" 匹配开始  地址={} ,remark={} ", vo.getAddressLine(), itemVo.getRemark());
                        AddressMappingResult result = this.mappingAddress(applicationVo, orderList);
                        if (result != null) {
                            Map<String, OrderAddressMappingResult> resultMap = result.getResultMap();

                            OrderAddressMappingResult mapResult = resultMap.get(vo.getOrderId());
                            responItem.setItemno(itemVo.getItemno());
                            responItems.add(this.handResult(responItem, mapResult));
                        } else {
                            LOGGER.warn(" 匹配开始  地址={} 失败 ", vo.getAddressLine());
                        }
                        LOGGER.info(" 匹配开始  地址={} ,result={},time= " + (System.currentTimeMillis() - start),
                                vo.getAddressLine(), responItem.toString());
                    }

                }
            } catch (Exception e) {
                LOGGER.error("匹配异常:" + e.toString(), e);
                head.setMsg("匹配异常:" + e.toString());
            }
        }
        return this.returnXml(head, responItems);
    }

    /**
     * 方法概要
     * <p>
     * 方法详细描述
     * </p>
     * @param responItems
     * @param responItem
     * @param mapResult
     * @param msgBuilder
     * @since 1.0
     */
    private CarrierMatchPointResponseItemVO handResult(CarrierMatchPointResponseItemVO responItem,
            OrderAddressMappingResult mapResult) {

        StringBuilder msgBuilder = new StringBuilder();
        msgBuilder.append("");
        if ((mapResult.getDeliveryStationList() != null) && (mapResult.getDeliveryStationList().size() > 0)) {
            if (mapResult.getDeliveryStationList().size() == 1) {
                DeliveryStationVo stationVo = mapResult.getDeliveryStationList().get(0);
                responItem.setNetid(stationVo.getExternalId() + "");
                responItem.setNetpoint(stationVo.getName());
                responItem.setTpsnetpoint(stationVo.getStationCode());

            } else if (mapResult.getDeliveryStationList().size() > 1) {
                // todo 匹配多站
                msgBuilder.append("匹配多个站点:");
                for (DeliveryStationVo stationVo : mapResult.getDeliveryStationList()) {
                    msgBuilder.append(stationVo.getName() + ",");
                }
            }
        } else {
            // 匹配站点失败
            if ((mapResult.getAddressList() == null) || CollectionUtils.isEmpty(mapResult.getAddressList())) {
                msgBuilder.append("该地址无法匹配关键词,请维护关键词");
            } else {
                msgBuilder.append("关键词[");
                for (AddressVo addressVo : mapResult.getAddressList()) {
                    msgBuilder.append(addressVo.getName() + ",");
                }
                msgBuilder.append("]没有维护站点");
            }
        }
        responItem.setRemark(msgBuilder.toString());
        return responItem;
    }

    protected ClientApplication validateApplication(ApplicationVo applicationVo) {
        ClientApplication clientApplication = this.applicationService.validateClientApplication(
                applicationVo.getCustomerId(), applicationVo.getPassword());
        return clientApplication;
    }

    public AddressMappingResult mappingAddress(ApplicationVo applicationVo, List<OrderVo> orderList) {
        AddressMappingResult result = new AddressMappingResult();
        try {
            Map<String, OrderAddressMappingResult> resultMap = this.addressService.search(
                    applicationVo.getCustomerId(), orderList);
            result.setResultMap(resultMap);
            result.setResultCode(ResultCodeEnum.success);
        } catch (Exception e) {
            AddressController.LOGGER.error("mappingAddress failed for customerId = {}", applicationVo.getCustomerId(),
                    e);
            result.setResultCode(ResultCodeEnum.failure);
            result.setMessage(e.toString());
        }
        return result;
    }

    /**
     * 解析请求xml参数
     * <p>
     * 方法详细描述
     * </p>
     * @param xml
     * @return
     * @since 1.0
     */
    public RequestVo decodeRequest(String xml) {
        RequestVo rquest = new RequestVo();

        try {
            Document doc = DocumentHelper.parseText(xml);// 将字符串转为XML
            Element root = doc.getRootElement(); // 获取根节点
            Element headElement = root.element("head");
            HeadVo head = new HeadVo();
            if (headElement != null) {
                head.setUsercode(headElement.elementTextTrim("usercode"));
                head.setBatchno(headElement.elementTextTrim("batchno"));
                head.setKey(headElement.elementTextTrim("key"));
            }
            rquest.setHead(head);
            Element itemXmls = root.element("items");
            if (itemXmls == null) {
                throw new RuntimeException("items 节点为空");
            }
            Iterator iterator = itemXmls.elementIterator("item");
            List<ItemVo> items = new ArrayList<ItemVo>();
            while (iterator.hasNext()) {// 遍历item节点
                Element element = null;

                element = (Element) iterator.next();
                String province = element.elementTextTrim("province");
                String city = element.elementTextTrim("city");
                String itemno = element.elementTextTrim("itemno");
                String area = element.elementTextTrim("area");
                String address = element.elementTextTrim("address");
                String pre_out_time = element.elementTextTrim("pre_out_time");
                String remark = element.elementTextTrim("remark");
                ItemVo item = new ItemVo();
                item.setAddress(address);
                item.setArea(area);
                item.setCity(city);
                item.setProvince(province);
                int intItemNo = Integer.parseInt(itemno);
                item.setItemno(intItemNo);
                item.setPre_out_time(pre_out_time);
                item.setRemark(remark);
                items.add(item);

            }
            rquest.setItems(items);
        } catch (Exception e) {
            AddressController.LOGGER.error("解析保存出错" + xml, e);
            throw new RuntimeException("解析保存出错,xml:" + xml);
        }

        return rquest;
    }
}
