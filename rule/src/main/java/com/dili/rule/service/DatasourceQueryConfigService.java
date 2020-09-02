/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dili.rule.service;

import com.dili.rule.domain.DatasourceQueryConfig;
import com.dili.ss.base.BaseServiceImpl;
import java.util.List;
import org.apache.commons.compress.utils.Lists;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author admin
 */
@Service
@Transactional
public class DatasourceQueryConfigService extends BaseServiceImpl<DatasourceQueryConfig, Long> {

    /**
     * 查询设置数据
     *
     * @param dataSourceId
     * @return
     */
    public List<DatasourceQueryConfig> findByDataSourceId(Long dataSourceId) {
        if (dataSourceId == null) {
            return Lists.newArrayList();
        }
        DatasourceQueryConfig query = new DatasourceQueryConfig();
        query.setDataSourceId(dataSourceId);
        return this.listByExample(query);

    }

}
