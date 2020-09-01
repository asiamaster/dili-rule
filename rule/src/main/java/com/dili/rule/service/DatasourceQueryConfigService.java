/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dili.rule.service;

import com.dili.rule.domain.DatasourceQueryConfig;
import com.dili.ss.base.BaseServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author admin
 */
@Service
@Transactional
public class DatasourceQueryConfigService extends  BaseServiceImpl<DatasourceQueryConfig, Long>{
    
}
