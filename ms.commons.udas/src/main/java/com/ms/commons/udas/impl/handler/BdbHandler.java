/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.udas.impl.handler;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;

import com.sleepycat.bind.serial.SerialBinding;
import com.sleepycat.bind.serial.StoredClassCatalog;
import com.sleepycat.je.Database;
import com.sleepycat.je.DatabaseConfig;
import com.sleepycat.je.DatabaseEntry;
import com.sleepycat.je.DatabaseException;
import com.sleepycat.je.Environment;
import com.sleepycat.je.EnvironmentConfig;
import com.sleepycat.je.LockMode;
import com.sleepycat.je.OperationStatus;
import com.ms.commons.log.LoggerFactoryWrapper;
import com.ms.commons.udas.impl.UdasObj;

/**
 * @author zxc Apr 12, 2013 5:35:30 PM
 */
public class BdbHandler extends AbstractKVHandler {

    private Logger      log = LoggerFactoryWrapper.getLogger(AbstractKVHandler.class);

    private Environment env;
    private Database    db;

    public BdbHandler(String config) {
        super();
        setConfig(config);
        String msg = "Path#DBNAME,其中路径和数据库以#号分开。For Example:(/Users/zxc/dbd#mydatabase)";
        String sp[] = config.split("#");
        try {
            log.debug("DDB path=" + sp[0] + "  dbName=" + sp[1]);
            setUp(sp[0], 1000000);
            open(sp[1]);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("" + config + " 语法错误正确配置如下:" + msg, e);
            throw new RuntimeException("语法错误，正确配置如下:" + msg, e);
        }
        try {
            log.debug(getEnv().getConfig().toString());
        } catch (DatabaseException e) {
            e.printStackTrace();
            log.error(e.getMessage(), e);
        }
    }

    public boolean putKV(String key, UdasObj value) throws Exception {
        // 创建键
        DatabaseEntry myKey = new DatabaseEntry(key.getBytes("UTF-8"));
        // 创建catalog
        StoredClassCatalog classCatalog = new StoredClassCatalog(db);
        // 创建值
        DatabaseEntry myData = new DatabaseEntry();

        SerialBinding<UdasObj> serialBinding = new SerialBinding<UdasObj>(classCatalog, UdasObj.class);
        serialBinding.objectToEntry(value, myData);
        // 存进数据库
        OperationStatus saveStatus = db.put(null, myKey, myData);
        return saveStatus == OperationStatus.SUCCESS;
    }

    public UdasObj getKV(String key) throws Exception {
        // 创建键
        DatabaseEntry myKey = new DatabaseEntry(key.getBytes("UTF-8"));
        // 创建catalog
        StoredClassCatalog classCatalog = new StoredClassCatalog(db);
        // 创建值
        DatabaseEntry myData = new DatabaseEntry();
        SerialBinding<UdasObj> serialBinding = new SerialBinding<UdasObj>(classCatalog, UdasObj.class);

        OperationStatus getStatus = db.get(null, myKey, myData, LockMode.DEFAULT);

        if (getStatus == OperationStatus.SUCCESS) {
            return (UdasObj) serialBinding.entryToObject(myData);
        }
        return null;
    }

    protected Map<String, UdasObj> getBulkKV(String... keys) throws Exception {
        if (keys == null || keys.length == 0) {
            return Collections.emptyMap();
        }
        Map<String, UdasObj> bulkMap = new HashMap<String, UdasObj>();
        for (String key : keys) {
            UdasObj kv = getKV(key);
            bulkMap.put(key, kv);
        }
        return bulkMap;
    }

    /**
     * 关闭berkeley db
     */
    public void close() {
        try {
            closeCache();
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage(), e);
        }
    }

    /**
     * 通过
     * 
     * @param key
     * @return
     * @throws Exception
     */
    public boolean delString(String key) throws Exception {
        DatabaseEntry queryKey = new DatabaseEntry();
        queryKey.setData(key.getBytes("UTF-8"));
        OperationStatus status = db.delete(null, queryKey);
        if (status == OperationStatus.SUCCESS) {
            return true;
        }
        return false;
    }

    /**
     * 通过key得到数据
     * 
     * @param key berkeley db中的key
     * @return
     * @throws Exception
     */
    public String getString(String key) throws Exception {
        DatabaseEntry queryKey = new DatabaseEntry();
        DatabaseEntry value = new DatabaseEntry();
        queryKey.setData(key.getBytes("UTF-8"));
        OperationStatus status = db.get(null, queryKey, value, LockMode.DEFAULT);
        if (status == OperationStatus.SUCCESS) {
            return new String(value.getData(), "utf-8");
        }
        return null;
    }

    public Environment getEnv() {
        return env;
    }

    /**
     * 向berkeley db中存入数据
     * 
     * @param key 存入berkeley db时的键
     * @param value 存入berkeley db时的值
     * @return
     * @throws Exception
     */
    public boolean putString(String key, String value) throws Exception {
        byte[] theKey = key.getBytes("UTF-8");
        byte[] theValue = value.getBytes("UTF-8");
        /**
         * Berkeley DB中的记录包括两个字段，就是键和值， 并且这些键和值都必须是com.sleepycat.je.DatabaseEntry类的实例。
         */
        OperationStatus status = db.put(null, new DatabaseEntry(theKey), new DatabaseEntry(theValue));
        if (status == OperationStatus.SUCCESS) {
            return true;
        }
        return false;
    }

    /**
     * 构建数据库的开发环境
     * 
     * @param path 数据库开发环境的目录
     * @param cacheSize 配置缓存大小
     */
    public void setUp(String path, long cacheSize) {
        EnvironmentConfig envConfig = new EnvironmentConfig();
        // 当设置为true时，说明若没有数据库的环境时，可以打开。否则就不能打开
        envConfig.setAllowCreate(true);
        // envConfig.setReadOnly(true);
        envConfig.setCacheSize(cacheSize);
        // 设置事务
        // envConfig.setTransactional(true);
        // 当提交事务的时候是否把缓存中的内容同步到磁盘中去。true 表示不同步，也就是说不写磁盘
        // envConfig.setTxnNoSync(true);
        // 当提交事务的时候，是否把缓冲的log写到磁盘上,true 表示不同步，也就是说不写磁盘
        // envConfig.setTxnWriteNoSync(true);
        try {
            env = new Environment(new File(path), envConfig);
        } catch (DatabaseException e) {
            e.printStackTrace();
            log.error(e.getMessage(), e);
        }
    }

    /**
     * 构建数据库
     * 
     * @param dbName 数据库的名称
     */
    public void open(String dbName) {
        DatabaseConfig dbConfig = new DatabaseConfig();
        // 设置数据的是否可以创建的属性
        dbConfig.setAllowCreate(true);
        try {
            db = env.openDatabase(null, dbName, dbConfig);
        } catch (DatabaseException e) {
            e.printStackTrace();
            log.error(e.getMessage(), e);
        }
    }

    protected boolean putKV(String key, int expireTimeInSeconds, UdasObj value) throws Exception {
        throw new UnsupportedOperationException("本地BDB不能支持给数据设置过期时间");
    }

    protected boolean delKV(String key) throws Exception {
        // 创建键
        DatabaseEntry myKey = new DatabaseEntry(key.getBytes("UTF-8"));
        OperationStatus saveStatus = db.delete(null, myKey);
        return saveStatus == OperationStatus.SUCCESS;
    }

    public boolean closeCache() throws Exception {
        if (db != null) {
            db.close();
        }
        if (env != null) {
            env.cleanLog();
            env.close();
        }
        return true;
    }

    protected boolean delAllKV() throws Exception {
        return false;
    }
}
