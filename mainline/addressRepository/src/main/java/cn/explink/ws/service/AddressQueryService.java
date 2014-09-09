package cn.explink.ws.service;

import javax.jws.WebService;

import cn.explink.ws.vo.AddressQueryResult;
import cn.explink.ws.vo.ApplicationVo;

@WebService
public interface AddressQueryService {

	AddressQueryResult getAddress(ApplicationVo applicationVo, Long addressId, Long deliveryStationId);

}
