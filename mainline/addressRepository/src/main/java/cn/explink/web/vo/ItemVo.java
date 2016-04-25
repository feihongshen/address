
package cn.explink.web.vo;

public class ItemVo {

    private Integer itemno;

    private String province;

    private String city;

    private String area;

    private String town;

    private String address;

    private String pre_out_time;

    private String remark;

    /**
     * @return the remark
     */
    public String getRemark() {
        return this.remark;
    }

    /**
     * @param remark the remark to set
     */
    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Integer getItemno() {
        return this.itemno;
    }

    public void setItemno(Integer itemno) {
        this.itemno = itemno;
    }

    public String getProvince() {
        return this.province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return this.city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getArea() {
        return this.area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getTown() {
        return this.town;
    }

    public void setTown(String town) {
        this.town = town;
    }

    public String getAddress() {
        return this.address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setPre_out_time(String pre_out_time) {
        this.pre_out_time = pre_out_time;
    }

    public String getPre_out_time() {
        return this.pre_out_time;
    }

}
