/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.udas.retry;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ms.commons.config.service.ConfigServiceLocator;

/**
 * 使用方法: 每个使用<code>RetryService</code>,必须实现{@link Retryable}接口,因为当执行重试的时候，需要回调<code>Retryable</code> 中的方法。
 * 
 * <pre>
 * 1.通过{@link RetryService#regiest(Retryable)}方法把自己注册进来,该方法将返回<code>RetryService</code>实例。
 * 注意该方法每次都会创建新的<code>RetryService</code>实例，请不要重复注册
 * 2.当然任务需要重试的时候，调用{@link RetryService#addRetryTask(String, RetryObject)} 即可。
 * 
 * </pre>
 * 
 * @author zxc Apr 12, 2013 5:30:09 PM
 */
public class RetryService {

    private static final Logger logger             = LoggerFactory.getLogger(RetryService.class);

    private static final String local_bdb_path_key = "S_localbdbpath";

    /** 失败任务的持久化服务 */
    // private PersistenceService persistenceService;
    /** 重试失败的BDB */
    // private PersistenceService retryfailedService;
    /**
     * 回调接口，以便执行重试时回调
     */
    @SuppressWarnings("unused")
    private Retryable           retryable;

    public RetryService(Retryable retryable, String namespace) {
        this.retryable = retryable;
        // initPersistentService(namespace);
        // createThreadPool();
    }

    /**
     * 创建PersistentService实例
     */
    protected void initPersistentService(String namespace) {
        // persistenceService = new PersistenceService(combinePath(namespace), namespace);
        // retryfailedService = new PersistenceService(combinePath("failed_" + namespace), namespace);
    }

    /**
     * <pre>
     * 组装BDB路径 
     * 类似于 path= /home/admin/localbdb/retry/product 或者 /home/admin/localbdb/retry/failed_product
     * </pre>
     */
    public static String combinePath(String namespace) {
        String dir = ConfigServiceLocator.getCongfigService().getKV(local_bdb_path_key, null);
        if (dir == null) {
            dir = System.getProperty("user.home") + File.separator + "bdb" + File.separator + "retry";
            logger.error("key=\"S_localbdbpath\" is not found ! get default path =" + dir);
        } else {
            // 自动转化路径
            boolean currentIsWindowsEnv = File.separator.equals("\\");
            String newDir = null;
            if (currentIsWindowsEnv) {
                newDir = dir.replace("/", File.separator);
            } else {
                newDir = dir.replace("\\", File.separator);
            }
            logger.error("Replace: ORGI DIR<" + dir + ">,New Dir<" + newDir + ">");
            dir = newDir;
        }
        return dir + File.separator + namespace;
    }

    /**
     * 初始化一个定时任务线程池
     */
    protected void createThreadPool() {
        // Executors.newSingleThreadScheduledExecutor().scheduleWithFixedDelay(new Runnable() {
        //
        // public void run() {
        // retryAllTasks();
        // }
        // }, 60, 60, TimeUnit.SECONDS);
    }

    /**
     * 增加一条重试信息
     */
    public void addRetryTask(String key, RetryObject retryObject) {
        // try {
        // // if (retryObject.getRetryCount() >= 5) {
        // // retryfailedService.persistent(key, retryObject);// 重试失败的BDB
        // // } else {
        // // persistenceService.persistent(key, retryObject); // 持久化到BDB
        // // }
        // } catch (Exception e) {
        // logger.error("持久化信息出错", e);
        // }
    }

    /**
     * 开始检索失败所有信息,然后重试！
     */
    protected void retryAllTasks() {
        // 获取所有的重试信息ID
        // Set<String> allKeys = persistenceService.getAllKey();

        // if (logger.isDebugEnabled()) {
        // logger.debug("--------开始重试任务，扫描BDB开始--------");
        // logger.debug("从本地读到需要重试的Keys集合: " + allKeys);
        // }
        //
        // for (String key : allKeys) {
        // retryTask(key);// 重试
        // }
        //
        // if (logger.isDebugEnabled()) {
        // logger.debug("--------重试任务结束--------");
        // }
    }

    /**
     * 一条重试信息
     */
    protected void retryTask(String key) {
        // RetryObject retryObject = persistenceService.getFromBDB(key);
        // // 取出后删除
        // persistenceService.del(key);
        // // 回调重试
        // if (retryObject != null) {
        // if (retryable.needRetry(key, retryObject)) {
        // retryable.retry(key, retryObject);
        // }
        // }
    }

    public boolean close() {
        return true;
        // return persistenceService.close();
    }
}
