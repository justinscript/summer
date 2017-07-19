/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.app.web.commons.tools;

import org.apache.commons.lang.StringUtils;

import com.ms.app.web.commons.utils.URLConfig;

/**
 * MSUN下所有web的网站域名
 * 
 * @author zxc Apr 12, 2013 10:54:24 PM
 */
public class WebSiteViewTools {

    private static final String CURRENT = "current.server";
    private static final String NISA    = "nisa.server";
    private static final String WARRIOR = "warrior.server";
    private static final String NILE    = "nile.server";

    private static boolean      nisa;
    private static boolean      warrior;
    private static boolean      nile;

    static {
        String current = URLConfig.get(CURRENT);
        nisa = StringUtils.equals(current, URLConfig.get(NISA));
        warrior = StringUtils.equals(current, URLConfig.get(WARRIOR));
        nile = StringUtils.equals(current, URLConfig.get(NILE));
    }

    public static String getCurrent() {
        return URLConfig.get(CURRENT);
    }

    public static String getNisa() {
        return URLConfig.get(NISA);
    }

    public static String getWarrior() {
        return URLConfig.get(WARRIOR);
    }

    public static String getNile() {
        return URLConfig.get(NILE);
    }

    public static boolean isNisa() {
        return nisa;
    }

    public static boolean isWarrior() {
        return warrior;
    }

    public static boolean isNile() {
        return nile;
    }
}
