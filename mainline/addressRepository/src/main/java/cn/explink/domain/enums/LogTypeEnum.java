package cn.explink.domain.enums;

import java.util.ArrayList;
import java.util.List;

public enum LogTypeEnum {
	addAddress(1, "新增关键词"), deleteAddress(2, "删除关键词"), addAlias(3, "新增别名"), deleteAlias(4, "删除别名"), addRule(5, "新增规则"), deleteRule(6, "删除规则"), addStation(7, "新增站点"), updateStation(8, "修改站点"), deleteStation(
			9, "删除站点"), changeStationRelation(10, "拆合站");

	private int value;
	private String text;

	private LogTypeEnum(int value, String text) {
		this.value = value;
		this.text = text;
	}

	public int getValue() {
		return this.value;
	}

	public void setValue(int value) {
		this.value = value;
	}

	public String getText() {
		return this.text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public static List<LogTypeEnum> getAllStatus() {
		List<LogTypeEnum> list = new ArrayList<LogTypeEnum>();
		list.add(addAddress);
		list.add(deleteAddress);
		list.add(addAlias);
		list.add(deleteAlias);
		list.add(addRule);
		list.add(deleteRule);
		list.add(addStation);
		list.add(updateStation);
		list.add(deleteStation);
		list.add(changeStationRelation);
		return list;
	}

	public static String getTextByValue(int value) {
		List<LogTypeEnum> list = LogTypeEnum.getAllStatus();
		for (LogTypeEnum temp : list) {
			if (temp.getValue() == value) {
				return temp.getText();
			}
		}
		return "";
	}

}
