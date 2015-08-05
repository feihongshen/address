package cn.explink.gis;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class GeoCoderMain {

	public static void main(String[] arges) {
		// Date d1=new Date();

		IGeoCoder geocoder = new BaiduV2GeoCoder();

		/*
		 * String[] addres=new String[]{ "北京市昌平区北七家镇平西王府青年公寓速八酒店后",
		 * "北京市昌平区北七家镇平西王府王府西苑别墅L01", "北京市昌平区北七家镇平西王府西966996公交总站旁意合公寓520室",
		 * "北京市昌平区北七家镇平西王府西宏福大夏蒙羊羊火锅城", "北京市昌平区北七家镇平西王府西青年公寓",
		 * "北京市昌平区北七家镇七北路宏福创业园2号楼2-10", "北京市昌平区北七家镇七家宏福中路中央戏剧学院",
		 * "北京市昌平区北七家镇七家镇郑各庄北京邮电大学宏福校区学二宿舍楼", "北京市昌平区北七家镇七家镇郑各庄村北京邮电大学宏福校区",
		 * "北京市昌平区北七家镇王府街1号北京王府中西医结合医院急诊科", "北京市昌平区北七家镇温都水城(宏福苑)南一区9号楼4单元703",
		 * "北京市昌平区北七家镇温都水城湖湾酒店(东区)", "北京市昌平区北七家镇温都水城金手杖C-0817号",
		 * "北京市昌平区北七家镇温都水城金手杖公寓3楼1117",
		 * "北京市昌平区北七家镇温都水城金手杖老年公寓1号楼北侧郑各庄村委会二楼物业总办",
		 * "北京市昌平区北七家镇温都水城南二区5号楼2单元18层西侧门", "北京市昌平区北七家镇郑个庄村北京邮电大学宏福校区",
		 * "北京市昌平区北七家镇郑各庄北京邮电大学", "北京市昌平区北七家镇郑各庄北京邮电大学宏福小区",
		 * "北京市昌平区北七家镇郑各庄村(北京邮电大学宏福校区)",
		 * "北京市昌平区北七家镇郑各庄村10号院4号厂房2层2-5(宏福科技园综合楼)",
		 * "北京市昌平区北七家镇郑各庄村10号院宏福科技园2号厂房2层2-1", "北京市昌平区北航沙河校区", "北京市昌平区沙河北航沙河校区",
		 * "北京市昌平区沙河北京航空航空大学沙河校区", "北京市昌平区沙河北京航空航天大?沙河校?",
		 * "北京市昌平区沙河北京航空航天大学沙河校区东门", "北京市昌平区沙河北京市昌平区沙河地区北京航空航天大学(沙河校区)3号公寓",
		 * "北京市昌平区沙河地区南三街9号北京航空航天大学(沙河校区)",
		 * "北京市昌平区沙河南丰路沙河大学城北京航空航天大学沙河校区2号公寓飞行学院", "北京市昌平区沙河北京市昌平区外交学院沙河校区",
		 * "北京市昌平区沙河北京市昌平区巩华北二区1号楼四单元1603", "北京市昌平区沙河昌平沙河大学城和畅园9号楼4单元1402室",
		 * "北京市昌平区沙河发展路8号院8-303", "北京市昌平区昌平东小口镇龙锦东一区1号楼4单元401",
		 * "北京市昌平区回龙观温都水城旁北京电影学院"};
		 *
		 *
		 * for (int i = 0; i < addres.length; i++) { GeoPoint
		 * result=geocoder.GetLocation(addres[i]);
		 * System.out.println("经度："+result.getLng()
		 * +"   纬度："+result.getLat()+"   查找方式:"
		 * +result.getPrecise()+"   可信度:"+result
		 * .getConfidence()+"   地址："+addres[i]); }
		 */

		// 文件数据
		String fileName = "e:\\address.txt";
		File file = new File(fileName);
		BufferedReader reader = null;
		int precise = 0;
		int unprecise = 0;

		int confidenceLT10 = 0;
		int confidence10To15 = 0;
		int confidence15To20 = 0;
		ArrayList<String> confidenceLT10AddressLineList = new ArrayList<String>();
		ArrayList<String> confidence10To15AddressLineList = new ArrayList<String>();
		ArrayList<String> confidence15To20AddressLineList = new ArrayList<String>();

		try {
			reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "GBK"));
			String tempString = null;
			while ((tempString = reader.readLine()) != null) {
				// byte[] bs = tempString.getBytes("US-ASCII");
				// 用新的字符编码生成字符串
				// tempString = new String(bs, "US-ASCII");
				try {
					GeoPoint result = geocoder.GetLocation(tempString);
					if (result != null) {
						if (result.getPrecise() == 1) {
							precise++;
						} else {
							unprecise++;
						}
						if (result.getConfidence() <= 10) {
							confidenceLT10++;
							confidenceLT10AddressLineList.add(tempString);
						} else if ((result.getConfidence() > 10) && (result.getConfidence() <= 15)) {
							confidence10To15++;
							confidence10To15AddressLineList.add(tempString);
						} else if ((result.getConfidence() > 15) && (result.getConfidence() <= 20)) {
							confidence15To20++;
							confidence15To20AddressLineList.add(tempString);
						}
						// System.out.println("经度：" + result.getLng() + "   纬度："
						// + result.getLat() + "   查找方式:" + result.getPrecise()
						// + "   可信度:" + result.getConfidence() + "   地址：" +
						// tempString);
					} else {
						// System.out.println("未能成功转换：    " + tempString);
					}
				} catch (Exception e) {
				}
			}
			reader.close();

			System.out.println("精确查找：" + precise + "    非精确查找" + unprecise);
			System.out.println("可信度小于10的地址有" + confidenceLT10 + "个:");
			for (String confidenceLT10AddressLine : confidenceLT10AddressLineList) {
				System.out.println(confidenceLT10AddressLine);
			}
			System.out.println("可信度大于10小于15的地址有" + confidence10To15 + "个:");
			for (String confidence10To15AddressLine : confidence10To15AddressLineList) {
				System.out.println(confidence10To15AddressLine);
			}
			System.out.println("可信度大于15小于20的地址有" + confidence15To20 + "个:");
			for (String confidence15To20AddressLine : confidence15To20AddressLineList) {
				System.out.println(confidence15To20AddressLine);
			}

		} catch (IOException e) {
			e.printStackTrace();
		}

		// GeoPoint result=geocoder.GetLocation("北京市昌平区回龙观温都水城旁北京电影学院");
		// GeoPoint
		// result=GeoCoder.getInstance().getGeoCoder().Search("北京市四惠大厦");

		// Date d2=new Date();

		// String lng= result.getString("lng");
		// String lat=result.getString("lat");

		// System.out.println("经度："+result.getLng()
		// +"   纬度："+result.getLat()+"   查找方式:"+result.getPrecise()+"   可信度:"+result.getConfidence());
		// System.out.println(lng);
		// System.out.println(lat);

		// 　点在多边形内测试

		// GeoPoint[] polygon=new GeoPoint[]{new
		// GeoPoint(116.013294323,40.045643),new
		// GeoPoint(117.013294323,40.045643),new
		// GeoPoint(117.54543435,39.133233),new
		// GeoPoint(117.013294323,38.125454),new
		// GeoPoint(116.145265,38.02457425),new GeoPoint(115.45865,38.4568754)};
		//
		// GeoPoint point=new GeoPoint(116.43294323,39.045643);
		//
		// Date d1=new Date();
		//
		// for (int i = 0; i < 10000000; i++) {
		//
		// GeoUtility.isInPolygon(point, polygon);
		//
		// }
		//
		// Date d2=new Date();
		//
		// long timespan= d2.getTime()-d1.getTime();
		//
		// System.out.println(timespan);
		// System.out.println(GeoUtility.isInPolygon(point, polygon));

		// JSON测试；

		// List<GeoPoint> pts=new ArrayList<GeoPoint>();
		// pts.add(new GeoPoint(116,34));
		// pts.add(new GeoPoint(123, 45));
		//
		// JSONArray adfArray =JSONArray.fromObject(pts);
		// String resultString=adfArray.toString();
		//
		// System.out.println(resultString);

		// 逆地理编码
		// IGeoCoder geocoder=new BaiduV2GeoCoder();
		// double lat=39.983424;
		// double lng=116.322987;
		//
		// ReGeoCoderResult result=geocoder.GetAddress(lng, lat);
		//
		// System.out.println(result.getAddressComponent().getCity());
		// System.out.println(result.getPois().get(0).getAddr());

		// IGeoCoder geocoder=new BaiduV2GeoCoder();
		//
		// String address="广西壮族自治区南宁市青秀区仙葫经济开发区管理委员会金质苑1栋";
		//
		// GeoPoint result1=geocoder.GetLocation(address);
		// GeoPoint
		// result2=GeoCoder.getInstance().getGeoCoder().Search(address);
		//
		// if (result1 != null) {
		// System.out.println(result1.getLng() + "   " + result1.getLat());
		// }
		//
		// if (result2 != null) {
		// System.out.println(result2.getLng() + "    " + result2.getLat());
		// }

		/*
		 * GeoPoint start=new GeoPoint(116.374837493, 40);
		 *
		 * GeoPoint end=new GeoPoint(116.374837493, 40);
		 *
		 *
		 * double result=
		 * GeoCoder.getInstance().getRouteMatrix().getRouteDistance(start, end);
		 *
		 *
		 * System.out.println(result);
		 */
	}
}
