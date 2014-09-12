package cn.explink.tree;


public class ZTreeNode{

	/**
	 * 当前节点名字
	 */
	private String name;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getpId() {
		return pId;
	}
	public void setpId(String pId) {
		this.pId = pId;
	}
	public String getT() {
		return t;
	}
	public void setT(String t) {
		this.t = t;
	}
	public boolean isOpen() {
		return open;
	}
	public void setOpen(boolean open) {
		this.open = open;
	}
	private String id;
	public ZTreeNode(String name, Long id, Long pId) {
		super();
		this.name = name;
		this.id = String.valueOf(id);
		this.pId = String.valueOf(pId);
	}
	private String  pId;
	private String t;
	private boolean open=true;


}
