/*
 * Copyright 2017-2025 msun.com All right reserved. This software is the confidential and proprietary information of
 * msun.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with msun.com.
 */
package com.ms.commons.summer.web.handler;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;
import org.springframework.web.servlet.mvc.multiaction.InternalPathMethodNameResolver;
import org.springframework.web.servlet.mvc.multiaction.MethodNameResolver;

/**
 * @author zxc Apr 12, 2013 4:11:02 PM
 */
public class MethodNameResolverAdapter {

    private MethodNameResolver                    defaultNameResolver = new InternalPathMethodNameResolver();
    private PathMatcher                           pathMatcher         = new AntPathMatcher();
    private final Map<String, MethodNameResolver> urlMap              = new HashMap<String, MethodNameResolver>();

    public void setMappings(Map<String, MethodNameResolver> mappings) {
        this.urlMap.putAll(mappings);
    }

    public Map<String, MethodNameResolver> getMappings() {
        return urlMap;
    }

    /**
     * @return
     */
    public PathMatcher getPathMatcher() {
        return pathMatcher;
    }

    /**
     * 根据url得到最匹配的method解析器
     * 
     * @param url
     * @return
     */
    public MethodNameResolver getMethodNameResolver(String urlPath) {
        Map<String, MethodNameResolver> mappings = getMappings();
        // direct match
        MethodNameResolver methodNameResolver = mappings.get(urlPath);
        if (methodNameResolver != null) {
            return methodNameResolver;
        }
        String bestPathMatch = null;
        for (Iterator<String> it = mappings.keySet().iterator(); it.hasNext();) {
            String registeredPath = it.next();
            if (getPathMatcher().match(registeredPath, urlPath)
                && (bestPathMatch == null || bestPathMatch.length() < registeredPath.length())) {
                bestPathMatch = registeredPath;
            }
        }
        if (bestPathMatch != null) {
            return methodNameResolver = mappings.get(bestPathMatch);
        }
        return defaultNameResolver;
    }
}
