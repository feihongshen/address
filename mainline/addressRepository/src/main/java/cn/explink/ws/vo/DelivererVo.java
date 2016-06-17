
package cn.explink.ws.vo;

public class DelivererVo {

    private Long externalId;// dmp-id

    private String name;// 小件员名称

    private Long customerId;

    private String userCode;// 小件员登录名

    private Long externalStationId;// 所属dmp站点id

    public String getUserCode() {
        return this.userCode;
    }

    public void setUserCode(String userCode) {
        this.userCode = userCode;
    }

    public Long getExternalStationId() {
        return this.externalStationId;
    }

    public void setExternalStationId(Long externalStationId) {
        this.externalStationId = externalStationId;
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
