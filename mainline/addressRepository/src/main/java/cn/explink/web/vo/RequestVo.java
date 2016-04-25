
package cn.explink.web.vo;

import java.util.List;

public class RequestVo {

    private HeadVo head;

    private List<ItemVo> items;

    public HeadVo getHead() {
        return this.head;
    }

    public void setHead(HeadVo head) {
        this.head = head;
    }

    public List<ItemVo> getItems() {
        return this.items;
    }

    public void setItems(List<ItemVo> items) {
        this.items = items;
    }
}
