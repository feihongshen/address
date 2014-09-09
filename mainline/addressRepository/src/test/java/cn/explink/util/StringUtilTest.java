package cn.explink.util;

import java.io.IOException;

import junit.framework.TestCase;

import org.apache.lucene.queryparser.classic.ParseException;
import org.junit.Test;

public class StringUtilTest extends TestCase {

	private static final String patten = ".*[0-9]+号.*";
	
	private static final String patten2 = "[0-9]+号.*";
	
	@Test
	public void testMatch() throws IOException, ParseException {
		String address = "北京市123朝阳区大望路soho现代城A座2912号";
		assertTrue(address + " 应当匹配：" + patten, StringUtil.match(address, patten));
		assertEquals("最小子串查找错误", "2912号", StringUtil.substring(address, patten2));

		address = "北京市朝阳区大望路soho现代城A座2912号天马座会议室";
		assertTrue(address + " 应当匹配：" + patten, StringUtil.match(address, patten));
		assertEquals("最小子串查找错误", "2912号天马座会议室", StringUtil.substring(address, patten2));
		
		address = "北京市朝阳区大望路soho现代城A座2912";
		assertFalse(address + " 不应当匹配：" + patten, StringUtil.match(address, patten));
		
		address = "北京市朝阳区大望路soho现代城A座2912 号";
		assertFalse(address + " 不应当匹配：" + patten, StringUtil.match(address, patten));
		
		address = "北京市朝阳区大望路soho现代城A座2912C号";
		assertFalse(address + " 不应当匹配：" + patten, StringUtil.match(address, patten));
		
		address = "北京市朝阳区大望路soho现代城A座2912天马座会议室";
		assertFalse(address + " 不应当匹配：" + patten, StringUtil.match(address, patten));
		
	}
}
