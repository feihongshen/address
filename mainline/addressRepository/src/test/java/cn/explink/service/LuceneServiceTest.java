package cn.explink.service;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.List;

import org.apache.lucene.queryparser.classic.ParseException;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import cn.explink.domain.Address;
import cn.explink.test.support.BaseTestCase;

public class LuceneServiceTest extends BaseTestCase {

	private static final Long CUSTOMER_ID = 1L;

	@Autowired
	private LuceneService service;

	@Test
	public void testSearch() throws IOException, ParseException {
		// TODO
		// 创建关键字"大望路"，"soho现代城"，绑定到默认的customerId上，并且执行定时任务，完成词典和lucene索引的维护。
		String addressString = "北京市朝阳区大望路soho现代城A座2912";
		List<Address> addressList = service.search(addressString, CUSTOMER_ID);
		assertNotNull("search result should be null.", addressList);
	}
}
