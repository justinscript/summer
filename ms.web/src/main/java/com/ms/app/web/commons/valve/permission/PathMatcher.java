/*
 * Copyright 2017-2025 msun.com All right reserved. This software is the confidential and proprietary information of
 * msun.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with msun.com.
 */
package com.ms.app.web.commons.valve.permission;

import org.springframework.util.AntPathMatcher;

/**
 * ANT 风格的URL路径匹配。目前是使用Spring的AntPathMatcher，用空可以自己实现一个简单的。
 * 
 * <pre>
 * 通配符：
 *  ? 匹配任何单字符
 *  * 匹配0或者任意数量的字符
 *  ** 匹配0或者更多的目录
 *   最长匹配原则(has more characters)
 * </pre>
 * 
 * @author zxc Apr 12, 2013 11:06:30 PM
 */
public class PathMatcher {

    private static AntPathMatcher antPathMatcher = new AntPathMatcher();

    public static boolean match(String pattern, String path) {
        return antPathMatcher.match(pattern, path);
    }

    public static void main(String[] args) {
        System.out.println(match("/*rpt/**/*.htm", "/csrpt/15/8433505.htm"));
    }
}
