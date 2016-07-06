
package cn.explink.ws.vo;

/**
 * 前端匹配小件员页面展示vo
 * <p>
 * 类详细描述
 * </p>
 * @author vince.zhou
 * @since 1.0
 */
public class DistributerBeanVo {

    private String key;

    public DistributerBeanVo(Long key, String val, String distributer) {
        super();
        this.key = String.valueOf(key);
        this.val = val;
        this.distributer = distributer;
    }

    public DistributerBeanVo() {
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
