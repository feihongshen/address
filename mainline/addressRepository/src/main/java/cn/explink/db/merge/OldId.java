
package cn.explink.db.merge;

public class OldId {

    // ID
    private Long id;

    // type
    private int type;

    // tab
    private String tab;

    // old_id
    private Long oldId;

    // customer_id
    private Long customerId;

    // new_id
    private Long newId;

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getType() {
        return this.type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getTab() {
        return this.tab;
    }

    public void setTab(String tab) {
        this.tab = tab;
    }

    public Long getOldId() {
        return this.oldId;
    }

    public void setOldId(Long oldId) {
        this.oldId = oldId;
    }

    public Long getCustomerId() {
        return this.customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public Long getNewId() {
        return this.newId;
    }

    public void setNewId(Long newId) {
        this.newId = newId;
    }

}
