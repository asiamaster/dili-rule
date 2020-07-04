package com.dili.rule;

import com.alibaba.fastjson.JSONObject;
import com.dili.rule.sdk.domain.input.QueryFeeInput;
import com.google.common.collect.Maps;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * <B></B>
 * <B>Copyright:本软件源代码版权归农丰时代科技有限公司及其研发团队所有,未经许可不得任意复制与传播.</B>
 * <B>农丰时代科技有限公司</B>
 *
 * @author yuehongbo
 * @date 2020/7/4 9:13
 */
@SpringBootTest
@ActiveProfiles("dev")
@AutoConfigureMockMvc
@WebAppConfiguration
public class ChargeRuleApiTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testQueryFee()  {
        QueryFeeInput queryFeeInput = new QueryFeeInput();
        queryFeeInput.setMarketId(1L);
        queryFeeInput.setBusinessType("1");
        queryFeeInput.setChargeItem(21L);
        Map<String, Object> calcParams = Maps.newHashMap();
        calcParams.put("weight",11);
        queryFeeInput.setCalcParams(calcParams);
        Map<String, Object> conditionParams = Maps.newHashMap();
        conditionParams.put("id",2);
        conditionParams.put("marketId",1);
        queryFeeInput.setConditionParams(conditionParams);
        try {
            MvcResult result = mockMvc.perform(post("/api/chargeRule/queryFee").content(JSONObject.toJSONString(queryFeeInput))
                    .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())// 模拟向testRest发送请求
                    .andExpect(content().contentType("application/json;charset=UTF-8"))// 预期返回值的媒体类型text/plain;
                    .andReturn();// 返回执行请求的结果
            System.out.println(result.getResponse().getContentAsString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
