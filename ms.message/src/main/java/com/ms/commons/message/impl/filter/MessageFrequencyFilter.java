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
 * 一定时间内，允许发送的信息总个数
 * 
 * @author zxc Apr 13, 2014 10:45:20 PM
 */
public class MessageFrequencyFilter implements Filter {

    // 每分钟允许发送的信息的最大次数
    private int                         maxCount        = 5;

    // 日志记录器
    private static final ExpandLogger   logger          = LoggerFactoryWrapper.getLogger(MessageFrequencyFilter.class);
    /**
     * 每过clearDelayInSenconds时间，清空一次
     */
    private static Map<String, Integer> messageCountner = new ConcurrentHashMap<String, Integer>();

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
     * 控制一定时间内发送信息的总量 -- 不管发送的内容是否相同
     */
    public List<String> doFilter(Message message) {
        ArrayList<String> list = null;
        String[] receivers = message.getAllReceiver();
        if (receivers != null) {
            for (int i = 0; i < receivers.length; i++) {
                if (messageCountner.containsKey(receivers[i])) {
                    int count = messageCountner.get(receivers[i]) + 1;
                    messageCountner.put(receivers[i], count);
                    if (count >= maxCount) {
                        if (null == list) {
                            list = new ArrayList<String>();
                        }
                        list.add(receivers[i]);
                        if (logger.isDebugEnabled()) {
                            logger.info("信息接收者<" + receivers[i] + ">未能通过验证！");
                        }
                    }
                } else {
                    messageCountner.put(receivers[i], 1);
                }
            }
        }
        return list;
    }

    public int getMaxCount() {
        return maxCount;
    }
}
