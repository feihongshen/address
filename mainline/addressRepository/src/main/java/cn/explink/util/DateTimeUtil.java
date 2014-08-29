package cn.explink.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateTimeUtil {

	private static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

	/**
	 * 判断两个日期大小，如日期1小于日期2，返回true，否则返回false
	 * 
	 * @param one
	 * @param two
	 * @return
	 */
	public static boolean isBefore(Date one, Date two) {
		if (one == null || two == null) {
			return false;
		}
		return one.getTime() < two.getTime();
	}

	public static Date getDateAfterToday(int day) {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, day);
		return cal.getTime();
	}

	public static Date parseDate(String dateString) throws ParseException {
		return parseDate(dateString, DEFAULT_DATE_FORMAT);
	}

	private static Date parseDate(String dateString, String format) throws ParseException {
		SimpleDateFormat formatter = new SimpleDateFormat(format);
		return formatter.parse(dateString);
	}

}
