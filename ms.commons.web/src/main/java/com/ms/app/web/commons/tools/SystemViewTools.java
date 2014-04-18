/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.app.web.commons.tools;

/**
 * 应用系统模式
 * 
 * @author zxc Apr 12, 2013 10:56:43 PM
 */
public class SystemViewTools {

    private static final String  mode;
    private static final boolean run;
    private static final boolean dev;
    private static final boolean test;

    static {
        mode = SystemInfos.getMode();
        run = "run".equalsIgnoreCase(mode);
        dev = "dev".equalsIgnoreCase(mode);
        test = "test".equalsIgnoreCase(mode);
    }

    public static String getIpaddress() {
        return SystemInfos.getIpaddress();
    }

    public static String getHostname() {
        return SystemInfos.getHostname();
    }

    public static String getDbInfo() {
        return SystemInfos.getDbinfo();
    }

    public static String getDbUser() {
        return SystemInfos.getUser();
    }

    public long currentTimeMillis() {
        return System.currentTimeMillis();
    }

    public static boolean isRun() {
        return run;
    }

    public static boolean isDev() {
        return dev;
    }

    public static boolean isTest() {
        return test;
    }

    public static String getMode() {
        return mode;
    }
}
