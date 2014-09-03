package cn.explink.lucene;

import java.io.File;
import java.io.IOException;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.wltea.analyzer.lucene.IKAnalyzer;

public class LuceneEnvironment {

	private static LuceneEnvironment instance = new LuceneEnvironment();

	// TODO move to config file
	public static final String indexPath = "e:/address/lucene/index";

	public static final int DEFAULT_MAX_RESULT_COUNT = 1000;

	private ExplinkIKConfig ikConfig;

	private File dictDirectory;

	private FSDirectory indexDirectory;

	private IndexWriter indexWriter;

	private Analyzer analyzer = new IKAnalyzer(false);

	private IndexSearcher indexSearch;

	private QueryParser queryParser;

	private LuceneEnvironment() {
		ikConfig = ExplinkIKConfig.getInstance();
		String dictDirectoryString = ikConfig.getDictionayDirectory();
		dictDirectory = new File(dictDirectoryString);
	}

	public static LuceneEnvironment getInstance() {
		return instance;
	}

	public File getDictDirectory() {
		return dictDirectory;
	}

	public FSDirectory getIndexDirectory() throws IOException {
		if (indexDirectory == null) {
			indexDirectory = FSDirectory.open(new File(indexPath));
		}
		return indexDirectory;
	}

	public IndexWriter getIndexWriter() throws IOException {
		if (indexWriter == null) {
			FSDirectory fsDirectory = getIndexDirectory();
			IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_47, analyzer);
			indexWriter = new IndexWriter(fsDirectory, config);
		}
		return indexWriter;
	}
	
	public IndexSearcher getIndexSearch() throws IOException {
		if (indexSearch == null) {
			indexSearch = new IndexSearcher(DirectoryReader.open(getIndexDirectory()));
		}
		return indexSearch;
	}

	public QueryParser getQueryParser() {
		if (queryParser == null) {
			queryParser = new QueryParser(Version.LUCENE_47, "name", analyzer);
		}
		return queryParser;
	}
}
