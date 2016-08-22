
package cn.explink.db.merge;

import java.sql.Timestamp;

import cn.explink.domain.Address;

public class AdressDto extends Address {

    private Long oldAddressId;

    private Timestamp createTime;

    public Long getOldAddressId() {
        return this.oldAddressId;
    }

    public void setOldAddressId(Long oldAddressId) {
        this.oldAddressId = oldAddressId;
    }

    public Timestamp getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Timestamp createTime) {
        this.createTime = createTime;
    }

}
