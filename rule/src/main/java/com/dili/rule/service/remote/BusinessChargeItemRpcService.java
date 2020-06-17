package com.dili.rule.service.remote;

import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dili.assets.sdk.dto.ChargeItemDto;
import com.dili.assets.sdk.rpc.BusinessChargeItemRpc;
import com.dili.ss.domain.BaseOutput;

import cn.hutool.core.collection.CollectionUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * <B></B>
 * <B>Copyright:本软件源代码版权归农丰时代科技有限公司及其研发团队所有,未经许可不得任意复制与传播.</B>
 * <B>农丰时代科技有限公司</B>
 *
 * @author yuehongbo
 * @date 2020/6/6 10:34
 */
@Slf4j
@Service
public class BusinessChargeItemRpcService {
	private static final Logger log=LoggerFactory.getLogger(BusinessChargeItemRpcService.class);
    @Autowired
    private BusinessChargeItemRpc businessChargeItemRpc;

    /**
     * 获取业务收费项数据集
     * @param marketId
     * @param businessType
     * @param isEnable
     * @return
     */
    public List<ChargeItemDto> list(Long marketId, String businessType, Boolean isEnable) {
        try {
            BaseOutput<List<ChargeItemDto>> output = businessChargeItemRpc.listItemByMarketAndBusiness(marketId, businessType, isEnable);
            return CollectionUtil.emptyIfNull(output.getData());
        } catch (Throwable t) {
            log.error(String.format("根据条件[%d::%s::%s]远程查询业务费用项失败[%s]", marketId, businessType, isEnable, t.getMessage()), t);
            return Collections.emptyList();
        }
    }
}
