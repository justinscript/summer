/*
 * Copyright 2017-2025 msun.com All right reserved. This software is the confidential and proprietary information of
 * msun.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with msun.com.
 */
package com.ms.commons.db.jdbc;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import org.apache.commons.dbcp.BasicDataSource;

import com.ms.commons.log.ExpandLogger;
import com.ms.commons.log.LoggerFactoryWrapper;

/**
 * 数据源信息
 * 
 * @author zxc Apr 12, 2013 5:04:49 PM
 */
public class DataSource extends BasicDataSource {

    public static ExpandLogger  logger                     = LoggerFactoryWrapper.getLogger(DataSource.class);

    // System properties中的属性key
    public static final String  KEY_DATA_SOURCE_PROPERTIES = "msun.datasource.properties";
    // 配置文件名
    public static final String  DATA_SOURCE_PROPERTIES     = "datasource.properties";

    private static final String JDBC_DRIVER                = "jdbc.driver";
    private static final String DB_JDBC_URL                = "db.jdbc.url";
    private static final String DB_JDBC_USER               = "db.jdbc.user";
    private static final String DB_JDBC_PWD                = "db.jdbc.pwd";

    // 数据库类型
    private String              dbtype                     = "";

    public void setDbtype(String dbtype) {
        this.dbtype = dbtype;
    }

    public void init() {
        String filename;
        String property = System.getProperty(KEY_DATA_SOURCE_PROPERTIES);
        if (property == null || property.trim().length() == 0) {
            String userdir = System.getProperty("user.home");
            filename = userdir + File.separator + DATA_SOURCE_PROPERTIES;
        } else {
            filename = property;
        }
        logger.info("A 使用数据配置文件:" + filename);
        logger.error("------>" + filename);
        File file = new File(filename);
        if (!file.exists()) {
            logger.error("B 数据库配置文件: {} 不存在", filename);
            throw new DataSourceException("C 数据库配置文件 " + filename + " 不存在");
        }

        Properties prop = new Properties();
        try {
            prop.load(new FileInputStream(file));
        } catch (FileNotFoundException e) {
            logger.error("D 数据库配置文件 " + filename + " 不存在", e);
            e.printStackTrace();
            throw new DataSourceException("E 数据库配置文件 " + filename + " 不存在", e);
        } catch (IOException e) {
            logger.error("F 数据库配置文件 " + filename + " 读取错误", e);
            e.printStackTrace();
            throw new DataSourceException("G 数据库配置文件 " + filename + " 读取错误", e);
        }

        // driver
        String jdbc_driver = JDBC_DRIVER + dbtype;
        String jdbc = prop.getProperty(jdbc_driver);
        if (jdbc == null || jdbc.trim().length() == 0) {
            logger.error("H 读取属性 {} 不存在", jdbc_driver);
            throw new DataSourceException("I 数据库配置信息 " + jdbc_driver + " 不存在");
        } else {
            setDriverClassName(jdbc);
            logger.info("J 设置数据库属性 {}＝{}", jdbc_driver, jdbc);
        }
        // url
        String db_jdbc_url = DB_JDBC_URL + dbtype;
        String url = prop.getProperty(db_jdbc_url);
        if (url == null || url.trim().length() == 0) {
            logger.error("K 读取属性 {} 不存在", db_jdbc_url);
            throw new DataSourceException("L 数据库配置信息 " + db_jdbc_url + " 不存在");
        } else {
            setUrl(url);
            logger.info("M 设置数据库属性 {}＝{}", db_jdbc_url, url);
        }
        // user
        String db_jdbc_user = DB_JDBC_USER + dbtype;
        String user = prop.getProperty(db_jdbc_user);
        if (user == null || user.trim().length() == 0) {
            logger.error("N 读取属性 {} 不存在", db_jdbc_user);
            throw new DataSourceException("O 数据库配置信息 " + db_jdbc_user + " 不存在");
        } else {
            setUsername(user);
            logger.info("P 设置数据库属性 {}＝{}", db_jdbc_user, user);
        }
        // password
        String db_jdbc_password = DB_JDBC_PWD + dbtype;
        String password = prop.getProperty(db_jdbc_password);
        if (password == null || password.trim().length() == 0) {
            logger.error("Q 读取属性 {} 不存在", db_jdbc_password);
            throw new DataSourceException("R 数据库配置信息 " + db_jdbc_password + " 不存在");
        } else {
            setPassword(password);
            logger.info("S 设置数据库属性 {}＝{}", db_jdbc_password, "******");
        }
    }
}
