/**
 * 
 */
package cn.explink.modle;

import java.util.List;

import cn.explink.tree.ZTreeNode;

/**
 * @author songkaojun 2014年12月18日 下午6:47:57
 * 
 */
public class KeywordMatchedResult {
	private List<String> keywordList;

	private List<ZTreeNode> zTreeNodeList;

	public List<String> getKeywordList() {
		return keywordList;
	}

	public void setKeywordList(List<String> keywordList) {
		this.keywordList = keywordList;
	}

	public List<ZTreeNode> getzTreeNodeList() {
		return zTreeNodeList;
	}

	public void setzTreeNodeList(List<ZTreeNode> zTreeNodeList) {
		this.zTreeNodeList = zTreeNodeList;
	}

}
