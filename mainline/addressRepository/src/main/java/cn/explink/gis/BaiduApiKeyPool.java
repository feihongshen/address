package cn.explink.gis;

import java.util.Random;

/**
 *
 * 百度api中必须用的key。此key可以在百度LBS开放平台申请 (http://lbsyun.baidu.com/apiconsole/key)
 * 每个账号每天可以试用10万次，所以，这个Key池每天可以提供20万次的调用
 *
 * @author songkaojun 2015年5月13日
 */
public class BaiduApiKeyPool {

	private static final String[] apiKeys = new String[] { "pz8gGVf2lcvlbQmg3e5QgOVi", "liVsUEXGw2g8SbGYdK4IYnKG" };

	public String getRandomKey() {
		Random random = new Random();
		int nextInt = random.nextInt(BaiduApiKeyPool.apiKeys.length);
		return BaiduApiKeyPool.apiKeys[nextInt];
	}
}
