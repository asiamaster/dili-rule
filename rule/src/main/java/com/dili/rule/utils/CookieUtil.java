/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dili.rule.utils;

import com.dili.uap.sdk.constant.SessionConstants;
import one.util.streamex.StreamEx;

import javax.servlet.http.HttpServletRequest;

/**
 *
 * @author admin
 */
public class CookieUtil {

    public static String getUapSessionId(HttpServletRequest request) {
        String uapSessionId = StreamEx.of(request.getCookies()).nonNull().findAny(c -> c.getName().equals(SessionConstants.ACCESS_TOKEN_KEY)).map(c -> c.getValue()).orElse("");
        return uapSessionId;
    }

}
