/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.udas.impl.commons;

import static com.ms.commons.udas.impl.commons.UdasContants.key_bundle_size;
import static com.ms.commons.udas.impl.commons.UdasContants.never_expire;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import net.spy.memcached.MemcachedClient;

import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ms.commons.concurrent.ConcurrentHashSet;
import com.ms.commons.utilities.CoreUtilities;

/**
 * Memcached的所有Key是通过该类进行保存的
 * 
 * @author zxc Apr 12, 2013 5:37:33 PM
 */
public class MemcachedKeyStore {

    private static final Logger logger   = LoggerFactory.getLogger(MemcachedKeyStore.class);

    private Set<String>         keyToAdd = new ConcurrentHashSet<String>();
    private Set<String>         keyToDel = new ConcurrentHashSet<String>();

    private MemcachedClient     memcachedClient;

    private String              nameSpace;
    private String              memcached_key_second_index_size;
    private String              memcached_key_format;

    // 初始化一个线程池

    public MemcachedKeyStore(String nameSpace, MemcachedClient memcachedClient) {
        this.memcachedClient = memcachedClient;
        this.nameSpace = nameSpace;
        this.memcached_key_format = nameSpace + "_key_2rd_%s";
        this.memcached_key_second_index_size = nameSpace + "_key_2rd_size";
        // createThreadPool();
    }

    @SuppressWarnings("unused")
    private void createThreadPool() {
        int periodInSenconds = 600;// 刷新时间间隔
        String period = System.getProperty("memcached.key.flush.time");
        logger.error("memcached.key.flush.time period : " + period);
        if (period != null) {
            int temp = NumberUtils.toInt(period);
            if (temp > 0) {
                periodInSenconds = temp;
            }
        }
        logger.error("Used: memcached.key.flush.time " + periodInSenconds + " Sendconds");

        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        executor.scheduleAtFixedRate(new Runnable() {

            @Override
            public void run() {
                flushKeyIfNecesery();
            }
        }, 1, periodInSenconds, TimeUnit.SECONDS);
    }

    /**
     * 删除一个Key
     */
    public void del(String key) {
        if (logger.isDebugEnabled()) {
            logger.debug("----------" + nameSpace + " 删除一个Key [" + key + "]-----------");
        }
        // TODO:暂时去掉，不放入
        // keyToDel.add(key);
    }

    /**
     * 保存一个value
     */
    public void append(String value) {
        if (logger.isDebugEnabled()) {
            logger.debug("---------- " + nameSpace + "添加一个新的Key [" + value + "]-----------");
        }
        // 暂时去掉，不放入
        // keyToAdd.add(value);
    }

    /**
     * 将内存中的Key，存储到Memcached中去
     */
    private boolean flushKeyIfNecesery() {
        boolean needSyncToRemote = keyToDel.size() > 0 || keyToAdd.size() > 0;
        logger.warn("flushKeyIfNecesery =  " + nameSpace + "   " + CoreUtilities.getIPAddress() + " keyToDel "
                    + keyToDel.size() + "  keyToAdd " + keyToAdd.size());
        if (needSyncToRemote) {
            try {
                flushKey();
            } catch (Exception e) {
                logger.error("Store to Memcached ERROR", e);
                return false;
            }
        }
        return true;
    }

    /**
     * 将内存中的Key，存储到Memcached中去
     */
    public void flushKey() {
        logger.debug("---------- 开始将保存在客户端保存的Key，迁移到Memached中...");
        // long t1 = System.currentTimeMillis();
        Set<String> allStoedKey = getAllStoredKey();// 目前保存的所有Key
        // long t2 = System.currentTimeMillis();
        // logger.warn(nameSpace + "flushKey: getAllStoredKey() " + (t2 - t1));
        if (logger.isDebugEnabled()) {
            logger.warn(" Key采用二级索引保存,当前二级索引的长度是: " + getIndexSize());
            logger.warn(" 已经保存的Key个数是: " + allStoedKey.size());
            logger.warn(" 需要新增的key个数是: " + keyToAdd.size());
            logger.warn(" 需要删除的key个数是: " + keyToDel.size());
        }

        allStoedKey.removeAll(keyToDel);
        allStoedKey.addAll(keyToAdd);

        // long t3 = System.currentTimeMillis();
        // 写回去
        restoreToRemote(allStoedKey);
        // long t4 = System.currentTimeMillis();
        // logger.warn(nameSpace + "flushKey: restoreToRemote() " + (t4 - t3));
        // 清空
        keyToAdd.clear();
        keyToDel.clear();

        if (logger.isDebugEnabled()) {
            logger.debug("-------恭喜！所有的Key到已经保存到服务器端了，二级索引的长度是: " + getIndexSize() + ",总共Key的个数是: "
                         + getAllStoredKey().size());
        }
    }

    /**
     * @param allStoedKey 新的Key集合
     * @param oldIndexSize 旧的二级索引长度
     */
    private void restoreToRemote(Set<String> allStoedKey) {
        // long t1 = System.currentTimeMillis();
        // 然后将数据，重新分批存回去
        String[] allStoredKeyArray = allStoedKey.toArray(new String[0]);
        // long t2 = System.currentTimeMillis();
        // logger.warn(nameSpace + "restoreToRemote: toArray() " + (t2 - t1));

        // 批次
        int newBundleCount = calIndexSize(allStoredKeyArray);
        // 前N-1次的处理
        // long t3 = System.currentTimeMillis();
        for (int i = 0; i < newBundleCount - 1; i++) {
            //
            String[] newKeyArray = Arrays.copyOfRange(allStoredKeyArray, 0, key_bundle_size);
            String[] remainKeyArray = Arrays.copyOfRange(allStoredKeyArray, key_bundle_size, allStoredKeyArray.length);
            allStoredKeyArray = remainKeyArray;
            // 保存
            Set<String> newSet = new HashSet<String>(Arrays.asList(newKeyArray));
            store(createIndexKey(i), newSet);
        }
        // long t4 = System.currentTimeMillis();
        // logger.warn(nameSpace + "restoreToRemote: copyOfRange() " + newBundleCount + "    " + (t4 - t3));

        // 最好一次直接保存
        Set<String> newSet = new HashSet<String>(Arrays.asList(allStoredKeyArray));
        store(createIndexKey(newBundleCount - 1), newSet);

        // 检测是否需要删除部分Key
        int currentIndex = getIndexSize();
        if (newBundleCount < currentIndex) {
            for (int i = newBundleCount; i < currentIndex; i++) {
                String keyId = createIndexKey(i);
                deleteFromMemcached(keyId);
            }
        }
        // 更新Keysize
        store(memcached_key_second_index_size, newBundleCount);
    }

    /**
     * 耕具集合大小来计算二级索引的长度
     */
    private int calIndexSize(String[] allStoredKeyArray) {
        int newBundleCount = 1;
        if (allStoredKeyArray.length > key_bundle_size) {
            // 倍数
            int multiple = allStoredKeyArray.length / key_bundle_size;
            // 如果大于，说明有余数，需要再加一
            boolean plusOne = allStoredKeyArray.length > (multiple * key_bundle_size);
            if (plusOne) {
                newBundleCount = multiple + 1;
            } else {
                newBundleCount = multiple;
            }
        }
        return newBundleCount;
    }

    /**
     * 存储到Memcached
     */
    private void store(String key, Object keyset) {
        memcachedClient.set(key, never_expire, keyset);
    }

    /**
     * 从Memcached中删除
     */
    private void deleteFromMemcached(String key) {
        memcachedClient.delete(key);
    }

    /**
     * 从Memcached中取
     */
    private Object getFromMemcached(String key) {
        return memcachedClient.get(key);
    }

    /**
     * 当前二级索引的长度，默认是1
     */
    private int getIndexSize() {
        int size = 1;
        try {
            Object value = getFromMemcached(memcached_key_second_index_size);
            if (value == null) {
                store(memcached_key_second_index_size, size);
            } else {
                if (value instanceof Integer) {
                    size = (Integer) value;
                } else {
                    size = Integer.parseInt((String) value);
                }
            }
        } catch (Exception e) {
            logger.error("试图获取当前二级索引的长度出错！", e);
        }
        return size;
    }

    /**
     * 取得所有保存的Key
     */
    @SuppressWarnings("unchecked")
    public Set<String> getAllStoredKey() {
        int size = getIndexSize();
        String[] keyArray = new String[size];
        for (int i = 0; i < size; i++) {
            keyArray[i] = createIndexKey(i);
        }
        Set<String> allKeys = new HashSet<String>();
        try {
            for (String key : keyArray) {
                Set<String> storedkey = (Set<String>) getFromMemcached(key);
                if (storedkey != null) allKeys.addAll(storedkey);
            }
        } catch (Exception e) {
            logger.error("获取服务器端所有存下的Key集合出错", e);
        }
        return allKeys;
    }

    /**
     * 利用当前二级索引的位置，来组合出二级索引的KEY。类似于key_2r_0,key_2rd_1 .....
     * 
     * <pre>
     * 例如：如果当前二级索引有个4个,那么他们各自对应的KEY分别是key_2r_0,key_2rd_1,key_2r_2,key_2rd_3
     * </pre>
     * 
     * @param id 当前二级所有的位置
     */
    public String createIndexKey(int currentindex) {
        return String.format(memcached_key_format, currentindex);
    }
}
