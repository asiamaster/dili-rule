package com.dili.rule.domain;

import com.alibaba.fastjson.annotation.JSONField;
import com.dili.ss.domain.BaseDomain;
import com.dili.ss.domain.annotation.Like;
import com.dili.ss.domain.annotation.Operator;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

/**
 * 由MyBatis Generator工具自动生成
 * 预定义数据源
 * This file was generated on 2020-05-13 11:19:20.
 */
@Getter
@Setter
@ToString(callSuper = true)
@Table(name = "`condition_data_source`")
public class ConditionDataSource extends BaseDomain {
    /**
     * 唯一ID
     */
    @Id
    @Column(name = "`id`")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 数据源名称
     */
    @Column(name = "`name`")
    @Like
    @NotBlank(message = "名称不能为空")
    private String name;

    /**
     * 通过用户输入的url查询数据
     */
    @Column(name = "`query_url`")
    private String queryUrl;

    /**
     * 通过ids/keys输入查询url
     */
    @Column(name = "`keys_url`")
    private String keysUrl;

    /**
     * 数据来源类型(本地、远程),具体参考DataSourceTypeEnum
     */
    @Column(name = "`data_source_type`")
    private String dataSourceType;

    /**
     * 是否分页
     */
    @Column(name = "`paged`")
    private Integer paged;

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
     * 如果是本地数据，则保存再此json中，如果为远程数据，则不保存
     */
    @Column(name = "`data_json`")
    private String dataJson;

}