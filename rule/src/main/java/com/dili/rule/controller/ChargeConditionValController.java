package com.dili.rule.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.dili.rule.service.ChargeConditionValService;

/**
 * <B></B>
 * <B>Copyright:本软件源代码版权归农丰时代科技有限公司及其研发团队所有,未经许可不得任意复制与传播.</B>
 * <B>农丰时代科技有限公司</B>
 *
 * @author yuehongbo
 * @date 2020/5/16 18:08
 */
@Controller
@RequestMapping("/chargeConditionVal")
public class ChargeConditionValController {

    @Autowired
    private ChargeConditionValService chargeConditionValService;
}
