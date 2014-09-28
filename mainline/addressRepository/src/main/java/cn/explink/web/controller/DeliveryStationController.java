package cn.explink.web.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jboss.logging.Param;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.explink.domain.AddressImportResult;
import cn.explink.domain.DeliveryStation;
import cn.explink.domain.User;
import cn.explink.domain.Vendor;
import cn.explink.modle.DataGrid;
import cn.explink.modle.DataGridReturn;
import cn.explink.modle.SortDirection;
import cn.explink.qbc.CriteriaQuery;
import cn.explink.service.AddressImportResultService;
import cn.explink.service.DeliveryStationService;
import cn.explink.util.HqlGenerateUtil;

@RequestMapping("/station")
@Controller
public class DeliveryStationController extends BaseController {

	private static Logger logger = LoggerFactory.getLogger(DeliveryStationController.class);
	@Autowired
	private DeliveryStationService  deliveryStationService;
	@RequestMapping("/list")
	public @ResponseBody DataGridReturn list(DeliveryStation deliveryStation,HttpServletRequest request, HttpServletResponse response, DataGrid dataGrid) {
		CriteriaQuery cq = new CriteriaQuery(DeliveryStation.class, dataGrid);
		cq.addOrder("name", SortDirection.asc);
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
	public String downloadStationAddress(@RequestParam(value = "id", required = false) Long id, HttpServletRequest request, HttpServletResponse response) {
		List<String> headerNameList = new ArrayList<String>();
		headerNameList.add("省/直辖市");
		headerNameList.add("市");
		headerNameList.add("区");
		headerNameList.add("地址1");
		headerNameList.add("地址2");
		headerNameList.add("地址3");
		headerNameList.add("原站点");
		DeliveryStation station =  (DeliveryStation) deliveryStationService.getById(id);
		List<List<String>>   addressList  =deliveryStationService.getAddressById(id,station.getName())  ;
		XSSFWorkbook wb = deliveryStationService.createAddressFile(headerNameList,addressList);
		String fileName = station.getName()+"关键字.xlsx";
		setDownloadFileName(response, fileName);
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
	public @ResponseBody List<DeliveryStation> listAll(HttpServletRequest request, HttpServletResponse response, DataGrid dataGrid) {
		return	this.deliveryStationService.listAll(getCustomerId());
	}
	
	@RequestMapping("/listAllVendor")
	public @ResponseBody List<Vendor> listAllVendor(HttpServletRequest request, HttpServletResponse response, DataGrid dataGrid) {
		return	this.deliveryStationService.listAllVendor(getCustomerId());
	}
	/**
	 * 根据站点IDs导出所有关键词库
	 * @param id
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/downloadStationAddresses")
	public String downloadStationAddresses(@RequestParam(value = "ids", required = false) String ids, HttpServletRequest request, HttpServletResponse response) {
		List<String> headerNameList = new ArrayList<String>();
		headerNameList.add("省/直辖市");
		headerNameList.add("市");
		headerNameList.add("区");
		headerNameList.add("地址1");
		headerNameList.add("地址2");
		headerNameList.add("地址3");
		headerNameList.add("原站点");
		headerNameList.add("新站点");
		List<List<String>>   addressList = new ArrayList<List<String>>();
		String fileName = "";
		if(StringUtils.isNotEmpty(ids)){
			String[]  id= ids.split(",");
			for(int i=0;i<id.length;i++){
				Long tid = Long.parseLong(id[i]);
				DeliveryStation station =  (DeliveryStation) deliveryStationService.getById(tid);
				addressList.addAll(deliveryStationService.getAddressById(tid,station.getName())) ;
				fileName+=station.getName()+"-";
			}
		}
		XSSFWorkbook wb = deliveryStationService.createAddressFile(headerNameList,addressList);
		  fileName = fileName+"关键字.xlsx";
		setDownloadFileName(response, fileName);
		try {
			ServletOutputStream out = response.getOutputStream();
			wb.write(out);
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}
