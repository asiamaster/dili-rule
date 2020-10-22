package com.dili.rule.domain;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.springframework.format.annotation.DateTimeFormat;

import com.alibaba.fastjson.annotation.JSONField;
import com.dili.ss.domain.BaseDomain;

/**
 * 由MyBatis Generator工具自动生成
 * 条件值
 * This file was generated on 2020-05-16 17:55:31.
 */
@Table(name = "`charge_condition_val`")
public class ChargeConditionVal extends BaseDomain {
    /**
     * 主键ID
     */
    @Id
    @Column(name = "`id`",updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 所属规则
     */
    @Column(name = "`rule_id`")
    private Long ruleId;

    /**
     * 条件标签
     */
    @Column(name = "`label`")
    private String label;

    /**
     * 匹配Key值(即：需要验证的值)
     */
    @Column(name = "`match_key`")
    private String matchKey;

    /**
     * 条件类型(大于,小于,等于)
     */
    @Column(name = "`match_type`")
    private Integer matchType;

    /**
     * 值类型值类型(小数、整数等)
     */
    @Column(name = "`data_type`")
    private Integer dataType;

    /**
     * 条件定义
     */
    @Column(name = "`definition_id`")
    private Long definitionId;

    /**
     * 创建时间
     */
    @Column(name = "`create_time`",updatable = false)
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
     * 值
     */
    @Column(name = "`val`")
    private String val;


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

    /**
     * @return String return the label
     */
    public String getLabel() {
        return label;
    }

    /**
     * @param label the label to set
     */
    public void setLabel(String label) {
        this.label = label;
    }

    /**
     * @return String return the matchKey
     */
    public String getMatchKey() {
        return matchKey;
    }

    /**
     * @param matchKey the matchKey to set
     */
    public void setMatchKey(String matchKey) {
        this.matchKey = matchKey;
    }



    public Integer getMatchType() {
		return matchType;
	}

	public void setMatchType(Integer matchType) {
		this.matchType = matchType;
	}

	/**
     * @return Integer return the dataType
     */
    public Integer getDataType() {
        return dataType;
    }

    /**
     * @param dataType the dataType to set
     */
    public void setDataType(Integer dataType) {
        this.dataType = dataType;
    }

    /**
     * @return Long return the definitionId
     */
    public Long getDefinitionId() {
        return definitionId;
    }

    /**
     * @param definitionId the definitionId to set
     */
    public void setDefinitionId(Long definitionId) {
        this.definitionId = definitionId;
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
     * @return String return the val
     */
    public String getVal() {
        return val;
    }

    /**
     * @param val the val to set
     */
    public void setVal(String val) {
        this.val = val;
    }

	@Override
	public String toString() {
		return "ChargeConditionVal [id=" + id + ", ruleId=" + ruleId + ", label=" + label + ", matchKey=" + matchKey
				+ ", matchType=" + matchType + ", dataType=" + dataType + ", definitionId=" + definitionId
				+ ", createTime=" + createTime + ", modifyTime=" + modifyTime + ", val=" + val + "]";
	}

}