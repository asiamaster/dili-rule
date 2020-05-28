package com.dili.rule.scheduler;

import com.dili.rule.component.ChargeRuleExpiresTask;
import com.dili.rule.domain.ChargeRule;
import com.dili.rule.domain.enums.RuleStateEnum;
import com.dili.rule.service.ChargeRuleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

/**
 * <B></B>
 * <B>Copyright:本软件源代码版权归农丰时代科技有限公司及其研发团队所有,未经许可不得任意复制与传播.</B>
 * <B>农丰时代科技有限公司</B>
 *
 * @author yuehongbo
 * @date 2020/5/28 15:43
 */
@Configuration
@EnableScheduling
public class ChargeRuleExpiresScheduler {

    @Autowired
    private ChargeRuleService chargeRuleService;
    @Autowired
    private ChargeRuleExpiresTask chargeRuleExpiresTask;

    // 任务默认执行间隔周期(10分钟)
    private static final Long fixedRate = 10L;

    /**
     * 获取即将开始的规则数据，并放入任务调度器中
     */
    @Scheduled(fixedRate = 10 * 60 * 1000, initialDelay = 10 * 1000)
    public void queryToStarted() {
        Example example = new Example(ChargeRule.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("state", RuleStateEnum.UN_STARTED.getCode());
        criteria.andLessThanOrEqualTo("expireStart", LocalDateTime.now().plusMinutes(fixedRate));
        List<ChargeRule> ruleList = chargeRuleService.selectByExample(example);
        this.updateRuleStatus(ruleList);
    }

    /**
     * 获取即将过期的规则数据，并放入任务调度器中
     */
    @Scheduled(fixedRate = 10 * 60 * 1000, initialDelay = 10 * 1000)
    public void queryToExpired() {
        Example example = new Example(ChargeRule.class);
        Example.Criteria criteria = example.createCriteria();
        // 此处只查询已启用、待审核状态下的快要过期的数据，其它状态值不变
        criteria.andIn("state",
                Arrays.asList(RuleStateEnum.ENABLED.getCode(), RuleStateEnum.UNAUDITED.getCode()));
        criteria.andLessThanOrEqualTo("expireEnd", LocalDateTime.now().plusMinutes(fixedRate));
        List<ChargeRule> ruleList = chargeRuleService.selectByExample(example);
        this.updateRuleStatus(ruleList);
    }

    /**
     * 批量更改规则状态
     * @param ruleList
     * @return
     */
    private boolean updateRuleStatus(List<ChargeRule> ruleList) {
        if (!CollectionUtils.isEmpty(ruleList)) {
            ruleList.stream().forEach(r -> {
                updateRuleStatus(r);
            });
        }
        return true;
    }

    /**
     * 更改规则状态
     * @param ruleInfo
     * @return
     */
    public boolean updateRuleStatus(ChargeRule ruleInfo) {
        Integer state = ruleInfo.getState();
        if (RuleStateEnum.ENABLED.getCode().equals(state)
                || RuleStateEnum.UNAUDITED.getCode().equals(state)) {
            LocalDateTime expireEnd = LocalDateTime.now().plusMinutes(fixedRate);
            if (ruleInfo.getExpireEnd().isBefore(expireEnd)) {
                chargeRuleExpiresTask.register(ruleInfo.getId(), ruleInfo.getExpireEnd());
            }
        } else if (RuleStateEnum.UN_STARTED.getCode().equals(state)) {
            LocalDateTime expireStart = LocalDateTime.now().plusMinutes(fixedRate);
            if (ruleInfo.getExpireStart().isBefore(expireStart)) {
                chargeRuleExpiresTask.register(ruleInfo.getId(), ruleInfo.getExpireStart());
            }
        }

        return true;
    }
}
