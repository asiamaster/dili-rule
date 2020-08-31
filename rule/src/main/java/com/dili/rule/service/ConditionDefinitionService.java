package com.dili.rule.service;

import com.dili.rule.domain.ConditionDefinition;
import com.dili.ss.base.BaseService;
import com.dili.ss.domain.BaseOutput;

/**
 * 由MyBatis Generator工具自动生成
 * This file was generated on 2020-05-13 11:23:41.
 */
public interface ConditionDefinitionService extends BaseService<ConditionDefinition, Long> {

    /**
     * 转换规则计算条件值为需要的期望值
     * 数据库里计算条件为 ([4]+6)*6  此种表达式，其中[4]表示的是规则定义ID，根据对应的需要转换成想要的label或matchKey
     * @param actionExpression 规则中存储的值
     * @param label 是否转换为显示label，如果为false，则转换显示为 matchKey
     * @return
     */
    String convertTargetValDefinition(String actionExpression,Boolean label);

    /**
     * 保存规则条件预定义信息
     * @param conditionDefinition 条件定义数据
     * @return 处理结果
     */
    BaseOutput save(ConditionDefinition conditionDefinition);
}