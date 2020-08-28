package com.dili.rule.domain;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.dili.ss.domain.BaseDomain;

/**
 * 规格计算表达式
 */
//@Table(name = "`rule_action`")
public class RuleAction extends BaseDomain {
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
     * 规则ID
     */
    @Column(name = "`rule_id`")
    private Long ruleId;

    /**
     * 顺序(优先级:值越小优先级越高)
     */
    @Column(name = "`seq_num`")
    private Long seqNum;

    /**
     * 前置条件(表达式)
     */
    @Column(name = "`condition_express`")
    private String conditionExpress;
    /**
     * 计算值(表达式)
     */
    @Column(name = "`action_express`")
    private String actionExpress;

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
     * @return Long return the seqNum
     */
    public Long getSeqNum() {
        return seqNum;
    }

    /**
     * @param seqNum the seqNum to set
     */
    public void setSeqNum(Long seqNum) {
        this.seqNum = seqNum;
    }

    /**
     * @return String return the conditionExpress
     */
    public String getConditionExpress() {
        return conditionExpress;
    }

    /**
     * @param conditionExpress the conditionExpress to set
     */
    public void setConditionExpress(String conditionExpress) {
        this.conditionExpress = conditionExpress;
    }

    /**
     * @return String return the actionExpress
     */
    public String getActionExpress() {
        return actionExpress;
    }

    /**
     * @param actionExpress the actionExpress to set
     */
    public void setActionExpress(String actionExpress) {
        this.actionExpress = actionExpress;
    }


    /**
     * @return Long return the ruleId
     */
    public Long getRuleId() {
        return ruleId;
    }

    /**
     * @param ruleId the ruleId to set
     */
    public void setRuleId(Long ruleId) {
        this.ruleId = ruleId;
    }

}