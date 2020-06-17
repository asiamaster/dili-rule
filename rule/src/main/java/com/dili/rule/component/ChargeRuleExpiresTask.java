package com.dili.rule.component;

import com.dili.rule.domain.dto.OperatorUser;
import com.dili.rule.service.ChargeRuleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.TriggerContext;
import org.springframework.scheduling.support.PeriodicTrigger;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;

/**
 * <B></B>
 * <B>Copyright:本软件源代码版权归农丰时代科技有限公司及其研发团队所有,未经许可不得任意复制与传播.</B>
 * <B>农丰时代科技有限公司</B>
 *
 * @author yuehongbo
 * @date 2020/5/28 15:51
 */
@Component
public class ChargeRuleExpiresTask {

    @Autowired
    private TaskScheduler taskScheduler;
    @Autowired
    private ChargeRuleService chargeRuleService;

    /**
     * 规则信息注册到任务中
     * @param id
     * @param dateTime 触发时间
     */
    public void register(final Long id, final LocalDateTime dateTime) {
        Runnable task = () -> {
            chargeRuleService.updateStateByExpires(id,new OperatorUser(0L, "auto"));
        };
        taskScheduler.schedule(task, getTrigger(dateTime.toInstant(ZoneOffset.of("+8")).toEpochMilli()));
    }


    /**
     * 指定触发器执行
     * @param mils 毫秒数
     * @return
     */
    private Trigger getTrigger(final Long mils) {
        return new PeriodicTrigger(mils) {
            @Override
            public Date nextExecutionTime(TriggerContext triggerContext) {
                Date lastExecution = triggerContext.lastScheduledExecutionTime();
                if (lastExecution == null) {
                    return new Date(mils);
                }
                return null;
            }
        };
    }
}
