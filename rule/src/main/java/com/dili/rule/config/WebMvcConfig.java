/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dili.rule.config;

import com.jxxc.admin.filters.ParameterTrimFilter;
import javax.servlet.DispatcherType;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 配置过滤器
 *
 * @author admin
 */
@Configuration
public class WebMvcConfig {

    @Bean(name = "parameterTrimFilter") // 不写name属性，默认beanName为方法名
    public FilterRegistrationBean<ParameterTrimFilter> parameterTrimFilter() {
        FilterRegistrationBean<ParameterTrimFilter> filter = new FilterRegistrationBean<>();
        filter.setDispatcherTypes(DispatcherType.REQUEST);
        filter.setFilter(new ParameterTrimFilter()); // 必须设置
        filter.addUrlPatterns("/*"); // 拦截所有请求，如果没有设置则默认“/*”
        filter.setName("parameterTrimFilter"); // 设置注册的名称，如果没有指定会使用Bean的名称。此name也是过滤器的名称
        filter.setOrder(FilterRegistrationBean.LOWEST_PRECEDENCE);// 该filter在filterChain中的执行顺序
        return filter;
    }
}
