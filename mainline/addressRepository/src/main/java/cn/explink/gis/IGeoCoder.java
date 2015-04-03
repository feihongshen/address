package cn.explink.gis;

/**
 * 地理编码\逆地理编码\POI检索
 * */
public interface IGeoCoder {

	/**
	 * 地理编码
	 * 通过给定的字符串格式的地址获取此地址所在位置(地理坐标表示)。
	 * */
	GeoPoint GetLocation(String address);
	
	/**
	 * 地理编码+POI检索
	 * 首先进行地理编码，如果存在返回结果那么返回。
	 * 如果地理编码失败那么进行POI检索，如果存在检索结果列表，那么返回第一个结果。
	 * */
	GeoPoint GetLocationDetails(String address);
	
	/**
	 * 逆地理编码
	 * 获取指定地理坐标处的地址描述
	 * */
	ReGeoCoderResult GetAddress(double lng,double lat);
	
	/**
	 * POI检索
	 * 按照指定地址关键字进行POI检索，如果检索结果存在多个，默认返回第一个。
	 * */
	GeoPoint Search(String address);
}
