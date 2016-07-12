
package cn.explink.tree;

public class ZTreeNode {

    /**
     * 当前节点名字
     */
    private String name;

    private String level;

    public String getLevel() {
        return this.level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getpId() {
        return this.pId;
    }

    public void setpId(String pId) {
        this.pId = pId;
    }

    public String getT() {
        return this.t;
    }

    public void setT(String t) {
        this.t = t;
    }

    public boolean isOpen() {
        return this.open;
    }

    public void setOpen(boolean open) {
        this.open = open;
    }

    private String id;

    public ZTreeNode(String name, Long id, Long pId, Integer addressLevel) {
        this.name = name;
        this.id = String.valueOf(id);
        this.pId = String.valueOf(pId);
        this.level = String.valueOf(addressLevel);
    }

    public ZTreeNode(String name, Long id, Long pId, Integer addressLevel, String t) {
        this.name = name;
        this.id = String.valueOf(id);
        this.pId = String.valueOf(pId);
        this.level = String.valueOf(addressLevel);
        this.t = t;
    }

    public ZTreeNode(String name, Long id, Long pId, Integer addressLevel, String t, boolean isParent) {
        this.name = name;
        this.id = String.valueOf(id);
        this.pId = String.valueOf(pId);
        this.level = String.valueOf(addressLevel);
        this.t = t;
        this.isParent = isParent;
    }

    private String pId;

    private String t;

    private boolean open = true;

    private boolean isParent = true;

    private Integer page = 1;

    private Integer pageSize = 10;

    private Integer maxPage = 1;

    private boolean isHasChild = false;

    public Integer getMaxPage() {
        return this.maxPage;
    }

    public void setMaxPage(Integer maxPage) {
        this.maxPage = maxPage;
    }

    public boolean isIsParent() {
        return this.isParent;
    }

    public void setIsParent(boolean isParent) {
        this.isParent = isParent;
    }

    public Integer getPage() {
        return this.page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getPageSize() {
        return this.pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

}
