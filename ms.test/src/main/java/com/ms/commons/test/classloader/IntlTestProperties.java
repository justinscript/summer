/*
 * Copyright 2017-2025 msun.com All right reserved. This software is the confidential and proprietary information of
 * msun.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with msun.com.
 */
package com.ms.commons.test.classloader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.Properties;

import com.ms.commons.test.classloader.util.SimpleAntxLoader;
import com.ms.commons.test.classloader.util.VelocityTemplateUtil;
import com.ms.commons.test.constants.IntlTestGlobalConstants;

/**
 * antx.properties
 * 
 * @author zxc Apr 13, 2013 11:07:22 PM
 */
public class IntlTestProperties {

    public static final String     ANTX_FILE;
    public static final Properties PROPERTIES;
    static {
        String antxFile = System.getProperty(IntlTestGlobalConstants.ANTX_FILE_KEY);
        if ((antxFile == null) || (antxFile.length() == 0)) {
            // 先从用户目录下寻找
            if (new File(IntlTestGlobalConstants.USER_DIR_ANTX_PROPERTIES).exists()) {
                antxFile = IntlTestGlobalConstants.USER_DIR_ANTX_PROPERTIES;
            }
            // 对于单元测试查找testcase_antx.properties，如果不存在再查找antx.properties文件
            else if (new File(IntlTestGlobalConstants.USER_HOME_TESTCASE_ANTX_PROPERTIES).exists()) {
                antxFile = IntlTestGlobalConstants.USER_HOME_TESTCASE_ANTX_PROPERTIES;
            } else {
                antxFile = IntlTestGlobalConstants.USER_HOME_ANTX_PROPERTIES;
            }
        }
        System.err.println("Current antx.properties file is:" + antxFile);

        ANTX_FILE = antxFile;

        Properties properties = new Properties();

        Properties simpleAntxProperties = null;
        try {
            simpleAntxProperties = SimpleAntxLoader.getAntxProperties(new File(antxFile));
        } catch (Throwable t) {
            t.printStackTrace();
        }

        if (simpleAntxProperties == null) {
            // add system properties to current properties
            try {
                for (Object key : System.getProperties().keySet()) {
                    if (String.class == key.getClass()) {
                        String strKey = (String) key;
                        properties.put(strKey, System.getProperty(strKey));
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            File file = new File(antxFile);
            if (file.exists()) {
                try {
                    properties.load(new FileInputStream(file));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {
            properties = simpleAntxProperties;
        }
        PROPERTIES = properties;

        // 5 times merge
        for (int i = 0; i < 5; i++) {
            for (Object key : properties.keySet()) {
                if (String.class == key.getClass()) {
                    String strKey = (String) key;
                    String oldValue = properties.getProperty(strKey);
                    String value = VelocityTemplateUtil.simpleMerge(properties, oldValue);

                    properties.setProperty(strKey, value);
                }
            }
        }

        try {
            FileOutputStream fos = new FileOutputStream(IntlTestGlobalConstants.TESTCASE_TEMP_DIR + File.separator
                                                        + "backup_antx.properties.xml");
            properties.storeToXML(fos, "Intl test properties backup!");
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static boolean isFlagOn(String value) {
        Collection<String> onCollection = Arrays.asList("true", "on", "open", "yes");
        return onCollection.contains((value == null) ? "" : value.trim().toLowerCase());
    }

    /**
     * 判断某项配置是否开启
     * 
     * <pre>
     * 通过判断配置项的值是否是true,on,open,yes
     * </pre>
     */
    public static boolean isAntxFlagOn(String key) {
        return isFlagOn(getAntxProperty(key));
    }

    public static String getAntxProperty(String key) {
        return (String) PROPERTIES.get(key);
    }
}
