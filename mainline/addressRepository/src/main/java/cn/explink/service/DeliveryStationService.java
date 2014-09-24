package cn.explink.service;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.explink.dao.CustomerDao;
import cn.explink.dao.DeliveryStationDao;
import cn.explink.domain.Address;
import cn.explink.domain.Customer;
import cn.explink.domain.DeliveryStation;
import cn.explink.domain.DeliveryStationRule;
import cn.explink.domain.Vendor;
import cn.explink.domain.enums.DeliveryStationStausEnmu;
import cn.explink.modle.ComboBox;
import cn.explink.ws.vo.DeliveryStationVo;

@Service ("deliveryStationService")
public class DeliveryStationService extends CommonServiceImpl<DeliveryStation,Long>{
 
	public DeliveryStationService() {
		super(DeliveryStation.class);
		// TODO Auto-generated constructor stub
	}

	@Autowired
	private DeliveryStationDao deliveryStationDao;
	
	@Autowired
	private CustomerDao customerDao;

	public DeliveryStation createDeliveryStation(DeliveryStationVo deliveryStationVo) {
		Customer customer = customerDao.get(deliveryStationVo.getCustomerId());
		if (customer == null) {
			throw new RuntimeException("customer is not exist");
		}
		
		DeliveryStation deliveryStation = deliveryStationDao.getDeliveryStation(deliveryStationVo.getCustomerId(), deliveryStationVo.getExternalId());
		if (deliveryStation == null) {
			deliveryStation = new DeliveryStation();
		}
		deliveryStation.setName(deliveryStationVo.getName());
		deliveryStation.setStatus(DeliveryStationStausEnmu.valid.getValue());
		deliveryStation.setCustomer(customer);
		deliveryStation.setExternalId(deliveryStationVo.getExternalId());
		deliveryStationDao.save(deliveryStation);
		return deliveryStation;
	}
	
	public DeliveryStation updateDeliveryStation(DeliveryStationVo deliveryStationVo) {
		DeliveryStation deliveryStation = deliveryStationDao.getDeliveryStation(deliveryStationVo.getCustomerId(), deliveryStationVo.getExternalId());
		deliveryStation.setName(deliveryStationVo.getName());
		deliveryStation.setStatus(DeliveryStationStausEnmu.valid.getValue());
		deliveryStationDao.save(deliveryStation);
		return deliveryStation;
	}
	
	public DeliveryStation deleteDeliveryStation(DeliveryStationVo deliveryStationVo) {
		DeliveryStation deliveryStation = deliveryStationDao.getDeliveryStation(deliveryStationVo.getCustomerId(), deliveryStationVo.getExternalId());
		deliveryStation.setStatus(DeliveryStationStausEnmu.invalid.getValue());
		deliveryStationDao.save(deliveryStation);
		return deliveryStation;
	}
	/**
	 * 生成关键词列表
	 * @param headerNameList
	 * @return
	 */
	public XSSFWorkbook createAddressFile(List<String> headerNameList,List<List<String>> address) {
		XSSFWorkbook wookbook = new XSSFWorkbook();
		XSSFSheet sheet = wookbook.createSheet();
		XSSFRow row = sheet.createRow(0);
		int columnIndex = 0;
		for (String headerName : headerNameList) {
			XSSFCell cell = row.createCell(columnIndex);
			cell.setCellValue(headerName);
			columnIndex++;
		}
		for(int i = 0;i<address.size();i++){
			columnIndex = 0;
			row = sheet.createRow(i+1);
			for (int j = 1;j< address.get(i).size();j++) {
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
		return deliveryStationDao.get(id);
	}
    /**
     * 根据站点ID查询所有关键词
     * @param id
     * @return
     */
	public List<List<String>> getAddressById(Long id,String stationName) {
	    List<Address> list = deliveryStationDao.getAddress(id);
	    Map<Long,String> addMap = new HashMap<Long,String >();
	    Set<Long> addIds = new HashSet<Long>();
	    for(Address a :list){
	    	if(a.getPath()!=null){
	    		String[] ids = a.getPath().split("-");
	    		for(int i =0;i<ids.length;i++){
	    			addIds.add(Long.parseLong(ids[i]));
	    		}
	    	}
	    }
	    List<Address> addressList  = deliveryStationDao.getAddressByIds(addIds);
	    for(Address a :addressList){
	    	addMap.put(a.getId(), a.getName());
	    }
	    List<List<String>> result = new ArrayList<List<String>>();
	    for(Address a:list){
	    	List<String> row = new ArrayList<String>();
	    	if(a.getPath()!=null){
	    		String[] ids = a.getPath().split("-");
	    		for(int i =0;i<ids.length;i++){
	    			row.add(addMap.get(Long.parseLong(ids[i])));
	    		}
	    		row.add(a.getName());
	    		for(int i=ids.length+1;i<7;i++){
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
		return deliveryStationDao.getComBoxDeliveryStation(customerId);
	}

	public DeliveryStation getByNameAndCustomerId(String deliveryStationName,
			Long customerId) {
		return deliveryStationDao.getByNameAndCustomerId(deliveryStationName,customerId);
	}

	public List<Vendor> listAllVendor(Long customerId) {
		return deliveryStationDao.listAllVendor(customerId);
	}

}
