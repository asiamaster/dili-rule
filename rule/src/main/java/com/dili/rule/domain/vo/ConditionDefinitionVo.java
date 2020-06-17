package com.dili.rule.domain.vo;

import com.dili.rule.domain.ConditionDefinition;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * <B></B>
 * <B>Copyright:本软件源代码版权归农丰时代科技有限公司及其研发团队所有,未经许可不得任意复制与传播.</B>
 * <B>农丰时代科技有限公司</B>
 *
 * @author yuehongbo
 * @date 2020/5/20 11:28
 */
@Getter
@Setter
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

}
