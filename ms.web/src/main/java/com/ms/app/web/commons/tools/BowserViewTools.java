/*
 * Copyright 2017-2025 msun.com All right reserved. This software is the confidential and proprietary information of
 * msun.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with msun.com.
 */
package com.ms.app.web.commons.tools;

import javax.servlet.http.HttpServletRequest;

import com.ms.app.web.commons.cons.WebBrowserEnum;
import com.ms.app.web.commons.utils.DeviceUtils;

/**
 * @author zxc Apr 12, 2013 10:59:34 PM
 */
public class BowserViewTools {

    /**
     * 是否是IE浏览器
     * 
     * @param request
     * @return
     */
    public static boolean isIE(HttpServletRequest request) {
        return DeviceUtils.getBowser(request) == WebBrowserEnum.IE;
    }

    /**
     * 是否是chrome
     * 
     * @param request
     * @return
     */
    public static boolean isChrome(HttpServletRequest request) {
        return DeviceUtils.getBowser(request) == WebBrowserEnum.CHROME;
    }

    /**
     * 是否是firfox
     * 
     * @param request
     * @return
     */
    public static boolean isFirefox(HttpServletRequest request) {
        return DeviceUtils.getBowser(request) == WebBrowserEnum.FIREFOX;
    }

    /**
     * 是否是Safari
     * 
     * @param request
     * @return
     */
    public static boolean isSafari(HttpServletRequest request) {
        return DeviceUtils.getBowser(request) == WebBrowserEnum.SAFARI;
    }

    /**
     * 是否是Opera
     * 
     * @param request
     * @return
     */
    public static boolean isOpera(HttpServletRequest request) {
        return DeviceUtils.getBowser(request) == WebBrowserEnum.OPERA;
    }
}
