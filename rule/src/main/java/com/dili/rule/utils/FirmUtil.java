package com.dili.rule.utils;

import com.dili.ss.dto.DTOUtils;
import com.dili.uap.sdk.domain.Firm;
import com.dili.uap.sdk.domain.UserTicket;
import com.dili.uap.sdk.session.SessionContext;

public class FirmUtil {
    public static Firm from(UserTicket ut){
        Firm firm= DTOUtils.newDTO(Firm.class);
        firm.setId(ut.getFirmId());
        firm.setCode(ut.getFirmCode());
        firm.setName(ut.getFirmName());
        return firm;
    }
}
