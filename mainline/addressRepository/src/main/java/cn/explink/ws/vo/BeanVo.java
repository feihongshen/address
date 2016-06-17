
package cn.explink.ws.vo;

public class BeanVo {

    private String key;

    public BeanVo(Long key, String val) {
        super();
        this.key = String.valueOf(key);
        this.val = val;
    }

    public BeanVo() {
    }

    private String val;

    private String distributer;

    public String getDistributer() {
        return this.distributer;
    }

    public void setDistributer(String distributer) {
        this.distributer = distributer;
    }

    public String getKey() {
        return this.key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getVal() {
        return this.val;
    }

    public void setVal(String val) {
        this.val = val;
    }

}
