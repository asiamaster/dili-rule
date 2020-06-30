package com.dili.rule.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.JSONObject;
import com.dili.commons.glossary.YesOrNoEnum;
import com.dili.rule.domain.ChargeConditionVal;
import com.dili.rule.domain.ChargeRule;
import com.dili.rule.domain.ConditionDefinition;
import com.dili.rule.domain.dto.CalculateFeeDto;
import com.dili.rule.domain.dto.OperatorUser;
import com.dili.rule.domain.enums.RuleStateEnum;
import com.dili.rule.domain.vo.ChargeRuleVo;
import com.dili.rule.domain.vo.ConditionVo;
import com.dili.rule.mapper.ChargeRuleMapper;
import com.dili.rule.scheduler.ChargeRuleExpiresScheduler;
import com.dili.rule.sdk.domain.input.QueryFeeInput;
import com.dili.rule.service.ChargeConditionValService;
import com.dili.rule.service.ChargeRuleService;
import com.dili.rule.service.ConditionDefinitionService;
import com.dili.rule.service.RuleEngineService;
import com.dili.ss.base.BaseServiceImpl;
import com.dili.ss.domain.BaseOutput;
import com.dili.ss.domain.EasyuiPageOutput;
import com.dili.ss.exception.BusinessException;
import com.dili.ss.metadata.ValueProviderUtils;
import com.dili.ss.util.POJOUtils;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.udojava.evalex.Expression;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

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

    private static final Logger logger = LoggerFactory.getLogger(ChargeRuleServiceImpl.class);

    public ChargeRuleMapper getActualMapper() {
        return (ChargeRuleMapper)getDao();
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
        if (StringUtils.isNotBlank(chargeRule.getSort())) {
            chargeRule.setSort(POJOUtils.humpToLineFast(chargeRule.getSort()));
        }
        List<ChargeRuleVo> chargeRuleVoList = getActualMapper().listForPage(chargeRule);
        long total = chargeRuleVoList instanceof Page ? ((Page) chargeRuleVoList).getTotal() : (long) chargeRuleVoList.size();
        List results = true ? ValueProviderUtils.buildDataByProvider(chargeRule, chargeRuleVoList) : chargeRuleVoList;
        return new EasyuiPageOutput((int) total, results);
    }

    @Transactional
    @Override
    public BaseOutput<ChargeRule> save(ChargeRuleVo chargeRuleVo,OperatorUser operatorUser) {
        ChargeRule inputRuleInfo = new ChargeRule();
        BeanUtils.copyProperties(chargeRuleVo, inputRuleInfo);

        inputRuleInfo.setState(RuleStateEnum.UNAUDITED.getCode());
        inputRuleInfo.setRevisable(YesOrNoEnum.YES.getCode());
        inputRuleInfo.setOperatorId(operatorUser.getUserId());
        inputRuleInfo.setOperatorName(operatorUser.getUserName());

        // 检查输入的计算指标及格式
        //TODO  计算指标相关
//        if (!TargetTypeEnum.CONSTANT.getCode().equals(inputRuleInfo.getTargetType())) {
//            // 没有找到计算指标
//            if (StringUtils.isBlank(inputRuleInfo.getTarget())) {
//                throw new IllegalArgumentException("没有找到计算指标");
//            }
//            String target = StringUtils.trim(inputRuleInfo.getTarget());
//            List<Integer> codes = JSONArray.parseArray(target, Integer.class);
//            List<TargetTypeValueEnum> targetTypeValueEnums = TargetTypeValueEnum.findAllEnumsByCodes(codes);
//            // 没有找到计算指标
//            if (targetTypeValueEnums.isEmpty()) {
//                throw new IllegalArgumentException("没有找到计算指标");
//            }
//        } else {
//            // 设置空的默认值
//            inputRuleInfo.setTarget("[]");
//        }
        ChargeRule temp = this.saveOrUpdateRuleInfo(inputRuleInfo,operatorUser);

        List<ChargeConditionVal> ruleConditionValList = this.parseRuleConditionVal(temp, chargeRuleVo);
        // 如果是更新,则先删除原来的设置(如果是插入,下面的delByRuleId将导致死锁)
        if (!CollectionUtils.isEmpty(ruleConditionValList)) {

            /*
             * if(vo.getId()==null) { //插入新的规则
             * ruleConditionValService.batchInsert(ruleConditionVals); }else
             * if(vo.getId().equals(upsertedRule.getId())){ //更新规则(先批量删除原有的,再增加新提交的)
             * ruleConditionValService.delByRuleId(vo.getId());
             * ruleConditionValService.batchInsert(ruleConditionVals); }else {
             * //原有的被改变状态,新的被创建 ruleConditionValService.batchInsert(ruleConditionVals); }
             */

            // 上面的代码被改为如下:
            if (Objects.nonNull(chargeRuleVo.getId()) && chargeRuleVo.getId().equals(temp.getId())) {
                // 更新规则(先批量删除原有的,再增加新提交的)
                chargeConditionValService.deleteByRuleId(chargeRuleVo.getId());
            }
            chargeConditionValService.batchInsert(ruleConditionValList);

        } else {
            // 上面的代码被改为如下:
            if (Objects.nonNull(chargeRuleVo.getId()) && chargeRuleVo.getId().equals(temp.getId())) {
                // 更新规则(先批量删除原有的,再增加新提交的)
                chargeConditionValService.deleteByRuleId(chargeRuleVo.getId());
            }
        }
        return BaseOutput.success().setData(temp);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateStateByExpires(ChargeRule rule,OperatorUser operatorUser) {
        if (null != rule && rule.getId() != null) {
            Long id = rule.getId();
            // 是否需要更改数据
            Boolean flag = false;
            /**
             * 如果规则的开始时间已早于当前时间，而状态还是未开始状态的话，那需要更新状态
             */
            if (rule.getExpireStart().isBefore(LocalDateTime.now())
                    && rule.getState().equals(RuleStateEnum.UN_STARTED.getCode())) {
                rule.setState(RuleStateEnum.ENABLED.getCode());
                flag = true;
                // 此条规则生效，需判断是否有原规则，如果有，需解除关系，并作废原规则
                if (null != rule.getOriginalId()) {
                    // 作废原规则
                    obsolete(rule.getOriginalId(),operatorUser);
                    rule.setOriginalId(null);
                }
            }
            /**
             * 如果规则的结束时间已早于当前时间，而状态是已启用、待审核的 话，那需要更新状态
             */
            if (rule.getExpireEnd().isBefore(LocalDateTime.now())
                    && (rule.getState().equals(RuleStateEnum.UNAUDITED.getCode())
                    || rule.getState().equals(RuleStateEnum.ENABLED.getCode()))) {
                rule.setState(RuleStateEnum.EXPIRED.getCode());
                flag = true;
                if (null != rule.getOriginalId()) {
                    ChargeRule old = new ChargeRule();
                    old.setId(rule.getOriginalId());
                    old.setRevisable(YesOrNoEnum.YES.getCode());
                    this.updateSelective(old);
                    rule.setOriginalId(null);
                }
            }
            if (flag) {
                Example example = new Example(ChargeRule.class);
                Example.Criteria criteria = example.createCriteria();
                criteria.andEqualTo("id", id);
                if (null != rule.getModifyTime()) {
                    criteria.andEqualTo("modifyTime", rule.getModifyTime());
                }
                rule.setModifyTime(LocalDateTime.now());
                getActualMapper().updateByExample(rule, example);
            }
        }
    }

    @Override
    public void updateStateByExpires(Long id,OperatorUser operatorUser) {
        ChargeRule rule = this.get(id);
        this.updateStateByExpires(rule,operatorUser);
    }

    @Override
    public CalculateFeeDto findRuleInfoAnaCalculateFee(QueryFeeInput queryFeeInput) {
        ChargeRule queryCondition = new ChargeRule();
        queryCondition.setMarketId(queryFeeInput.getMarketId());
        queryCondition.setBusinessType(queryFeeInput.getBusinessType());
        queryCondition.setChargeItem(queryFeeInput.getChargeItem());
        queryCondition.setSort("group_id,modified,created");
        queryCondition.setOrder("DESC,DESC,DESC");
        queryCondition.setState(RuleStateEnum.ENABLED.getCode());
        List<ChargeRule> chargeRuleList = this.listByExample(queryCondition);
        //返回对象
        CalculateFeeDto result = new CalculateFeeDto();
        if (CollectionUtil.isEmpty(chargeRuleList)) {
            result.setMessage(Optional.of("未找到可用的规则"));
        } else {
            for (ChargeRule ruleInfo : chargeRuleList) {
                boolean checkRuleResult = this.ruleEngineService.checkChargeRule(ruleInfo, queryFeeInput.getConditionParams());
                if (checkRuleResult) {
                    result.setRuleInfo(ruleInfo);
                    logger.info("条件匹配的规则: {}", ruleInfo);
                    try {
                        BigDecimal fee = this.calcFeeByRule(ruleInfo, queryFeeInput.getCalcParams());
                        if (fee.equals(new BigDecimal(Integer.MIN_VALUE))) {
                            result.setMessage(Optional.of("根据规则及参数计算费用时异常"));
                        } else {
                            logger.info("条件匹配的规则: {},计算的费用为: {}", ruleInfo, fee);
                            result.setFee(fee);
                        }
                        return result;
                    }catch (BusinessException e){
                        logger.error(e.getMessage(), e);
                        result.setMessage(Optional.of(e.getErrorMsg()));
                        return result;
                    } catch (Throwable t) {
                        logger.error(t.getMessage(), t);
                        result.setMessage(Optional.of("计算费用金额出错: " + t.getMessage()));
                        return result;
                    }

                }
            }
        }
        return result;
    }


    /**
     * 根据id是否为空,分别进行插入或者其他操作
     *
     * @param inputRuleInfo
     * @return
     */
    private ChargeRule saveOrUpdateRuleInfo(ChargeRule inputRuleInfo,OperatorUser operatorUser) {
        if (null == inputRuleInfo.getId()) {
            if (this.isExistsSameRuleName(inputRuleInfo)) {
                throw new IllegalArgumentException("已存在规则名称相同的记录");
            }
            // 插入新的记录
            this.insertSelective(inputRuleInfo);
            return inputRuleInfo;
        } else {
            ChargeRule old = this.get(inputRuleInfo.getId());
            if (YesOrNoEnum.NO.getCode().equals(old.getRevisable())) {
            	throw new IllegalArgumentException("此规则已存在被修改的记录，暂时不能修改");
            }
            // 修改原记录的状态并新增一条 或者更新
           return this.createRuleForRevisable(inputRuleInfo, old,operatorUser);
        }
    }

    /**
     * 根据Revisable的值,创建新的规则并更新原有数据的状态
     *
     * @param inputRuleInfo
     * @param old
     * @return
     */
    private ChargeRule createRuleForRevisable(ChargeRule inputRuleInfo, ChargeRule old,OperatorUser operatorUser) {
        if (RuleStateEnum.ENABLED.getCode().equals(old.getState())) {
            inputRuleInfo.setOriginalId(old.getId());
            inputRuleInfo.setId(null);

            if (this.isExistsSameRuleName(inputRuleInfo)) {
            	throw new IllegalArgumentException("已存在规则名称相同的记录");
            }
            // 插入一条新的RuleInfo
            this.insertSelective(inputRuleInfo);
            old.setRevisable(YesOrNoEnum.NO.getCode());
            // 更改原来数据的状态
            this.updateRuleInfoWithExpire(old,operatorUser);
        } else {
            if (this.isExistsSameRuleName(inputRuleInfo)) {
            	throw new IllegalArgumentException("已存在规则名称相同的记录");
            }
            this.updateSelective(inputRuleInfo);
        }
        return inputRuleInfo;
    }


    /**
     * 根据规则有效期，更改规则信息
     * @param ruleInfo
     * @return
     */
    private int updateRuleInfoWithExpire(ChargeRule ruleInfo,OperatorUser operatorUser) {
        if (ruleInfo.getState().equals(RuleStateEnum.ENABLED.getCode()) && null != ruleInfo.getOriginalId()) {
            // 作废原规则
            obsolete(ruleInfo.getOriginalId(),operatorUser);
            ruleInfo.setOriginalId(null);
        }
        super.updateSelective(ruleInfo);
        chargeRuleExpiresScheduler.updateRuleStatus(ruleInfo);
        return 1;
    }

    /**
     * 作废某条规则信息
     *
     * @param id 需要作废的规则ID
     * @return
     */
    private Integer obsolete(Long id,OperatorUser operatorUser) {
        ChargeRule rule = new ChargeRule();
        rule.setId(id);
        rule.setState(RuleStateEnum.OBSOLETE.getCode());
        rule.setOperatorId(operatorUser.getUserId());
        rule.setOperatorName(operatorUser.getUserName());
        rule.setRevisable(YesOrNoEnum.YES.getCode());
        return this.updateSelective(rule);
    }

    /**
     * 组装规则条件信息
     * @param rule
     * @param vo
     * @return
     */
    private List<ChargeConditionVal> parseRuleConditionVal(ChargeRule rule, ChargeRuleVo vo) {
    	List<ConditionVo> conditions = vo.getConditions();
        // 需要保存的规则条件信息
        List<ChargeConditionVal> ruleConditionVals = conditions.stream().map((c) -> {
            // 获得对应的ConditionDefinition并进行数据格式校验
            Long definitionId = c.getDefinitionId();
            ConditionDefinition definition = conditionDefinitionService.get(definitionId);
            if (definition == null) {
                throw new IllegalArgumentException("条件指标参数不正确");
            }
            // 转换为RuleConditionVal对象
            String val = JSONObject.toJSONString(c.getMatchValues());
            ChargeConditionVal conditionValItem = new ChargeConditionVal();
            conditionValItem.setRuleId(rule.getId());
            conditionValItem.setLabel(definition.getLabel());
            conditionValItem.setMatchKey(definition.getMatchKey());
            conditionValItem.setMatchType(definition.getMatchType());
            conditionValItem.setDataType(definition.getDataType());
            conditionValItem.setVal(val);
            conditionValItem.setDefinitionId(definition.getId());
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
        long sameNameCount = this.listByExample(condition).stream().filter((r) -> !r.getId().equals(chargeRule.getId())).count();
        return sameNameCount > 0;
    }

    /**
     * 根据规则信息，计算费用
     * @param ruleInfo
     * @param calcParams
     * @return 计算后的结果值
     */
    private BigDecimal calcFeeByRule(ChargeRule ruleInfo, Map<String, Object> calcParams) {
        Expression expression = new Expression(ruleInfo.getTargetVal());
        try {
            for (String var : expression.getUsedVariables()) {
                if (calcParams.containsKey(var)){
                    expression.setVariable(var, String.valueOf(calcParams.get(var)));
                }
            }
            return expression.eval();
        } catch (Exception e) {
            logger.error(String.format("根据规则[%s]及参数[%s]生成的表达式[%s]计算金额异常[%s]",ruleInfo,calcParams,expression.toString(),e.getMessage() ),e);
            throw new BusinessException("1","根据规则及参数计算费用异常");
        }

    }
}
