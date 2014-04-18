/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.summer.web.handler;

import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.servlet.handler.SimpleUrlHandlerMapping;

/**
 * @author zxc Apr 12, 2013 4:11:51 PM
 */
public class ComponentMethodHandlerMapping extends SimpleUrlHandlerMapping {

    @SuppressWarnings("rawtypes")
    public Object lookupHandler(String urlPath, HttpServletRequest request) throws Exception {
        Map handlerMap = getHandlerMap();
        // Direct match?
        Object handler = handlerMap.get(urlPath);
        if (handler != null) {
            handler = getHandleInstance(String.valueOf(handler));
            validateHandler(handler, request);
            return buildPathExposingHandler(handler, urlPath);
        }
        // Pattern match?
        String bestPathMatch = null;
        for (Iterator it = handlerMap.keySet().iterator(); it.hasNext();) {
            String registeredPath = (String) it.next();
            if (getPathMatcher().match(registeredPath, urlPath)
                && (bestPathMatch == null || bestPathMatch.length() < registeredPath.length())) {
                bestPathMatch = registeredPath;
            }
        }
        if (bestPathMatch != null) {
            handler = handlerMap.get(bestPathMatch);
            handler = getHandleInstance(String.valueOf(handler));
            validateHandler(handler, request);
            String pathWithinMapping = getPathMatcher().extractPathWithinPattern(bestPathMatch, urlPath);
            return buildPathExposingHandler(handler, pathWithinMapping);
        }
        // No handler found...
        return null;
    }

    private Object getHandleInstance(String name) {
        if (name == null) {
            logger.error("bean的name为null，请检查summer映射配置是否正确");
        }
        return getApplicationContext().getBean(name);
    }

    public Object getDefaultHandler() {
        Object defaultHandler = super.getDefaultHandler();
        return getHandleInstance((String) defaultHandler);
    }

    public Object getRootHandler() {
        Object rootHandler = super.getRootHandler();
        return getHandleInstance((String) rootHandler);
    }

}
