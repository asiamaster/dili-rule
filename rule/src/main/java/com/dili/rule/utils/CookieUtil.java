/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dili.rule.utils;

import javax.servlet.http.HttpServletRequest;
import one.util.streamex.StreamEx;

/**
 *
 * @author admin
 */
public class CookieUtil {

    public static String getUapSessionId(HttpServletRequest request) {
        String uapSessionId = StreamEx.of(request.getCookies()).nonNull().findAny(c -> c.getName().equals("UAP_SessionId")).map(c -> c.getValue()).orElse("");
        return uapSessionId;
    }

}
