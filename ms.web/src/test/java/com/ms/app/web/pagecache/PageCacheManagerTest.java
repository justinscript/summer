/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.app.web.pagecache;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import junit.framework.TestCase;

import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import com.ms.app.web.commons.pagecache.PageCacheManager;
import com.ms.app.web.commons.pagecache.PageCacheManagerLocator;

/**
 * @author zxc Apr 12, 2013 11:22:29 PM
 */
public class PageCacheManagerTest extends TestCase {

    @Test
    public void testdoFilter() {
        PageCacheManager pageCacheManager = PageCacheManagerLocator.getPageCacheManager();
        pageCacheManager.setEnable(true);
        MockHttpServletRequest request = new MockHttpServletRequest("post", "/baobei/123.htm");
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();
        try {
            pageCacheManager.doFilter(request, response, chain);
        } catch (Exception e) {
            fail(e.getMessage());
        }
        try {
            assertEquals("/baobei/123.htm", response.getContentAsString());
        } catch (UnsupportedEncodingException e1) {
            fail(e1.getMessage());
        }
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        chain.flag_123 = true;
        try {
            pageCacheManager.doFilter(request, response, chain);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    public static class MockFilterChain implements FilterChain {

        public boolean flag_123 = true;

        public void doFilter(ServletRequest request, ServletResponse response) throws IOException, ServletException {
            HttpServletRequest httpServletRequest = (HttpServletRequest) request;
            String url = httpServletRequest.getRequestURI();
            if (url.equals("/baobei/123.htm")) {
                if (flag_123) {
                    flag_123 = false;
                } else {
                    fail("cache fail");
                }
            }
            response.getOutputStream().write(url.getBytes());
            response.getOutputStream().flush();
        }

    }
}
