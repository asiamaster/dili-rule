package com.dili.rule.scheduler;

import com.dili.commons.glossary.YesOrNoEnum;
import com.dili.rule.domain.ChargeRule;
import com.dili.rule.domain.enums.RuleStateEnum;
import com.dili.rule.service.ChargeRuleService;
import com.google.common.collect.Lists;
import java.time.Duration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import one.util.streamex.StreamEx;
import tk.mybatis.mapper.entity.Example;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Queue;
import javax.jms.Session;
import org.apache.activemq.ScheduledMessage;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessagePostProcessor;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.transaction.annotation.Transactional;

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

    private static final Logger logger = LoggerFactory.getLogger(ChargeRuleExpiresScheduler.class);

    @Autowired
    private ChargeRuleService chargeRuleService;

    // 任务默认执行间隔周期(10分钟)
    private static final Long fixedRate = 10L;

    @Autowired
    private JmsTemplate jmsTemplate;

    private Queue queue = new ActiveMQQueue("activemq.queue.chargerule");

    /**
     * 接受并处理状态
     *
     * @param ruleId
     * @param headers
     * @param message
     * @param session
     */
    @JmsListener(destination = "activemq.queue.chargerule")
    public void receiveAndHandleChargeRule(@Payload Long ruleId) {//, @Headers MessageHeaders headers, Message message, Session session) {
        logger.info("receiveMessage={}}", ruleId);
        ChargeRule item = this.chargeRuleService.get(ruleId);
        if (YesOrNoEnum.YES.getCode().equals(item.getIsBackup())) {
            ChargeRule mainRule = this.queryMainRule(item.getId());
            ChargeRule backupRule = item;
            this.enableChargeRuleList(Lists.newArrayList(mainRule, backupRule));
            this.expireChargeRuleList(Lists.newArrayList(mainRule, backupRule));
            this.checkAndUpdateMainRule(mainRule, backupRule);

        } else {
            ChargeRule mainRule = item;
            ChargeRule backupRule = this.chargeRuleService.get(mainRule.getBackupedRuleId());
            this.enableChargeRuleList(Lists.newArrayList(mainRule, backupRule));
            this.expireChargeRuleList(Lists.newArrayList(mainRule, backupRule));
            this.checkAndUpdateMainRule(mainRule, backupRule);
        }

    }

    private void enableChargeRuleList(List<ChargeRule> ruleList) {

        StreamEx.of(CollectionUtils.emptyIfNull(ruleList)).nonNull().map(ChargeRule::getId).nonNull().forEach(ruleId -> {
            ChargeRule item = this.chargeRuleService.get(ruleId);
            if (item != null && YesOrNoEnum.NO.getCode().equals(item.getIsDeleted())) {
                logger.info("ruleId={},state={}", ruleId, item.getState());
                LocalDateTime now = LocalDateTime.now();

                ChargeRule condition = new ChargeRule();
                condition.setId(ruleId);
                condition.setState(item.getState());
                condition.setIsDeleted(item.getIsDeleted());

                ChargeRule domain = new ChargeRule();

                if (RuleStateEnum.UN_STARTED.getCode().equals(item.getState())) {
                    if (item.getExpireStart() != null && (item.getExpireStart().isBefore(now) || item.getExpireStart().isEqual(now))) {
                        domain.setState(RuleStateEnum.ENABLED.getCode());
                        this.chargeRuleService.updateSelectiveByExample(domain, condition);
                    }
                }
            }

        });

    }

    private void expireChargeRuleList(List<ChargeRule> ruleList) {
        StreamEx.of(CollectionUtils.emptyIfNull(ruleList)).nonNull().map(ChargeRule::getId).nonNull().forEach(ruleId -> {
            ChargeRule item = this.chargeRuleService.get(ruleId);
            if (item != null && YesOrNoEnum.NO.getCode().equals(item.getIsDeleted())) {
                logger.info("ruleId={},state={}", ruleId, item.getState());
                LocalDateTime now = LocalDateTime.now();

                ChargeRule condition = new ChargeRule();
                condition.setId(ruleId);
                condition.setState(item.getState());
                condition.setIsDeleted(item.getIsDeleted());

                ChargeRule domain = new ChargeRule();

                if (RuleStateEnum.ENABLED.getCode().equals(item.getState())) {
                    if (item.getExpireEnd() != null && (item.getExpireEnd().isBefore(now) || item.getExpireEnd().isEqual(now))) {
                        domain.setState(RuleStateEnum.EXPIRED.getCode());
                        this.chargeRuleService.updateSelectiveByExample(domain, condition);
                    }
                }
            }
        });

    }

    /**
     * 获取即将开始的规则数据，并放入任务调度器中
     */
    @Scheduled(fixedRate = 10 * 60 * 1000, initialDelay = 10 * 1000)
    public void queryToStarted() {
        StreamEx.of(this.queryUnStartedRule()).append(this.queryEnabledRule()).forEach(this::delayChargeRule);
    }

    /**
     * 通过id来查询并延时处理
     *
     * @param ruleId
     */
    @Transactional
    public void queryAndScheduleUpdateState(Long ruleId) {
        StreamEx.of(queryUnStartedRule(Lists.newArrayList(ruleId))).append(queryEnabledRule(Lists.newArrayList(ruleId))).forEach(rule -> {
            this.handleChargeRule(rule, false);
        });
    }

    private ChargeRule queryMainRule(Long backedRuleId) {

        ChargeRule mainRuleQuery = new ChargeRule();
        mainRuleQuery.setBackupedRuleId(backedRuleId);
        return StreamEx.of(this.chargeRuleService.listByExample(mainRuleQuery)).findFirst().orElse(null);

    }

    /**
     * 检查并更新主规则的状态
     *
     * @param ruleId
     */
    private void checkAndUpdateMainRule(ChargeRule main, ChargeRule backup) {
        if (backup == null || backup.getId() == null) {
            return;
        }
        ChargeRule backupRuleItem = this.chargeRuleService.get(backup.getId());
        if (backupRuleItem == null) {
            return;
        }
        if (YesOrNoEnum.NO.getCode().equals(backupRuleItem.getIsBackup())) {
            return;
        }

        if (RuleStateEnum.ENABLED.getCode().equals(backupRuleItem.getState()) || RuleStateEnum.EXPIRED.getCode().equals(backupRuleItem.getState())) {

            ChargeRule updatableBackupRule = new ChargeRule();
            updatableBackupRule.setId(backupRuleItem.getId());
            updatableBackupRule.setIsBackup(YesOrNoEnum.NO.getCode());

            if (main != null) {
                ChargeRule mainRuleItem = this.chargeRuleService.get(main.getId());

                if (mainRuleItem != null && YesOrNoEnum.NO.getCode().equals(mainRuleItem.getIsDeleted())) {
                    ChargeRule updatableMainRule = new ChargeRule();
                    updatableMainRule.setId(mainRuleItem.getId());
                    updatableMainRule.setIsDeleted(YesOrNoEnum.YES.getCode());

                    this.chargeRuleService.updateSelective(updatableMainRule);
                    updatableBackupRule.setPriority(mainRuleItem.getPriority());
                }

            }

            this.chargeRuleService.updateSelective(updatableBackupRule);

        }
    }

    private void handleChargeRule(ChargeRule rule, boolean inQueue) {
        Long ruleId = rule.getId();
        if (!inQueue) {
            this.receiveAndHandleChargeRule(ruleId);
            StreamEx.of(queryUnStartedRule(Lists.newArrayList(ruleId))).append(queryEnabledRule(Lists.newArrayList(ruleId))).forEach(ruleItem -> {
                this.handleChargeRule(ruleItem, true);
            });
            return;
        }
        try {

            this.calculateDelay(rule).ifPresent(delay -> {
                jmsTemplate.convertAndSend(queue, ruleId, new MessagePostProcessor() {
                    @Override
                    public Message postProcessMessage(Message message) throws JMSException {
//                System.out.println("postProcessMessage executed ");
                        message.setLongProperty(ScheduledMessage.AMQ_SCHEDULED_DELAY, delay < 0 ? 0 : delay);
//                    System.out.println("long time " + message
//                            .getLongProperty(ScheduledMessage.AMQ_SCHEDULED_DELAY));
                        return message;
                    }
                });

            });

        } catch (Exception er) {
            logger.error(er.getMessage(), er);
        }

    }

    /**
     * 将ruleid放入延时队列
     *
     * @param rule
     */
    private void delayChargeRule(ChargeRule rule) {
        this.handleChargeRule(rule, true);
    }

    /**
     * 计算延时
     *
     * @param rule
     * @return
     */
    private Optional<Long> calculateDelay(ChargeRule rule) {

        LocalDateTime now = LocalDateTime.now();
        if (RuleStateEnum.ENABLED.getCode().equals(rule.getState())) {
            long delay = Duration.between(now, rule.getExpireEnd()).toMillis();
            return Optional.of(delay);
        } else if (RuleStateEnum.UN_STARTED.getCode().equals(rule.getState())) {
            long delay = Duration.between(now, rule.getExpireStart()).toMillis();
            return Optional.of(delay);
        }
        return Optional.empty();

    }

    /**
     * 查询所有10分钟内将开始的规则
     *
     * @return
     */
    private List<ChargeRule> queryUnStartedRule() {

        return this.queryUnStartedRule(Lists.newArrayList());
    }

    /**
     * 查询所有10分钟内将开始的规则
     *
     * @return
     */
    private List<ChargeRule> queryUnStartedRule(List<Long> idList) {

        Example example = new Example(ChargeRule.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("isDeleted", YesOrNoEnum.NO.getCode());
        criteria.andEqualTo("state", RuleStateEnum.UN_STARTED.getCode());
        if (idList != null && !idList.isEmpty()) {
            criteria.andIn("id", idList);
        }
        LocalDateTime now = LocalDateTime.now();
        criteria.andLessThanOrEqualTo("expireStart", now.plusMinutes(10));
//        criteria.andGreaterThanOrEqualTo("expireStart", now);
        return chargeRuleService.selectByExample(example);

    }

    /**
     * 查询所有10分钟内将结束的规则
     *
     * @return
     */
    private List<ChargeRule> queryEnabledRule(List<Long> idList) {
        Example example = new Example(ChargeRule.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("isDeleted", YesOrNoEnum.NO.getCode());
        criteria.andEqualTo("state", RuleStateEnum.ENABLED.getCode());
        if (idList != null && !idList.isEmpty()) {
            criteria.andIn("id", idList);
        }
        LocalDateTime now = LocalDateTime.now();
        criteria.andLessThanOrEqualTo("expireEnd", now.plusMinutes(10));
//        criteria.andGreaterThanOrEqualTo("expireEnd", now);
        return chargeRuleService.selectByExample(example);
    }

    /**
     * 查询所有10分钟内将结束的规则
     *
     * @return
     */
    private List<ChargeRule> queryEnabledRule() {
        return queryEnabledRule(Lists.newArrayList());
    }

}
