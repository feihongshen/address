package cn.explink.gis;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import cn.explink.util.StringUtil;

/**
 * 通过百度webAPIv2.0版本实现的IGeoCoder接口
 * */
public class BaiduV2GeoCoder implements IGeoCoder {

	/**
	 * 百度api中必须用的key。此key可以在百度LBS开放平台申请
	 */
	private static String apiKey = BaiduApiKeyPool.getRandomKey();

	@Override
	public GeoPoint GetLocation(String address) {
		String url = "http://api.map.baidu.com/geocoder/v2/";
		try {
			String addressh = URLEncoder.encode(address, "UTF-8");
			String keywords = "ak=" + BaiduV2GeoCoder.apiKey + "&callback=?&output=json&address=" + addressh;
			String result = HttpUtility.sendGet(url, keywords); // 返回结果
			if (StringUtil.isEmpty(result)) {
				return null;
			}
			JSONObject json = JSONObject.fromObject(result); // 转成json字符串
			int status = json.getInt("status"); // 获取执行状态
			if (status == 0) // 成功
			{
				JSONObject resultObject = json.getJSONObject("result");
				JSONObject lnglat = resultObject.getJSONObject("location");
				double lng = lnglat.getDouble("lng");
				double lat = lnglat.getDouble("lat");
				int precise = resultObject.getInt("precise");
				int confidence = resultObject.getInt("confidence");
				GeoPoint resultPoint = new GeoPoint(lng, lat);
				resultPoint.setPrecise(precise);
				resultPoint.setConfidence(confidence);
				return resultPoint;
			} else {
				return null;
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public GeoPoint GetLocationDetails(String address) {
		GeoPoint coderResult = this.GetLocation(address);
		if ((coderResult == null) || (coderResult.getConfidence() <= 30)) {
			return this.Search(address);
		}
		return coderResult;
	}

	@Override
	public ReGeoCoderResult GetAddress(double lng, double lat) {
		// TODO Auto-generated method stub

		String url = "http://api.map.baidu.com/geocoder/v2/";

		try {
			String keyWords = "ak=" + BaiduV2GeoCoder.apiKey + "&callback=&location=" + lat + "," + lng + "&output=json&pois=1";

			String result = HttpUtility.sendGet(url, keyWords);
			JSONObject json = JSONObject.fromObject(result); // 转成json字符串
			int status = json.getInt("status"); // 获取执行状态
			if (status == 0) // 成功
			{
				ReGeoCoderResult reGeoResult = new ReGeoCoderResult();
				JSONObject jsonResult = json.getJSONObject("result");

				JSONObject address = jsonResult.getJSONObject("addressComponent");
				if (address != null) {
					reGeoResult.setAddressComponent((AddressComponent) JSONObject.toBean(address, AddressComponent.class));
				}

				JSONArray poiArr = jsonResult.getJSONArray("pois");
				if (poiArr != null) {
					reGeoResult.setPois((List<POI>) JSONArray.toCollection(poiArr, POI.class));
				}

				return reGeoResult;
			} else {
				return null;
			}

		} catch (Exception e) {
			// TODO: handle exception
		}

		return null;
	}

	@Override
	public GeoPoint Search(String address) {
		// TODO Auto-generated method stub
		String url = "http://api.map.baidu.com/place/v2/search";
		try {
			String addressh = URLEncoder.encode(address, "UTF-8");
			String region = "12,73,53,136"; // 表示中国的范围(外包围框)
			String keywords = "ak=" + BaiduV2GeoCoder.apiKey + "&bounds=" + region + "&output=json&q=" + addressh;
			String result = HttpUtility.sendGet(url, keywords);
			JSONObject json = JSONObject.fromObject(result);

			int status = json.getInt("status"); // 获取执行状态
			if (status == 0) // 成功
			{
				JSONArray results = json.getJSONArray("results");
				if (!results.isEmpty()) {
					// 默认取第一个poi
					JSONObject lnglat = results.getJSONObject(0).getJSONObject("location");
					if (lnglat.isNullObject()) {
						return null;
					} else {
						double lng = lnglat.getDouble("lng");
						double lat = lnglat.getDouble("lat");
						return new GeoPoint(lng, lat);
					}
				}
			} else {
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
