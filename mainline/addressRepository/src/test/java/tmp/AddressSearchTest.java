package tmp;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.cn.smart.SmartChineseAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.wltea.analyzer.dic.Dictionary;
import org.wltea.analyzer.lucene.IKAnalyzer;

import cn.explink.dao.AddressDao;
import cn.explink.domain.Address;
import cn.explink.lucene.ExplinkIKConfig;
import cn.explink.test.support.BaseTestCase;

public class AddressSearchTest extends BaseTestCase {

	static String indexPath = "index";

	@Autowired
	private AddressDao addressDao;

	Analyzer analyzer = new SmartChineseAnalyzer(Version.LUCENE_47);

	Directory directory = new RAMDirectory();

	private HashMap<String, Address> addressMap;

	@Test
	public void testCreateIndex() throws IOException, ParseException {
		analyzer = new IKAnalyzer();

		ikInit();

		createIndex();

		// IndexSearcher searcher = new
		// IndexSearcher(DirectoryReader.open(FSDirectory.open(new
		// File(indexPath))));
		IndexSearcher searcher = new IndexSearcher(DirectoryReader.open(directory));
		QueryParser parser = new QueryParser(Version.LUCENE_47, "name", analyzer);
		
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		String line = null;
		do {
			System.out.println("请输入地址：");
			line = br.readLine();
//			String queryStr = "北京市朝阳区大望路soho现代城A座2912";
			long start = System.currentTimeMillis();
			search(searcher, parser, line);
			System.out.println("匹配时间 = " + (System.currentTimeMillis() - start));
		} while (line != null && !line.equals("exit"));
	}

	public void createIndex() throws IOException {
		// b.创建IndexWriter
		// Directory dir = FSDirectory.open(new File(indexPath));
		// Version操作开始变得非常常见
		// 中文分词器的引入，好像4.7.0对庖丁等第三方分词器兼容得并不好，可能也是因为apache对原生的做了一些整合的缘故
		// 同时引入了IndexWriterConfig对象，封装了早期版本的一大堆参数
		IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_47, analyzer);
		IndexWriter indexWriter = new IndexWriter(directory, config);
		// c.根据索引数据创建索引
		List<Address> addressList = addressDao.getAllAddresses();
		addressMap = new HashMap<String, Address>();
		for (Address addr : addressList) {
			addressMap.put(addr.getId() + "", addr);
		}
		
		Collection<String> words = new ArrayList<String>();
		for (Address addr : addressList) {
			words.add(addr.getName());
		}
		Dictionary.getSingleton().addWords(words);
		
		for (Address addr : addressList) {
			indexWriter.addDocument(createDocument(indexWriter, addr));
		}

		indexWriter.close();
	}

	public Document createDocument(IndexWriter indexWriter, Address addr) throws IOException {
		Document doc = new Document();
		
		// 就像有某个商品，查询结果列表要展示商品的名称，ID，和跳转链接地址，所以从数据库取出name,id,url字段
//			StringBuilder sb = new StringBuilder();
//			if (addr.getAddressLevel() > 0) {
//				String[] paths = addr.getPath().split("-");
//				for (int i = 1; i < paths.length; i++) {
//					sb.append(addressMap.get(Long.parseLong(paths[i])).getName());
//				}
//				sb.append(addr.getName());
//			}
//			doc.add(new Field("name", sb.toString(), TextField.TYPE_STORED));
		
//		if (addr.getAddressLevel() > 3) {
//			doc.add(new Field("name", addr.getName(), TextField.TYPE_STORED));
//		} else {
//			doc.add(new StringField("name", addr.getName(), Field.Store.YES));
//		}
		doc.add(new StringField("id", String.valueOf(addr.getId()), Field.Store.YES));

		// 这个keywords就像博客文章的自定义“关键字”，这些字有多个，而且都会做用到索引并且接受分词操作的，“css学习”会被拆分为“css”和“学习”
		doc.add(new StringField("name", addr.getName(), Field.Store.YES));
//		doc.add(new TextField("name", addr.getName(), Field.Store.YES));
		return doc;
	}

	public void search(IndexSearcher searcher, QueryParser parser, String queryStr) throws ParseException, IOException {
		Query query = parser.parse(queryStr);
		// Query query = new TermQuery(new Term("name", queryStr));
		// query = new TermQuery(new Term("id", queryStr));
		int maxCount = 10;
		TopDocs topDocs = searcher.search(query, maxCount);
		System.out.println("maxScore = " + topDocs.getMaxScore());
		if (topDocs.scoreDocs != null) {
			System.out.println("length = " + topDocs.scoreDocs.length);
			
			Map<Address, Score> mappingAddressMap = new HashMap<Address, Score>();
			for (ScoreDoc doc : topDocs.scoreDocs) {
				Document document = searcher.doc(doc.doc);
				System.out.println("document = " + document);
				IndexableField idField = document.getField("id");
				String id = idField.stringValue();
				Address address = addressMap.get(id);
				System.out.println("address = " + address);
				
				boolean mergedResult = false;
				for (Address mappingAddress : mappingAddressMap.keySet()) {
					if (contain(mappingAddress, address)) {
						addScore(address, mappingAddressMap.get(mappingAddress));
						mergedResult = true;
					} else if (contain(address, mappingAddress)) {
						mappingAddressMap.put(address, mappingAddressMap.get(mappingAddress));
						mappingAddressMap.remove(mappingAddress);
						addScore(address, mappingAddressMap.get(address));
						mergedResult = true;
					}
				}
				if (!mergedResult) {
					Score score = mappingAddressMap.get(address);
					if (score == null) {
						score = new Score();
						mappingAddressMap.put(address, score);
					}
					addScore(address, score);
				}
			}
			for (Address address : mappingAddressMap.keySet()) {
				Score score = mappingAddressMap.get(address);
				if (score.districtCount > 0 && score.keyCount > 0) {
					System.out.println("匹配地址: id = " + address.getId() + ", name = " + address.getName());
				}
			}
		}
	}

	public void addScore(Address address, Score score) {
		if (address.getAddressLevel() > 3) {
			score.keyCount ++;
		} else {
			score.districtCount ++;
		}
	}

	private boolean contain(Address one, Address two) {
		String[] paths = one.getPath().split("-");
		String id = two.getId() + "";
		for (String path : paths) {
			if (path.equals(id)) {
				return true;
			}
		}
		return false;
	}

	public void ikInit() throws IOException {
//		Dictionary.initial(DefaultConfig.getInstance());
		Dictionary.initial(ExplinkIKConfig.getInstance());
	}
	
	class Score {
		int keyCount = 0;
		int districtCount = 0; 
	}
}
