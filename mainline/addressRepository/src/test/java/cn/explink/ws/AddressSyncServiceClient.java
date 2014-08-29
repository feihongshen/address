package cn.explink.ws;

import org.apache.cxf.interceptor.LoggingInInterceptor;
import org.apache.cxf.interceptor.LoggingOutInterceptor;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;

import cn.explink.ws.service.AddressSyncService;
import cn.explink.ws.vo.ApplicationVo;
import cn.explink.ws.vo.AddressSyncServiceResult;
import cn.explink.ws.vo.DeliveryStationVo;

public class AddressSyncServiceClient {

	public static void main(String[] args) {
		runCreateDeliveryStation();
		runUpdateDeliveryStation();
	}

	protected static void runCreateDeliveryStation() {
		AddressSyncService client = createAddressSyncService();
		ApplicationVo applicationVo = getApplicationVo();
		DeliveryStationVo deliveryStationVo = getDeliveryStationVo();
		AddressSyncServiceResult result = client.createDeliveryStation(applicationVo, deliveryStationVo);
		System.out.println("createDeliveryStation = " + result);
	}

	protected static void runUpdateDeliveryStation() {
		AddressSyncService client = createAddressSyncService();
		ApplicationVo applicationVo = getApplicationVo();
		DeliveryStationVo deliveryStationVo = getDeliveryStationVo();
		AddressSyncServiceResult result = client.updateDeliveryStation(applicationVo, deliveryStationVo);
		System.out.println("updateDeliveryStation = " + result);
	}

	protected static AddressSyncService createAddressSyncService() {
		JaxWsProxyFactoryBean factoryBean = new JaxWsProxyFactoryBean();
		factoryBean.getInInterceptors().add(new LoggingInInterceptor());
		factoryBean.getOutInterceptors().add(new LoggingOutInterceptor());
		factoryBean.setServiceClass(AddressSyncService.class);
		factoryBean.setAddress("http://localhost/address/service/AddressSyncService");
		AddressSyncService client = (AddressSyncService) factoryBean.create();
		return client;
	}

	protected static DeliveryStationVo getDeliveryStationVo() {
		DeliveryStationVo deliveryStationVo = new DeliveryStationVo();
		deliveryStationVo.setName("testDeliveryStation");
		deliveryStationVo.setExternalId(100L);
		deliveryStationVo.setCustomerId(1L);
		return deliveryStationVo;
	}

	protected static ApplicationVo getApplicationVo() {
		ApplicationVo applicationVo = new ApplicationVo();
		applicationVo.setId(1L);
		applicationVo.setPassword("password");
		return applicationVo;
	}

}