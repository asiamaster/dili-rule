package com.dili.rule;

import com.dili.ss.dto.DTOScan;
import com.dili.ss.retrofitful.annotation.RestfulScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.client.RestTemplate;
import tk.mybatis.spring.annotation.MapperScan;

/**
 * <B></B>
 * <B>Copyright:本软件源代码版权归农丰时代科技有限公司及其研发团队所有,未经许可不得任意复制与传播.</B>
 * <B>农丰时代科技有限公司</B>
 *
 * @author yuehongbo
 * @date 2020/5/7 14:12
 */
@SpringBootApplication
@EnableFeignClients(basePackages = {"com.dili.logger.sdk.rpc", "com.dili.assets.sdk.rpc"})
@MapperScan(basePackages = {"com.dili.rule.mapper", "com.dili.ss.dao"})
@ComponentScan(basePackages = {"com.dili.ss", "com.dili.rule", "com.dili.uap.sdk", "com.dili.logger.sdk"})
@RestfulScan({"com.dili.uap.sdk.rpc"})
@DTOScan(value = {"com.dili.ss", "com.dili.rule.domain"})
@EnableAsync
public class RuleApplication extends SpringBootServletInitializer {

    @LoadBalanced
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    public static void main(String[] args) {

        SpringApplication.run(RuleApplication.class, args);
    }

}
