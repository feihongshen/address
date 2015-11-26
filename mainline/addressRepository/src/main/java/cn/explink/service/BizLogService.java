package cn.explink.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.explink.dao.AddressDao;
import cn.explink.dao.BizLogDAO;
import cn.explink.domain.Address;
import cn.explink.domain.BizLog;
import cn.explink.domain.enums.LogTypeEnum;

@Service
public class BizLogService {
	@Autowired
	private BizLogDAO bizLogDAO;
	@Autowired
	private AddressDao addressDAO;

	public void loggerInfo(Class clazz, BizLog bizLog) {
		Logger logger = LoggerFactory.getLogger(clazz);
		String msg = "";
		logger.info(msg);
	}

	/**
	 *
	 * @Title: findInfo
	 * @description 根据查询日志页面上传过来的条件，查询日志信息
	 * @author 刘武强
	 * @date  2015年11月25日上午11:14:47
	 * @param  @param operationType
	 * @param  @param beginTime
	 * @param  @param endTime
	 * @param  @param operationIP
	 * @param  @param page
	 * @param  @param pageNumber
	 * @param  @return
	 * @return  Map<String,Object>
	 * @throws
	 */
	@SuppressWarnings("unchecked")
	public Map<String, Object> findInfo(int operationType, String beginTime, String endTime, String operationIP, int page, int pageNumber) {
		Map<String, Object> map = this.bizLogDAO.findInfo(operationType, beginTime, endTime, operationIP, page, pageNumber);
		//拼接日志内容
		List<BizLog> list = (List<BizLog>) map.get("list");
		for (BizLog temp : list) {
			String allAddress = "";

			//如果是拆合站，那么就需要把地址id转化为名字
			if (temp.getOperationType() == LogTypeEnum.changeStationRelation.getValue()) {
				Set<Long> addressIds = new HashSet<Long>();
				if (temp.getSplitCombineAddress() != null) {
					for (String addressId : temp.getSplitCombineAddress().split(",")) {
						if ((addressId != null) && BizLogService.isNumeric(addressId)) {
							addressIds.add(Long.parseLong(addressId));
						}
					}
					List<Address> addressList = this.addressDAO.getAddressList(addressIds);
					List<String> addressNameList = new ArrayList<String>();
					for (Address address : addressList) {
						addressNameList.add(address.getName());
					}
					allAddress = StringUtils.join(addressNameList.toArray(), ",");
				}
			}
			temp.jointText(allAddress);
			temp.setOperationName(LogTypeEnum.getTextByValue(temp.getOperationType()));
		}
		return map;
	}

	/**
	 *
	 * @Title: isNumeric
	 * @description 用正则表达式判断字符串是否由数字组成
	 * @author 刘武强
	 * @date  2015年11月26日上午11:25:04
	 * @param  @param str
	 * @param  @return
	 * @return  boolean
	 * @throws
	 */
	public static boolean isNumeric(String str) {
		Pattern pattern = Pattern.compile("[0-9]*");
		return pattern.matcher(str).matches();
	}
}
