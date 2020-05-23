package com.dili.rule.service.impl;

import com.dili.rule.domain.ChargeRule;
import com.dili.rule.domain.vo.ChargeRuleVo;
import com.dili.rule.mapper.ChargeRuleMapper;
import com.dili.rule.service.ChargeRuleService;
import com.dili.ss.base.BaseServiceImpl;
import com.dili.ss.domain.EasyuiPageOutput;
import com.dili.ss.metadata.ValueProviderUtils;
import com.dili.ss.util.POJOUtils;
import com.dili.uap.sdk.session.SessionContext;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <B></B>
 * <B>Copyright:本软件源代码版权归农丰时代科技有限公司及其研发团队所有,未经许可不得任意复制与传播.</B>
 * <B>农丰时代科技有限公司</B>
 *
 * @author yuehongbo
 * @date 2020/5/16 18:01
 */
@Service
public class ChargeRuleServiceImpl extends BaseServiceImpl<ChargeRule, Long> implements ChargeRuleService {

    public ChargeRuleMapper getActualMapper() {
        return (ChargeRuleMapper)getDao();
    }

    @Override
    public EasyuiPageOutput listForEasyuiPage(ChargeRule chargeRule) throws Exception {
        if (chargeRule.getRows() != null && chargeRule.getRows() >= 1) {
            PageHelper.startPage(chargeRule.getPage(), chargeRule.getRows());
        }
        if (StringUtils.isNotBlank(chargeRule.getSort())) {
            chargeRule.setSort(POJOUtils.humpToLineFast(chargeRule.getSort()));
        }
        List<ChargeRuleVo> chargeRuleVoList = getActualMapper().listForPage(chargeRule);
        long total = chargeRuleVoList instanceof Page ? ((Page) chargeRuleVoList).getTotal() : (long) chargeRuleVoList.size();
        List results = true ? ValueProviderUtils.buildDataByProvider(chargeRule, chargeRuleVoList) : chargeRuleVoList;
        return new EasyuiPageOutput((int) total, results);
    }
}
