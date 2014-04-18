/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.app.web.commons.tools;

import java.io.IOException;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;

import com.ms.commons.combiz.service.CommonBizServiceLocator;
import com.ms.commons.db.jdbc.DataSource;
import com.ms.commons.utilities.CoreUtilities;

/**
 * 记录一些系统的属性
 * 
 * @author zxc Apr 12, 2013 10:57:36 PM
 */
public class SystemInfos {

    private static final String RUN_MODE = "run.mode";
    // 记录IP地址
    private static final String ipAddress;
    // 记录HostName
    private static final String hostName;

    private static final String dbInfo;
    private static final String user;
    // 运行模式
    private static final String mode;
    // 应用实例名字，为了区别同一台机器上相同应用而设置。 例如：online_mustang1 offline
    private static final String appName;

    static {
        hostName = CoreUtilities.getHostName();
        ipAddress = CoreUtilities.getIPAddress();
        DataSource dataSource = CommonBizServiceLocator.getDataSource();
        String dbUrl = dataSource.getUrl();
        dbInfo = getDBIP(dbUrl);
        user = dataSource.getUsername();
        Properties properties = new Properties();
        try {
            properties.load(SystemInfos.class.getResourceAsStream("/META-INF/web-url.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        mode = properties.getProperty(RUN_MODE);
        appName = properties.getProperty("app_name");
    }

    private static String getDBIP(String dburl) {
        if (dburl == null || dburl.length() == 0) {
            return StringUtils.EMPTY;
        }
        int index = dburl.indexOf('@');
        if (index < 0) {
            return StringUtils.EMPTY;
        }
        String t = dburl.substring(index + 1);
        if (t == null || t.length() == 0) {
            return StringUtils.EMPTY;
        }
        index = t.indexOf(':');
        return t.substring(0, index);
    }

    public static String getIpaddress() {
        return ipAddress;
    }

    public static String getHostname() {
        return hostName;
    }

    public static String getDbinfo() {
        return dbInfo;
    }

    public static String getUser() {
        return user;
    }

    public static String getMode() {
        return mode;
    }

    public static String getAppname() {
        return appName;
    }
}
