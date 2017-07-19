/*
 * Copyright 2017-2025 msun.com All right reserved. This software is the confidential and proprietary information of
 * msun.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with msun.com.
 */
package com.ms.commons.request;

/**
 * 封装请求信息
 * 
 * @author zxc Apr 12, 2013 1:37:11 PM
 */
public class RequestInfo {

    private static ThreadLocal<String> cache = new ThreadLocal<String>() {

                                                 protected String initialValue() {
                                                     return "unKnow";
                                                 }
                                             };

    public static String get() {
        return cache.get();
    }

    public static void set(String info) {
        cache.set(info);
    }
}
