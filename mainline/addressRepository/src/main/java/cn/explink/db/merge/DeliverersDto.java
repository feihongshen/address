
package cn.explink.db.merge;

import java.sql.Timestamp;

import cn.explink.domain.Deliverer;

public class DeliverersDto extends Deliverer {

    private Timestamp creatTime;

    public Timestamp getCreatTime() {
        return creatTime;
    }

    public void setCreatTime(Timestamp creatTime) {
        this.creatTime = creatTime;
    }

}
