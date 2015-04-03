package cn.explink.spliter.consts;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

public class CommonKeyWord {
	private static final String[] keywords = new String[] { "省", "自治区", "市", "区", "县", "街道", "街", "大道", "路", "小区", "校区", "幼儿园", "小学", "中学", "附中", "大学", "学院", "大学城", "新城", "科学城", "工业城", "皮具城", "纺织城",
			"工业区", "开发区", "新区", "特区", "管理区", "交易区", "东区", "西区", "南区", "北区", "厂区", "镇", "乡", "村", "园", "公寓", "宾馆", "酒店", "集团", "公司", "站", "所", "院内", "社区", "常委会", "居委会", "大廈", "商行", "营业厅", "市场", "医院",
			"银行", "机场", "超市", "农场", "研究院", "居", "轩", "苑", "湾", "画廊", "公馆", "广场", "中心", "宿舍", "公安局", "税务局", "工商局", "分局", "总局" };

	public static Set<String> getKeyWordSet() {
		return new LinkedHashSet<String>(Arrays.asList(CommonKeyWord.keywords));
	}
}
