/*
 * Copyright 2017-2025 msun.com All right reserved. This software is the confidential and proprietary information of
 * msun.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with msun.com.
 */
package com.ms.commons.udas.retry;

import java.io.File;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ms.commons.udas.impl.handler.BdbHandler;

/**
 * 负责持久化到本地的服务
 * 
 * <pre>
 * 内部用的是BDB实现
 * </pre>
 * 
 * @author zxc Apr 12, 2013 5:31:15 PM
 */
public class PersistenceService {

    private static final Logger logger        = LoggerFactory.getLogger(PersistenceService.class);
    private static final String ALL_RETRY_KEY = "retry_key";
    private BdbHandler          bdbHandler;

    public PersistenceService(String path, String datasourcename) {
        // 如果文件夹不存在则创建
        File file = new File(path);
        if (!file.exists()) {
            if (logger.isDebugEnabled()) {
                logger.debug("本地文件夹[" + path + " ] 不存在，开始创建！");
            }
            file.mkdirs();
        }
        String config = path + "#" + datasourcename;
        bdbHandler = new BdbHandler(config);
        if (logger.isDebugEnabled()) {
            logger.debug("初始化一个本地BDB DataSources，:[" + config + "] ");
        }
    }

    /**
     * 持久化一个RetryObject
     */
    public boolean persistent(String key, RetryObject retryObject) {
        if (logger.isDebugEnabled()) {
            logger.debug("------开始持久一条数据,Key:[" + key + "] RetryObject 是[ " + retryObject + " ]-----");
        }
        if (retryObject == null) return false;

        boolean flag = true;
        try {
            flag &= putToBDB(key, retryObject);
            flag &= appendkey(key);
        } catch (Exception e) {
            logger.error("持久化出错了", e);
            flag = false;
        }

        if (logger.isDebugEnabled()) {
            logger.debug("------持久化结束Key:[" + key + "] -----");
        }
        return flag;
    }

    /**
     * 删除时一个RetryObject
     */
    public boolean del(String key) {
        if (logger.isDebugEnabled()) logger.debug("-----从存储中删除key是[" + key + " ]的数据-------");
        try {
            delFromBDB(key);
            delKey(key);
        } catch (Exception e) {
            logger.error("试图通过Key" + key + " 来删除一个 RetryObject 出错了!", e);
            return false;
        }
        if (logger.isDebugEnabled()) logger.debug("-----------删除key是[" + key + " ]的数据完成--------");
        return true;
    }

    /**
     * 获取所有Key
     * 
     * @return 如果没有会返回一个Size为0的Set<String> 集合
     */
    public Set<String> getAllKey() {
        return getCollectionFromBDB(ALL_RETRY_KEY);
    }

    /**
     * 增加Key
     */
    protected boolean appendkey(String key) {
        logger.debug("开始添加Key......");

        // 取出原有的Key
        Set<String> result = getCollectionFromBDB(ALL_RETRY_KEY);
        result.add(key);
        boolean flag = putCollectionFromBDB(ALL_RETRY_KEY, result);

        if (logger.isDebugEnabled()) {
            logger.debug("添加结束了!,添加后所有Key的集合: " + getCollectionFromBDB(ALL_RETRY_KEY));
        }
        return flag;
    }

    protected void delKey(String key) {
        // 取出原有的Key
        Set<String> result = getCollectionFromBDB(ALL_RETRY_KEY);
        if (logger.isDebugEnabled()) {
            logger.debug("本地Key的集合中开始删除一个Key[" + key + "]...");
            logger.debug("现在保存的Key集合是" + result);
        }

        if (result.isEmpty()) {
            logger.debug("试图从原有的Key集合中删除[" + key + " ],但是该key本来就不存在！");
            return;
        }

        // 剔除旧Key
        result.remove(key);
        putCollectionFromBDB(ALL_RETRY_KEY, result);

        if (logger.isDebugEnabled()) {
            logger.debug("删除[ " + key + " ]成功!剩下的Key集合是: " + getCollectionFromBDB(ALL_RETRY_KEY));
        }
    }

    /** --------- 以下是对BDB的操作接口 --------- */

    public void delFromBDB(String key) throws Exception {

        bdbHandler.delString(key);

        if (logger.isDebugEnabled()) {
            if (getFromBDB(key) == null) {
                logger.debug("从本地BDB中删除数据 key [" + key + "] 删除成功! ");
            } else {
                logger.debug("从本地BDB中删除数据 key [" + key + "] 失败,数据[" + getFromBDB(key) + "]还存在 ");
            }
        }
    }

    public RetryObject getFromBDB(String key) {
        Object result = bdbHandler.get(key);

        if (logger.isDebugEnabled()) {
            logger.debug("从本地BDB中取数据 key [" + key + "] 结果 [" + result + "]");
        }
        return result != null ? (RetryObject) result : null;
    }

    private boolean putToBDB(String key, RetryObject value) {
        if (logger.isDebugEnabled()) {
            logger.debug("向本地BDB中写入数据 key [" + key + "] Value [" + value + "]");
        }
        return bdbHandler.put(key, value);
    }

    /**
     * 如果数据不存在，则返回size为0的集合
     */
    @SuppressWarnings("unchecked")
    private Set<String> getCollectionFromBDB(String key) {
        RetryObject result = getFromBDB(key);
        if (result != null) {
            return (Set<String>) result.getValue();
        }
        return new HashSet<String>(0);
    }

    private boolean putCollectionFromBDB(String key, Set<String> value) {
        RetryObject obj = new RetryObject(System.currentTimeMillis(), (Serializable) value);
        return putToBDB(key, obj);
    }

    public boolean close() {
        bdbHandler.close();
        return true;
    }
}
