package com.dili.rule.service.remote;

import cn.hutool.core.collection.CollectionUtil;
import com.dili.ss.domain.BaseOutput;
import com.dili.ss.dto.DTOUtils;
import com.dili.uap.sdk.domain.Firm;
import com.dili.uap.sdk.domain.UserDataAuth;
import com.dili.uap.sdk.domain.UserTicket;
import com.dili.uap.sdk.domain.dto.FirmDto;
import com.dili.uap.sdk.glossary.DataAuthType;
import com.dili.uap.sdk.rpc.DataAuthRpc;
import com.dili.uap.sdk.rpc.FirmRpc;
import com.dili.uap.sdk.session.SessionContext;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * <B></B>
 * <B>Copyright:本软件源代码版权归农丰时代科技有限公司及其研发团队所有,未经许可不得任意复制与传播.</B>
 * <B>农丰时代科技有限公司</B>
 *
 * @author yuehongbo
 * @date 2020/5/15 17:54
 */
@Service
public class MarketRpcService {

    @Autowired
    private DataAuthRpc dataAuthRpc;
    @Autowired
    private FirmRpc firmRpc;

    /**
     * 根据条件查询市场
     * @param firm
     * @author wangmi
     * @return
     */
    public List<Firm> listByExample(FirmDto firm){
        BaseOutput<List<Firm>> output = firmRpc.listByExample(firm);
        return output.isSuccess() ? output.getData() : null;
    }

    /**
     * 当前用户拥有访问权限的firmId
     * @return
     */
    public Set<Long> getCurrentUserFirmIds() {
        return getCurrentUserFirmIds(null);
    }

    /**
     * 当前用户拥有访问权限的firmId集
     * @param userId 用户ID
     * @return
     */
    public Set<Long> getCurrentUserFirmIds(Long userId) {
        List<Firm> list = this.getCurrentUserFirms(userId);
        Set<Long> resultSet = list.stream().distinct().map(Firm::getId).collect(Collectors.toSet());
        return resultSet;
    }

    /**
     * 当前用户拥有访问权限的firmId
     * @param firmId 市场ID
     * @return
     */
    public Set<Long> getCurrentUserAvaliableFirmIds(Long firmId) {
        Set<Long> resultSet = this.getCurrentUserFirmIds(null);
        if (Objects.isNull(firmId) || !resultSet.contains(firmId)) {
            return resultSet;
        } else {
            return Sets.newHashSet(firmId);
        }
    }

    /**
     * 通过id查询firm
     * @param firmId
     * @return
     */
    public Optional<Firm> getFirmById(Long firmId) {
        if (Objects.isNull(firmId)) {
            return Optional.empty();
        }
        BaseOutput<Firm> out = firmRpc.getById(firmId);
        if (out.isSuccess()) {
            Firm firm = out.getData();
            return Optional.ofNullable(firm);
        }
        return Optional.empty();
    }

    /**
     * 获得当前用户拥有的所有Firm
     * @return
     */
    public List<Firm> getCurrentUserFirms() {
        return getCurrentUserFirms(null);
    }

    /**
     * 获得当前用户拥有的所有Firm
     * @param userId 用户ID，如果为空，则从session中获取，如果未获取到，返回空
     * @return
     */
    public List<Firm> getCurrentUserFirms(Long userId) {
        UserDataAuth userDataAuth = DTOUtils.newDTO(UserDataAuth.class);
        if (null == userId) {
            UserTicket userTicket = SessionContext.getSessionContext().getUserTicket();
            if (null == userTicket){
                return Lists.newArrayListWithCapacity(0);
            }
            userDataAuth.setUserId(SessionContext.getSessionContext().getUserTicket().getId());
        } else {
            userDataAuth.setUserId(userId);
        }
        userDataAuth.setRefCode(DataAuthType.MARKET.getCode());
        BaseOutput<List<Map>> out = dataAuthRpc.listUserDataAuthDetail(userDataAuth);
        if (out.isSuccess() && CollectionUtil.isNotEmpty(out.getData())) {
            List<String> firmCodeList = (List<String>) out.getData().stream().flatMap(m -> m.keySet().stream()).collect(Collectors.toList());
            FirmDto firmDto = DTOUtils.newInstance(FirmDto.class);
            firmDto.setCodes(firmCodeList);
            BaseOutput<List<Firm>> listBaseOutput = firmRpc.listByExample(firmDto);
            if (listBaseOutput.isSuccess() && CollectionUtil.isNotEmpty(listBaseOutput.getData())) {
                return Lists.newArrayList(listBaseOutput.getData());
            } else {
                return Lists.newArrayListWithCapacity(0);
            }
        } else {
            return Lists.newArrayListWithCapacity(0);
        }
    }
}
