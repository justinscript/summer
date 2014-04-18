/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.app.web.commons.pagecache;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;

import com.ms.commons.config.interfaces.ConfigService;
import com.ms.commons.config.listener.ConfigListener;
import com.ms.commons.config.service.ConfigServiceLocator;
import com.ms.commons.log.LoggerFactoryWrapper;
import com.ms.commons.udas.impl.UdasObj;
import com.ms.commons.udas.interfaces.UdasService;
import com.ms.commons.utilities.CoreUtilities;

/**
 * @author zxc Apr 12, 2013 10:44:46 PM
 */
public class PageCacheManager implements ConfigListener {

    private static final Logger    LOG    = LoggerFactoryWrapper.getLogger(PageCacheManager.class);
    private Map<String, PageCache> pageCacheMap;
    private String                 pageCacheKey;
    private boolean                enable = false;
    private String                 ipAddress;

    public PageCacheManager() {
    }

    public void init() {
        if (pageCacheMap == null) {
            pageCacheMap = new HashMap<String, PageCache>();
        }
        initConfig();
        ConfigService congfigService = ConfigServiceLocator.getCongfigService();
        congfigService.addConfigListener(this);
        ipAddress = CoreUtilities.getIPAddress();
    }

    private void initConfig() {
        ConfigService congfigService = ConfigServiceLocator.getCongfigService();
        enable = congfigService.getKV(pageCacheKey, false);
    }

    public void updateConfig() {
        initConfig();
    }

    public String getName() {
        return null;
    }

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public void setPageCacheKey(String pageCacheKey) {
        this.pageCacheKey = pageCacheKey;
    }

    public void setPageCacheMap(Map<String, PageCache> pageCacheMap) {
        this.pageCacheMap = pageCacheMap;
    }

    public void addPageCache(String name, PageCache pageCache) {
        if (StringUtils.isEmpty(name) || pageCache == null) {
            return;
        }
        if (!pageCacheMap.containsKey(name)) {
            pageCacheMap.put(name, pageCache);
        }
    }

    public void doFilter(final HttpServletRequest request, final HttpServletResponse response, final FilterChain chain)
                                                                                                                       throws IOException,
                                                                                                                       ServletException {
        // 不需要缓存
        if (!enable) {
            // 不需要，filter继续
            chain.doFilter(request, response);
            return;
        }
        // 查找是否需要缓存
        PageCache pageCache = getPageCache(request);
        if (pageCache == null) {
            // 不需要，filter继续
            chain.doFilter(request, response);
            return;
        }
        PageInfo pageInfo = buildPageInfo(pageCache, request, response, chain);
        writeResponse(request, response, pageInfo);
    }

    /**
     * 根据请求获取PageCache
     * 
     * @param request
     * @return
     */
    private PageCache getPageCache(HttpServletRequest request) {
        if (pageCacheMap == null || pageCacheMap.isEmpty()) {
            return null;
        }
        Collection<PageCache> values = pageCacheMap.values();
        for (PageCache element : values) {
            if (element.isSupport(request)) {
                return element;
            }
        }
        return null;
    }

    /**
     * 根据spring中配置的key获取pageCache
     * 
     * @param key
     * @return
     */
    public PageCache getPageCache(String key) {
        if (pageCacheMap == null || pageCacheMap.isEmpty()) {
            return null;
        }
        return pageCacheMap.get(key);
    }

    private PageInfo buildPageInfo(PageCache pageCache, final HttpServletRequest request,
                                   final HttpServletResponse response, final FilterChain chain) throws IOException,
                                                                                               ServletException {
        String key = pageCache.calculateKey(request);
        UdasService udasService = pageCache.getUdasService();
        Serializable value = udasService.getKV(key);
        if (value == null || !(value instanceof PageInfo)) {
            // 把抓住的流放入缓存中
            PageInfo pageInfo = buildPage(request, response, chain);
            putLog(pageCache, key, false);
            udasService.put(key, pageCache.getEffectiveTime(), new UdasObj(pageInfo));
            return pageInfo;
        } else {
            PageInfo pi = (PageInfo) value;
            // 数据是空的或过期了
            if (pi.isEmpty() || (System.currentTimeMillis() - pi.getCreateTime() > pageCache.getEffectiveTime() * 1000)) {
                PageInfo pageInfo = buildPage(request, response, chain);
                putLog(pageCache, key, true);
                udasService.put(key, pageCache.getEffectiveTime(), new UdasObj(pageInfo));
                return pageInfo;
            } else {
                getLog(pi, key);
                return pi;
            }
        }
    }

    protected void getLog(PageInfo pi, String key) {
        StringBuilder sb = new StringBuilder(200);
        sb.append("pagecache--get:").append("key=").append(key).append(";");
        sb.append((System.currentTimeMillis() - pi.getCreateTime())).append(";").append(ipAddress);
        LOG.warn(sb.toString());
    }

    protected void putLog(PageCache pageCache, String key, boolean isExpire) {
        StringBuilder sb = new StringBuilder(200);
        sb.append("pagecache--put:").append("key=").append(key);
        sb.append(";effectiveTime:").append(pageCache.getEffectiveTime());
        sb.append(";").append(ipAddress);
        if (isExpire) {
            sb.append(";").append("对象过期,memecache有效");
        }
        LOG.warn(sb.toString());
    }

    /**
     * Builds the PageInfo object by passing the request along the filter chain
     * 
     * @param request
     * @param response
     * @param chain
     * @return a Serializable value object for the page or page fragment
     * @throws Exception
     */
    protected PageInfo buildPage(final HttpServletRequest request, final HttpServletResponse response,
                                 final FilterChain chain) throws IOException, ServletException {

        final ByteArrayOutputStream outstr = new ByteArrayOutputStream();
        final PageCacheResponseWrapper wrapper = new PageCacheResponseWrapper(response, outstr);
        // 正常执行
        chain.doFilter(request, wrapper);
        wrapper.flush();
        // Return the page info
        return new PageInfo(wrapper.getContentType(), outstr.toByteArray(), wrapper.getStatus());
    }

    /**
     * 把PageInfo中的内容写入response中
     * 
     * @param request
     * @param response
     * @param pageInfo
     * @throws IOException
     */
    protected void writeResponse(final HttpServletRequest request, final HttpServletResponse response,
                                 final PageInfo pageInfo) throws IOException {

        setStatus(response, pageInfo);
        setContentType(response, pageInfo);
        writeContent(request, response, pageInfo);
    }

    protected void setStatus(final HttpServletResponse response, final PageInfo pageInfo) {
        response.setStatus(pageInfo.getStatusCode());
    }

    protected void setContentType(final HttpServletResponse response, final PageInfo pageInfo) {
        response.setContentType(pageInfo.getContentType());
    }

    /**
     * 把内容写入响应中
     * 
     * @param request
     * @param response
     * @param pageInfo
     * @throws IOException
     */
    protected void writeContent(final HttpServletRequest request, final HttpServletResponse response,
                                final PageInfo pageInfo) throws IOException {
        byte[] body = pageInfo.getBody();
        response.setContentLength(body.length);
        response.getOutputStream().write(body);
    }
}
