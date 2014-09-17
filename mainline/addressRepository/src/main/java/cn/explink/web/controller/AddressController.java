package cn.explink.web.controller;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
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
import cn.explink.exception.ExplinkRuntimeException;
import cn.explink.modle.AjaxJson;
import cn.explink.modle.DataGrid;
import cn.explink.modle.DataGridReturn;
import cn.explink.qbc.CriteriaQuery;
import cn.explink.service.AddressImportResultService;
import cn.explink.service.AddressImportService;
import cn.explink.service.AddressService;
import cn.explink.service.LuceneService;
import cn.explink.tree.ZTreeNode;
import cn.explink.util.DateTimeUtil;
import cn.explink.util.HqlGenerateUtil;
import cn.explink.util.StringUtil;
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
		if(null==parentId){
			parentId=1L;
		}
		return addressService.getAsyncAddress(customerId,parentId);
	}
	
	
	
	@RequestMapping("/getZTree")
	public @ResponseBody List<ZTreeNode> getZTree(String name,  Integer band) {
		Long customerId = getCustomerId();
		return addressService.getZAddress(customerId,name,band);
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
			 MultipartFile file) {
		InputStream in = null;
		AjaxJson aj=new AjaxJson();
		try {
			in = file.getInputStream();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		AddressImportResult addressImportResult = addressImportService.importAddress(in, getLogginedUser());
		if(null==addressImportResult){
			aj.setSuccess(false);
			aj.setMsg("数据异常");
//			Map<String, Object> attributes=new HashMap<String, Object>();
//			attributes.put("importTable", addressImportResult.getAddressImportDetails());
//			aj.setAttributes(attributes);
			return aj;
		}
		aj.setSuccess(true);
		aj.setInfo(addressImportResult.getId().toString());
//		Map<String, Object> attributes=new HashMap<String, Object>();
//		attributes.put("importTable", addressImportResult.getAddressImportDetails());
//		aj.setAttributes(attributes);
		return aj;
	}

	@RequestMapping("/importKwAddress")
	public @ResponseBody AjaxJson importKwAddress( HttpServletRequest request, HttpServletResponse response,
			 MultipartFile file) {
		InputStream in = null;
		AjaxJson aj=new AjaxJson();
		try {
			in = file.getInputStream();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		AddressImportResult addressImportResult = addressImportService.importKwAddress(in, getLogginedUser());
		if(null==addressImportResult){
			aj.setSuccess(false);
			aj.setMsg("数据异常");
			return aj;
		}
		aj.setSuccess(true);
		aj.setInfo(addressImportResult.getId().toString());
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
		CriteriaQuery cq = new CriteriaQuery(AddressImportResult.class, dataGrid);
		HqlGenerateUtil.installHql(cq, addressImportResult, request.getParameterMap());
		String begin=request.getParameter("importDate_begin");
		String end=request.getParameter("importDate_end");
		if(StringUtils.isNotBlank(begin)){
			cq.ge("importDate", begin);
		}
		if(StringUtils.isNotBlank(end)){
			cq.le("importDate", end);
		}
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
		try{
			if(stationId!=null){
				 addressService.addAddressWithStation(parentId,addresses,stationId,customerId); 
			}else{
				 addressService.addAddress(parentId,addresses,customerId); 
			}
		}catch(Exception e){
			aj.setSuccess(false);
			aj.setMsg(e.getMessage());
		}
		return aj;
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
	
}
