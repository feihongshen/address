package cn.explink.util;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.SimpleHttpConnectionManager;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpClientParams;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JSONReslutUtil {
	private static final Logger LOGGER = LoggerFactory.getLogger(JSONReslutUtil.class);

	/**
	 * @param url
	 *            请求的新接口路径
	 * @param method
	 *            请求方式（post）
	 * @return
	 * @throws IOException
	 */
	public static String getResultMessage(String url, String params, String method) throws IOException {
		String responseXml = "";
		// HttpClient httpClient = new HttpClient();
		HttpClient httpClient = new HttpClient(new HttpClientParams(), new SimpleHttpConnectionManager(true));
		httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(10000);
		httpClient.getHttpConnectionManager().getParams().setSoTimeout(40000);
		PostMethod postMethod = new PostMethod(url);
		postMethod.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, "UTF-8");
		postMethod.getParams().setSoTimeout(30 * 1000);
		String[] paramStr = params.split("&");// 按“&”拆分
		if ((paramStr != null) && (paramStr.length > 0) && (params.indexOf("&") > -1)) {
			for (int i = 0; i < paramStr.length; i++) {
				postMethod.addParameter(paramStr[i].split("=")[0], paramStr[i].split("=")[1]);// 按“=”拆分，第一个做为参数名，第二个作为参数值
			}
		}
		try {
			int statusCode = httpClient.executeMethod(postMethod); // post数据
			if (statusCode == HttpStatus.SC_OK) {
				responseXml = postMethod.getResponseBodyAsString();
			} else {
				System.err.println("Response Code: " + statusCode);
			}
		} catch (HttpException e) {
			JSONReslutUtil.LOGGER.error(e.getMessage());
		} catch (IOException e) {
			JSONReslutUtil.LOGGER.error(e.getMessage());
		} finally {
			postMethod.releaseConnection();
		}
		JSONReslutUtil.LOGGER.info("RE : " + method + " : " + url + "?" + params + " : " + responseXml);
		return responseXml;

	}

	/**
	 * 不需要写日志的，用于迁移使用
	 *
	 * @param url
	 * @param params
	 * @param method
	 * @return
	 * @throws IOException
	 */
	public static String getResultMessageChangeLog(String url, String params, String method) throws IOException {
		String responseXml = "";
		HttpClient httpClient = new HttpClient();
		httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(10000);
		httpClient.getHttpConnectionManager().getParams().setSoTimeout(40000);
		PostMethod postMethod = new PostMethod(url);
		postMethod.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, "UTF-8");
		postMethod.getParams().setSoTimeout(30 * 1000);
		String[] paramStr = params.split("&");// 按“&”拆分
		if ((paramStr != null) && (paramStr.length > 0)) {
			for (int i = 0; i < paramStr.length; i++) {
				postMethod.addParameter(paramStr[i].split("=")[0], paramStr[i].split("=")[1]);// 按“=”拆分，第一个做为参数名，第二个作为参数值
			}
		}
		try {
			httpClient.executeMethod(postMethod); // post数据
			responseXml = postMethod.getResponseBodyAsString();
		} catch (HttpException e) {
			JSONReslutUtil.LOGGER.error(e.getMessage());
		} catch (IOException e) {
			JSONReslutUtil.LOGGER.error(e.getMessage());
		} finally {
			postMethod.releaseConnection();
		}
		return responseXml;

	}

	public static void sendUrl(String param) {

		try {
			URL url = new URL(param);
			HttpURLConnection httpUrlConnection = (HttpURLConnection) url.openConnection();
			// 设置是否从httpUrlConnection读入，默认情况下是true;
			httpUrlConnection.setDoInput(true);
			httpUrlConnection.setRequestProperty("Content-type", "application/x-java-serialized-object");
			// 设定请求的方法为"POST"，默认是GET
			httpUrlConnection.setRequestMethod("GET");
			// 设置为3 毫秒超时失效
			httpUrlConnection.setConnectTimeout(3000);
			httpUrlConnection.setReadTimeout(1);
			httpUrlConnection.connect();// 发送请求
		} catch (Exception e) {
			JSONReslutUtil.LOGGER.error(e.getMessage());
		}

	}

}
