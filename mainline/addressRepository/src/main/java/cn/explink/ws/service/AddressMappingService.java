package cn.explink.ws.service;

import java.util.List;

import javax.jws.WebService;

import cn.explink.ws.vo.AddressMappingResult;
import cn.explink.ws.vo.ApplicationVo;
import cn.explink.ws.vo.OrderVo;

@WebService
public interface AddressMappingService {

	AddressMappingResult mappingAddress(ApplicationVo applicationVo, List<OrderVo> orderList);

}
