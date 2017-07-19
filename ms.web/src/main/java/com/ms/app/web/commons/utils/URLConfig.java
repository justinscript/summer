/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.app.web.commons.utils;

import java.io.IOException;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;

import com.ms.commons.log.LoggerFactoryWrapper;

/**
 * 读取/META-INF/web-url.properties中的配置信息
 * 
 * @author zxc Apr 12, 2013 10:51:16 PM
 */
public class URLConfig {

    private static Properties properties = new Properties();
    private static Logger     logger     = LoggerFactoryWrapper.getLogger(URLConfig.class);

    static {
        try {
            properties.load(URLConfig.class.getResourceAsStream("/META-INF/web-url.properties"));
            String property = properties.getProperty("current.server");
            if (StringUtils.isEmpty(property)) {
                throw new IllegalArgumentException("请配置【/META-INF/web-url.properties】中的current.server");
            }
        } catch (IOException e) {
            logger.error("读取[/META-INF/web-url.properties]失败", e);
        }
    }

    public static String get(String key) {
        return properties.getProperty(key);
    }
}
