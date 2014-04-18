/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.app.web.commons.utils;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;

/**
 * @author zxc Apr 12, 2013 10:53:26 PM
 */
public class HttpUtil {

    private static final String[] INTRANET_IPS_PREFIX = { "192.168.1." };
    private static final String[] INTRANET_IPS        = { "127.0.0.1", "localhost" };

    /**
     * 获取访问者的ip<br>
     * 此ip是从请求头中取出（x-forwarded-for），如果没取到则取remoteAddr
     * 
     * @param request
     * @return
     */
    public static String getIpAddr(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip != null && ip.length() != 0 && !"unknown".equalsIgnoreCase(ip)) {
            return ip;
        }
        // X-Real-IP
        // Proxy-Client-IP
        // WL-Proxy-Client-IP
        return request.getRemoteAddr();
    }

    /**
     * 检查此请求是否是内网请求的ip
     * 
     * @param request
     * @return
     */
    public static boolean isIntranetIp(HttpServletRequest request) {
        return isIntranetIp(getIpAddr(request));
    }

    /**
     * 检查此ip是否是内网ip
     * 
     * @param ip
     * @return
     */
    public static boolean isIntranetIp(String ip) {
        if (StringUtils.isEmpty(ip)) {
            return true;
        }
        for (String temp : INTRANET_IPS) {
            if (temp.equals(ip)) {
                return true;
            }
        }
        for (String temp : INTRANET_IPS_PREFIX) {
            if (ip.startsWith(temp)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获取外网的IP<br/>
     * IP格式:101.226.52.82, 192.168.1.102
     * 
     * @return
     */
    public static String getExternalIP(HttpServletRequest request) {
        String ipAddr = getIpAddr(request);
        return _getExternalIP(ipAddr);
    }

    private static String _getExternalIP(String ipAddr) {
        if (StringUtils.isEmpty(ipAddr)) {
            return null;
        }
        int index = ipAddr.indexOf(',');
        if (index == -1) {
            return ipAddr;
        }
        ipAddr = ipAddr.substring(0, index);
        if (isIntranetIp(ipAddr)) {
            return null;
        }
        return ipAddr;
    }
}
