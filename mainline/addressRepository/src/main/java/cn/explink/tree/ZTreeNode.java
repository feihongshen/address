package cn.explink.tree;


public class ZTreeNode{

	/**
	 * 当前节点名字
	 */
	private String name;
	private String level;
	
	public String getLevel() {
		return level;
	}
	public void setLevel(String level) {
		this.level = level;
	}
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
	public ZTreeNode(String name, Long id, Long pId,Integer addressLevel) {
		this.name = name;
		this.id = String.valueOf(id);
		this.pId = String.valueOf(pId);
		this.level=String.valueOf(addressLevel);
		if(null!=pId){
			this.isParent=true;
			this.open=true;
		}
	}
	
	public ZTreeNode(String name, Long id, Long pId,Integer addressLevel,String t) {
		this.name = name;
		this.id = String.valueOf(id);
		this.pId = String.valueOf(pId);
		this.level=String.valueOf(addressLevel);
		this.t=t;
		if(null!=pId){
			this.isParent=true;
			this.open=true;
		}
	}
	
	private String  pId;
	private String t;
	private boolean open=true;
	
	private boolean isParent=false;

	public boolean isIsParent() {
		return isParent;
	}
	public void setIsParent(boolean isParent) {
		this.isParent = isParent;
	}


}
