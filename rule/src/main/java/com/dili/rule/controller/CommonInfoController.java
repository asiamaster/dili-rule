package com.dili.rule.controller;

import com.dili.ss.domain.BaseOutput;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

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
        if (null != fromLocal && fromLocal){

        }
        Map maps = Maps.newLinkedHashMap();
        maps.put("1","进门");
        maps.put("2","神农");
        maps.put("3","报表");
        maps.put("4","摊位");
        //todo 目前数据怎么存，从哪里来，还没确定
        return BaseOutput.success().setData(maps);
    }
}
