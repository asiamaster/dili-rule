package com.dili.rule.domain;

import com.alibaba.fastjson.annotation.JSONField;
import com.dili.ss.domain.BaseDomain;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 由MyBatis Generator工具自动生成
 * 规则
 * This file was generated on 2020-05-16 17:51:56.
 */
@Getter
@Setter
@ToString(callSuper = true)
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
     * 计费规则编码
     */
    @Column(name = "`code`", updatable = false)
    private String code;

    /**
     * 规则所属于某个市场
     */
    @Column(name = "`market_id`", updatable = false)
    private Long marketId;

    /**
     * 规则所属于的某系统
     */
    @Column(name = "`system_code`", updatable = false)
    private String systemCode;

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
    private String chargeItem;

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
     * 优先级
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
     * 计算指标类型
     */
    @Column(name = "`target_type`")
    private Integer targetType;

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
     * 计算指标
     */
    @Column(name = "`target_val`")
    private String targetVal;

}