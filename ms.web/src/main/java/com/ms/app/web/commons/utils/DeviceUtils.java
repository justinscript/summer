/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.app.web.commons.utils;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;

import com.ms.app.web.commons.cons.WebBrowserEnum;

/**
 * 设备工具类
 * 
 * @author zxc Apr 12, 2013 10:54:07 PM
 */
public class DeviceUtils {

    private static final String   USER_AGENT = "User-Agent";
    private static final String[] DEVICES    = new String[] { "android", "iphone", "ipad", "ipod", "blackberry",
            "windows ce", "symbian os", "iemobile", "palmos", "windows phone os", "meego" };

    /**
     * 根据UA获取设备信息
     * 
     * @param ua
     * @return 如果是手机:0 其他(电脑):1
     */
    public static int getDeviceByUa(String ua) {
        if (StringUtils.isBlank(ua)) {
            return 1;
        }
        ua = StringUtils.lowerCase(ua);
        // 判断是否为手机
        if (StringUtils.indexOfAny(ua, DEVICES) < 0) {
            return 1;
        }
        return 0;
    }

    /**
     * 根据UA获取浏览器类型
     * 
     * @param ua
     * @return
     */
    public static WebBrowserEnum getBowser(HttpServletRequest request) {
        return request == null ? null : getBowser(request.getHeader(USER_AGENT));
    }

    /**
     * 根据UA获取浏览器类型
     * 
     * @param ua
     * @return
     */
    public static WebBrowserEnum getBowser(String ua) {
        if (StringUtils.isEmpty(ua)) {
            return null;
        }
        ua = ua.toLowerCase();
        if (ua.contains("msie")) {
            return WebBrowserEnum.IE;
        }
        if (ua.contains("chrome")) {
            return WebBrowserEnum.CHROME;
        }
        if (ua.contains("firefox")) {
            return WebBrowserEnum.FIREFOX;
        }
        if (ua.contains("safari")) {
            return WebBrowserEnum.SAFARI;
        }
        if (ua.contains("opera")) {
            return WebBrowserEnum.OPERA;
        }
        return null;
    }

    public static void main(String[] args) {
        String ua = "android andr asdasd windows phone OS asdasdasd palmo";
        System.out.println(getDeviceByUa(ua));
    }
}
