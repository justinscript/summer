/*
 * Copyright 2017-2025 msun.com All right reserved. This software is the confidential and proprietary information of
 * msun.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with msun.com.
 */
package com.ms.app.web.commons.request;

import java.util.Enumeration;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.ms.app.web.commons.utils.HttpUtil;
import com.ms.commons.request.RequestInfo;

/**
 * 获取当前请求的详细信息，一边出异常时使用
 * 
 * @author zxc Apr 12, 2013 10:42:21 PM
 */
public class RequestDigger {

    @SuppressWarnings("rawtypes")
    public static StringBuilder saveRequestInfo(HttpServletRequest request) {
        // 获取header
        Enumeration headerNames = request.getHeaderNames();
        StringBuilder sb = new StringBuilder();
        while (headerNames.hasMoreElements()) {
            Object object = (Object) headerNames.nextElement();
            sb.append((String) object).append(":").append(request.getHeader((String) object)).append("\r\n");
        }
        // 获取IP，可能有代理地址在header中
        sb.append("remoteAddr:").append(HttpUtil.getIpAddr(request)).append("\r\n");
        // 获取当前请求的路径以及参数
        sb.append("requestURI:").append(request.getRequestURI()).append("\r\n");
        Map parameterMap = request.getParameterMap();
        for (Object key : parameterMap.keySet()) {
            sb.append(key.toString()).append(":").append(request.getParameter(key.toString())).append("\r\n");
        }
        // 保存到当前线程上下文中去
        RequestInfo.set(sb.toString());
        return sb;
    }

    public static String getSavedRequestInfo() {
        return RequestInfo.get();
    }
}
