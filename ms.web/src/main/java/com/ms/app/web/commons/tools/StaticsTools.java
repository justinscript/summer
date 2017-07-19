/*
 * Copyright 2017-2025 msun.com All right reserved. This software is the confidential and proprietary information of
 * msun.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with msun.com.
 */
package com.ms.app.web.commons.tools;

import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;

/**
 * @author zxc Apr 12, 2013 10:58:23 PM
 */
public class StaticsTools {

    private static String               randomStaticVersion = "";
    private static ThreadLocal<Boolean> isDebugMode         = new ThreadLocal<Boolean>() {

                                                                protected Boolean initialValue() {
                                                                    return Boolean.FALSE;// 保证不为空，默认值为false
                                                                }

                                                            };
    static {
        long currentTimeMillis = System.currentTimeMillis();
        randomStaticVersion = String.valueOf(currentTimeMillis / TimeUnit.MINUTES.toMillis(5));
    }

    /**
     * 如果判断出当前请求模式是Debug Mode的话，就输出"_debug"
     * 
     * @return
     */
    public static String getSuffix() {
        if (isDebugMode.get()) {
            return "_debug";
        } else {
            return null;
        }
    }

    public static void setDebugModeIfEixisted(HttpServletRequest request) {
        String isDebug = request.getParameter("_is_debug_");
        if (StringUtils.equalsIgnoreCase(isDebug, "true")) {
            isDebugMode.set(true);
        } else {
            isDebugMode.set(false);
        }
    }

    public static String getVersion() {
        if (!StringUtils.equals(SystemInfos.getMode(), "run")) {
            return "";
        }
        return randomStaticVersion;
    }
}
