/*
 * Copyright 2017-2025 msun.com All right reserved. This software is the confidential and proprietary information of
 * msun.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with msun.com.
 */
package com.ms.commons.message.impl.filter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.ms.commons.log.ExpandLogger;
import com.ms.commons.log.LoggerFactoryWrapper;
import com.ms.commons.message.interfaces.Filter;
import com.ms.commons.message.interfaces.Message;

/**
 * 一定时间段内(默认是1分钟)连续发送相同信息(默认是1个)的个数，该Filter不通过。
 * 
 * @author zxc Apr 13, 2014 10:44:51 PM
 */
public class MessageStormFilter implements Filter {

    // 每分钟允许发送的相同信息的最大次数
    private int                         maxCount        = 1;

    // 日志记录器
    private static final ExpandLogger   logger          = LoggerFactoryWrapper.getLogger(MessageStormFilter.class);
    /**
     * 每过clearDelayInSenconds时间，清空一次
     */
    private static Map<MsgKey, Integer> messageCountner = new ConcurrentHashMap<MsgKey, Integer>();

    static {
        // 每过一分钟清理一次
        ScheduledExecutorService threadpool = Executors.newSingleThreadScheduledExecutor();
        threadpool.scheduleWithFixedDelay(new Runnable() {

            public void run() {
                messageCountner.clear();
            }
        }, 60, 60, TimeUnit.SECONDS);
    }

    /**
     * 相同的信息内容1分钟只允许发送一次
     */
    public List<String> doFilter(Message message) {
        ArrayList<String> list = null;
        MsgKey[] msgKeys = message.identity();
        for (int i = 0; i < msgKeys.length; i++) {
            // System.out.println("hashcode=" + msgKeys[i].hashCode());
            // System.out.println("toString=" + msgKeys[i].toString());
            if (messageCountner.containsKey(msgKeys[i])) {
                int count = messageCountner.get(msgKeys[i]) + 1;
                messageCountner.put(msgKeys[i], count);
                if (count >= maxCount) {
                    if (null == list) {
                        list = new ArrayList<String>();
                    }
                    list.add(msgKeys[i].getTo());
                }
            } else {
                messageCountner.put(msgKeys[i], 1);
            }
        }
        // 把本次访问存下来
        if (list != null && list.size() > 0) {
            logger.info("MessageStoreFilter过滤掉了一个信息[" + list.size() + "]");
        } else {
            logger.info("MessageStoreFilter信息通过了过滤验证 [" + message.dumpInfo() + "]");
        }
        return list;
    }

    public int getMaxCount() {
        return maxCount;
    }
}
