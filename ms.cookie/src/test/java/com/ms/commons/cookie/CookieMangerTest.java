/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.cookie;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import com.ms.commons.cookie.manager.CookieManager;
import com.ms.commons.cookie.manager.DefaultCookieManager;
import com.ms.commons.cookie.parser.CookieUtils;

/**
 * @author zxc Apr 12, 2014 7:37:27 PM
 */
public class CookieMangerTest {

    CookieManager cookieManager;

    @Ignore
    @Test
    public void clear() {
        // init
        HttpServletRequest request = EasyMock.createMock(HttpServletRequest.class);
        String value = "c_id=1";
        value = CookieUtils.encrypt(value);
        Cookie[] cookies = new Cookie[] { new Cookie(CookieNameEnum.msun_cookie_forever.getCookieName(), value) };
        EasyMock.expect(request.getCookies()).andReturn(cookies);
        EasyMock.replay(request);
        //
        cookieManager = new DefaultCookieManager(request, null);
        Assert.assertNotNull(cookieManager.get(CookieKeyEnum.cookie_id));

        cookieManager.clear(CookieNameEnum.msun_cookie_forever);
        Assert.assertNull(cookieManager.get(CookieKeyEnum.cookie_id));
    }

    @Test
    public void getcomplexValue() {
        // init
        HttpServletRequest request = EasyMock.createMock(HttpServletRequest.class);
        String value = "c_id=1";
        value = CookieUtils.encrypt(value);
        Cookie[] cookies = new Cookie[] { new Cookie(CookieNameEnum.msun_cookie_forever.getCookieName(), value) };
        EasyMock.expect(request.getCookies()).andReturn(cookies);
        EasyMock.replay(request);
        //
        cookieManager = new DefaultCookieManager(request, null);
        Assert.assertTrue("1".equals(cookieManager.get(CookieKeyEnum.cookie_id)));
        // 值不存在时
        Assert.assertTrue(cookieManager.get(CookieKeyEnum.last_access_time) == null);
    }

    /**
     * 目前还不知道怎么测试
     */
    @Test
    public void save() {
        // 先update再save看下
        // init
        HttpServletRequest request = EasyMock.createMock(HttpServletRequest.class);
        HttpServletResponse response = EasyMock.createMock(HttpServletResponse.class);
        String value = "c_id=1";
        value = CookieUtils.encrypt(value);
        Cookie cookie = new Cookie(CookieNameEnum.msun_cookie_forever.getCookieName(), value);
        Cookie[] cookies = new Cookie[] { cookie };
        EasyMock.expect(request.getCookies()).andReturn(cookies);
        response.addCookie(cookie);
        EasyMock.expectLastCall().times(3);
        EasyMock.replay(request);
        //
        cookieManager = new DefaultCookieManager(request, response);
        cookieManager.set(CookieKeyEnum.cookie_id, "test_cookie_id");
        cookieManager.save();
    }

    /**
     * 当值不存在时set
     */
    @Test
    public void setValueNotExisted() {
        // init easy mock
        HttpServletRequest request = EasyMock.createMock(HttpServletRequest.class);
        String value = "null";
        value = CookieUtils.encrypt(value);
        Cookie[] cookies = new Cookie[] { new Cookie(CookieNameEnum.msun_cookie_forever.getCookieName(), value) };
        EasyMock.expect(request.getCookies()).andReturn(cookies);
        EasyMock.expect(request.getRemoteHost()).andReturn("192.168.1.105");
        EasyMock.expect(request.getRemoteAddr()).andReturn("192.168.1.105");
        EasyMock.expect(request.getRemoteUser()).andReturn("192.168.1.105");
        EasyMock.expect(request.getHeader("X-Forwarded-For")).andReturn("11");

        EasyMock.replay(request);
        //
        cookieManager = new DefaultCookieManager(request, null);
        cookieManager.set(CookieKeyEnum.last_access_time, "test");
        Assert.assertTrue("test".equals(cookieManager.get(CookieKeyEnum.last_access_time)));
    }

    /**
     * 当值不存在时进行更新
     */
    @Test
    public void setValueExisted() {
        // init
        HttpServletRequest request = EasyMock.createMock(HttpServletRequest.class);
        String value = "c_id=1";
        value = CookieUtils.encrypt(value);
        Cookie[] cookies = new Cookie[] { new Cookie(CookieNameEnum.msun_cookie_forever.getCookieName(), value) };
        EasyMock.expect(request.getCookies()).andReturn(cookies);
        EasyMock.replay(request);
        //
        cookieManager = new DefaultCookieManager(request, null);
        cookieManager.set(CookieKeyEnum.cookie_id, "test_cookie_id");
        Assert.assertTrue("test_cookie_id".equals(cookieManager.get(CookieKeyEnum.cookie_id)));
    }

    @Test
    public void getsimpleValue() {
        // init
        HttpServletRequest request = EasyMock.createMock(HttpServletRequest.class);
        String value = "2";
        Cookie[] cookies = new Cookie[] { new Cookie(CookieNameEnum.msun_click_track.getCookieName(), value) };
        EasyMock.expect(request.getCookies()).andReturn(cookies);
        EasyMock.replay(request);
        //
        cookieManager = new DefaultCookieManager(request, null);
        Assert.assertTrue("2".equals(cookieManager.get(CookieNameEnum.msun_click_track)));
        // 复杂cookie不能使用该方法
        try {
            cookieManager.get(CookieNameEnum.msun_cookie_forever);
            Assert.fail();
        } catch (RuntimeException e) {
            // 正常
        }
    }

    /**
     * 值不存在时进行设置
     */
    @Test
    public void setSimpleValue() {
        // init
        HttpServletRequest request = EasyMock.createMock(HttpServletRequest.class);
        Cookie[] cookies = new Cookie[] {};
        EasyMock.expect(request.getCookies()).andReturn(cookies);
        EasyMock.replay(request);
        //
        cookieManager = new DefaultCookieManager(request, null);
        cookieManager.set(CookieNameEnum.msun_click_track, "test_cookie_id");
        Assert.assertTrue("test_cookie_id".equals(cookieManager.get(CookieNameEnum.msun_click_track)));
    }

    /**
     * 值存在时进行设置
     */
    @Test
    public void setSimpleExistedValue() {
        // init
        HttpServletRequest request = EasyMock.createMock(HttpServletRequest.class);
        String value = "2";
        Cookie[] cookies = new Cookie[] { new Cookie(CookieNameEnum.msun_click_track.getCookieName(), value) };
        EasyMock.expect(request.getCookies()).andReturn(cookies);
        EasyMock.replay(request);
        //
        cookieManager = new DefaultCookieManager(request, null);
        cookieManager.set(CookieNameEnum.msun_click_track, "test_cookie_id");
        Assert.assertTrue("test_cookie_id".equals(cookieManager.get(CookieNameEnum.msun_click_track)));
    }
}
