/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.app.web.commons.pagecache;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author zxc Apr 12, 2013 10:45:39 PM
 */
public class PageCacheFilter implements Filter {

    private PageCacheManager pageCacheManager;

    public void init(FilterConfig filterConfig) throws ServletException {
        pageCacheManager = PageCacheManagerLocator.getPageCacheManager();
    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
                                                                                             ServletException {
        pageCacheManager.doFilter((HttpServletRequest) request, (HttpServletResponse) response, chain);
    }

    public void destroy() {

    }
}
