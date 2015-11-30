package cn.explink.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Type;

import cn.explink.domain.enums.LogTypeEnum;

@Entity
@Table(name = "biz_log")
public class BizLog {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	/*
	 * 操作类型
	 */
	@Column(name = "OPERATION_TYPE")
	private int operationType;

	/*
	 * 客户id
	 */
	@Column(name = "CUSTOMER_ID")
	private Long customerId;

	/*
	 * 关键词id
	 */
	@Column(name = "ADDRESS_ID")
	private Long addressId;

	/*
	 * 关键词
	 */
	@Column(name = "ADDRESS_NAME")
	private String addressName;

	/*
	 * 别名id
	 */
	@Column(name = "ALIAS_ID")
	private Long AliasId;

	/*
	 * 别名
	 */
	@Column(name = "ALIAS_NAME")
	private String AliasName;

	/*
	 * 配送规则id
	 */
	@Column(name = "DELIVERY_STATION_RULE_ID")
	private Long deliveryStationRuleId;

	/*
	 * 配送规则
	 */
	@Column(name = "RULE_EXPRESSION")
	private String ruleExpression;

	/*
	 * 原站点id
	 */
	@Column(name = "ORIGIN_STATION_ID")
	private Long originStationId;

	/*
	 * 原站点
	 */
	@Column(name = "ORIGIN_STATION_NAME")
	private String originStationNAME;

	/*
	 * 修改后站点id
	 */
	@Column(name = "MODIFIED_STATION_ID")
	private Long modifideStationId;

	/*
	 * 修改后站点
	 */
	@Column(name = "MODIFIED_STATION_NAME")
	private String modifideStationName;

	/*
	 * 拆合站源站点站点id
	 */
	@Column(name = "SOURCE_STATION_ID")
	private Long sourceStationId;

	/*
	 * 拆合站源站点站点
	 */
	@Column(name = "SOURCE_STATION_NAME")
	private String sourceStationName;

	/*
	 * 拆合站目的站点站点id
	 */
	@Column(name = "DEST_STATION_ID")
	private Long destStationId;

	/*
	 * 拆合站目的站点站点
	 */
	@Column(name = "DEST_STATION_NAME")
	private String destStationName;

	/*
	 * 拆合站关键词ID列表
	 */
	@Lob
	@Type(type = "text")
	@Column(name = "SPLIT_COMBINE_ADDRESS")
	private String splitCombineAddress;

	/*
	 * 操作人IP
	 */
	@Column(name = "OPERATOR_IP")
	private String operationIP;

	/*
	 * 操作时间
	 */
	@Column(name = "OPERATION_TIME")
	private Date operationTime;

	/*
	 * 要显示的日志内容（不存到数据库）
	 */
	@Transient
	private String logText;

	/*
	 * 操作类型名字
	 */
	@Transient
	private String operationName;

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public int getOperationType() {
		return this.operationType;
	}

	public void setOperationType(int operationType) {
		this.operationType = operationType;
	}

	public Long getAddressId() {
		return this.addressId;
	}

	public void setAddressId(Long addressId) {
		this.addressId = addressId;
	}

	public String getAddressName() {
		return this.addressName;
	}

	public void setAddressName(String addressName) {
		this.addressName = addressName;
	}

	public Long getAliasId() {
		return this.AliasId;
	}

	public void setAliasId(Long aliasId) {
		this.AliasId = aliasId;
	}

	public String getAliasName() {
		return this.AliasName;
	}

	public void setAliasName(String aliasName) {
		this.AliasName = aliasName;
	}

	public Long getDeliveryStationRuleId() {
		return this.deliveryStationRuleId;
	}

	public void setDeliveryStationRuleId(Long deliveryStationRuleId) {
		this.deliveryStationRuleId = deliveryStationRuleId;
	}

	public String getRuleExpression() {
		return this.ruleExpression;
	}

	public void setRuleExpression(String ruleExpression) {
		this.ruleExpression = ruleExpression;
	}

	public Long getOriginStationId() {
		return this.originStationId;
	}

	public void setOriginStationId(Long originStationId) {
		this.originStationId = originStationId;
	}

	public String getOriginStationNAME() {
		return this.originStationNAME;
	}

	public void setOriginStationNAME(String originStationNAME) {
		this.originStationNAME = originStationNAME;
	}

	public Long getModifideStationId() {
		return this.modifideStationId;
	}

	public void setModifideStationId(Long modifideStationId) {
		this.modifideStationId = modifideStationId;
	}

	public String getModifideStationName() {
		return this.modifideStationName;
	}

	public void setModifideStationName(String modifideStationName) {
		this.modifideStationName = modifideStationName;
	}

	public Long getSourceStationId() {
		return this.sourceStationId;
	}

	public void setSourceStationId(Long sourceStationId) {
		this.sourceStationId = sourceStationId;
	}

	public String getSourceStationName() {
		return this.sourceStationName;
	}

	public void setSourceStationName(String sourceStationName) {
		this.sourceStationName = sourceStationName;
	}

	public Long getDestStationId() {
		return this.destStationId;
	}

	public void setDestStationId(Long destStationId) {
		this.destStationId = destStationId;
	}

	public String getDestStationName() {
		return this.destStationName;
	}

	public void setDestStationName(String destStationName) {
		this.destStationName = destStationName;
	}

	public String getSplitCombineAddress() {
		return this.splitCombineAddress;
	}

	public void setSplitCombineAddress(String splitCombineAddress) {
		this.splitCombineAddress = splitCombineAddress;
	}

	public String getOperationIP() {
		return this.operationIP;
	}

	public void setOperationIP(String operationIP) {
		this.operationIP = operationIP;
	}

	public Date getOperationTime() {
		return this.operationTime;
	}

	public void setOperationTime(Date operationTime) {
		this.operationTime = operationTime;
	}

	public String getLogText() {
		return this.logText;
	}

	public void setLogText(String logText) {
		this.logText = logText;
	}

	public String getOperationName() {
		return this.operationName;
	}

	public void setOperationName(String operationName) {
		this.operationName = operationName;
	}

	public Long getCustomerId() {
		return this.customerId;
	}

	public void setCustomerId(Long customerId) {
		this.customerId = customerId;
	}

	public void jointText(String allAddressString) {
		if (this.operationType == LogTypeEnum.addAddress.getValue()) {
			this.logText = "新增关键词：" + this.addressName + "(id=" + this.addressId + ")";
		} else if (this.operationType == LogTypeEnum.deleteAddress.getValue()) {
			this.logText = "删除关键词：" + this.addressName + "(id=" + this.addressId + ")";
		} else if (this.operationType == LogTypeEnum.addAlias.getValue()) {
			this.logText = "新增别名：" + this.AliasName + "(id=" + this.AliasId + ")";
		} else if (this.operationType == LogTypeEnum.deleteAlias.getValue()) {
			this.logText = "删除别名：" + this.AliasName + "(id=" + this.AliasId + ")";
		} else if (this.operationType == LogTypeEnum.addRule.getValue()) {
			this.logText = "新增规则：" + this.ruleExpression + "(id=" + this.deliveryStationRuleId + "),站点：" + this.originStationNAME + "(id=" + this.originStationId + "),关键词：" + this.addressName + "(id=" + this.addressId + ")";
		} else if (this.operationType == LogTypeEnum.deleteRule.getValue()) {
			this.logText = "删除规则：" + this.ruleExpression + "(id=" + this.deliveryStationRuleId + "),站点：" + this.originStationNAME + "(id=" + this.originStationId + "),关键词：" + this.addressName + "(id=" + this.addressId + ")";
		} else if (this.operationType == LogTypeEnum.addStation.getValue()) {
			this.logText = "新增站点：" + this.originStationNAME + "(id=" + this.originStationId + ")";
		} else if (this.operationType == LogTypeEnum.updateStation.getValue()) {
			this.logText = "修改站点：" + this.modifideStationName + "(id=" + this.modifideStationId + ")";
		} else if (this.operationType == LogTypeEnum.deleteStation.getValue()) {
			this.logText = "删除站点：" + this.modifideStationName + "(id=" + this.modifideStationId + ")";
		} else if (this.operationType == LogTypeEnum.changeStationRelation.getValue()) {
			this.logText = "拆合站：源站点：" + this.sourceStationName + "(id=" + this.sourceStationId + ");目的站点：" + this.destStationName + "(id=" + this.destStationId + ");移动的关键词：" + allAddressString + ")";
		}
	}
}
