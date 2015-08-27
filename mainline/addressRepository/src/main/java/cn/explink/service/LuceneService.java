package cn.explink.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.wltea.analyzer.dic.Dictionary;

import cn.explink.dao.AddressDao;
import cn.explink.dao.AliasDao;
import cn.explink.dao.OrderDao;
import cn.explink.domain.Address;
import cn.explink.domain.Alias;
import cn.explink.lucene.AddressFilter;
import cn.explink.lucene.Constants;
import cn.explink.lucene.DictChange;
import cn.explink.lucene.ExplinkIKConfig;
import cn.explink.lucene.LuceneEnvironment;
import cn.explink.modle.KeywordMatchedResult;
import cn.explink.tree.ZTreeNode;
import cn.explink.util.StringUtil;

@Service
public class LuceneService {

	private static final Logger LOGGER = LoggerFactory.getLogger(LuceneService.class);

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
							LuceneService.LOGGER.info("创建IK分词器词典路径成功！");
						} else {
							String message = "创建IK分词器词典路径失败！";
							LuceneService.LOGGER.error(message);
							throw new RuntimeException(message);
						}
						this.initIndex();
					} catch (IOException e) {
						LuceneService.LOGGER.error(e.getMessage());
					}
				} else {
					// 分词词典目录已经存在
					this.readAnalyzerDicts(dictDirectory);
				}
			}
		} else {
			// 分词词典目录已经存在
			this.readAnalyzerDicts(dictDirectory);
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
				LuceneService.LOGGER.error(e.getMessage());
			} finally {
				if (br != null) {
					try {
						br.close();
					} catch (IOException e) {
						LuceneService.LOGGER.error(e.getMessage());
					}
				}
			}
		}
		LuceneService.LOGGER.info("读取词典花费时间为:{}ms", (System.currentTimeMillis() - startTime));
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
			List<Address> addressList = this.addressDao.getBaseAddress(start, end);
			if ((addressList != null) && (addressList.size() > 0)) {
				this.refreshDictAndIndex(addressList, null);
			}
			if ((addressList == null) || (addressList.size() < pageSize)) {
				break;
			}
		}
		long analyzerTime = System.currentTimeMillis() - beginTime;
		LuceneService.LOGGER.info("初始化IK分词器用时{}ms", analyzerTime);
		this.addressDao.baseAddressIndexed();
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
			this.refreshDictChange(dictChange, dictDirectory);
		}

		// 刷新索引
		this.refreshIndex(addressList, aliasList);
	}

	/**
	 * 刷新词典改动
	 *
	 * @param dictChange
	 * @param dictDirectory
	 * @throws IOException
	 */
	private void refreshDictChange(DictChange dictChange, File dictDirectory) throws IOException {
		// Set<String> removeSet = dictChange.getRemoveSet();
		Set<String> addSet = dictChange.getAddSet();
		Dictionary dictinonary = Dictionary.getSingleton();
		// TODO 删除词典可能导致同名地址无法正常拆分
		// dictinonary.disableWords(removeSet);
		dictinonary.addWords(addSet);

		File dict = new File(dictDirectory, dictChange.getDictName());
		Set<String> existingSet = new HashSet<String>();
		if (dict.exists()) {
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(dict)));
			String line = null;
			while ((line = br.readLine()) != null) {
				// TODO 删除词典可能导致同名地址无法正常拆分
				// if (!removeSet.contains(line)) {
				existingSet.add(line);
				// }
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
				indexWriter.addDocument(this.createDocument(address));
				this.addressDao.updateAddressIndex(address.getId());
				LuceneService.LOGGER.info("已经为关键词[{}]成功创建索引！", address.getName());
			}
		}
		if (aliasList != null) {
			for (Alias alias : aliasList) {
				query = new TermQuery(new Term("aliasId", String.valueOf(alias.getId())));
				indexWriter.deleteDocuments(query);
				indexWriter.addDocument(this.createDocument(alias));
				LuceneService.LOGGER.info("已经为别名[{}]成功创建索引！", alias.getName());
			}
		}
		indexWriter.commit();
		LuceneEnvironment.getInstance().resetSearcher();
	}

	private Document createDocument(Address address) throws IOException {
		Document doc = new Document();
		doc.add(new StringField("addressId", String.valueOf(address.getId()), Field.Store.YES));
		doc.add(new StringField("name", address.getName().toLowerCase(), Field.Store.YES));
		return doc;
	}

	private Document createDocument(Alias alias) {
		Document doc = new Document();
		doc.add(new StringField("aliasId", String.valueOf(alias.getId()), Field.Store.YES));
		doc.add(new StringField("addressId", String.valueOf(alias.getAddressId()), Field.Store.YES));
		doc.add(new StringField("name", alias.getName().toLowerCase(), Field.Store.YES));
		return doc;
	}

	/**
	 * 更新索引入口
	 *
	 * @param addressIdList
	 * @param aliasIdList
	 * @throws IOException
	 */
	public void updateIndex(List<Long> addressIdList, List<Long> aliasIdList) throws IOException {
		List<Address> addressList = this.addressDao.getAddressByIdList(addressIdList);
		List<Alias> aliasList = this.aliasDao.getAliasByIdList(aliasIdList);
		this.refreshDictAndIndex(addressList, aliasList);
	}

	/**
	 * 单个地址搜索入口
	 *
	 * @param addressLine
	 * @return
	 * @throws IOException
	 * @throws ParseException
	 */
	public List<Address> search(String addressLine, Long customerId) throws IOException, ParseException {
		String filterString = StringUtil.filterQureyStr(StringUtil.full2Half(addressLine));

		List<Address> matchAddrList = this.getLuceneMatchAddrList(filterString, customerId);
		List<Address> allPathAddrList = this.getAllPathAddress(matchAddrList);
		// 得分评估，过滤掉不符合条件的地址
		List<Address> afterFilerAddrList = AddressFilter.filter(filterString, allPathAddrList);

		return afterFilerAddrList;
	}

	private List<Address> getAllPathAddress(List<Address> matchAddrList) {
		List<Address> allAddrList = new ArrayList<Address>();
		allAddrList.addAll(matchAddrList);

		Set<Long> pathSet = this.getPathSet(matchAddrList);
		List<Address> pathAddrList = this.addressDao.getAddressByIdSet(pathSet);
		allAddrList.addAll(pathAddrList);

		return allAddrList;
	}

	private Set<Long> getPathSet(List<Address> matchAddrList) {
		Set<Long> existAddrIdSet = new HashSet<Long>();
		for (Address addr : matchAddrList) {
			existAddrIdSet.add(addr.getId());
		}
		Set<Long> pathSet = new HashSet<Long>();
		for (Address addr : matchAddrList) {
			this.addPathToSet(pathSet, addr);
		}
		pathSet.removeAll(existAddrIdSet);

		return pathSet;
	}

	private void addPathToSet(Set<Long> pathSet, Address address) {
		String[] path = this.getAddressPath(address);
		if (path.length == 0) {
			return;
		}
		for (String part : path) {
			pathSet.add(Long.valueOf(part));
		}
	}

	private String[] getAddressPath(Address address) {
		String path = address.getPath();
		if ((path == null) || path.isEmpty()) {
			return new String[0];
		}
		return path.split("-");
	}

	public KeywordMatchedResult getKeyWordMatchResult(String addressLine, Long customerId) throws IOException, ParseException {
		KeywordMatchedResult result = new KeywordMatchedResult();
		List<Address> matchAddrList = this.getLuceneMatchAddrList(addressLine, customerId);
		if ((matchAddrList == null) || matchAddrList.isEmpty()) {
			return result;
		}
		this.fillKeyWord(result, matchAddrList);
		this.fillTreeNode(result, matchAddrList);

		return result;
	}

	private void fillKeyWord(KeywordMatchedResult result, List<Address> matchAddrList) {
		Set<String> keyWordSet = new HashSet<String>();
		for (Address address : matchAddrList) {
			keyWordSet.add(address.getName());
		}
		result.setKeywordList(new ArrayList<String>(keyWordSet));
	}

	private void fillTreeNode(KeywordMatchedResult result, List<Address> matchAddrList) {
		Set<Long> addrFullPathIdSet = this.getAddrFullPathIdSet(matchAddrList);
		List<Address> addrList = this.addressDao.getAddressByIdList(new ArrayList<Long>(addrFullPathIdSet));
		List<ZTreeNode> nodeList = this.getZTreeNodeList(addrList);
		result.setzTreeNodeList(nodeList);
	}

	private List<ZTreeNode> getZTreeNodeList(List<Address> addrList) {
		List<ZTreeNode> nodeList = new ArrayList<ZTreeNode>();
		for (Address addr : addrList) {
			nodeList.add(this.transfer(addr));
		}
		return nodeList;
	}

	private ZTreeNode transfer(Address addr) {
		return new ZTreeNode(addr.getName(), addr.getId(), addr.getParentId(), addr.getAddressLevel());
	}

	private Set<Long> getAddrFullPathIdSet(List<Address> matchAddrList) {
		Set<Long> addrIdSet = new HashSet<Long>();
		for (Address address : matchAddrList) {
			addrIdSet.addAll(this.getAddrFullPathIdSet(address));
		}
		return addrIdSet;
	}

	private Set<Long> getAddrFullPathIdSet(Address address) {
		Set<Long> addrIdSet = new HashSet<Long>();
		addrIdSet.add(address.getId());

		String path = address.getPath();
		if ((path == null) || path.isEmpty()) {
			return addrIdSet;
		}
		String[] strIds = path.split("-");
		for (String strId : strIds) {
			addrIdSet.add(Long.valueOf(strId));
		}
		return addrIdSet;
	}

	private List<Address> getLuceneMatchAddrList(String addressLine, Long customerId) throws ParseException, IOException {
		List<Long> addressIdList = new ArrayList<Long>();
		List<Document> docList = this.getLuceneMatchDocList(addressLine);

		// added by songkaojun 2015-01-28 添加别名权重
		Map<Long, List<Alias>> addressAliasMap = new HashMap<Long, List<Alias>>();
		for (Document doc : docList) {
			IndexableField addressIdField = doc.getField("addressId");
			IndexableField aliasIdField = doc.getField("aliasId");
			if (aliasIdField != null) {
				String aliasId = aliasIdField.stringValue();
				if (aliasId != null) {
					Alias alias = this.aliasDao.get(Long.parseLong(aliasId));

					if ((alias != null) && (alias.getCustomerId() != null) && (customerId.longValue() != alias.getCustomerId().longValue())) {
						continue;
					}
					if (alias == null) {
						continue;
					}
					Long addressId = Long.parseLong(addressIdField.stringValue());
					if (!addressAliasMap.containsKey(addressId)) {
						List<Alias> aliasList = new ArrayList<Alias>();
						aliasList.add(alias);
						addressAliasMap.put(addressId, aliasList);
					} else {
						addressAliasMap.get(addressId).add(alias);
					}
				}
			}
			addressIdList.add(Long.parseLong(addressIdField.stringValue()));
		}
		// 相关的地址
		if ((addressIdList != null) && !addressIdList.isEmpty()) {
			List<Address> relatedAddressList = this.addressDao.getAddressByIdListAndCustomerId(addressIdList, customerId);
			Set<Long> addressIdSet = addressAliasMap.keySet();
			for (Address relatedAddress : relatedAddressList) {
				for (Long addressId : addressIdSet) {
					if (relatedAddress.getId().equals(addressId)) {
						relatedAddress.setAliasList(addressAliasMap.get(addressId));
					}
				}
			}
			return relatedAddressList;
		}
		return new ArrayList<Address>();
	}

	private List<Document> getLuceneMatchDocList(String addressLine) throws IOException, ParseException {
		LuceneService.LOGGER.info("search for {}", addressLine);
		LuceneEnvironment luceneEnv = LuceneEnvironment.getInstance();
		IndexSearcher searcher = luceneEnv.getIndexSearch();
		QueryParser parser = luceneEnv.getQueryParser();
		String filterString = StringUtil.filterQureyStr(StringUtil.full2Half(addressLine));
		Query query = parser.parse(filterString);
		TopDocs topDocs = searcher.search(query, LuceneEnvironment.DEFAULT_MAX_RESULT_COUNT);
		List<Document> docList = new ArrayList<Document>();
		if (topDocs.scoreDocs == null) {
			return docList;
		}
		for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
			docList.add(searcher.doc(scoreDoc.doc));

		}
		return docList;
	}
}
