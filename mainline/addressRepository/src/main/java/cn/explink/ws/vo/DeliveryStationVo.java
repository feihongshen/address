
package cn.explink.ws.vo;

public class DeliveryStationVo {

    private Long externalId;

    private String name;

    private Long customerId;

    public Long getExternalId() {
        return this.externalId;
    }

    public void setExternalId(Long externalId) {
        this.externalId = externalId;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getCustomerId() {
        return this.customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    private String tpsBranchCode;

    /**
     * @return the tpsBranchCode
     */
    public String getTpsBranchCode() {
        return this.tpsBranchCode;
    }

    /**
     * @param tpsBranchCode the tpsBranchCode to set
     */
    public void setTpsBranchCode(String tpsBranchCode) {
        this.tpsBranchCode = tpsBranchCode;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("DeliveryStationVo [");
        if (this.externalId != null) {
            builder.append("externalId=").append(this.externalId).append(", ");
        }
        if (this.name != null) {
            builder.append("name=").append(this.name).append(", ");
        }
        if (this.customerId != null) {
            builder.append("customerId=").append(this.customerId);
        }
        if (this.tpsBranchCode != null) {
            builder.append("tpsBranchCode=").append(this.tpsBranchCode);
        }
        builder.append("]");
        return builder.toString();
    }

}
