
package cn.explink.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "old_id")
public class OldId {

    // id int
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    // type int
    @Column(name = "type", nullable = false)
    private Integer type;

    // table varchar
    @Column(name = "tab", nullable = false)
    private String table;

    // old_id int
    @Column(name = "old_id", nullable = false)
    private Long oldId;

    // customer_id
    @Column(name = "customer_id", nullable = false)
    private Long customerId;

    // new_id
    @Column(name = "new_id", nullable = false)
    private Long newId;

    public Long getNewId() {
        return this.newId;
    }

    public void setNewId(Long newId) {
        this.newId = newId;
    }

    public Long getCustomerId() {
        return this.customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getType() {
        return this.type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getTable() {
        return this.table;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public Long getOldId() {
        return this.oldId;
    }

    public void setOldId(Long oldId) {
        this.oldId = oldId;
    }

}
