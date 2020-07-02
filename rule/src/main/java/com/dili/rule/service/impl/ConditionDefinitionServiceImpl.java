package com.dili.rule.service.impl;

import com.dili.rule.domain.ConditionDefinition;
import com.dili.rule.mapper.ConditionDefinitionMapper;
import com.dili.rule.service.ConditionDefinitionService;
import com.dili.ss.base.BaseServiceImpl;
import one.util.streamex.StreamEx;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 由MyBatis Generator工具自动生成
 * This file was generated on 2020-05-13 11:23:41.
 */
@Service
public class ConditionDefinitionServiceImpl extends BaseServiceImpl<ConditionDefinition, Long> implements ConditionDefinitionService {

    public ConditionDefinitionMapper getActualDao() {
        return (ConditionDefinitionMapper)getDao();
    }

    @Override
    public String convertTargetValDefinition(String targetVal, Boolean label) {
        if (StringUtils.isBlank(targetVal) || targetVal.indexOf("[") < 0) {
            return targetVal;
        }
        List<Long> idList = parseVariableId(targetVal);
        Map<Long, String> idLableMap = StreamEx.of(idList).map(id -> {
            return this.get(id);
        }).nonNull().toMap(ConditionDefinition::getId, t -> {
                    if (label) {
                        return t.getLabel();
                    } else {
                        return t.getMatchKey();
                    }
                }
        );
        for (Long id : idList) {
            targetVal = targetVal.replaceAll("\\[" + id + "\\]", idLableMap.getOrDefault(id, ""));
        }
        return targetVal;
    }


    /**
     * 获取表达式中的关联定义ID
     * @param text 表达式文本
     * @return 关联的计算ID集
     */
    private List<Long> parseVariableId(String text) {
        Pattern pattern = Pattern.compile("(\\[\\d+\\])");
        Matcher m = pattern.matcher(text);
        List<Long> idList = new ArrayList<>();
        while (m.find()) {
            idList.add(Long.parseLong(m.group().replace("[", "").replace("]", "")));
        }
        return idList;
    }
}