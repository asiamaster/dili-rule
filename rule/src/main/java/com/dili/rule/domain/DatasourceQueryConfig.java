package com.dili.rule.domain;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.alibaba.fastjson.annotation.JSONField;
import com.dili.ss.domain.BaseDomain;

import org.springframework.format.annotation.DateTimeFormat;

@Table(name = "`data_source_query_config`")
public class DatasourceQueryConfig extends BaseDomain {
    /**
     *
     */
    @Transient
    private static final long serialVersionUID = 1L;

    /**
     * 唯一ID
     */
    @Id
    @Column(name = "`id`")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 所属数据源ID
     */
    @Column(name = "`data_source_id`")
    private Long dataSourceId;

           /**
     * 列编码
     */
    @Column(name = "`label`")
    private String label;
       /**
     * 列编码
     */
    @Column(name = "`query_key`")
    private String queryKey;
    
         /**
     * 列编码
     */
    @Column(name = "`default_val`")
    private String defaultVal;


    /**
     * 数据类型
     */
    @Column(name = "`data_type`")
    private Integer dataType;
    
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
     * @return String return the queryKey
     */
    public String getQueryKey() {
        return queryKey;
    }

    /**
     * @param queryKey the queryKey to set
     */
    public void setQueryKey(String queryKey) {
        this.queryKey = queryKey;
    }

    public Integer getDataType() {
        return dataType;
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

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getDefaultVal() {
        return defaultVal;
    }

    public void setDefaultVal(String defaultVal) {
        this.defaultVal = defaultVal;
    }

}