package com.dili.rule.domain.vo;

import com.alibaba.fastjson.JSONObject;
import java.util.ArrayList;
import java.util.List;

import com.dili.rule.domain.ConditionDefinition;
import one.util.streamex.StreamEx;

/**
 * <B></B>
 * <B>Copyright:本软件源代码版权归农丰时代科技有限公司及其研发团队所有,未经许可不得任意复制与传播.</B>
 * <B>农丰时代科技有限公司</B>
 *
 * @author yuehongbo
 * @date 2020/5/20 11:28
 */
public class ConditionDefinitionVo extends ConditionDefinition {

    /**
     * 规则值集合
     */
    private List<Object> values = new ArrayList<Object>();

    /**
     * 规则文件集合
     */
    private List<Object> texts = new ArrayList<Object>();

    /**
     * @return List<Object> return the values
     */
    public List<Object> getValues() {
        return values;
    }

    /**
     * @param values the values to set
     */
    public void setValues(List<Object> values) {
        this.values = values;
    }

    /**
     * @return List<Object> return the texts
     */
    public List<Object> getTexts() {
        return texts;
    }

    /**
     * @param texts the texts to set
     */
    public void setTexts(List<Object> texts) {
        this.texts = texts;
    }

    public String getJsonValues() {
        if (this.values != null) {
            List<Object>list=StreamEx.of(this.values).map(item->{
                if(item!=null){
                    return String.valueOf(item).trim();
                }
                return item;
            
            }).toList();
            return JSONObject.toJSONString(list);
        }
        return "[]";

    }

}
