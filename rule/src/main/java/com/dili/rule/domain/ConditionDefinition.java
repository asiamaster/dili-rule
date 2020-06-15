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
     * 条件类型(大于,小于,等于),具体参考ConditionTypeEnum
     */
    @Column(name = "`condition_type`")
    private Integer conditionType;

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

}