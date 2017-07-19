/*
 * Copyright 2017-2025 msun.com All right reserved. This software is the confidential and proprietary information of
 * msun.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with msun.com.
 */
package com.ms.commons.cookie.parser;

import java.lang.reflect.Field;
import java.util.Map;

import junit.framework.Assert;

import org.junit.Ignore;
import org.junit.Test;

import com.ms.commons.cookie.CookieKeyEnum;
import com.ms.commons.cookie.CookieNameEnum;

/**
 * @author zxc Apr 12, 2014 7:40:42 PM
 */
public class CookieNamePolicyParserTest {

    @Ignore
    @Test
    public void getCookieName() {
        Assert.assertTrue(CookieNamePolicyParser.getCookieName(CookieKeyEnum.cookie_id) == CookieNameEnum.msun_cookie_forever);
        Assert.assertTrue(CookieNamePolicyParser.getCookieName(CookieKeyEnum.last_access_time) == CookieNameEnum.msun_last_login);
        Assert.assertTrue(CookieNamePolicyParser.getCookieName(CookieKeyEnum.member_id) == CookieNameEnum.msun_cookie_temp);
    }

    @Test
    public void getCookieNamePolicyMap() {
        Map<CookieNameEnum, CookieNameConfig> cookieNamePolicyMap = CookieNamePolicyParser.getCookieNamePolicyMap();
        //
        Field[] fields = CookieNameEnum.class.getFields();
        Assert.assertTrue(fields.length == cookieNamePolicyMap.size());

        // ********/
        // System.out.println("-----CookieNamePolicyMap----");
        // System.out.println("-----Size:" + cookieNamePolicyMap.size());
        // System.out.println("-----all detail information-----");
        // for (CookieNameEnum cookieNameEnum : cookieNamePolicyMap.keySet()) {
        // System.out.println("------Name:" + cookieNameEnum.name());
        // System.out.println("------All Keys:" + cookieNamePolicyMap.get(cookieNameEnum).getAllKeys());
        // }
    }
}
