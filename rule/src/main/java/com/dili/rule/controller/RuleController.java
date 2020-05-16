package com.dili.rule.controller;

import com.dili.rule.service.RuleService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 由MyBatis Generator工具自动生成
 * This file was generated on 2020-05-13 11:17:11.
 */
@Api("/rule")
@Controller
@RequestMapping("/rule")
public class RuleController {
    @Autowired
    RuleService ruleService;

}