package cn.explink.util;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.httpclient.util.DateUtil;

import cn.explink.exception.ExplinkRuntimeException;

/**
 * reflection utilities
 */
public class ReflectionUtil {

	private static final String DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss.0";

	/**
	 * set field value for given result set
	 * 
	 * @param targetObject
	 * @param field
	 * @param rs
	 * @param columnName 
	 */
	public static void setFieldValue(Object targetObject, Field field, ResultSet rs, String columnName) {
		boolean isAccessible = field.isAccessible();
		field.setAccessible(true);

		Class<?> fieldType = field.getType();

		try {
			if (fieldType.equals(boolean.class) || fieldType.equals(Boolean.class)) {
				field.set(targetObject, rs.getBoolean(columnName));
			} else if (fieldType.equals(byte.class) || fieldType.equals(Byte.class)) {
				field.set(targetObject, rs.getByte(columnName));
			} else if (fieldType.equals(char.class) || fieldType.equals(Character.class)) {
				field.set(targetObject, Character.valueOf(rs.getString(columnName).charAt(0)));
			} else if (fieldType.equals(double.class) || fieldType.equals(Double.class)) {
				field.set(targetObject, rs.getDouble(columnName));
			} else if (fieldType.equals(float.class) || fieldType.equals(Float.class)) {
				field.set(targetObject, rs.getFloat(columnName));
			} else if (fieldType.equals(int.class) || fieldType.equals(Integer.class)) {
				field.set(targetObject, rs.getInt(columnName));
			} else if (fieldType.equals(long.class) || fieldType.equals(Long.class)) {
				field.set(targetObject, rs.getLong(columnName));
			} else if (fieldType.equals(short.class) || fieldType.equals(Short.class)) {
				field.set(targetObject, rs.getShort(columnName));
			} else if (fieldType.equals(Date.class)) {
				field.set(targetObject, rs.getTimestamp(columnName));
			} else {
				field.set(targetObject, rs.getString(columnName));
			}
		} catch (Exception e) {
			StringBuilder sb = new StringBuilder();
			sb.append("setFieldValue failed for ").append(targetObject.getClass().getName()).append(".").append(field.getName());
			throw new ExplinkRuntimeException(sb.toString(), e);
		}

		field.setAccessible(isAccessible);
	}
	
	/**
	 * set field value for given object
	 * @param targetObject
	 * @param field
	 * @param value
	 */
	public static void setFieldValue(Object targetObject, Field field, String value) {
		boolean isAccessible = field.isAccessible();
		field.setAccessible(true);
		
		Class<?> fieldType = field.getType();

		try {
			if (fieldType.equals(boolean.class) || fieldType.equals(Boolean.class)) {
				field.set(targetObject, Boolean.valueOf(value));
			} else if (fieldType.equals(byte.class) || fieldType.equals(Byte.class)) {
				field.set(targetObject, Byte.valueOf(value));
			} else if (fieldType.equals(char.class) || fieldType.equals(Character.class)) {
				field.set(targetObject, Character.valueOf(value.charAt(0)));
			} else if (fieldType.equals(double.class) || fieldType.equals(Double.class)) {
				field.set(targetObject, Double.valueOf(value));
			} else if (fieldType.equals(float.class) || fieldType.equals(Float.class)) {
				field.set(targetObject, Float.valueOf(value));
			} else if (fieldType.equals(int.class) || fieldType.equals(Integer.class)) {
				field.set(targetObject, Integer.valueOf(value));
			} else if (fieldType.equals(long.class) || fieldType.equals(Long.class)) {
				field.set(targetObject, Long.valueOf(value));
			} else if (fieldType.equals(short.class) || fieldType.equals(Short.class)) {
				field.set(targetObject, Short.valueOf(value));
			} else if (fieldType.equals(Date.class)) {
				if (value != null) {
					List<String> formatters = new ArrayList<String>();
					formatters.add(DATETIME_FORMAT);
					field.set(targetObject, DateUtil.parseDate(value, formatters));
				}
			} else {
				field.set(targetObject, value);
			}
		} catch (Exception e) {
			StringBuilder sb = new StringBuilder();
			sb.append("setFieldValue failed for ").append(targetObject.getClass().getName()).append(".").append(field.getName()).append(". value = ")
					.append(value);
			throw new ExplinkRuntimeException(sb.toString(), e);
		}
		
		field.setAccessible(isAccessible);
	}

	/**
	 * get the field value for given object
	 * @param targetObject
	 * @param field
	 * @param fieldType
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> T getFieldValue(Object targetObject, Field field, Class<T> fieldType) {
		boolean isAccessible = field.isAccessible();
		field.setAccessible(true);
		Object value = null;
		try {
			value = field.get(targetObject);
		} catch (Exception e) {
			StringBuilder sb = new StringBuilder();
			sb.append("getFieldValue failed for ").append(targetObject.getClass().getName()).append(".").append(field.getName()).append(". value = ")
					.append(value);
			throw new ExplinkRuntimeException(sb.toString(), e);
		}
		
		field.setAccessible(isAccessible);
		return (T) value;
	}
}
