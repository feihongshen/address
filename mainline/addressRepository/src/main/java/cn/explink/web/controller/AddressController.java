package cn.explink.web.controller;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jfree.util.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import cn.explink.domain.Address;
import cn.explink.domain.AddressImportDetail;
import cn.explink.domain.AddressImportResult;
import cn.explink.domain.Alias;
import cn.explink.domain.Deliverer;
import cn.explink.domain.DeliveryStation;
import cn.explink.domain.User;
import cn.explink.domain.enums.AddressImportDetailStatsEnum;
import cn.explink.exception.ExplinkRuntimeException;
import cn.explink.modle.AjaxJson;
import cn.explink.modle.DataGrid;
import cn.explink.modle.DataGridReturn;
import cn.explink.qbc.CriteriaQuery;
import cn.explink.service.AddressImportResultService;
import cn.explink.service.AddressImportService;
import cn.explink.service.AddressService;
import cn.explink.service.DelivererService;
import cn.explink.service.DeliveryStationService;
import cn.explink.service.LuceneService;
import cn.explink.tree.ZTreeNode;
import cn.explink.util.DateTimeUtil;
import cn.explink.util.HqlGenerateUtil;
import cn.explink.util.StringUtil;
import cn.explink.web.vo.AddressImportTypeEnum;
import cn.explink.ws.vo.OrderVo;

@RequestMapping("/address")
@Controller
public class AddressController extends BaseController {

	private static Logger logger = LoggerFactory.getLogger(AddressController.class);
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
	
	@RequestMapping("/index")
	public String index(Model model) {
		return "/address/index";
	}

	@RequestMapping("/getAddress")
	public String getAddress(Model model, @RequestParam(value = "addressId", required = false) Long addressId) {
		if (addressId != null) {
		}
		return "/address/getAddress";
	}

	@RequestMapping("/saveAddress")
	public String saveAddress(Model model, Address address) {
		addressService.createAndBindAddress(address, null, getCustomerId());
		model.addAttribute("address", address);
		return "/address/getAddress";
	}

	@RequestMapping("/searchAddress")
	public String searchAddress(Model model, @RequestParam(value = "addressLine", required = false) String addressLine) throws IOException, ParseException {
		if (!StringUtil.isEmpty(addressLine)) {
			List<Address> addressList = luceneService.search(addressLine, getCustomerId());
			System.out.println("search result = " + addressList);
		}
		return "/address/getAddress";
	}

	@RequestMapping("/createAlias")
	public String createAlias(Model model, Alias alias) throws IOException, ParseException {
		if (alias != null) {
			addressService.createAlias(alias);
		}
		return "/address/getAddress";
	}

	@RequestMapping("/getAddressTree")
	public @ResponseBody List<ZTreeNode>  getAddressTree( @RequestParam(value = "id", required = false) Long parentId) {
		Long customerId = getCustomerId();
		return addressService.getAsyncAddress(customerId,parentId);
	}
	
	@RequestMapping("/getStationAddressTree")
	public @ResponseBody List<ZTreeNode>  getStationAddressTree( @RequestParam(value = "id", required = false) Long parentId,@RequestParam(value = "level", required = false) Long level) {
		Long customerId = getCustomerId();
		return addressService.getStationAddressTree(customerId,parentId);
	}
	
	
	
	@RequestMapping("/getZTree")
	public @ResponseBody List<ZTreeNode> getZTree(String name,  Integer band) {
		Long customerId = getCustomerId();
		return addressService.getZAddress(customerId,name,band);
	}
	
	@RequestMapping("/getAdressByStation")
	public @ResponseBody List<ZTreeNode> getAdressByStation(String stationId) {
		Long customerId = getCustomerId();
		return addressService.getAdressByStation(customerId,stationId);
	}
	
	
	
	/**
	 * 
	 * @param model
	 * @param parentId
	 * @return
	 */
	@RequestMapping("/addressImportPage")
	public String addressImportPage(Model model) {
//		List<AddressImportDetail> detailList= addressImportService.getAll();
//		List<AddressImportResult> resultList= addressImportResultService.getAll();
//		model.addAttribute("detailList", detailList);
//		model.addAttribute("resultList", resultList);
		return "address/importDatagrid";
	}
	@RequestMapping("/addressMapping")
	public String addressMapping(Model model) {
//		List<AddressImportDetail> detailList= addressImportService.getAll();
//		List<AddressImportResult> resultList= addressImportResultService.getAll();
//		model.addAttribute("detailList", detailList);
//		model.addAttribute("resultList", resultList);
		return "address/addressMapping";
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
		XSSFWorkbook wb = addressImportService.createAddressTemplate(headerNameList);
		String fileName = "地址导入模板.xlsx";
		setDownloadFileName(response, fileName);
		try {
			// response.setHeader("Content-Disposition", "attachment;filename="
			// + URLEncoder.encode("地址导入模板.xlsx", "UTF-8"));
			ServletOutputStream out = response.getOutputStream();
			wb.write(out);
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	@RequestMapping("/importAddress")
	public @ResponseBody AjaxJson importAddress( HttpServletRequest request, HttpServletResponse response,
			 MultipartFile file,Integer importType,Long stationId) {
		if(importType==null){
			importType = AddressImportTypeEnum.init.getValue();
		}
		InputStream in = null;
		AjaxJson aj=new AjaxJson();
		try {
			in = file.getInputStream();
			AddressImportResult addressImportResult = importAddress(in, getLogginedUser(),importType,stationId);
			if(null==addressImportResult){
				aj.setSuccess(false);
				aj.setMsg("数据异常");
				return aj;
			}
			aj.setSuccess(true);
			aj.setInfo("导入成功："+addressImportResult.getSuccessCount()+"个；导入失败："+addressImportResult.getFailureCount()+"个");
		} catch (IOException e) {
			aj.setSuccess(false);
			aj.setInfo("导入文件异常！");
		}
		return aj;
	}
	
	
	@RequestMapping("/getPromtInfo")
	public @ResponseBody AjaxJson getPromtInfo( HttpServletRequest request, HttpServletResponse response) {
		AjaxJson aj=new AjaxJson();
		Long customerId = getCustomerId();
		Map map=addressService.getAdressPromtInfo(customerId);
		aj.setAttributes(map);
		aj.setSuccess(true);
		return aj;
	}

	
	
	/**
	 * 查询地址导入结果
	 * @param model
	 * @param startDateString
	 * @param endDateString
	 * @return
	 */
	@RequestMapping("/importAddressResultList")
	public String importAddressResultList(Model model, @RequestParam(value = "startDate", required = false) String startDateString
			,@RequestParam(value ="endDate",required = false) String endDateString) {
		try {
			Date startDate = DateTimeUtil.parseDate(startDateString);
			Date endDate = DateTimeUtil.parseDate(endDateString);
			List<AddressImportResult> resultList = addressImportService.getImportAddressResults(startDate, endDate);
			model.addAttribute("resultList", resultList);
		} catch (java.text.ParseException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@RequestMapping("/deleteImportAddressResult")
	public @ResponseBody AjaxJson  deleteImportAddressResult(Model model
			,@RequestParam(value ="id",required = false) Long id) {
		AjaxJson aj=new AjaxJson();
		addressImportService.deleteImportAddressResult(id, getCustomerId());
		aj.setSuccess(true);
		return aj;
	}
	
	@RequestMapping("/datagrid")
	public @ResponseBody DataGridReturn datagrid(AddressImportDetail addressImportDetail,HttpServletRequest request, HttpServletResponse response, DataGrid dataGrid) {
		CriteriaQuery cq = new CriteriaQuery(AddressImportDetail.class, dataGrid);
		AddressImportResult addressImportResult=new AddressImportResult();
		addressImportResult.setId(Long.parseLong(request.getParameter("resultId")));
		addressImportDetail.setAddressImportResult(addressImportResult);
		HqlGenerateUtil.installHql(cq, addressImportDetail, request.getParameterMap());
		
		return this.addressImportService.getDataGridReturn(cq, true);
		
	}
	
	@RequestMapping("/subdatagrid")
	public @ResponseBody DataGridReturn subdatagrid(AddressImportResult addressImportResult,HttpServletRequest request, HttpServletResponse response, DataGrid dataGrid) {
		if(StringUtils.isBlank(dataGrid.getSort())){
			dataGrid.setSort("importDate");
		}
		
		CriteriaQuery cq = new CriteriaQuery(AddressImportResult.class, dataGrid);
		String begin=request.getParameter("importDate_begin");
		String end=request.getParameter("importDate_end");
		try {
		if(StringUtils.isNotBlank(begin)){
			Date beginDate = DateUtils.parseDate(begin, "yyyy-MM-dd");
			cq.ge("importDate", beginDate);
		}
		if(StringUtils.isNotBlank(end)){
			Date endDate=DateUtils.parseDate(end, "yyyy-MM-dd");
			cq.le("importDate", endDate);
		}
		} catch (java.text.ParseException e) {
			logger.error(e.getMessage());
		}
		HqlGenerateUtil.installHql(cq, addressImportResult, request.getParameterMap());
		return this.addressImportResultService.getDataGridReturn(cq, true);
		
	}
	
	@RequestMapping("/del")
	public @ResponseBody AjaxJson del(AddressImportResult addressImportResult,HttpServletRequest request, HttpServletResponse response, DataGrid dataGrid) {
		AjaxJson aj=new AjaxJson();
		addressImportResultService.delete(addressImportResult.getId());
		aj.setSuccess(true);
		return aj;
		
	}
	
	@RequestMapping("/parseAdress")
	public @ResponseBody AjaxJson parseAdress(String needMatched,HttpServletRequest request, HttpServletResponse response) {
		AjaxJson aj=new AjaxJson();
		//TODO GET CUSTOMER FROM USER
		Long customerId=getCustomerId();
		List<OrderVo> list=new ArrayList<OrderVo>();
		for(String addressLine : needMatched.split("\n")){
			if(addressLine.trim().length()==0){
				continue;
			}
			OrderVo order=new OrderVo();
			order.setCustomerId(customerId);
			order.setAddressLine(addressLine);
			list.add(order);
		}
		try {
			Map<String, Object> attributes = addressService.match(customerId, list);
			attributes.put("insum", list.size());
			aj.setAttributes(attributes);
			
		} catch (Exception e) {
			aj.setSuccess(false);
			aj.setMsg("匹配异常"+e.getMessage());
		} 
		aj.setSuccess(true);
		aj.setMsg("完成匹配");
		return aj;
		
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
	@RequestMapping("/add")
	public @ResponseBody AjaxJson  add( @RequestParam(value = "stationId" ) Long stationId, @RequestParam(value = "parentId", required = false) Long parentId, @RequestParam(value = "addresses", required = false) String addresses) {
		AjaxJson aj = new AjaxJson();
		aj.setSuccess(true);
		Long customerId = getCustomerId();
		List<Address> list = null;
		List<ZTreeNode> zList = null;
		try{
			if(stationId!=null){
				list =  addressService.addAddressWithStation(parentId,addresses,stationId,customerId); 
			}else{
				list = 	 addressService.addAddress(parentId,addresses,customerId); 
			}
			zList = transAddress(list);
		}catch(Exception e){
			aj.setSuccess(false);
			aj.setMsg(e.getMessage());
		}
		aj.setObj(zList);
		return aj;
	}
	private List<ZTreeNode> transAddress(List<Address> list) {
		 List<ZTreeNode> nlist = new ArrayList<ZTreeNode>();
		if(list!=null){
			for(Address a:list){
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
	public @ResponseBody AjaxJson  addAlias( @RequestParam(value = "addressId" ) Long addressId,   @RequestParam(value = "alias", required = false) String alias) {
		AjaxJson aj = null;
		Long customerId = this.getCustomerId();
		if(alias!=null&&addressId!=null){
			aj =  addressService.addAlias(addressId,alias,customerId);
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
	public @ResponseBody List<Alias>  getAlias( @RequestParam(value = "addressId" ) Long addressId ) {
			Long customerId = this.getCustomerId();
	      return addressService.getAliasByAddressId(addressId,customerId);
	}
	/**
	 * 删除别名
	 * @param addressId
	 * @return
	 */
	@RequestMapping("/delAlias")
	public @ResponseBody AjaxJson  delAlias( @RequestParam(value = "id" ) Long id ) {
		AjaxJson aj = new AjaxJson();
		aj.setSuccess(true);
		addressService.deleteAlias(id);
		return aj;
	}
	/**
	 * 删除别名
	 * @param addressId
	 * @return
	 */
	@RequestMapping("/delAddress")
	public @ResponseBody AjaxJson  Address( Long addressId ) {
		AjaxJson aj = new AjaxJson();
		aj.setSuccess(true);
		try{
			addressService.deleteAddress(addressId,this.getCustomerId());
		}catch(Exception e){
			e.printStackTrace();
			aj.setSuccess(false);
		}
		return aj;
	}
	/**
	 * 
	 * @param in
	 * @return
	 */
	public AddressImportResult importAddress(InputStream in, User user,Integer importType,Long stationId) {
		Long customerId = user.getCustomer().getId();
		AddressImportResult result = new AddressImportResult();
		List<AddressImportDetail> details = new ArrayList<AddressImportDetail>();
		Map<String,Address> map = new HashMap<String,Address>();//省市区地址MAP(Key:省-市-区)
		Map<String,Address> addressMap = new HashMap<String,Address>();//关键字MAP(Key:父ID-名称)
		Map<String,DeliveryStation> stationMap = new HashMap<String,DeliveryStation>();	//站点MAP(Key:客户ID-名称)
		Map<String,Deliverer> delivererMap = new HashMap<String,Deliverer>();//小件员MAP(Key:客户ID-名称)
		Set<String> addressNames = new HashSet<String>();
		Set<String> adminNames = new HashSet<String>();
		try {
			XSSFWorkbook wb = new XSSFWorkbook(in);
			XSSFSheet sheet = wb.getSheetAt(0);
			int rowNum = 1;
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
				
				addressImportService.addNonNullValue(adminNames, province);
				addressImportService.addNonNullValue(adminNames, city);
				addressImportService.addNonNullValue(adminNames, district);
				addressImportService.addNonNullValue(addressNames, address1);
				addressImportService.addNonNullValue(addressNames, address2);
				addressImportService.addNonNullValue(addressNames, address3);
				
				AddressImportDetail detail = new AddressImportDetail();
				detail.setProvince(province);
				detail.setCity(city);
				detail.setDistrict(district);
				detail.setAddress1(address1);
				detail.setAddress2(address2);
				detail.setAddress3(address3);
				detail.setDeliveryStationName(deliveryStationName);
				detail.setDelivererName(delivererName);
				detail.setAddressImportResult(result);
				details.add(detail);
			}
			
		} catch (IOException e) {
			logger.error("importAddress failed due to {}", e);
			return null;
		}
			//查找客户已有关键词并构造addressMap
			List<Address> addressList = addressService.getAddressByNames(addressNames,customerId);
			if(addressList!=null&&!addressList.isEmpty()){
				for(Address a:addressList){
					addressMap.put(a.getParentId()+"-"+a.getName(), a);
				}
			}
			//查找所有行政关键词并构造map
			List<Address> list = addressService.getAdministrationAddress(adminNames,customerId);
			if(list!=null&&!list.isEmpty()){
				Map<String,String> m = new HashMap<String,String>();
				for(Address a:list){
					m.put(a.getId()+"", a.getName());
				}
				for(Address a:list){
					if(new Integer(3).equals(a.getAddressLevel())){
						String path = a.getPath();
						String[] ids = path.split("-");
						map.put(m.get(ids[1])+"-"+m.get(ids[2])+"-"+a.getName(), a);
					}
				}
			}
			//构造所有站点Map
			 List<DeliveryStation> stationList =  deliveryStationService.listAll(customerId);
			 if(stationList!=null&&!stationList.isEmpty()){
				 for(DeliveryStation ds:stationList){
					 stationMap.put(customerId+"-"+ds.getName(), ds);
				 }
			 }
			 
			//构造所有小件员Map
			 List<Deliverer> delivererList =  delivererService.listAll(customerId);
			 if(delivererList!=null&&!delivererList.isEmpty()){
				 for(Deliverer d:delivererList){
					 delivererMap.put(customerId+"-"+d.getName(), d);
				 }
			 }
			for (AddressImportDetail detail : details) {
				
				try{
					addressImportService.txNewImportDetail(map, detail, addressMap, stationMap, delivererMap, customerId, importType,stationId);
				}catch(Exception e){
					detail.setStatus(AddressImportDetailStatsEnum.failure.getValue());
					logger.info(e.getMessage());
					detail.setMessage(e.getMessage());
				}
              
			}
			
		Set<AddressImportDetail> detailSet = new HashSet<AddressImportDetail>();
		detailSet.addAll(details);
		result.setAddressImportDetails(detailSet);
		int successCount = 0;
		int failureCount = 0;
		for (AddressImportDetail detail : details) {
			if (detail.getStatus() != null && detail.getStatus().intValue() == AddressImportDetailStatsEnum.success.getValue()) {
				successCount++;
			} else {
				failureCount++;
			}
		}
		result.setSuccessCount(successCount);
		result.setFailureCount(failureCount);
		result.setImportDate(new Date());
		result.setUserId(user.getId());
		if(importType==AddressImportTypeEnum.init.getValue()){
			addressImportResultService.save(result);
		}
		return result;
	}

	public LuceneService getLuceneService() {
		return luceneService;
	}

	public void setLuceneService(LuceneService luceneService) {
		this.luceneService = luceneService;
	}

	public AddressService getAddressService() {
		return addressService;
	}

	public void setAddressService(AddressService addressService) {
		this.addressService = addressService;
	}

	public AddressImportService getAddressImportService() {
		return addressImportService;
	}

	public void setAddressImportService(AddressImportService addressImportService) {
		this.addressImportService = addressImportService;
	}

	public AddressImportResultService getAddressImportResultService() {
		return addressImportResultService;
	}

	public void setAddressImportResultService(
			AddressImportResultService addressImportResultService) {
		this.addressImportResultService = addressImportResultService;
	}

	public DeliveryStationService getDeliveryStationService() {
		return deliveryStationService;
	}

	public void setDeliveryStationService(
			DeliveryStationService deliveryStationService) {
		this.deliveryStationService = deliveryStationService;
	}

	public DelivererService getDelivererService() {
		return delivererService;
	}

	public void setDelivererService(DelivererService delivererService) {
		this.delivererService = delivererService;
	}
	
}
