/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.app.web.commons.pagecache;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;

/**
 * 用于静态网页（帮助，about等，可以长期缓存）的页面缓存
 * 
 * @author zxc Apr 12, 2013 10:46:23 PM
 */
public class DefaultPageCache extends AbstractPageCache {

    static final int     EFFECTIVE_TIME = 3600 * 24;
    private PathMatcher  pathMatcher    = new AntPathMatcher();

    private List<String> matchUrls;

    public DefaultPageCache() {
        setEffectiveTime(EFFECTIVE_TIME);
    }

    /**
     * 此请求是否支持缓存
     * 
     * @param request
     * @return
     */
    public boolean isSupport(HttpServletRequest request) {
        if (!isEnable()) {
            return false;
        }
        return isSupport(request.getRequestURI(), matchUrls);
    }

    /**
     * path是否满足需要缓存
     * 
     * @param path
     * @param matchUrls
     * @return
     */
    protected boolean isSupport(String path, List<String> matchUrls) {
        if (matchUrls == null || matchUrls.isEmpty()) {
            return false;
        }
        for (String url : matchUrls) {
            // 直接匹配
            if (url.equals(path)) {
                return true;
            }
            // match
            if (pathMatcher.match(url, path)) {
                return true;
            }
        }
        return false;
    }

    public String calculateKey(HttpServletRequest request) {
        return request.getRequestURI();
    }

    protected PathMatcher getPathMatcher() {
        return pathMatcher;
    }

    public void setMatchUrls(List<String> matchUrls) {
        this.matchUrls = matchUrls;
    }

    /**
     * 添加match url<br>
     * url必须以"/"开头
     * 
     * @param url
     */
    void addMatchUrl(String url) {
        if (StringUtils.isEmpty(url)) {
            return;
        }
        if (!url.startsWith(PATH_SEP)) {
            return;
        }
        if (this.matchUrls == null) {
            this.matchUrls = new ArrayList<String>();
        }
        matchUrls.add(url);
    }
}
