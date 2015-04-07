package cn.explink.spliter;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import cn.explink.spliter.consts.CommonKeyWord;
import cn.explink.spliter.vo.AddressDetail;
import cn.explink.spliter.vo.AddressStation;
import cn.explink.util.StringUtil;

/**
 * @author songkaojun
 *
 *         2015年1月16日
 */
public class AddressSplitter {

	private List<AddressStation> addressStationList;

	public AddressSplitter(List<AddressStation> addressStationList) {
		this.addressStationList = addressStationList;
	}

	public List<AddressDetail> split() {
		List<AddressDetail> addressDetailList = new ArrayList<AddressDetail>();
		for (AddressStation addressStation : this.addressStationList) {
			String addressLine = StringUtil.filterQureyStr(addressStation.getAddressLine());

			AddressDetail addressDetail = new AddressDetail();

			this.splitAddress(addressLine, addressDetail);
			addressDetail.setDeliveryStationName(addressStation.getStationName());

			addressDetailList.add(addressDetail);
		}

		List<AddressDetail> splittedAddressList = this.splitMultiAddress(addressDetailList);
		return splittedAddressList;

	}

	private List<AddressDetail> splitMultiAddress(List<AddressDetail> addressDetailList) {
		List<AddressDetail> splittedAddressList = new ArrayList<AddressDetail>();
		for (AddressDetail addressDetail : addressDetailList) {
			if (StringUtil.isEmpty(addressDetail.getAddressName1())) {
				continue;
			}
			if (!StringUtil.isEmpty(addressDetail.getAddressName3())) {
				AddressDetail ad1 = new AddressDetail(addressDetail.getProvince(), addressDetail.getCity(), addressDetail.getDistrict(), addressDetail.getAddressName1(), "", "",
						addressDetail.getDeliveryStationName());
				AddressDetail ad2 = new AddressDetail(addressDetail.getProvince(), addressDetail.getCity(), addressDetail.getDistrict(), addressDetail.getAddressName1(),
						addressDetail.getAddressName2(), "", addressDetail.getDeliveryStationName());
				splittedAddressList.add(ad1);
				splittedAddressList.add(ad2);
			} else if (!StringUtil.isEmpty(addressDetail.getAddressName2())) {
				AddressDetail ad1 = new AddressDetail(addressDetail.getProvince(), addressDetail.getCity(), addressDetail.getDistrict(), addressDetail.getAddressName1(), "", "",
						addressDetail.getDeliveryStationName());
				splittedAddressList.add(ad1);
			}
			AddressDetail ad = new AddressDetail(addressDetail.getProvince(), addressDetail.getCity(), addressDetail.getDistrict(), addressDetail.getAddressName1(), addressDetail.getAddressName2(),
					addressDetail.getAddressName3(), addressDetail.getDeliveryStationName());
			splittedAddressList.add(ad);
		}

		return splittedAddressList;
	}

	private void splitAddress(String addressLine, AddressDetail addressDetail) {
		int minIndex = Integer.MAX_VALUE;
		List<String> keyWordList = this.getKeywordPostList();
		String matchedKeyword = "";
		for (String keyWord : keyWordList) {
			int index = addressLine.indexOf(keyWord);
			if ((index != -1) && (index < minIndex)) {
				minIndex = index;
				matchedKeyword = keyWord;
			}
		}
		if (minIndex != Integer.MAX_VALUE) {
			String subAddress = addressLine.substring(0, minIndex);

			// TODO 对直辖市的处理
			if (subAddress.startsWith("北京") || subAddress.startsWith("天津") || subAddress.startsWith("重庆") || subAddress.startsWith("上海") || addressLine.endsWith("Municipality")) {
				this.setMunicipalityAddressDetail(addressDetail, subAddress + matchedKeyword);
				addressLine = addressLine + "Municipality";
			} else {
				this.setAddressDetail(addressDetail, subAddress + matchedKeyword);
			}

			this.splitAddress(addressLine.substring(minIndex + matchedKeyword.length()), addressDetail);
		} else {
			return;
		}
	}

	private void setMunicipalityAddressDetail(AddressDetail addressDetail, String subAddress) {
		// 过滤掉一个字的关键词
		if ((subAddress != null) && (subAddress.length() == 1)) {
			return;
		}
		if (subAddress.endsWith("市")) {
			addressDetail.setProvince(subAddress);
		} else if (subAddress.endsWith("县") && !StringUtil.isEmpty(addressDetail.getProvince())) {
			addressDetail.setCity("县");
			addressDetail.setDistrict(subAddress);
		} else if (subAddress.endsWith("区") && this.exceptDistrict(subAddress) && !StringUtil.isEmpty(addressDetail.getProvince())) {
			addressDetail.setCity("市辖区");
			addressDetail.setDistrict(subAddress);
		} else if (!StringUtil.isEmpty(addressDetail.getDistrict()) && StringUtil.isEmpty(addressDetail.getAddressName1())) {
			addressDetail.setAddressName1(subAddress);
		} else if (!StringUtil.isEmpty(addressDetail.getAddressName1()) && StringUtil.isEmpty(addressDetail.getAddressName2())) {
			addressDetail.setAddressName2(subAddress);
		} else if (!StringUtil.isEmpty(addressDetail.getAddressName2()) && StringUtil.isEmpty(addressDetail.getAddressName3())) {
			addressDetail.setAddressName3(subAddress);
		}

		// "省", "自治区", "市", "区", "街道", "路", "小区"
	}

	private void setAddressDetail(AddressDetail addressDetail, String subAddress) {
		// 过滤掉一个字的关键词
		if ((subAddress != null) && (subAddress.length() == 1)) {
			return;
		}
		if (subAddress.endsWith("省") || subAddress.endsWith("自治区")) {
			addressDetail.setProvince(subAddress);
		} else if (subAddress.endsWith("市") && this.exceptCity(subAddress) && !StringUtil.isEmpty(addressDetail.getProvince())) {
			addressDetail.setCity(subAddress);
		} else if (subAddress.endsWith("县") || (subAddress.endsWith("区") && this.exceptDistrict(subAddress) && !StringUtil.isEmpty(addressDetail.getCity()))) {
			addressDetail.setDistrict(subAddress);
		} else if (!StringUtil.isEmpty(addressDetail.getDistrict()) && StringUtil.isEmpty(addressDetail.getAddressName1())) {
			addressDetail.setAddressName1(subAddress);
		} else if (!StringUtil.isEmpty(addressDetail.getAddressName1()) && StringUtil.isEmpty(addressDetail.getAddressName2())) {
			addressDetail.setAddressName2(subAddress);
		} else if (!StringUtil.isEmpty(addressDetail.getAddressName2()) && StringUtil.isEmpty(addressDetail.getAddressName3())) {
			addressDetail.setAddressName3(subAddress);
		}

		// "省", "自治区", "市", "区", "街道", "路", "小区"
	}

	private boolean exceptCity(String subAddress) {
		return !subAddress.endsWith("超市") && !subAddress.endsWith("水果市");
	}

	private boolean exceptDistrict(String subAddress) {
		return !subAddress.endsWith("小区") && !subAddress.endsWith("特区") && !subAddress.endsWith("山区") && !subAddress.endsWith("城区") && !subAddress.endsWith("校区") && !subAddress.endsWith("东区")
				&& !subAddress.endsWith("西区") && !subAddress.endsWith("南区") && !subAddress.endsWith("北区") && !subAddress.endsWith("中区") && !subAddress.endsWith("一区") && !subAddress.endsWith("二区")
				&& !subAddress.endsWith("三区") && !subAddress.endsWith("四区") && !subAddress.endsWith("工业区") && !subAddress.endsWith("开发区") && !subAddress.endsWith("新区") && !subAddress.endsWith("社区")
				&& !subAddress.endsWith("A区") && !subAddress.endsWith("B区") && !subAddress.endsWith("C区") && !subAddress.endsWith("D区") && !subAddress.endsWith("T区") && !subAddress.endsWith("管理区")
				&& !subAddress.endsWith("交易区") && !subAddress.endsWith("厂区") && !subAddress.endsWith("度假区") && !subAddress.endsWith("生活区") && !subAddress.endsWith("军区");
	}

	public List<String> getKeywordPostList() {
		Set<String> commonKeyWordSet = CommonKeyWord.getKeyWordSet();

		// TODO 添加用户维护的关键词后缀
		// XMLParser.getInstance().loadXMLFile();
		// List<SplitKeywordPostInfo> splitKeywordPostList =
		// SplitKeywordPostDataManager.getInstance().getAllSelectedInfo();
		// for (SplitKeywordPostInfo splitKeywordPostInfo :
		// splitKeywordPostList) {
		// commonKeyWordSet.add(splitKeywordPostInfo.getKeywordPostName());
		// }
		return new ArrayList<String>(commonKeyWordSet);
	}

	public List<AddressStation> getAddressStationList() {
		return this.addressStationList;
	}

	public void setAddressStationList(List<AddressStation> addressStationList) {
		this.addressStationList = addressStationList;
	}

}
