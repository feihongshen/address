
package cn.explink.web.vo;

public class CarrierMatchPointResponseItemVO {

    private Integer itemno;

    private String netid;

    private String netpoint;

    private String tpsnetpoint;

    private String remark;

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (this.itemno != null) {
            sb.append("itemno=" + this.itemno);
        }
        if (this.netid != null) {
            sb.append("netid=" + this.netid);
        }
        if (this.netpoint != null) {
            sb.append("netpoint=" + this.netpoint);
        }
        if (this.tpsnetpoint != null) {
            sb.append("tpsnetpoint=" + this.tpsnetpoint);
        }
        if (this.remark != null) {
            sb.append("remark=" + this.remark);
        }
        return sb.toString();
    }

    public String getRemark() {
        return this.remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Integer getItemno() {
        return this.itemno;
    }

    public void setItemno(Integer itemno) {
        this.itemno = itemno;
    }

    public String getNetid() {
        return this.netid;
    }

    public void setNetid(String netid) {
        this.netid = netid;
    }

    public String getNetpoint() {
        return this.netpoint;
    }

    public void setNetpoint(String netpoint) {
        this.netpoint = netpoint;
    }

    /**
     * @return the tpsnetpoint
     */
    public String getTpsnetpoint() {
        return this.tpsnetpoint;
    }

    /**
     * @param tpsnetpoint the tpsnetpoint to set
     */
    public void setTpsnetpoint(String tpsnetpoint) {
        this.tpsnetpoint = tpsnetpoint;
    }

}
