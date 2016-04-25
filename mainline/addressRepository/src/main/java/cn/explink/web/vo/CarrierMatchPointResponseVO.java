
package cn.explink.web.vo;

import java.util.List;

public class CarrierMatchPointResponseVO {

    private CarrierMatchPointResponseHeadVO head;

    private List<CarrierMatchPointResponseItemVO> items;

    public CarrierMatchPointResponseHeadVO getHead() {
        return this.head;
    }

    public void setHead(CarrierMatchPointResponseHeadVO head) {
        this.head = head;
    }

    public List<CarrierMatchPointResponseItemVO> getItems() {
        return this.items;
    }

    public void setItems(List<CarrierMatchPointResponseItemVO> items) {
        this.items = items;
    }

}
