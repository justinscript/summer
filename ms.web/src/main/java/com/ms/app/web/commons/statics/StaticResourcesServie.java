/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.app.web.commons.statics;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;

/**
 * 静态资源文件（css，js）的处理
 * 
 * @author zxc Apr 12, 2013 10:40:10 PM
 */
public class StaticResourcesServie {

    private List<StaticResources> resources;
    private StaticResources       defaultResource;

    public void setResources(List<StaticResources> resources) {
        this.resources = resources;
    }

    StaticResources getDefaultResource() {
        return defaultResource;
    }

    public void setDefaultResource(StaticResources defaultResource) {
        this.defaultResource = defaultResource;
    }

    public void init() {
        if (defaultResource != null) {
            defaultResource.initData();
        }
        if (resources != null && !resources.isEmpty()) {
            for (StaticResources resource : resources) {
                resource.initData();
            }
        }
    }

    /**
     * 获取资源对象
     * 
     * @param request
     * @return
     */
    public StaticResources getResource(HttpServletRequest request) {
        if (request == null) {
            return defaultResource;
        }
        if (resources == null || resources.isEmpty()) {
            return defaultResource;
        }
        String urlPath = request.getRequestURI();
        return getResource(urlPath);
    }

    /**
     * 获取资源对象
     * 
     * @param urlPath
     * @return
     */
    public StaticResources getResource(String urlPath) {
        if (StringUtils.isEmpty(urlPath)) {
            return defaultResource;
        }
        for (StaticResources sr : resources) {
            if (sr.lookup(urlPath)) {
                return sr;
            }
        }
        return defaultResource;
    }
}
