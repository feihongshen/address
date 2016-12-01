package cn.explink.gis;

import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * 百度api中必须用的key。此key可以在百度LBS开放平台申请 (http://lbsyun.baidu.com/apiconsole/key)
 * 每个账号每天可以试用10万次，所以，这个Key池每天可以提供20万次的调用
 *
 * @author songkaojun 2015年5月13日
 */
public class BaiduApiKeyPool {
	private static final Logger LOGGER = LoggerFactory.getLogger(BaiduApiKeyPool.class);
	private static int nextInt = 0;
//	private static final String[] apiKeys = new String[] { "pz8gGVf2lcvlbQmg3e5QgOVi", "liVsUEXGw2g8SbGYdK4IYnKG" };

//	public String getRandomKey() {
//		Random random = new Random();
//		int nextInt = random.nextInt(BaiduApiKeyPool.apiKeys.length);
//		return BaiduApiKeyPool.apiKeys[nextInt];
//	}
	
	/**
	 * 轮循方式获取百度地图key
	 * @return string 百度地图key
	 */
	public String getRandomKey() {
		String key = null;
		//LOGGER.info(System.currentTimeMillis()+":百度地图key:"+Arrays.toString(CommonKeyWordSuffix.baidu_map_key.toArray()));
		try {
			key = cn.explink.Constants.baidu_map_key.get(BaiduApiKeyPool.getKey());
		} catch (Exception e) {
			LOGGER.info("BaiduApiKeyPool.getRandomKey() error:改用随机获取百度地图key.", e.getMessage());
		}
		
		if (key == null || key.trim().isEmpty()) {
			key = this.getApiKey();
		}
		
		if (key != null) {//是否需要trim():由初始化统一trim()
			key = key.trim();
		}
		
		LOGGER.info("百度地图key(当前返回):"+key);
		
		return key;
	}
	
	/**
	 * 随机获取百度地图key
	 * @return
	 */
	private String getApiKey() {
		String key = null;
		try {
			if (cn.explink.Constants.baidu_map_key == null || cn.explink.Constants.baidu_map_key.isEmpty()) {
				LOGGER.error("BaiduApiKeyPool.getRandomKey error: 百度地图key为null");
			} else {
				Random random = new Random();
				key = cn.explink.Constants.baidu_map_key.get(random.nextInt(cn.explink.Constants.baidu_map_key.size()));
			}
		} catch (Exception e) {
			LOGGER.info("BaiduApiKeyPool.getRandomKey() error:", e.getMessage());
		}
		return key;
	}
	
	/**
	 * 返回index
	 * 当 cn.explink.Constants.baidu_map_key为isempty时会报错。
	 * @return
	 */
	private static synchronized int getKey() {
		nextInt++;
		if (nextInt < 0 || nextInt >= cn.explink.Constants.baidu_map_key.size()) {
			nextInt = 0;
		}
		return nextInt;
	}
	
	public static void main(String [] args){
		//6C4PXqYKl3KvbOIi4zkyQTXUMj28pSLb,z2RoCbclxOBdAoxhEWi8aNd0V4Dxn6jA, xFcTZ0R0EdUc3wuCGKfZMhrdEy8PFKSW
		cn.explink.Constants.baidu_map_key.add("6C4PXqYKl3KvbOIi4zkyQTXUMj28pSLb");
		cn.explink.Constants.baidu_map_key.add("z2RoCbclxOBdAoxhEWi8aNd0V4Dxn6jA");
		cn.explink.Constants.baidu_map_key.add("xFcTZ0R0EdUc3wuCGKfZMhrdEy8PFKSW");
		BaiduApiKeyPool pool = new BaiduApiKeyPool();
		int t = 0;
		long l= System.currentTimeMillis();
		for (int i = 0; i < 20; i++) {
			t++;
			System.out.println("第"+t+"次:"+pool.getRandomKey());
		}
		System.out.println(System.currentTimeMillis()-l);
	}
}
