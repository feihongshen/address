package cn.explink.quick;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cn.explink.dao.AddressDao;
import cn.explink.dao.DeliveryStationDao;
import cn.explink.dao.DeliveryStationRuleDao;
import cn.explink.domain.Address;
import cn.explink.domain.DeliveryStation;

@Service
public class QuickSerivce {

	@Autowired
	private AddressDao addressDAO = null;

	@Autowired
	private DeliveryStationDao deliverStationDAO = null;

	@Autowired
	private DeliveryStationRuleDao deliverStationRuleDAO = null;

	@Transactional
	public List<FullAddrStationPair> getFullAddrStationPair(int page, int pageSize) {
		List<AddressStationPair> pairList = this.getAddressDAO().getPageAddressList(page, pageSize);
		List<Address> fullPathAddrList = this.getAddressDAO().getFullPathAddrList(pairList);
		List<DeliveryStation> delStatList = this.getDeliverStationDAO().getDeliverStation(pairList);

		return this.getFullPairList(pairList, fullPathAddrList, delStatList);
	}

	@Transactional
	public List<DeliveryStation> getAllDeliverStation() {
		return this.getDeliverStationDAO().getAllDeliverStation();
	}

	@Transactional
	public void updateAddressStation(Long addressId, Long stationId) {
		this.getDeliverStationRuleDAO().updateAddressStation(addressId, stationId);
	}

	private List<FullAddrStationPair> getFullPairList(List<AddressStationPair> pairList, List<Address> addrList, List<DeliveryStation> delStatList) {
		List<FullAddrStationPair> fullPairList = new ArrayList<FullAddrStationPair>();
		Map<Long, Address> addrMap = this.getAddressMap(addrList);
		Map<Long, DeliveryStation> delStatMap = this.getDeliveryStationMap(delStatList);
		for (AddressStationPair pair : pairList) {
			fullPairList.add(this.createFullAddrStationPair(addrMap, delStatMap, pair));
		}
		return fullPairList;
	}

	private FullAddrStationPair createFullAddrStationPair(Map<Long, Address> addrMap, Map<Long, DeliveryStation> delStatMap, AddressStationPair pair) {
		FullAddrStationPair fullPair = new FullAddrStationPair();
		List<Address> fullPathAddrList = this.getFullPathAddrList(pair, addrMap);
		List<AddressQuickVO> addressQuickVOList = new ArrayList<AddressQuickVO>();
		for (Address address : fullPathAddrList) {
			AddressQuickVO addressQuickVO = new AddressQuickVO(address.getId(), address.getName());
			addressQuickVOList.add(addressQuickVO);
		}
		fullPair.setAddrList(addressQuickVOList);
		fullPair.setDelStat(this.getDeliverStation(pair, delStatMap));

		return fullPair;
	}

	private List<Address> getFullPathAddrList(AddressStationPair pair, Map<Long, Address> addrMap) {
		List<Address> addrList = new ArrayList<Address>();
		Address address = addrMap.get(pair.getAddressId());
		addrList.add(address);
		String path = address.getPath();
		if ((path == null) || path.isEmpty()) {
			return addrList;
		}
		String[] parts = path.split("-");
		for (String part : parts) {
			addrList.add(addrMap.get(Long.valueOf(part)));
		}
		this.sortAddrList(addrList);

		return addrList;
	}

	private DeliveryStation getDeliverStation(AddressStationPair pair, Map<Long, DeliveryStation> delStatMap) {
		return delStatMap.get(pair.getStationId());
	}

	private Map<Long, DeliveryStation> getDeliveryStationMap(List<DeliveryStation> delStatList) {
		Map<Long, DeliveryStation> delStatMap = new HashMap<Long, DeliveryStation>();
		for (DeliveryStation delStat : delStatList) {
			delStatMap.put(delStat.getId(), delStat);
		}
		return delStatMap;
	}

	private Map<Long, Address> getAddressMap(List<Address> addrList) {
		Map<Long, Address> addrMap = new HashMap<Long, Address>();
		for (Address addr : addrList) {
			addrMap.put(addr.getId(), addr);
		}
		return addrMap;
	}

	private AddressDao getAddressDAO() {
		return this.addressDAO;
	}

	private DeliveryStationDao getDeliverStationDAO() {
		return this.deliverStationDAO;
	}

	private DeliveryStationRuleDao getDeliverStationRuleDAO() {
		return this.deliverStationRuleDAO;
	}

	private void sortAddrList(List<Address> addrList) {
		Collections.sort(addrList, this.getAddressCompartor());
	}

	private AddressComparator addressCompartor = new AddressComparator();

	private AddressComparator getAddressCompartor() {
		return this.addressCompartor;
	}

	private class AddressComparator implements Comparator<Address> {

		@Override
		public int compare(Address o1, Address o2) {
			if ((o1.getPath() == null) || o1.getPath().isEmpty()) {
				return -1;
			}
			if ((o2.getPath() == null) || o2.getPath().isEmpty()) {
				return 1;
			}
			return o1.getPath().length() - o2.getPath().length();
		}
	}

}
