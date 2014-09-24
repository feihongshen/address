package cn.explink.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.wltea.analyzer.dic.Dictionary;

import cn.explink.dao.AddressDao;
import cn.explink.dao.AliasDao;
import cn.explink.dao.OrderDao;
import cn.explink.domain.Address;
import cn.explink.domain.Alias;
import cn.explink.domain.Order;
import cn.explink.lucene.Constants;
import cn.explink.lucene.DictChange;
import cn.explink.lucene.ExplinkIKConfig;
import cn.explink.lucene.LuceneEnvironment;
import cn.explink.lucene.ScoreFilter;
import cn.explink.ws.vo.OrderVo;
import cn.explink.ws.vo.OrderAddressMappingResult;

@Service
public class LuceneService {

	private static Logger logger = LoggerFactory.getLogger(LuceneService.class);

	@Autowired
	private AddressDao addressDao;

	@Autowired
	private AliasDao aliasDao;

	@Autowired
	private OrderDao orderDao;

	/**
	 * 初始化lucene和IKAnalyzer，服务启动时被调用一次，其他时期不应当调用次方法。
	 * 因事物问题，此方法需要在LuceneInitializer中被调用，否则此方法应为私有的。 TODO 被调用时检查lucene是否已经初始化完毕。
	 */
	public void initLucene() {
		ExplinkIKConfig ikConfig = ExplinkIKConfig.getInstance();
		Dictionary.initial(ikConfig);

		File dictDirectory = LuceneEnvironment.getInstance().getDictDirectory();
		if (!dictDirectory.exists()) {
			synchronized (LuceneService.class) {
				if (!dictDirectory.exists()) {
					try {
						if (dictDirectory.mkdirs()) {
							logger.info("create ik dict path success.");
						} else {
							String message = "create ik dict path failure.";
							logger.error(message);
							throw new RuntimeException(message);
						}
						initIndex();
					} catch (IOException e) {
						e.printStackTrace();
					}
				} else {
					// 分词词典目录已经存在
					readAnalyzerDicts(dictDirectory);
				}
			}
		} else {
			// 分词词典目录已经存在
			readAnalyzerDicts(dictDirectory);
		}
	}

	/**
	 * 读取词典目录下的所有词典
	 * 
	 * @param dictDirectory
	 */
	private void readAnalyzerDicts(File dictDirectory) {
		Dictionary dictionary = Dictionary.getSingleton();

		long startTime = System.currentTimeMillis();
		String[] childs = dictDirectory.list();
		for (String dictString : childs) {
			BufferedReader br = null;
			List<String> words = new ArrayList<String>(Constants.DEFAULT_DICT_SIZE);
			try {
				br = new BufferedReader(new InputStreamReader(new FileInputStream(new File(dictDirectory, dictString))));
				String line = null;
				while ((line = br.readLine()) != null) {
					words.add(line);
				}
				dictionary.addWords(words);
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if (br != null) {
					try {
						br.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
		logger.info("read dict time = {}", (System.currentTimeMillis() - startTime));
	}

	/**
	 * 初始化分词器词典目录和lucene索引，只在系统第一次启动时会被调用
	 * 
	 * @param indexWriter
	 * @param dictDirectory
	 * @throws IOException
	 */
	private void initIndex() throws IOException {
		long beginTime = System.currentTimeMillis();
		int start = 0, end = 0, pageSize = Constants.DEFAULT_DICT_SIZE;
		while (true) {
			start = end;
			end = start + pageSize;
			List<Address> addressList = addressDao.getBaseAddress(start, end);
			if (addressList != null && addressList.size() > 0) {
				refreshDictAndIndex(addressList, null);
			}
			if (addressList == null || addressList.size() < pageSize) {
				break;
			}
		}
		long analyzerTime = System.currentTimeMillis() - beginTime;
		logger.info("init IK analyzer time = {}", analyzerTime);
		addressDao.baseAddressIndexed();
	}

	/**
	 * 刷新词典和索引
	 * 
	 * @param addressList
	 * @throws IOException
	 */
	private void refreshDictAndIndex(List<Address> addressList, List<Alias> aliasList) throws IOException {
		File dictDirectory = LuceneEnvironment.getInstance().getDictDirectory();
		// 先刷新词典，才能刷新索引
		Map<Long, DictChange> dictChangeMap = new HashMap<Long, DictChange>();
		if (addressList != null) {
			for (Address address : addressList) {
				Long dictId = address.getId() / Constants.DEFAULT_DICT_SIZE;
				DictChange dictChange = dictChangeMap.get(dictId);
				if (dictChange == null) {
					dictChange = new DictChange();
					dictChange.setDictName("dict_" + dictId + ".dic");
					dictChangeMap.put(dictId, dictChange);
				}
				dictChange.addAddress(address);
			}
		}

		if (aliasList != null) {
			for (Alias alias : aliasList) {
				Long dictId = alias.getAddressId() / Constants.DEFAULT_DICT_SIZE;
				DictChange dictChange = dictChangeMap.get(dictId);
				if (dictChange == null) {
					dictChange = new DictChange();
					dictChange.setDictName("dict_" + dictId + ".dic");
					dictChangeMap.put(dictId, dictChange);
				}
				dictChange.addAlias(alias);
			}
		}

		for (Long dictId : dictChangeMap.keySet()) {
			DictChange dictChange = dictChangeMap.get(dictId);
			refreshDictChange(dictChange, dictDirectory);
		}

		// 刷新索引
		refreshIndex(addressList, aliasList);
	}

	/**
	 * 刷新词典改动
	 * 
	 * @param dictChange
	 * @param dictDirectory
	 * @throws IOException
	 */
	private void refreshDictChange(DictChange dictChange, File dictDirectory) throws IOException {
//		Set<String> removeSet = dictChange.getRemoveSet();
		Set<String> addSet = dictChange.getAddSet();
		Dictionary dictinonary = Dictionary.getSingleton();
//		TODO 删除词典可能导致同名地址无法正常拆分
//		dictinonary.disableWords(removeSet);
		dictinonary.addWords(addSet);

		File dict = new File(dictDirectory, dictChange.getDictName());
		Set<String> existingSet = new HashSet<String>();
		if (dict.exists()) {
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(dict)));
			String line = null;
			while ((line = br.readLine()) != null) {
//				TODO 删除词典可能导致同名地址无法正常拆分
//				if (!removeSet.contains(line)) {
					existingSet.add(line);
//				}
			}
			br.close();
		}
		dict.delete();
		dict.createNewFile();

		existingSet.addAll(addSet);
		PrintWriter pw = new PrintWriter(new FileOutputStream(dict, true));
		for (String name : existingSet) {
			pw.println(name);
		}
		pw.close();
	}

	private void refreshIndex(List<Address> addressList, List<Alias> aliasList) throws IOException {
		IndexWriter indexWriter = LuceneEnvironment.getInstance().getIndexWriter();
		Query query = null;
		if (addressList != null) {
			for (Address address : addressList) {
				query = new TermQuery(new Term("addressId", String.valueOf(address.getId())));
				indexWriter.deleteDocuments(query);
				indexWriter.addDocument(createDocument(address));
				addressDao.updateAddressIndex(address.getId());
			}
		}
		if (aliasList != null) {
			for (Alias alias : aliasList) {
				query = new TermQuery(new Term("aliasId", String.valueOf(alias.getId())));
				indexWriter.deleteDocuments(query);
				indexWriter.addDocument(createDocument(alias));
			}
		}
		
		indexWriter.commit();
	}

	private Document createDocument(Address address) throws IOException {
		Document doc = new Document();
		doc.add(new StringField("addressId", String.valueOf(address.getId()), Field.Store.YES));
		doc.add(new StringField("name", address.getName(), Field.Store.YES));
		return doc;
	}
	
	private Document createDocument(Alias alias) {
		Document doc = new Document();
		doc.add(new StringField("aliasId", String.valueOf(alias.getId()), Field.Store.YES));
		doc.add(new StringField("addressId", String.valueOf(alias.getAddressId()), Field.Store.YES));
		doc.add(new StringField("name", alias.getName(), Field.Store.YES));
		return doc;
	}

	/**
	 * 更新索引入口
	 * @param addressIdList
	 * @param aliasIdList
	 * @throws IOException
	 */
	public void updateIndex(List<Long> addressIdList, List<Long> aliasIdList) throws IOException {
		List<Address> addressList = addressDao.getAddressByIdList(addressIdList);
		List<Alias> aliasList = aliasDao.getAliasByIdList(aliasIdList);
		refreshDictAndIndex(addressList, aliasList);
	}

	/**
	 * 单个地址搜索入口
	 * @param addressLine
	 * @return
	 * @throws IOException
	 * @throws ParseException
	 */
	public List<Address> search(String addressLine, Long customerId) throws IOException, ParseException {
		logger.info("search for {}", addressLine);
		List<Address> addressList = new ArrayList<Address>();
		LuceneEnvironment luceneEnv = LuceneEnvironment.getInstance();
		IndexSearcher searcher = luceneEnv.getIndexSearch();
		QueryParser parser = luceneEnv.getQueryParser();
		Query query = parser.parse(addressLine);
		TopDocs topDocs = searcher.search(query, LuceneEnvironment.DEFAULT_MAX_RESULT_COUNT);
		System.out.println("maxScore = " + topDocs.getMaxScore());
		if (topDocs.scoreDocs != null) {
			System.out.println("length = " + topDocs.scoreDocs.length);
			List<Long> addressIdList = new ArrayList<Long>();
			for (ScoreDoc doc : topDocs.scoreDocs) {
				Document document = searcher.doc(doc.doc);
				IndexableField addressIdField = document.getField("addressId");
				addressIdList.add(Long.parseLong(addressIdField.stringValue()));
			}
			
			// 相关的地址
			if(addressIdList!=null&&!addressIdList.isEmpty()){
				List<Address> relatedAddressList = addressDao.getAddressByIdListAndCustomerId(addressIdList, customerId);
				logger.info("relatedAddressList = " + relatedAddressList);
				// 得分评估，过滤掉不符合条件的地址
				addressList = ScoreFilter.filter(relatedAddressList);
			}
		}

		return addressList;
	}

}
