/*
 * Copyright 2017-2025 msun.com All right reserved. This software is the confidential and proprietary information of
 * msun.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with msun.com.
 */
package com.ms.app.web.commons.utils;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;

/**
 * 请求的类型
 * 
 * @author zxc Apr 12, 2013 10:53:08 PM
 */
public class InvokeTypeTools {

    public static final String INVOKE_TYPE = "Invoke-Type";

    public static boolean isAjax(HttpServletRequest request) {
        String type = request.getHeader(INVOKE_TYPE);
        return InvokeType.isAjax(type);
    }

    public static InvokeType getInvokeType(HttpServletRequest request) {
        String type = request.getHeader(INVOKE_TYPE);
        return InvokeType.getEnum(type);
    }

    public static enum InvokeType {

        AJAX, HTTP;

        public static boolean isAjax(String type) {
            return AJAX.name().equalsIgnoreCase(type);
        }

        public static boolean isHttp(String type) {
            return HTTP.name().equalsIgnoreCase(type);
        }

        public static InvokeType getEnum(String type) {
            for (InvokeType t : values()) {
                if (StringUtils.equalsIgnoreCase(t.name(), type)) {
                    return t;
                }
            }
            return null;
        }
    }
}
