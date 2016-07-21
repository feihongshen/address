
package cn.explink.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.codehaus.jackson.annotate.JsonIgnore;

import cn.explink.annocation.Excel;

@Entity
@Table(name = "ADDRESS_IMPORT_DETAILS")
public class AddressImportDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "ADDRESS_ID")
    private Long addressId;

    @Excel(exportName = "省")
    @Column(name = "PROVINCE")
    private String province;

    @Excel(exportName = "市")
    @Column(name = "CITY")
    private String city;

    @Excel(exportName = "区/县")
    @Column(name = "DISTRICT")
    private String district;

    @Excel(exportName = "关键字")
    @Column(name = "ADDRESS1")
    private String address1;

    @Column(name = "ADDRESS2")
    private String address2;

    @Column(name = "ADDRESS3")
    private String address3;

    @Column(name = "DELIVERY_STATION_ID")
    private Long deliveryStationId;

    @Excel(exportName = "站点")
    @Column(name = "DELIVERY_STATION_NAME")
    private String deliveryStationName;

    @Column(name = "DELIVERER_ID")
    private Long delivererId;

    @Excel(exportName = "小件员")
    @Column(name = "DELIVERER_NAME")
    private String delivererName;

    @Excel(exportName = "结果")
    @Column(name = "STATUS")
    private Integer status;

    @Excel(exportName = "信息")
    @Column(name = "MESSAGE")
    private String message;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "RESULT_ID")
    private AddressImportResult addressImportResult;

    @Transient
    private String deliveryStationOldName;

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getAddressId() {
        return this.addressId;
    }

    public void setAddressId(Long addressId) {
        this.addressId = addressId;
    }

    public Integer getStatus() {
        return this.status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
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

    public String getDistrict() {
        return this.district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getAddress1() {
        return this.address1;
    }

    public void setAddress1(String address1) {
        this.address1 = address1;
    }

    public String getAddress2() {
        return this.address2;
    }

    public void setAddress2(String address2) {
        this.address2 = address2;
    }

    public String getAddress3() {
        return this.address3;
    }

    public void setAddress3(String address3) {
        this.address3 = address3;
    }

    public Long getDeliveryStationId() {
        return this.deliveryStationId;
    }

    public void setDeliveryStationId(Long deliveryStationId) {
        this.deliveryStationId = deliveryStationId;
    }

    public String getDeliveryStationName() {
        return this.deliveryStationName;
    }

    public void setDeliveryStationName(String deliveryStationName) {
        this.deliveryStationName = deliveryStationName;
    }

    public Long getDelivererId() {
        return this.delivererId;
    }

    public void setDelivererId(Long delivererId) {
        this.delivererId = delivererId;
    }

    public String getDelivererName() {
        return this.delivererName;
    }

    public void setDelivererName(String delivererName) {
        this.delivererName = delivererName;
    }

    public AddressImportResult getAddressImportResult() {
        return this.addressImportResult;
    }

    public void setAddressImportResult(AddressImportResult addressImportResult) {
        this.addressImportResult = addressImportResult;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("AddressImportDetail [");
        if (this.id != null) {
            builder.append("id=").append(this.id).append(", ");
        }
        if (this.addressId != null) {
            builder.append("addressId=").append(this.addressId).append(", ");
        }
        if (this.status != null) {
            builder.append("status=").append(this.status).append(", ");
        }
        if (this.message != null) {
            builder.append("message=").append(this.message).append(", ");
        }
        if (this.province != null) {
            builder.append("province=").append(this.province).append(", ");
        }
        if (this.city != null) {
            builder.append("city=").append(this.city).append(", ");
        }
        if (this.district != null) {
            builder.append("district=").append(this.district).append(", ");
        }
        if (this.address1 != null) {
            builder.append("address1=").append(this.address1).append(", ");
        }
        if (this.address2 != null) {
            builder.append("address2=").append(this.address2).append(", ");
        }
        if (this.address3 != null) {
            builder.append("address3=").append(this.address3).append(", ");
        }
        if (this.deliveryStationId != null) {
            builder.append("deliveryStationId=").append(this.deliveryStationId).append(", ");
        }
        if (this.deliveryStationName != null) {
            builder.append("deliveryStationName=").append(this.deliveryStationName).append(", ");
        }
        if (this.delivererId != null) {
            builder.append("delivererId=").append(this.delivererId).append(", ");
        }
        if (this.delivererName != null) {
            builder.append("delivererName=").append(this.delivererName).append(", ");
        }
        if (this.addressImportResult != null) {
            builder.append("addressImportResult=").append(this.addressImportResult);
        }
        builder.append("]");
        return builder.toString();
    }

    public String getDeliveryStationOldName() {
        return this.deliveryStationOldName;
    }

    public void setDeliveryStationOldName(String deliveryStationOldName) {
        this.deliveryStationOldName = deliveryStationOldName;
    }
}
