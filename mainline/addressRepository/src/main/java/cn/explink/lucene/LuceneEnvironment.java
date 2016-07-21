
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

import cn.explink.util.ApplicationContextUtil;
import cn.explink.util.ResourceBundleUtil;

public class LuceneEnvironment {

    private static int initWriterCount = 0;

    private static int initSearchCount = 0;

    private static LuceneEnvironment instance = new LuceneEnvironment();

    public static final int DEFAULT_MAX_RESULT_COUNT = 1000;

    private File dictDirectory;

    private FSDirectory indexDirectory;

    private IndexWriter indexWriter;

    private Analyzer analyzer = new IKAnalyzer(false);

    private IndexSearcher indexSearch;

    ResourceBundleUtil resourceBundleUtil;

    private LuceneEnvironment() {
        this.resourceBundleUtil = ApplicationContextUtil.getBean("resourceBundleUtil");
        this.dictDirectory = new File(this.resourceBundleUtil.getLuceneDictPath());
    }

    public static LuceneEnvironment getInstance() {
        return LuceneEnvironment.instance;
    }

    public File getDictDirectory() {
        return this.dictDirectory;
    }

    public FSDirectory getIndexDirectory() throws IOException {
        if (this.indexDirectory == null) {
            this.indexDirectory = FSDirectory.open(new File(this.resourceBundleUtil.getLuceneIndexPath()));
        }
        return this.indexDirectory;
    }

    public IndexWriter getIndexWriter() throws IOException {
        if (this.indexWriter == null) {
            FSDirectory fsDirectory = this.getIndexDirectory();
            IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_47, this.analyzer);
            this.indexWriter = new IndexWriter(fsDirectory, config);
            LuceneEnvironment.initWriterCount++;
        }
        return this.indexWriter;
    }

    public IndexSearcher getIndexSearch() throws IOException {
        if (this.indexSearch == null) {
            this.indexSearch = new IndexSearcher(DirectoryReader.open(this.getIndexDirectory()));
            LuceneEnvironment.initSearchCount++;
        }
        return this.indexSearch;
    }

    public QueryParser getQueryParser() {
        // if (queryParser == null) {
        // queryParser = new QueryParser(Version.LUCENE_47, "name", analyzer);
        // }
        // return queryParser;
        // Note that QueryParser is not thread-safe.
        return new QueryParser(Version.LUCENE_47, "name", this.analyzer);
    }

    /**
     * 重置查询方法
     */
    public void resetSearcher() {
        try {
            this.indexSearch = new IndexSearcher(DirectoryReader.open(this.getIndexDirectory()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
