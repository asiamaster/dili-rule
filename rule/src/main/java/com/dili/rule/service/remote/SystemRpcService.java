package com.dili.rule.service.remote;

import cn.hutool.core.collection.CollectionUtil;
import com.dili.ss.domain.BaseOutput;
import com.dili.uap.sdk.domain.Systems;
import com.dili.uap.sdk.domain.dto.SystemDto;
import com.dili.uap.sdk.rpc.SystemRpc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <B></B>
 * <B>Copyright:本软件源代码版权归农丰时代科技有限公司及其研发团队所有,未经许可不得任意复制与传播.</B>
 * <B>农丰时代科技有限公司</B>
 *
 * @author yuehongbo
 * @date 2020/5/15 17:56
 */
@Service
public class SystemRpcService {
    @Autowired
    private SystemRpc systemRpc;
    /**
     * 根据条件查询系统信息
     * @param systemDto
     * @return
     */
    public List<Systems> listByExample(SystemDto systemDto) {
        BaseOutput<List<Systems>> output = systemRpc.listByExample(systemDto);
        return output.isSuccess() && CollectionUtil.isNotEmpty(output.getData()) ? output.getData() : null;
    }
}
