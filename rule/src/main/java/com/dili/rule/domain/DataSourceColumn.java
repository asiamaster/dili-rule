package com.dili.rule.domain;

import com.alibaba.fastjson.annotation.JSONField;
import com.dili.ss.domain.BaseDomain;
import com.dili.ss.metadata.FieldEditor;
import com.dili.ss.metadata.annotation.EditMode;
import com.dili.ss.metadata.annotation.FieldDef;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import javax.persistence.*;

/**
 * 由MyBatis Generator工具自动生成
 * 数据来源的属性列
 * This file was generated on 2020-05-13 11:19:57.
 */
@Getter
@Setter
@ToString(callSuper = true)
@Table(name = "`data_source_column`")
public class DataSourceColumn extends BaseDomain {
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
    @Column(name = "`column_code`")
    private String columnCode;

    /**
     * 列名称
     */
    @Column(name = "`column_name`")
    private String columnName;

    /**
     * 列索引
     */
    @Column(name = "`column_index`")
    private Integer columnIndex;

    /**
     * 是否用于显示，当选择数据后，此字段是否用于展示
     */
    @Column(name = "`display`")
    private Integer display;

    /**
     * 是否可见
     */
    @Column(name = "`visible`")
    private Integer visible;

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
     * @return String return the columnCode
     */
    public String getColumnCode() {
        return columnCode;
    }

    /**
     * @param columnCode the columnCode to set
     */
    public void setColumnCode(String columnCode) {
        this.columnCode = columnCode;
    }

    /**
     * @return String return the columnName
     */
    public String getColumnName() {
        return columnName;
    }

    /**
     * @param columnName the columnName to set
     */
    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    /**
     * @return Integer return the columnIndex
     */
    public Integer getColumnIndex() {
        return columnIndex;
    }

    /**
     * @param columnIndex the columnIndex to set
     */
    public void setColumnIndex(Integer columnIndex) {
        this.columnIndex = columnIndex;
    }

    /**
     * @return Integer return the display
     */
    public Integer getDisplay() {
        return display;
    }

    /**
     * @param display the display to set
     */
    public void setDisplay(Integer display) {
        this.display = display;
    }

    /**
     * @return Integer return the visible
     */
    public Integer getVisible() {
        return visible;
    }

    /**
     * @param visible the visible to set
     */
    public void setVisible(Integer visible) {
        this.visible = visible;
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

}