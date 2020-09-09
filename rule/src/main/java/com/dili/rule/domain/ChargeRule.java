package com.dili.rule.domain;

import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.annotation.JSONField;
import com.dili.ss.dao.sql.DateNextVersion;
import com.dili.ss.domain.BaseDomain;
import org.springframework.format.annotation.DateTimeFormat;
import tk.mybatis.mapper.annotation.Version;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * 由MyBatis Generator工具自动生成 规则 This file was generated on 2020-05-16 17:51:56.
 */
@Table(name = "`charge_rule`")
public class ChargeRule extends BaseDomain implements Serializable {

    @Transient
    private static final long serialVersionUID = -7819259156170543497L;

    /**
     * 唯一ID
     */
    @Id
    @Column(name = "`id`", updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 原始ID(在非启用状态下，修改数据，会生成一条新规则，需记录原始规则ID)
     */
    @Column(name = "`original_id`")
    private Long originalId;

    /**
     * 规则所属于某个市场
     */
    @Column(name = "`market_id`", updatable = false)
    private Long marketId;

    /**
     * 所属的业务类型
     */
    @Column(name = "`business_type`", updatable = false)
    private String businessType;

    /**
     * 组别
     */
    @Column(name = "`group_id`")
    private Long groupId;

    /**
     * 收费项
     */
    @Column(name = "`charge_item`")
    private Long chargeItem;

    /**
     * 规则名称
     */
    @Column(name = "`rule_name`")
    private String ruleName;

    /**
     * 规则状态
     */
    @Column(name = "`state`")
    private Integer state;

    /**
     * 优先级(数字越大，优先级越高)
     */
    @Column(name = "`priority`")
    private Integer priority;

    /**
     * 有效期起始
     */
    @Column(name = "`expire_start`")
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime expireStart;

    /**
     * 有效期止
     */
    @Column(name = "`expire_end`")
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime expireEnd;

    /**
     * 计算指标参数
     */
    @Column(name = "`action_expression_params`")
    private String actionExpressionParams;

    /**
     * 计算指标类型
     */
    @Column(name = "`action_expression_type`")
    private Integer actionExpressionType;

    /**
     * 计算指标
     */
    @Column(name = "`action_expression`")
    private String actionExpression;

    /**
     * 匹配到此规则时最低应支付的金额
     */
    @Column(name = "`min_payment`")
    private BigDecimal minPayment;

    /**
     * 匹配到此规则时最高支付金额
     */
    @Column(name = "`max_payment`")
    private BigDecimal maxPayment;

    /**
     * 备注
     */
    @Column(name = "`remark`")
    private String remark;

    /**
     * 是否可修改,如果因修改而产生了新记录，则本记录不可再修改
     */
    @Column(name = "`revisable`")
    private Integer revisable;

    /**
     * 操作员
     */
    @Column(name = "`operator_id`")
    private Long operatorId;

    /**
     * 操作员姓名
     */
    @Column(name = "`operator_name`")
    private String operatorName;

    /**
     * 创建时间
     */
    @Column(name = "`create_time`", updatable = false)
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    /**
     * 修改时间
     */
    @Column(name = "`modify_time`")
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
//    @Version(nextVersion = DateNextVersion.class)
    private LocalDateTime modifyTime;

    /**
     * 审核人ID
     */
    @Column(name = "`approver_id`")
    private Long approverId;

    /**
     * 审核人姓名
     */
    @Column(name = "`approver_name`")
    private String approverName;

    /**
     * 审核时间
     */
    @Column(name = "`approval_time`")
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime approvalTime;

    /**
     * 是否是一个后备rule
     */
    @Column(name = "`is_backup`")
    private Integer isBackup;

    /**
     * 当前规则的后备规则
     */
    @Column(name = "`backuped_rule_id`")
    private Long backupedRuleId;
    
    
    /**
     * 是否删除
     */
    @Column(name = "`is_deleted`")
    private Integer isDeleted;

//    /**
//     * 生效时间
//     */
//    @Column(name = "`activate_datetime`")
//    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
//    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
//    private LocalDateTime activateDatetime;
//
//    /**
//     * 生效规则ID
//     */
//    @Column(name = "`target_activate_rule_id`")
//    private Long targetActivateRuleId;
    /**
     * 有效期文本
     */
    @Transient
    private String expireValue;

    
    @Transient
    private String sortSql;
        
    public String getExpireValue() {
        StringBuilder str = new StringBuilder();
        if (Objects.nonNull(getExpireStart())) {
            str.append(DateUtil.formatLocalDateTime(getExpireStart()));
        }
        str.append(" 至 ");
        if (Objects.nonNull(getExpireEnd())) {
            str.append(DateUtil.formatLocalDateTime(getExpireEnd()));
        }
        return str.toString();
    }

    /**
     * @return Long return the id
     */
    public Long getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * @return Long return the originalId
     */
    public Long getOriginalId() {
        return originalId;
    }

    /**
     * @param originalId the originalId to set
     */
    public void setOriginalId(Long originalId) {
        this.originalId = originalId;
    }

    /**
     * @return Long return the marketId
     */
    public Long getMarketId() {
        return marketId;
    }

    /**
     * @param marketId the marketId to set
     */
    public void setMarketId(Long marketId) {
        this.marketId = marketId;
    }

    /**
     * @return String return the businessType
     */
    public String getBusinessType() {
        return businessType;
    }

    /**
     * @param businessType the businessType to set
     */
    public void setBusinessType(String businessType) {
        this.businessType = businessType;
    }

    /**
     * @return Long return the groupId
     */
    public Long getGroupId() {
        return groupId;
    }

    /**
     * @param groupId the groupId to set
     */
    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    /**
     * @return Long return the chargeItem
     */
    public Long getChargeItem() {
        return chargeItem;
    }

    /**
     * @param chargeItem the chargeItem to set
     */
    public void setChargeItem(Long chargeItem) {
        this.chargeItem = chargeItem;
    }

    /**
     * @return String return the ruleName
     */
    public String getRuleName() {
        return ruleName;
    }

    /**
     * @param ruleName the ruleName to set
     */
    public void setRuleName(String ruleName) {
        this.ruleName = ruleName;
    }

    /**
     * @return Integer return the state
     */
    public Integer getState() {
        return state;
    }

    /**
     * @param state the state to set
     */
    public void setState(Integer state) {
        this.state = state;
    }

    /**
     * @return Integer return the priority
     */
    public Integer getPriority() {
        return priority;
    }

    /**
     * @param priority the priority to set
     */
    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    /**
     * @param expireStart the expireStart to set
     */
    public void setExpireStart(LocalDateTime expireStart) {
        this.expireStart = expireStart;
    }

    /**
     * @param expireEnd the expireEnd to set
     */
    public void setExpireEnd(LocalDateTime expireEnd) {
        this.expireEnd = expireEnd;
    }

    /**
     * @return BigDecimal return the minPayment
     */
    public BigDecimal getMinPayment() {
        return minPayment;
    }

    /**
     * @param minPayment the minPayment to set
     */
    public void setMinPayment(BigDecimal minPayment) {
        this.minPayment = minPayment;
    }

    /**
     * @return BigDecimal return the maxPayment
     */
    public BigDecimal getMaxPayment() {
        return maxPayment;
    }

    /**
     * @param maxPayment the maxPayment to set
     */
    public void setMaxPayment(BigDecimal maxPayment) {
        this.maxPayment = maxPayment;
    }

    /**
     * @return String return the remark
     */
    public String getRemark() {
        return remark;
    }

    /**
     * @param remark the remark to set
     */
    public void setRemark(String remark) {
        this.remark = remark;
    }

    /**
     * @return Integer return the revisable
     */
    public Integer getRevisable() {
        return revisable;
    }

    /**
     * @param revisable the revisable to set
     */
    public void setRevisable(Integer revisable) {
        this.revisable = revisable;
    }

    /**
     * @return Long return the operatorId
     */
    public Long getOperatorId() {
        return operatorId;
    }

    /**
     * @param operatorId the operatorId to set
     */
    public void setOperatorId(Long operatorId) {
        this.operatorId = operatorId;
    }

    /**
     * @return String return the operatorName
     */
    public String getOperatorName() {
        return operatorName;
    }

    /**
     * @param operatorName the operatorName to set
     */
    public void setOperatorName(String operatorName) {
        this.operatorName = operatorName;
    }

    /**
     * @return LocalDateTime return the createTime
     */
    public LocalDateTime getCreateTime() {
        return createTime;
    }

    /**
     * @param createTime the createTime to set
     */
    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    /**
     * @return LocalDateTime return the modifyTime
     */
    public LocalDateTime getModifyTime() {
        return modifyTime;
    }

    /**
     * @param modifyTime the modifyTime to set
     */
    public void setModifyTime(LocalDateTime modifyTime) {
        this.modifyTime = modifyTime;
    }

    /**
     * @return Long return the approverId
     */
    public Long getApproverId() {
        return approverId;
    }

    /**
     * @param approverId the approverId to set
     */
    public void setApproverId(Long approverId) {
        this.approverId = approverId;
    }

    /**
     * @return String return the approverName
     */
    public String getApproverName() {
        return approverName;
    }

    /**
     * @param approverName the approverName to set
     */
    public void setApproverName(String approverName) {
        this.approverName = approverName;
    }

    /**
     * @return LocalDateTime return the approvalTime
     */
    public LocalDateTime getApprovalTime() {
        return approvalTime;
    }

    /**
     * @param approvalTime the approvalTime to set
     */
    public void setApprovalTime(LocalDateTime approvalTime) {
        this.approvalTime = approvalTime;
    }

    /**
     * @param expireValue the expireValue to set
     */
    public void setExpireValue(String expireValue) {
        this.expireValue = expireValue;
    }

    /**
     * @return the expireStart
     */
    public LocalDateTime getExpireStart() {
        return expireStart;
    }

    /**
     * @return the expireEnd
     */
    public LocalDateTime getExpireEnd() {
        return expireEnd;
    }

    /**
     * @return String return the actionExpression
     */
    public String getActionExpression() {
        return actionExpression;
    }

    /**
     * @param actionExpression the actionExpression to set
     */
    public void setActionExpression(String actionExpression) {
        this.actionExpression = actionExpression;
    }

    /**
     * @return String return the actionExpressionParams
     */
    public String getActionExpressionParams() {
        return actionExpressionParams;
    }

    /**
     * @param actionExpressionParams the actionExpressionParams to set
     */
    public void setActionExpressionParams(String actionExpressionParams) {
        this.actionExpressionParams = actionExpressionParams;
    }

    /**
     * @return Integer return the actionExpressionType
     */
    public Integer getActionExpressionType() {
        return actionExpressionType;
    }

    /**
     * @param actionExpressionType the actionExpressionType to set
     */
    public void setActionExpressionType(Integer actionExpressionType) {
        this.actionExpressionType = actionExpressionType;
    }

    public Integer getIsBackup() {
        return isBackup;
    }

    public void setIsBackup(Integer isBackup) {
        this.isBackup = isBackup;
    }

    public Long getBackupedRuleId() {
        return backupedRuleId;
    }

    public void setBackupedRuleId(Long backupedRuleId) {
        this.backupedRuleId = backupedRuleId;
    }

    public Integer getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(Integer isDeleted) {
        this.isDeleted = isDeleted;
    }

    public String getSortSql() {
        return sortSql;
    }

    public void setSortSql(String sortSql) {
        this.sortSql = sortSql;
    }

    @Override
    public String toString() {
        return "ChargeRule{" + "id=" + id + ", originalId=" + originalId + ", marketId=" + marketId + ", businessType=" + businessType + ", groupId=" + groupId + ", chargeItem=" + chargeItem + ", ruleName=" + ruleName + ", state=" + state + ", priority=" + priority + ", expireStart=" + expireStart + ", expireEnd=" + expireEnd + ", actionExpressionParams=" + actionExpressionParams + ", actionExpressionType=" + actionExpressionType + ", actionExpression=" + actionExpression + ", minPayment=" + minPayment + ", maxPayment=" + maxPayment + ", remark=" + remark + ", revisable=" + revisable + ", operatorId=" + operatorId + ", operatorName=" + operatorName + ", createTime=" + createTime + ", modifyTime=" + modifyTime + ", approverId=" + approverId + ", approverName=" + approverName + ", approvalTime=" + approvalTime + ", isBackup=" + isBackup + ", backupedRuleId=" + backupedRuleId + ", isDeleted=" + isDeleted + ", expireValue=" + expireValue + '}';
    }


}
