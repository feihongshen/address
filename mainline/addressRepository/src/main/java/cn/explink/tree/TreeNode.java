package cn.explink.tree;

import java.util.HashMap;
import java.util.Map;

public class TreeNode<Element> {

	/**
	 * 当前节点名字
	 */
	private String name;

	/**
	 * 当前节点内容
	 */
	private Element self;

	/**
	 * 父节点
	 */
	private TreeNode<Element> parent;

	private Map<String, TreeNode<Element>> childrenMap;

	/**
	 * 添加子节点
	 * @param name
	 * @param child
	 * @return
	 */
	public TreeNode<Element> addChild(String name, TreeNode<Element> child) {
		return getChildrenMap().put(name, child);
	}
	
	/**
	 * 根据名字获取子节点
	 * 
	 * @param name
	 * @return
	 */
	public TreeNode<Element> getChild(String name) {
		return getChildrenMap().get(name);
	}

	/**
	 * 获取子节点map
	 * 
	 * @return
	 */
	private Map<String, TreeNode<Element>> getChildrenMap() {
		if (childrenMap == null) {
			synchronized (this) {
				if (childrenMap == null) {
					childrenMap = new HashMap<String, TreeNode<Element>>();
				}
			}
		}
		return childrenMap;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Element getSelf() {
		return self;
	}

	public void setSelf(Element current) {
		this.self = current;
	}

	public TreeNode<Element> getParent() {
		return parent;
	}

	public void setParent(TreeNode<Element> parent) {
		this.parent = parent;
	}

	public void setChildrenMap(Map<String, TreeNode<Element>> childrenMap) {
		this.childrenMap = childrenMap;
	}

}
