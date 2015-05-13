package cn.explink.gis;

import java.util.Random;

/**
 *
 * 百度api中必须用的key。此key可以在百度LBS开放平台申请 (http://lbsyun.baidu.com/apiconsole/key)
 * 每个key每天可以试用10万次，所以，这个Key池每天可以提供100万次的调用
 *
 * @author songkaojun 2015年5月13日
 */
public class BaiduApiKeyPool {
	private static final String[] apiKeys = new String[] { "liVsUEXGw2g8SbGYdK4IYnKG", "VnVXKligBlRazNDwSkiALUG2", "p3G98UeIQehkdXThaq5kNzEo", "E1NVx5ww0w0ikWOtkaOMcTuF", "s6bno5eZxLYLpBBhUbG4sX4d",
			"u2kTovoYNLG31yqwWCTlKoXp", "e5O2opP4URoyxlQBwq2DzzLX", "vhIng6GH6GQuZsQNvX3GPGy7", "ewGBszZGghwRF5XFsITEDnSN", "HTPPp9PqGLmzibNoGBM7xhpY" };

	public static String getRandomKey() {
		Random random = new Random();
		int nextInt = random.nextInt(BaiduApiKeyPool.apiKeys.length);
		return BaiduApiKeyPool.apiKeys[nextInt];
	}
}
