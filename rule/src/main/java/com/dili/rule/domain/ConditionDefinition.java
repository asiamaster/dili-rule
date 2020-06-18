package com.dili.rule.domain;

import com.alibaba.fastjson.annotation.JSONField;
import com.dili.ss.domain.BaseDomain;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 由MyBatis Generator工具自动生成
 * 规则条件预定义
 * This file was generated on 2020-05-13 11:23:41.
 */
@Getter
@Setter
@ToString(callSuper = true)
@Table(name = "`condition_definition`")
public class ConditionDefinition extends BaseDomain {
    /**
     * 唯一ID
     */
    @Id
    @Column(name = "`id`")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 所属市场
     */
    @Column(name = "`market_id`")
    private Long marketId;

    /**
     * 所属某个业务
     */
    @Column(name = "`business_type`")
    private String businessType;

    /**
     * 条件标签(显示文本)
     */
    @Column(name = "`label`")
    private String label;

    /**
     * 条件定义key值(关联匹配字段)
     */
    @Column(name = "`matched_key`")
    private String matchedKey;

    /**
     * 条件类型(大于,小于,等于),具体参考MatchTypeEnum
     */
    @Column(name = "`match_type`")
    private Integer matchType;

    /**
     * 条件默认值，多个以逗号隔开
     */
    @Column(name = "`default_values`")
    private String defaultValues;

    /**
     * 值类型(小数、整数等),具体参考ValueDataTypeEnum
     */
    @Column(name = "`data_type`")
    private Integer dataType;

    /**
     * 数据来源ID(用于设置规则时，通过什么方法获取数据)
     */
    @Column(name = "`data_source_id`")
    private Long dataSourceId;

    /**
     * 匹配数据源中的某列值
     */
    @Column(name = "`matched_column`")
    private String matchedColumn;

    /**
     * 来源数据显示方法
     */
    @Column(name = "`view_mode`")
    private Integer viewMode;

    /**
     * 数据归于某个来源(用于设置查询条件等时的数据)
     */
    @Column(name = "`data_target_id`")
    private Long dataTargetId;

    /**
     * 此条件是否用户规则条件定义，如果不是，则认为是查询条件
     */
    @Column(name = "`rule_condition`")
    private Integer ruleCondition;

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
     * 是否是计算指标 
     */
    @Column(name = "`is_variable`")
    private Integer isVariable;

    /**
     * @param id the id to set
     */
    public void setId(Long id) {
        this.id = id;
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
     * @return String return the matchedKey
     */
    public String getMatchedKey() {
        return matchedKey;
    }

    /**
     * @param matchedKey the matchedKey to set
     */
    public void setMatchedKey(String matchedKey) {
        this.matchedKey = matchedKey;
    }



    public Integer getMatchType() {
		return matchType;
	}

	public void setMatchType(Integer matchType) {
		this.matchType = matchType;
	}

	/**
     * @return String return the defaultValues
     */
    public String getDefaultValues() {
        return defaultValues;
    }

    /**
     * @param defaultValues the defaultValues to set
     */
    public void setDefaultValues(String defaultValues) {
        this.defaultValues = defaultValues;
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
     * @return Long return the dataSourceId
     */
    public Long getDataSourceId() {
        return dataSourceId;
    }

    /**
     * @param dataSourceId the dataSourceId to set
     */
    public void setDataSourceId(Long dataSourceId) {
        this.dataSourceId = dataSourceId;
    }

    /**
     * @return String return the matchedColumn
     */
    public String getMatchedColumn() {
        return matchedColumn;
    }

    /**
     * @param matchedColumn the matchedColumn to set
     */
    public void setMatchedColumn(String matchedColumn) {
        this.matchedColumn = matchedColumn;
    }

    /**
     * @return Integer return the viewMode
     */
    public Integer getViewMode() {
        return viewMode;
    }

    /**
     * @param viewMode the viewMode to set
     */
    public void setViewMode(Integer viewMode) {
        this.viewMode = viewMode;
    }

    /**
     * @return Long return the dataTargetId
     */
    public Long getDataTargetId() {
        return dataTargetId;
    }

    /**
     * @param dataTargetId the dataTargetId to set
     */
    public void setDataTargetId(Long dataTargetId) {
        this.dataTargetId = dataTargetId;
    }

    /**
     * @return Integer return the ruleCondition
     */
    public Integer getRuleCondition() {
        return ruleCondition;
    }

    /**
     * @param ruleCondition the ruleCondition to set
     */
    public void setRuleCondition(Integer ruleCondition) {
        this.ruleCondition = ruleCondition;
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

	public Long getId() {
		return id;
	}

	public Integer getIsVariable() {
		return isVariable;
	}

	public void setIsVariable(Integer isVariable) {
		this.isVariable = isVariable;
	}

}