package com.dili.rule.scheduler;

import com.dili.commons.glossary.YesOrNoEnum;
import com.dili.rule.component.ChargeRuleExpiresTask;
import com.dili.rule.domain.ChargeRule;
import com.dili.rule.domain.enums.RuleStateEnum;
import com.dili.rule.service.ChargeRuleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.util.CollectionUtils;

import one.util.streamex.StreamEx;
import tk.mybatis.mapper.entity.Example;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * <B></B> <B>Copyright:本软件源代码版权归农丰时代科技有限公司及其研发团队所有,未经许可不得任意复制与传播.</B>
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
        List<ChargeRule> ruleList = this.queryAllWillStartedRule();
        this.updateRuleStatus(ruleList);
    }

    /**
     * 根据ID查询即将开始或者结束的规则并启用或者禁用
     * 
     * @param ruleId
     * @return
     */
    public Optional<ChargeRule> queryAndScheduleUpdateRuleStatusById(Long ruleId) {
        if (ruleId == null) {
            return Optional.empty();
        }
        LocalDateTime now = LocalDateTime.now();
        Example example = new Example(ChargeRule.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("id", ruleId);
        criteria.andEqualTo("state", RuleStateEnum.UN_STARTED.getCode());
        criteria.andEqualTo("isDeleted", YesOrNoEnum.NO.getCode());
        criteria.andLessThanOrEqualTo("expireStart", now.plusMinutes(fixedRate));

        return StreamEx.of(this.queryAllWillStartedRule(ruleId)).append(this.queryAllWillEndedRule(ruleId))
                .map(rule -> {
                    this.updateRuleStatus(rule);
                    return rule;
                }).findFirst();

    }

    public Optional<ChargeRule> checkRuleStateEnum(Long ruleId) {
        ChargeRule item = this.chargeRuleService.get(ruleId);
        LocalDateTime now = LocalDateTime.now();

        ChargeRule updateItem = new ChargeRule();
        updateItem.setId(item.getId());
        updateItem.setModifyTime(item.getModifyTime());
        if(item.getExpireEnd()!=null){
            if (item.getExpireEnd().isBefore(now) || item.getExpireEnd().isEqual(now)) {
                updateItem.setState(RuleStateEnum.EXPIRED.getCode());
                return Optional.of(updateItem);
            }
    
        }
        
        if (item.getExpireStart().isAfter(now)) {
            updateItem.setState(RuleStateEnum.UN_STARTED.getCode());
            return Optional.of(updateItem);
        }

        if (item.getExpireStart().isBefore(now) || item.getExpireStart().isEqual(now)) {
            updateItem.setState(RuleStateEnum.ENABLED.getCode());
            return Optional.of(updateItem);
        }
        return Optional.empty();
    }

    private List<ChargeRule> queryAllWillStartedRule(Long... ruleIds) {
        LocalDateTime now = LocalDateTime.now();
        Example example = new Example(ChargeRule.class);
        Example.Criteria criteria = example.createCriteria();
        if (ruleIds != null && ruleIds.length > 0) {
            criteria.andIn("id", Arrays.asList(ruleIds));
        }
        criteria.andEqualTo("state", RuleStateEnum.UN_STARTED.getCode());
        criteria.andLessThanOrEqualTo("expireStart", now.plusMinutes(fixedRate));
        return chargeRuleService.selectByExample(example);
    }

    private List<ChargeRule> queryAllWillEndedRule(Long... ruleIds) {
        LocalDateTime now = LocalDateTime.now();
        Example example = new Example(ChargeRule.class);
        Example.Criteria criteria = example.createCriteria();
        if (ruleIds != null && ruleIds.length > 0) {
            criteria.andIn("id", Arrays.asList(ruleIds));
        }
        // 此处只查询已启用、待审核状态下的快要过期的数据，其它状态值不变
        criteria.andIn("state", Arrays.asList(RuleStateEnum.ENABLED.getCode()));
        criteria.andLessThanOrEqualTo("expireEnd", now.plusMinutes(fixedRate));
        return chargeRuleService.selectByExample(example);
    }

    /**
     * 获取即将过期的规则数据，并放入任务调度器中
     */
    @Scheduled(fixedRate = 10 * 60 * 1000, initialDelay = 10 * 1000)
    public void queryToExpired() {
        List<ChargeRule> ruleList = this.queryAllWillEndedRule();
        this.updateRuleStatus(ruleList);
    }

    /**
     * 批量更改规则状态
     * 
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
     * 
     * @param ruleInfo
     * @return
     */
    public boolean updateRuleStatus(ChargeRule ruleInfo) {
        Integer state = ruleInfo.getState();
        if (RuleStateEnum.ENABLED.getCode().equals(state) ) {
            LocalDateTime expireEnd = LocalDateTime.now().plusMinutes(fixedRate);
            if (ruleInfo.getExpireEnd()!=null&&ruleInfo.getExpireEnd().isBefore(expireEnd)) {
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
