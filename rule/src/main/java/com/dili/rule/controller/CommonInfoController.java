package com.dili.rule.controller;

import com.dili.assets.sdk.dto.BusinessChargeItemDto;
import com.dili.rule.service.remote.BusinessChargeItemRpcService;
import com.dili.ss.domain.BaseOutput;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Objects;

/**
 * <B>公共信息服务控制层</B>
 * <B>Copyright:本软件源代码版权归农丰时代科技有限公司及其研发团队所有,未经许可不得任意复制与传播.</B>
 * <B>农丰时代科技有限公司</B>
 *
 * @author yuehongbo
 * @date 2020/5/16 10:18
 */
@Controller
@RequestMapping(value = "/commonInfo")
@Slf4j
public class CommonInfoController {

    @Autowired
    private BusinessChargeItemRpcService businessChargeItemRpcService;

    /**
     * 根据市场、系统获取对应的业务类型
     * @param marketId 市场
     * @param systemCode 系统
     * @param fromLocal 是否从本地获取数据(即：不调用远程接口，查询本地数据库)
     * @return
     */
    @RequestMapping("/getBusinessType.action")
    @ResponseBody
    public BaseOutput<Object> getBusinessType(Long marketId, String systemCode, Boolean fromLocal){
        return BaseOutput.failure("业务类型目前采用数据字典配置,此方法为预留方法");
    }

    /**
     * 根据市场、系统、业务类型获取对应的收费项
     * @param marketId 市场
     * @param businessType 业务类型
     * @return
     */
    @RequestMapping("/getChargeItem.action")
    @ResponseBody
    public BaseOutput<List<BusinessChargeItemDto>> getChargeItem(Long marketId, String businessType) {
        if (Objects.nonNull(marketId) && Objects.nonNull(businessType)) {
            return BaseOutput.success().setData(businessChargeItemRpcService.list(marketId, businessType, true));
        }
        return BaseOutput.failure("参数丢失");
    }
}
