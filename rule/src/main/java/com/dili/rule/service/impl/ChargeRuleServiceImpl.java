package com.dili.rule.service.impl;

import cn.hutool.core.collection.CollectionUtil;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dili.commons.glossary.YesOrNoEnum;
import com.dili.rule.domain.ChargeConditionVal;
import com.dili.rule.domain.ChargeRule;
import com.dili.rule.domain.ConditionDefinition;
import com.dili.rule.domain.dto.OperatorUser;
import com.dili.rule.domain.enums.ActionExpressionTypeEnum;
import com.dili.rule.domain.enums.RuleStateEnum;
import com.dili.rule.domain.vo.ChargeRuleVo;
import com.dili.rule.domain.vo.ConditionVo;
import com.dili.rule.mapper.ChargeRuleMapper;
import com.dili.rule.scheduler.ChargeRuleExpiresScheduler;
import com.dili.rule.sdk.domain.input.QueryFeeInput;
import com.dili.rule.sdk.domain.output.QueryFeeOutput;
import com.dili.rule.service.ChargeConditionValService;
import com.dili.rule.service.ChargeRuleService;
import com.dili.rule.service.ConditionDefinitionService;
import com.dili.rule.service.RuleEngineService;
import com.dili.ss.base.BaseServiceImpl;
import com.dili.ss.constant.ResultCode;
import com.dili.ss.domain.BaseOutput;
import com.dili.ss.domain.EasyuiPageOutput;
import com.dili.ss.exception.BusinessException;
import com.dili.ss.metadata.ValueProviderUtils;
import com.dili.ss.util.POJOUtils;
import com.dili.uap.sdk.domain.UserTicket;
import com.dili.uap.sdk.session.SessionContext;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.udojava.evalex.AbstractLazyFunction;
import com.udojava.evalex.Expression;
import com.udojava.evalex.Expression.LazyNumber;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import one.util.streamex.StreamEx;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * <B></B> <B>Copyright:本软件源代码版权归农丰时代科技有限公司及其研发团队所有,未经许可不得任意复制与传播.</B>
 * <B>农丰时代科技有限公司</B>
 *
 * @author yuehongbo
 * @date 2020/5/16 18:01
 */
@Service
public class ChargeRuleServiceImpl extends BaseServiceImpl<ChargeRule, Long> implements ChargeRuleService {

    private static final Logger logger = LoggerFactory.getLogger(ChargeRuleServiceImpl.class);

    public ChargeRuleMapper getActualMapper() {
        return (ChargeRuleMapper) getDao();
    }

    @Autowired
    private ChargeConditionValService chargeConditionValService;
    @Autowired
    private ConditionDefinitionService conditionDefinitionService;
    @Autowired
    private ChargeRuleExpiresScheduler chargeRuleExpiresScheduler;
    @Autowired
    private RuleEngineService ruleEngineService;

    @Override
    public EasyuiPageOutput listForEasyuiPage(ChargeRule chargeRule) throws Exception {
        if (chargeRule.getRows() != null && chargeRule.getRows() >= 1) {
            PageHelper.startPage(chargeRule.getPage(), chargeRule.getRows());
        }
        StringBuilder sortSql = new StringBuilder();
        if (StringUtils.isNotBlank(chargeRule.getSort())) {
            if (chargeRule.getSort().equalsIgnoreCase("groupId")) {
                sortSql.append(chargeRule.getSort()).append(" ").append(chargeRule.getOrder()).append(",priority desc");
            } else {
                sortSql.append(chargeRule.getSort()).append(" ").append(chargeRule.getOrder()).append(",group_id desc").append(",priority desc");
            }
        } else {
            sortSql.append(",group_id desc").append(",priority desc");
        }
        chargeRule.setSortSql(sortSql.toString());
        chargeRule.setIsBackup(YesOrNoEnum.NO.getCode());
        chargeRule.setIsDeleted(YesOrNoEnum.NO.getCode());

        List<ChargeRuleVo> chargeRuleVoList = getActualMapper().listForPage(chargeRule);
        long total = chargeRuleVoList instanceof Page ? ((Page) chargeRuleVoList).getTotal()
                : (long) chargeRuleVoList.size();
        List results = true ? ValueProviderUtils.buildDataByProvider(chargeRule, chargeRuleVoList) : chargeRuleVoList;
        return new EasyuiPageOutput((int) total, results);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public BaseOutput<ChargeRule> save(ChargeRuleVo chargeRuleVo, OperatorUser operatorUser) {
        ChargeRule inputRuleInfo = new ChargeRule();
        BeanUtils.copyProperties(chargeRuleVo, inputRuleInfo);
        inputRuleInfo.setState(RuleStateEnum.UN_STARTED.getCode());
        inputRuleInfo.setRevisable(YesOrNoEnum.YES.getCode());
        inputRuleInfo.setOperatorId(operatorUser.getUserId());
        inputRuleInfo.setOperatorName(operatorUser.getUserName());
        inputRuleInfo.setIsDeleted(YesOrNoEnum.NO.getCode());
        if (inputRuleInfo.getIsBackup() == null) {
            inputRuleInfo.setIsBackup(YesOrNoEnum.NO.getCode());
        }

        ChargeRule temp = this.saveOrUpdateRuleInfo(inputRuleInfo, operatorUser);
        List<ChargeConditionVal> ruleConditionValList = this.parseRuleConditionVal(temp, chargeRuleVo);
        // 如果是更新,则先删除原来的设置(如果是插入,下面的delByRuleId将导致死锁)
        if (CollectionUtil.isNotEmpty(ruleConditionValList)) {
            if (Objects.nonNull(chargeRuleVo.getId()) && chargeRuleVo.getId().equals(temp.getId())) {
                // 更新规则(先批量删除原有的,再增加新提交的)
                chargeConditionValService.deleteByRuleId(chargeRuleVo.getId());
            }
            chargeConditionValService.batchInsert(ruleConditionValList);
        } else {
            if (Objects.nonNull(chargeRuleVo.getId()) && chargeRuleVo.getId().equals(temp.getId())) {
                // 更新规则(先批量删除原有的,再增加新提交的)
                chargeConditionValService.deleteByRuleId(chargeRuleVo.getId());
            }
        }

        if (YesOrNoEnum.YES.getCode().equals(temp.getIsBackup())) {

            ChargeRule queryMain = new ChargeRule();
            queryMain.setBackupedRuleId(temp.getId());
            this.checkAndUpdateRuleStatus(StreamEx.of(super.listByExample(queryMain)).findFirst().orElse(null), temp);
        } else {
            this.checkAndUpdateRuleStatus(temp, null);
        }

        return BaseOutput.success().setData(temp);
    }

    private void checkAndUpdateRuleStatus(ChargeRule rule, ChargeRule backupRule) {
        if (backupRule != null) {
            this.chargeRuleExpiresScheduler.checkRuleStateEnum(backupRule.getId()).map(updatableItem -> {
//                updatableItem.setIsBackup(YesOrNoEnum.NO.getCode());
                if (RuleStateEnum.ENABLED.getCode().equals(updatableItem.getState()) || RuleStateEnum.EXPIRED.getCode().equals(updatableItem.getState())) {
                    if (rule != null) {
                        rule.setIsDeleted(YesOrNoEnum.YES.getCode());
                        updatableItem.setPriority(rule.getPriority());
                        this.update(rule);
                    }
                    updatableItem.setIsBackup(YesOrNoEnum.NO.getCode());
                }

                int v = this.updateSelective(updatableItem);
                return backupRule.getId();
            }).orElseGet(() -> {
                return this.chargeRuleExpiresScheduler.queryAndScheduleUpdateRuleStatusById(backupRule.getId())
                        .map(ChargeRule::getId).orElse(null);
            });

        } else {
            this.chargeRuleExpiresScheduler.checkRuleStateEnum(rule.getId()).map(updatableItem -> {
//                updatableItem.setModifyTime(this.get(updatableItem.getId()).getModifyTime());
                int v = this.updateSelective(updatableItem);
                return rule.getId();
            }).orElseGet(() -> {
                return this.chargeRuleExpiresScheduler.queryAndScheduleUpdateRuleStatusById(rule.getId())
                        .map(ChargeRule::getId).orElse(null);
            });

        }

    }

    @Override
    public void updateStateByExpires(Long id, OperatorUser operatorUser) {
        ChargeRule temp = this.get(id);

        if (YesOrNoEnum.YES.getCode().equals(temp.getIsBackup())) {

            ChargeRule queryMain = new ChargeRule();
            queryMain.setBackupedRuleId(temp.getId());
            this.checkAndUpdateRuleStatus(StreamEx.of(super.listByExample(queryMain)).findFirst().orElse(null), temp);
        } else {
            this.checkAndUpdateRuleStatus(temp, null);
        }
    }

    @Override
    public QueryFeeOutput findRuleInfoAnaCalculateFee(QueryFeeInput queryFeeInput) {
        ChargeRule queryCondition = new ChargeRule();
        queryCondition.setMarketId(queryFeeInput.getMarketId());
        queryCondition.setBusinessType(queryFeeInput.getBusinessType());
        queryCondition.setChargeItem(queryFeeInput.getChargeItem());
        queryCondition.setSort("group_id,priority,modifyTime,createTime");
        queryCondition.setOrder("DESC,DESC,DESC,DESC");
        queryCondition.setState(RuleStateEnum.ENABLED.getCode());
        queryCondition.setIsDeleted(YesOrNoEnum.NO.getCode());
        List<ChargeRule> chargeRuleList = this.listByExample(queryCondition);
        // 返回对象
        QueryFeeOutput result = new QueryFeeOutput();
        BeanUtils.copyProperties(queryFeeInput, result);
        if (CollectionUtil.isEmpty(chargeRuleList)) {
            result.setCode(ResultCode.NOT_FOUND);
            result.setMessage("未找到可用的规则");
        } else {
            for (ChargeRule ruleInfo : chargeRuleList) {
                boolean checkRuleResult = this.ruleEngineService.checkChargeRule(ruleInfo,
                        queryFeeInput.getConditionParams());
                if (checkRuleResult) {
                    result.setRuleId(ruleInfo.getId());
                    result.setRuleName(ruleInfo.getRuleName());
                    logger.info("条件匹配的规则: {}", ruleInfo);
                    try {
                        BigDecimal fee = this.calcFeeByRule(ruleInfo, queryFeeInput.getCalcParams());
                        if (fee.equals(new BigDecimal(Integer.MIN_VALUE))) {
                            result.setMessage("根据规则及参数计算费用时异常");
                            result.setCode(ResultCode.APP_ERROR);
                        } else {
                            logger.info("条件匹配的规则: {},计算的费用为: {}", ruleInfo, fee);
                            result.setCode(ResultCode.OK);
                            result.setSuccess(true);
                            result.setTotalFee(fee);
                        }
                        return result;
                    } catch (BusinessException e) {
                        logger.error(e.getMessage(), e);
                        result.setMessage(e.getMessage());
                        result.setCode(ResultCode.APP_ERROR);
                        return result;
                    } catch (Throwable t) {
                        logger.error(t.getMessage(), t);
                        result.setMessage("计算费用金额出错: " + t.getMessage());
                        result.setCode(ResultCode.APP_ERROR);
                        return result;
                    }
                }
            }
            result.setCode(ResultCode.DATA_ERROR);
            result.setMessage("未匹配到任何规则");
        }
        return result;
    }

//    @Override
//    @Transactional(rollbackFor = Exception.class)
//    public BaseOutput<Object> approve(Long id, Boolean pass) {
//        ChargeRule rule = this.get(id);
//        if (null == rule) {
//            return BaseOutput.failure("规则不存在");
//        }
//        if (!RuleStateEnum.UNAUDITED.getCode().equals(rule.getState())
//                && !RuleStateEnum.NOT_PASS.getCode().equals(rule.getState())) {
//            return BaseOutput.failure("状态已变更，不能进行此操作");
//        }
//        UserTicket userTicket = SessionContext.getSessionContext().getUserTicket();
//        rule.setOperatorId(userTicket.getId());
//        rule.setOperatorName(userTicket.getRealName());
//        rule.setApproverId(userTicket.getId());
//        rule.setApproverName(userTicket.getRealName());
//        rule.setApprovalTime(LocalDateTime.now());
//        if (pass) {
//            rule.setState(this.calculateRuleState(rule, RuleStateEnum.ENABLED).getCode());
//        } else {
//            rule.setState(RuleStateEnum.NOT_PASS.getCode());
//        }
//        this.updateRuleInfoWithExpire(rule, OperatorUser.fromSessionContext());
//        return BaseOutput.success();
//    }
    @Override
    @Transactional(rollbackFor = Exception.class)
    public BaseOutput<Object> enable(Long id, Boolean enable) {
        ChargeRule rule = this.get(id);
        if (null == rule) {
            return BaseOutput.failure("规则不存在");
        }
        UserTicket userTicket = SessionContext.getSessionContext().getUserTicket();
        rule.setOperatorId(userTicket.getId());
        rule.setOperatorName(userTicket.getRealName());
        if (enable) {
            List<Integer> allowedStatus = Arrays.asList(RuleStateEnum.DISABLED.getCode());
            if (!allowedStatus.contains(rule.getState())) {
                return BaseOutput.failure("状态已变更，不能进行此操作");
            }

            rule.setState(RuleStateEnum.ENABLED.getCode());
        } else {
            List<Integer> allowedStatus = Arrays.asList(RuleStateEnum.ENABLED.getCode(),
                    RuleStateEnum.UN_STARTED.getCode());
            if (!allowedStatus.contains(rule.getState())) {
                return BaseOutput.failure("状态已变更，不能进行此操作");
            }
            rule.setState(RuleStateEnum.DISABLED.getCode());
        }
        this.updateRuleInfoWithExpire(rule, OperatorUser.fromSessionContext());
        this.chargeRuleExpiresScheduler.checkRuleStateEnum(rule.getId()).map(updatableItem -> {

//                updatableItem.setModifyTime(this.get(updatableItem.getId()).getModifyTime());
            int v = this.updateSelective(updatableItem);

            return rule.getId();
        }).orElseGet(() -> {
            return this.chargeRuleExpiresScheduler.queryAndScheduleUpdateRuleStatusById(rule.getId())
                    .map(ChargeRule::getId).orElse(null);
        });

        return BaseOutput.success();
    }

//    @Override
//    @Transactional(rollbackFor = Exception.class)
//    public Integer obsolete(Long id, OperatorUser operatorUser) {
//        ChargeRule item = this.get(id);
//        if (item == null) {
//            return 0;
//        }
//        ChargeRule rule = new ChargeRule();
//        rule.setId(id);
//        rule.setModifyTime(item.getModifyTime());
//        rule.setState(RuleStateEnum.OBSOLETE.getCode());
//        rule.setOperatorId(operatorUser.getUserId());
//        rule.setOperatorName(operatorUser.getUserName());
//        rule.setRevisable(YesOrNoEnum.YES.getCode());
//        return this.updateSelective(rule);
//    }
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer updateRuleInfoWithExpire(ChargeRule ruleInfo, OperatorUser operatorUser) {
//        if (ruleInfo.getState().equals(RuleStateEnum.ENABLED.getCode()) && null != ruleInfo.getOriginalId()) {
        // 作废原规则
//            obsolete(ruleInfo.getOriginalId(), operatorUser);
        ruleInfo.setOriginalId(null);
//        }
        super.updateSelective(ruleInfo);
        chargeRuleExpiresScheduler.updateRuleStatus(ruleInfo);
        return 1;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public BaseOutput<Boolean> enlargePriority(long id) {
        ChargeRule chargeRule = get(id);
        ChargeRule query = new ChargeRule();
        query.setMarketId(chargeRule.getMarketId());
        query.setBusinessType(chargeRule.getBusinessType());
        query.setChargeItem(chargeRule.getChargeItem());
//        query.setPriority(chargeRule.getPriority() + 1);
        query.setIsBackup(YesOrNoEnum.NO.getCode());
        query.setIsDeleted(YesOrNoEnum.NO.getCode());
        query.setGroupId(chargeRule.getGroupId());
        query.setOrder("asc");
        query.setSort("priority");
        ChargeRule old = StreamEx.of(super.listByExample(query)).unordered().filter(item -> item.getPriority().compareTo(chargeRule.getPriority()) > 0).findFirst().orElse(null);
        if (old == null) {
            return BaseOutput.failure("当前优先级已经最高！");
        } else {
            Integer oldPriority = old.getPriority();
            old.setPriority(chargeRule.getPriority());
            update(old);
            chargeRule.setPriority(oldPriority);
            this.update(chargeRule);
            return BaseOutput.successData(true);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public BaseOutput<Boolean> reducePriority(long id) {
        ChargeRule chargeRule = get(id);

        ChargeRule query = new ChargeRule();
        query.setMarketId(chargeRule.getMarketId());
        query.setBusinessType(chargeRule.getBusinessType());
        query.setChargeItem(chargeRule.getChargeItem());
//        query.setPriority(chargeRule.getPriority() - 1);
        query.setIsBackup(YesOrNoEnum.NO.getCode());
        query.setIsDeleted(YesOrNoEnum.NO.getCode());
        query.setGroupId(chargeRule.getGroupId());
        query.setOrder("desc");
        query.setSort("priority");
        ChargeRule old = StreamEx.of(super.listByExample(query)).unordered().filter(item -> item.getPriority().compareTo(chargeRule.getPriority()) < 0).findFirst().orElse(null);
        if (old == null) {
            return BaseOutput.failure("当前优先级已经最低！");
        } else {
            Integer oldPriority = old.getPriority();
            old.setPriority(chargeRule.getPriority());
            update(old);
            chargeRule.setPriority(oldPriority);
            this.update(chargeRule);
            return BaseOutput.successData(true);
        }
    }

    /**
     * 根据id是否为空,分别进行插入或者其他操作
     *
     * @param inputRuleInfo
     * @return
     */
    private ChargeRule saveOrUpdateRuleInfo(ChargeRule input, OperatorUser operatorUser) {
        if (null == input.getId()) {
            if (this.isExistsSameRuleName(input)) {
                throw new IllegalArgumentException("已存在规则名称相同的记录");
            }
            // 插入新的记录
            getActualMapper().insertBy(input);
            return input;
        } else {
            ChargeRule item = this.get(input.getId());
//            if (YesOrNoEnum.NO.getCode().equals(item.getRevisable())) {
//                throw new IllegalArgumentException("此规则已存在被修改的记录，暂时不能修改");
//            }
            // 修改原记录的状态并新增一条 或者更新
//            return this.createRuleForRevisable(inputRuleInfo, item, operatorUser);

            if (item.getIsBackup().equals(YesOrNoEnum.NO.getCode())) {
                if (YesOrNoEnum.YES.getCode().equals(input.getIsBackup())) {

                    input.setId(null);
                    input.setCreateTime(LocalDateTime.now());
                    input.setModifyTime(LocalDateTime.now());
                    this.insert(input);
                    item.setBackupedRuleId(input.getId());
                    this.update(item);
                } else {
                    //                inputRuleInfo.setModifyTime(old.getModifyTime());
                    input.setIsBackup(item.getIsBackup());
                    input.setBackupedRuleId(item.getBackupedRuleId());
                    input.setIsDeleted(item.getIsDeleted());
                    input.setPriority(item.getPriority());

                    this.update(input);
                }

            } else if (YesOrNoEnum.YES.getCode().equals(item.getIsBackup())) {
//            inputRuleInfo.setModifyTime(old.getModifyTime());
                input.setIsBackup(item.getIsBackup());
                input.setBackupedRuleId(item.getBackupedRuleId());
                input.setIsDeleted(item.getIsDeleted());
                input.setPriority(item.getPriority());
                this.update(input);
            }
            return input;
        }
    }

    /**
     * 根据Revisable的值,创建新的规则并更新原有数据的状态
     *
     * @param inputRuleInfo
     * @param old
     * @return
     */
//    private ChargeRule createRuleForRevisable(ChargeRule input, ChargeRule item, OperatorUser operatorUser) {
//
//        if (item.getIsBackup().equals(YesOrNoEnum.NO.getCode())) {
//            if (YesOrNoEnum.YES.getCode().equals(input.getIsBackup())) {
//
//                input.setId(null);
//                input.setCreateTime(LocalDateTime.now());
//                input.setModifyTime(LocalDateTime.now());
//                this.insert(input);
//                item.setBackupedRuleId(input.getId());
//                this.update(item);
//            } else {
//                //                inputRuleInfo.setModifyTime(old.getModifyTime());
//                input.setIsBackup(item.getIsBackup());
//                this.update(input);
//            }
//
//        } else if (YesOrNoEnum.YES.getCode().equals(item.getIsBackup())) {
////            inputRuleInfo.setModifyTime(old.getModifyTime());
//            input.setIsBackup(item.getIsBackup());
//            this.update(input);
//        }
//        return input;
//    }
    /**
     * 组装规则条件信息
     *
     * @param rule
     * @param vo
     * @return
     */
    private List<ChargeConditionVal> parseRuleConditionVal(ChargeRule rule, ChargeRuleVo vo) {
        if (CollectionUtil.isEmpty(vo.getConditionList())) {
            return Collections.emptyList();
        }
        List<ConditionVo> conditionList = vo.getConditionList();
        // 需要保存的规则条件信息
        List<ChargeConditionVal> ruleConditionVals = conditionList.stream().map((c) -> {
            // 获得对应的ConditionDefinition并进行数据格式校验
            Long definitionId = c.getDefinitionId();
            ConditionDefinition definition = conditionDefinitionService.get(definitionId);
            if (definition == null) {
                throw new IllegalArgumentException("条件指标参数不正确");
            }
            // 转换为RuleConditionVal对象

            String val = "[]";
            
            List<String>valueList=StreamEx.of(CollectionUtil.emptyIfNull(c.getMatchValues())).nonNull().filter(StringUtils::isNotBlank).toList();
            if (valueList.isEmpty()) {
                 val = JSONObject.toJSONString(valueList);
            }else{
                 val = JSONObject.toJSONString(c.getMatchValues());
            }
            ChargeConditionVal conditionValItem = new ChargeConditionVal();
            conditionValItem.setRuleId(rule.getId());
            conditionValItem.setLabel(definition.getLabel());
            conditionValItem.setMatchKey(definition.getMatchKey());
            conditionValItem.setMatchType(definition.getMatchType());
            conditionValItem.setDataType(definition.getDataType());
            conditionValItem.setVal(val);
            conditionValItem.setDefinitionId(definition.getId());
            conditionValItem.setCreateTime(LocalDateTime.now());
            conditionValItem.setModifyTime(conditionValItem.getCreateTime());
            return conditionValItem;
        }).collect(Collectors.toList());
        return ruleConditionVals;
    }

    /**
     * 检查是否有相同名字的规则(同市场,同系统,同业务,同收费模块)
     *
     * @param chargeRule
     * @return
     */
    private boolean isExistsSameRuleName(ChargeRule chargeRule) {
        ChargeRule condition = new ChargeRule();
        condition.setRuleName(chargeRule.getRuleName());
        condition.setMarketId(chargeRule.getMarketId());
        condition.setBusinessType(chargeRule.getBusinessType());
        condition.setChargeItem(chargeRule.getChargeItem());
        long sameNameCount = this.listByExample(condition).stream().filter((r) -> !r.getId().equals(chargeRule.getId()))
                .count();
        return sameNameCount > 0;
    }

    /**
     * 根据规则信息，计算费用
     *
     * @param ruleInfo
     * @param calcParams
     * @return 计算后的结果值
     */
    private BigDecimal calcFeeByRule(ChargeRule ruleInfo, Map<String, Object> calcParams) {
        String actionExpression = conditionDefinitionService.convertTargetValDefinition(ruleInfo.getActionExpression(),
                false);
        Expression expression = new Expression(actionExpression);
        if (ActionExpressionTypeEnum.COMPLEX.equalsToCode(ruleInfo.getActionExpressionType())) {
            Map<String, Object> actionExpressionParams = (Map<String, Object>) JSON
                    .parse(ruleInfo.getActionExpressionParams());
            String matchKey = (String) actionExpressionParams.get("_start");
            String _first_tiered_period = (String) actionExpressionParams.get("_first_tiered_period");
            String _first_tiered_fee = (String) actionExpressionParams.get("_first_tiered_fee");
            String _second_tiered_period = (String) actionExpressionParams.get("_second_tiered_period");
            String _second_tiered_fee = (String) actionExpressionParams.get("_second_tiered_fee");
            String startValue = String.valueOf(calcParams.get(matchKey));

            expression.setVariable("_first_tiered_period", _first_tiered_period);
            expression.setVariable("_first_tiered_fee", _first_tiered_fee);
            expression.setVariable("_second_tiered_period", _second_tiered_period);
            expression.setVariable("_second_tiered_fee", _second_tiered_fee);

            //增加自定义分钟函数
            expression.addLazyFunction(new AbstractLazyFunction("diffMinute", 2) {
                @Override
                public LazyNumber lazyEval(List<LazyNumber> lazyParams) {

                    LocalDateTime firstArg
                            = LocalDateTime.ofEpochSecond(lazyParams.get(0).eval().longValue(), 0, OffsetDateTime.now().getOffset());
                    LocalDateTime secondArg = LocalDateTime.ofEpochSecond(lazyParams.get(1).eval().longValue(), 0, OffsetDateTime.now().getOffset());
                    long minutes = Duration.between(firstArg, secondArg).toMinutes();
                    // System.out.println(firstArg.format( DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                    // System.out.println(secondArg.format( DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

                    // System.out.println(minutes);
                    return new LazyNumber() {
                        public BigDecimal eval() {
                            return new BigDecimal(minutes);
                        }

                        public String getString() {
                            return String.valueOf(minutes);
                        }
                    };
                }

            });
            //增加内部开始时间变量
            expression.with("_start", new LazyNumber() {
                LocalDateTime obj = LocalDateTime.parse(startValue,
                        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

                public BigDecimal eval() {
                    return new BigDecimal(obj.atZone(ZoneOffset.systemDefault()).toInstant().toEpochMilli()).divide(new BigDecimal(1000));
                }

                public String getString() {
                    return startValue;
                }
            });
            //增加内部now()函数
            expression.addLazyFunction(new AbstractLazyFunction("now", 0) {
                LocalDateTime now = LocalDateTime.now();

                @Override
                public LazyNumber lazyEval(List<LazyNumber> lazyParams) {
                    return new LazyNumber() {
                        public BigDecimal eval() {
                            return new BigDecimal(now.atZone(ZoneOffset.systemDefault()).toInstant().toEpochMilli()).divide(new BigDecimal(1000));
                        }

                        public String getString() {
                            return String.valueOf(now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                        }
                    };
                }

            });
        } else {
            for (String var : expression.getUsedVariables()) {
                if (calcParams.containsKey(var)) {
                    expression.setVariable(var, String.valueOf(calcParams.get(var)));
                }
            }
        }
        try {

            BigDecimal fee = new BigDecimal(expression.eval().toPlainString());
            BigDecimal min = ruleInfo.getMinPayment();
            BigDecimal max = ruleInfo.getMaxPayment();
            if (min != null && fee.compareTo(min) < 0) {
                return min;
            }
            if (max != null && max.compareTo(fee) < 0) {
                return max;
            }
            return fee;
        } catch (Exception e) {
            logger.error(String.format("根据规则[%s]及参数[%s]生成的表达式[%s]计算金额异常[%s]", ruleInfo, calcParams,
                    expression.toString(), e.getMessage()), e);
            throw new BusinessException("1", "根据规则及参数计算费用异常");
        }
    }

    /**
     * 更新groupid
     *
     * @param ruleId
     * @param groupId
     */
    @Override
    public BaseOutput updateGroupId(Long ruleId, Long groupId) {
        if (ruleId == null || groupId == null) {
            return BaseOutput.failure("参数错误");
        }
        ChargeRule item = super.get(ruleId);
        if (item == null) {
            return BaseOutput.failure("数据不存在");
        }

        item.setGroupId(groupId);
        this.update(item);
        return BaseOutput.success();

    }

    /**
     * 基于当前的状态和时间范围 及期望的状态值,来计算最终可用状态
     *
     * @param ruleInfo 规则信息
     * @param expectedState 预期的下一个状态
     * @return 最终的状态
     */
//    private RuleStateEnum calculateRuleState(ChargeRule ruleInfo, RuleStateEnum expectedState) {
//        LocalDateTime start = ruleInfo.getExpireStart();
//        LocalDateTime end = ruleInfo.getExpireEnd();
//        LocalDateTime now = LocalDateTime.now();
//        // 规则目前的状态
//        Integer originalState = ruleInfo.getState();
//
//        if (RuleStateEnum.ENABLED == expectedState) {
//            // 当前值为待审核时
//            if (RuleStateEnum.UNAUDITED.getCode().equals(originalState)) {
//                if (end.isBefore(now)) {
//                    return RuleStateEnum.EXPIRED;
//                } else if (start.isAfter(now)) {
//                    return RuleStateEnum.UN_STARTED;
//                } else {
//                    return RuleStateEnum.ENABLED;
//                }
//            } else if (RuleStateEnum.DISABLED.getCode().equals(originalState)) {
//                if (end.isBefore(now)) {
//                    return RuleStateEnum.EXPIRED;
//                } else if (start.isAfter(now)) {
//                    return RuleStateEnum.UN_STARTED;
//                } else {
//                    return RuleStateEnum.ENABLED;
//                }
//            }
//        }
//        return expectedState;
//    }
}
