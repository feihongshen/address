package cn.explink.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtil {

	public static boolean isEmpty(String string) {
		return string == null || string.trim().isEmpty();
	}

	/**
	 * 从前到后逐个截取，找到能匹配上指定patten的第一个最小字串
	 * 
	 * @param str
	 * @param patten
	 * @return
	 */
	public static String substring(String str, String patten) {
		while (true) {
			if (match(str, patten)) {
				break;
			}
			str = str.substring(1, str.length());
			if (str.length() == 0) {
				return null;
			}
		}
		return str;
	}

	/**
	 * 判断是否匹配指定的正则表达式
	 * 
	 * @param str
	 * @param pattenString
	 * @return
	 */
	public static boolean match(String str, String pattenString) {
		Pattern pattern = Pattern.compile(pattenString);
		Matcher matcher = pattern.matcher(str);
		return matcher.matches();
	}

	public static boolean isNumeric(String str) {
		for (int i = str.length(); --i >= 0;) {
			int chr = str.charAt(i);
			if (chr < 48 || chr > 57)
				return false;
		}
		return true;
	}

	/**
	 * 取第一个最大数字<br/>
	 * 如：133xxx，返回133
	 * 
	 * @param string
	 * @return
	 */
	public static int startNumeric(String string) {
		int numericLength = 0;
		for (int i = 0; i < string.length(); i++) {
			if (isNumeric(string.charAt(i))) {
				numericLength++;
			} else {
				break;
			}
		}
		return Integer.parseInt(string.substring(0, numericLength));
	}

	private static boolean isNumeric(char charAt) {
		if (charAt < 48 || charAt > 57) {
			return false;
		}
		return true;
	}

}
