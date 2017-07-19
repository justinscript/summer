/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.cookie.parser;

import java.lang.annotation.Annotation;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import junit.framework.Assert;

import org.easymock.EasyMock;
import org.junit.Ignore;
import org.junit.Test;

import com.ms.commons.cookie.CookieKeyEnum;
import com.ms.commons.cookie.CookieNameEnum;
import com.ms.commons.cookie.annotation.CookieDomain;
import com.ms.commons.cookie.annotation.CookieNamePolicy;
import com.ms.commons.cookie.annotation.CookiePath;

/**
 * @author zxc Apr 12, 2014 7:35:10 PM
 */
public class CookieParserTest {

    CookieNamePolicy encryptedCookieNamePolicy = new CookieNamePolicy() {

                                                   public Class<? extends Annotation> annotationType() {
                                                       return null;
                                                   }

                                                   public CookiePath path() {
                                                       return CookiePath.ROOT;
                                                   }

                                                   public int maxAge() {
                                                       return 0;
                                                   }

                                                   public boolean isSimpleValue() {
                                                       return false;
                                                   }

                                                   public boolean isEncrypt() {
                                                       return true;
                                                   }

                                                   public CookieDomain domain() {
                                                       return CookieDomain.DOT_MSUN_COM;
                                                   }
                                               };

    CookieNamePolicy simple                    = new CookieNamePolicy() {

                                                   public Class<? extends Annotation> annotationType() {
                                                       return null;
                                                   }

                                                   public CookiePath path() {
                                                       return CookiePath.ROOT;
                                                   }

                                                   public int maxAge() {
                                                       return 0;
                                                   }

                                                   public boolean isSimpleValue() {
                                                       return true;
                                                   }

                                                   public boolean isEncrypt() {
                                                       return true;
                                                   }

                                                   public CookieDomain domain() {
                                                       return CookieDomain.DOT_MSUN_COM;
                                                   }
                                               };

    /**
     * 将一个加密的CookieValue解析成成一个Map<cookiekey,String>
     */
    @Test
    public void paserEncrpytCookieValue() {
        // init
        CookieNameConfig cookieNameConfig = new CookieNameConfig(CookieNameEnum.msun_cookie_forever,
                                                                 encryptedCookieNamePolicy);
        cookieNameConfig.appendKey(CookieKeyEnum.cookie_id);
        cookieNameConfig.appendKey(CookieKeyEnum.last_access_time);
        cookieNameConfig.appendKey(CookieKeyEnum.member_id);
        // value
        String cookieValue = "c_id=1&l_t=2&y_m_id=3";
        cookieValue = CookieUtils.encrypt(cookieValue);
        System.out.println("encypted:" + cookieValue);
        //
        CookieNameHelper cookieNameHelper = CookieParser.paserCookieValue(cookieNameConfig, cookieValue);
        Assert.assertNotNull(cookieNameHelper);
        Assert.assertTrue(cookieNameHelper.getAllKeys().size() == 2);
        Assert.assertTrue(cookieNameHelper.getValue(CookieKeyEnum.cookie_id).equals("1"));
        // Assert.assertTrue(cookieNameHelper.getValue(CookieKeyEnum.uzai_last_access_time).equals("2"));
        Assert.assertTrue(cookieNameHelper.getValue(CookieKeyEnum.member_id).equals("3"));
    }

    /**
     * 对简单CookieValue的解析
     */
    @Test
    public void paserSimpleCookieValue() {
        // init
        CookieNameConfig cookieNameConfig = new CookieNameConfig(CookieNameEnum.msun_cookie_forever, simple);

        // value
        String cookieValue = "1";
        cookieValue = CookieUtils.encrypt(cookieValue);
        System.out.println("encypted:" + cookieValue);
        //
        CookieNameHelper cookieNameHelper = CookieParser.paserCookieValue(cookieNameConfig, cookieValue);
        Assert.assertNotNull(cookieNameHelper);
        Assert.assertTrue(cookieNameHelper.getAllKeys().size() == 0);
        Assert.assertTrue(cookieNameHelper.getValue().equals("1"));
    }

    /**
     * 测试下从Request中加载Cookie
     */
    @Ignore
    @Test
    public void loadCookie() {
        HttpServletRequest request = EasyMock.createMock(HttpServletRequest.class);
        Cookie[] cookies = new Cookie[] {
                new Cookie(CookieNameEnum.msun_cookie_forever.getCookieName(), CookieUtils.encrypt("c_id=1")),
                new Cookie(CookieNameEnum.msun_last_login.getCookieName(), CookieUtils.encrypt("l_t=2")) };
        EasyMock.expect(request.getCookies()).andReturn(cookies);
        EasyMock.replay(request);
        Map<CookieNameEnum, CookieNameHelper> allCookieValues = CookieParser.loadCookie(request);
        Assert.assertTrue(allCookieValues.size() == 2);
        String cookieValue1 = allCookieValues.get(CookieNameEnum.msun_cookie_forever).getValue(CookieKeyEnum.cookie_id);
        String cookieValue2 = allCookieValues.get(CookieNameEnum.msun_last_login).getValue(CookieKeyEnum.last_access_time);
        Assert.assertTrue("1".equals(cookieValue1));
        Assert.assertTrue("2".equals(cookieValue2));
    }
}
