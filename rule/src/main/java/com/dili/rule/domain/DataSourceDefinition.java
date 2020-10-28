package com.dili.rule.domain;

import com.alibaba.fastjson.annotation.JSONField;
import com.dili.ss.domain.BaseDomain;
import com.dili.ss.domain.annotation.Like;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 由MyBatis Generator工具自动生成
 * 预定义数据源
 * This file was generated on 2020-05-13 11:19:20.
 */
@Table(name = "`data_source_definition`")
public class DataSourceDefinition extends BaseDomain {
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
//    @NotBlank(message = "名称不能为空")
    private String name;

    /**
     * 通过用户输入的url查询数据
     */
    @Column(name = "`query_url`")
    private String queryUrl;

	/**
	 * 查询数据时的固定查询条件
	 */
	@Column(name = "`query_condition`")
	private String queryCondition;

    /**
     * 通过ids/keys输入查询url
     */
    @Column(name = "`keys_url`")
    private String keysUrl;

    /**
     * 关键key查询时的字段名称
     */
    @Column(name = "`keys_field`")
    private String keysField;

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

    @Override
    public Long getId() {
        return id;
    }
    @Override
    public void setId(Long id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getQueryUrl() {
        return queryUrl;
    }
    public void setQueryUrl(String queryUrl) {
        this.queryUrl = queryUrl;
    }
    public String getQueryCondition() {
        return queryCondition;
    }
    public void setQueryCondition(String queryCondition) {
        this.queryCondition = queryCondition;
    }
    public String getKeysUrl() {
        return keysUrl;
    }
    public void setKeysUrl(String keysUrl) {
        this.keysUrl = keysUrl;
    }
    public String getKeysField() {
        return keysField;
    }
    public void setKeysField(String keysField) {
        this.keysField = keysField;
    }
    public String getDataSourceType() {
        return dataSourceType;
    }
    public void setDataSourceType(String dataSourceType) {
        this.dataSourceType = dataSourceType;
    }
    public Integer getPaged() {
        return paged;
    }
    public void setPaged(Integer paged) {
        this.paged = paged;
    }
    public LocalDateTime getCreateTime() {
        return createTime;
    }
    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }
    public LocalDateTime getModifyTime() {
        return modifyTime;
    }
    public void setModifyTime(LocalDateTime modifyTime) {
        this.modifyTime = modifyTime;
    }
    public String getDataJson() {
        return dataJson;
    }
    public void setDataJson(String dataJson) {
        this.dataJson = dataJson;
    }
}