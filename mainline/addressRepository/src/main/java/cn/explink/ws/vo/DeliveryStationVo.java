
package cn.explink.ws.vo;

public class DeliveryStationVo {

    private Long externalId;

    private String name;

    private Long customerId;

    /**
     * 站点编码
     */
    private String stationCode = "";

    public String getStationCode() {
        return this.stationCode;
    }

    public void setStationCode(String stationCode) {
        this.stationCode = stationCode;
    }

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
        builder.append("]");
        return builder.toString();
    }

}
