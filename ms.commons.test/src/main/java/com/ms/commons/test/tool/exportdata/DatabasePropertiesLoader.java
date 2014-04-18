/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.test.tool.exportdata;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.commons.io.IOUtils;

import com.ms.commons.test.common.ExceptionUtil;
import com.ms.commons.test.tool.exportdata.exception.MessageException;
import com.ms.commons.test.tool.util.StrUtil;

/**
 * @author zxc Apr 14, 2013 12:16:43 AM
 */
public class DatabasePropertiesLoader {

    public static List<DatabaseConfigItem> getDatabaseConfigItems() {
        File fn = new File(System.getProperty("user.home") + ".frameworktest_db_config.properties");

        if (!fn.exists()) {
            throwNoConfigException(fn);
        }

        Properties properties = loadProperties(fn);
        List<String> dbConfs = StrUtil.splitStringToList(properties.getProperty("intltest.conns"), ',');
        if (dbConfs.isEmpty()) {
            throwNoConfigException(fn);
        }

        List<DatabaseConfigItem> itemList = new ArrayList<DatabaseConfigItem>();
        for (String dbConfName : dbConfs) {
            itemList.add(getDatabaseConfigItem(properties, dbConfName));
        }

        return itemList;
    }

    private static DatabaseConfigItem getDatabaseConfigItem(Properties properties, String name) {
        DatabaseConfigItem item = new DatabaseConfigItem();
        item.setName(name);
        item.setDriver(getNotBlankItem(properties, name, "driver"));
        item.setUrl(getNotBlankItem(properties, name, "url"));
        item.setUsername(getNotBlankItem(properties, name, "username"));
        item.setPassword(getNotBlankItem(properties, name, "password"));
        return item;
    }

    private static String getNotBlankItem(Properties properties, String name, String key) {
        String nk = "intltest.conn." + name + "." + key;
        String value = properties.getProperty(nk);
        if (value == null) {
            throw new RuntimeException("Value for key '" + nk + "' is empty.");
        }
        return value.trim();
    }

    private static Properties loadProperties(File file) {
        Properties properties = new Properties();
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(file);
            properties.load(fis);
        } catch (Exception e) {
            throw ExceptionUtil.wrapToRuntimeException(e);
        } finally {
            IOUtils.closeQuietly(fis);
        }
        return properties;
    }

    private static void throwNoConfigException(File fn) {

        StringBuilder sb = new StringBuilder();
        sb.append("File '" + fn + "' not exists or empty.\n\n");
        sb.append("Format like this:\n\n");
        sb.append(".intltest.conns=dev1,dev2\n");
        sb.append(".intltest.conn.dev1.driver=com.mysql.jdbc.Driver\n");
        sb.append(".intltest.conn.dev1.url=jdbc:mysql://10.20.36.26:3306/india\n");
        sb.append(".intltest.conn.dev1.username=india\n");
        sb.append(".intltest.conn.dev1.password=india\n");
        sb.append(".intltest.conn.dev2.driver=oracle.jdbc.driver.OracleDriver\n");
        sb.append(".intltest.conn.dev2.url=jdbc:oracle:thin:@10.20.36.20:1521:oindev\n");
        sb.append(".intltest.conn.dev2.username.ms1949\n");
        sb.append(".intltest.conn.dev2.password.ms1949\n");

        throw new MessageException(sb.toString());
    }
}
