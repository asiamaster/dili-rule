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
 * 条件值
 * This file was generated on 2020-05-16 17:55:31.
 */
@Getter
@Setter
@ToString(callSuper = true)
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
    @Column(name = "`matched_key`")
    private String matchedKey;

    /**
     * 条件类型(大于,小于,等于)
     */
    @Column(name = "`condition_type`")
    private Integer conditionType;

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

}