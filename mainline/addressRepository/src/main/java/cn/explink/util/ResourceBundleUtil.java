
package cn.explink.util;

/**
 * 读取文件路径配置文件
 */

public class ResourceBundleUtil {

    // lucene索引目录
    private String luceneIndexPath;

    // addressResourceBundle.getString("luceneIndexPath");

    // lucene词典目录
    private String luceneDictPath;

    /**
     * @return the luceneIndexPath
     */
    public String getLuceneIndexPath() {
        return this.luceneIndexPath;
    }

    /**
     * @param luceneIndexPath the luceneIndexPath to set
     */
    public void setLuceneIndexPath(String luceneIndexPath) {
        this.luceneIndexPath = luceneIndexPath;
    }

    /**
     * @return the luceneDictPath
     */
    public String getLuceneDictPath() {
        return this.luceneDictPath;
    }

    /**
     * @param luceneDictPath the luceneDictPath to set
     */
    public void setLuceneDictPath(String luceneDictPath) {
        this.luceneDictPath = luceneDictPath;
    }

}
