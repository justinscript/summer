/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.utilities;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author zxc Apr 12, 2013 1:35:59 PM
 */
public class StaticContentDeploy {

    public static final String  KEY_DATA_SOURCE_PROPERTIES = "msun.datasource.properties";
    public static final String  STATIC_VERSION             = "static.version";
    private static final Log    logger                     = LogFactory.getLog(StaticContentDeploy.class);

    private static final String defaultVersion             = StringUtils.EMPTY;
    private static String       jsVersion                  = defaultVersion;
    private static String       imgVersion                 = defaultVersion;
    private static String       cssVersion                 = defaultVersion;

    public static String getJsVersion() {
        return jsVersion;
    }

    public static String getImgVersion() {
        return imgVersion;
    }

    public static String getCssVersion() {
        return cssVersion;
    }

    public static synchronized void init() {
        String staticVersion = getStaticVersion();
        if (StringUtils.isBlank(staticVersion) || StringUtils.equals(staticVersion, "0")
            || !staticVersion.matches("\\d+")) {
            staticVersion = StringUtils.EMPTY;
        }
        StaticContentDeploy.jsVersion = staticVersion;
        StaticContentDeploy.imgVersion = staticVersion;
        StaticContentDeploy.cssVersion = staticVersion;
    }

    private static String getStaticVersion() {
        String property = System.getProperty(KEY_DATA_SOURCE_PROPERTIES);
        if (StringUtils.isBlank(property)) {
            logger.warn("can not find system property:" + KEY_DATA_SOURCE_PROPERTIES);
            return StringUtils.EMPTY;
        }
        File propertyFile = new File(property);
        if (!propertyFile.exists()) {
            logger.warn("property file not exists:" + property);
            return StringUtils.EMPTY;
        }
        String staticVersion = getPropertyFromFile(propertyFile, STATIC_VERSION);
        return staticVersion;
    }

    private static String getPropertyFromFile(File propertyFile, String property) {
        FileInputStream inputStream = null;
        InputStreamReader reader = null;
        try {
            inputStream = new FileInputStream(propertyFile);
            reader = new InputStreamReader(inputStream, "utf-8");
            Properties properties = new Properties();
            properties.load(reader);
            return properties.getProperty(property, StringUtils.EMPTY);
        } catch (Exception e) {
            logger.error("StaticContentDeploy init failed", e);
        } finally {
            closeQuietly(inputStream, reader);
        }
        return StringUtils.EMPTY;
    }

    private static void closeQuietly(Closeable... closeables) {
        for (Closeable closeable : closeables) {
            try {
                if (closeable != null) {
                    closeable.close();
                }
            } catch (IOException e) {
                logger.error("close", e);
            }
        }
    }
}
