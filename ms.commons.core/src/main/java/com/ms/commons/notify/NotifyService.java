/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.notify;

import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.ms.commons.notify.NotifyUtils.MethodDescriptor;
import com.ms.commons.notify.event.Event;
import com.ms.commons.notify.event.EventType;
import com.ms.commons.result.Result;

/**
 * @author zxc Apr 12, 2013 2:57:20 PM
 */
public class NotifyService implements NotifyConstants {

    private static Map<EventType, Set<MethodDescriptor>> container = new ConcurrentHashMap<EventType, Set<MethodDescriptor>>();

    /**
     * 消息队列
     */
    private static Queue<Event>                          events    = new ConcurrentLinkedQueue<Event>();

    protected static int                                 deplaySeconds = 10, corePoolSize = 10;
    protected static int                                 fixRate       = 10;
    static {
        Executors.newScheduledThreadPool(corePoolSize).scheduleAtFixedRate(new Runnable() {

            @Override
            public void run() {
                try {
                    // logger.debug("poll the queue...");
                    fireEvent();
                } catch (Throwable e) {
                    logger.error(e.getMessage(), e);
                }
            }
        }, deplaySeconds, fixRate, TimeUnit.SECONDS);
    }

    /**
     * 注册一个异步事件监听器。系统会有一个定时线程扫描所关注的事件是否发生。 因为是异步所以不会关注监听器中异常和返回值，同时事务也无法保证。
     * 
     * <pre>
     * 
     * </pre>
     * 
     * @param listener 时间发生后的回调接口
     * @return 注册是否成功，如果失败result中会包含失败的原因。
     */
    public static Result regist(NotifyListener listener) {
        return NotifyUtils.getListenedEvent(container, listener);
    }

    /**
     * 添加一个事件
     */
    public static void notify(Event event) {
        if (logger.isDebugEnabled()) {
            logger.debug("Receive a event" + event.summary());
        }
        events.add(event);
    }

    protected static void fireEvent() {
        Event poll = events.poll();
        if (poll == null) {
            return;
        }
        if (logger.isDebugEnabled()) {
            logger.debug("fire a event" + poll.summary());
        }
        EventType eventType = poll.getEventType();
        if (eventType == null) {
            logger.warn("A ERROR Event Found!" + poll.summary());
            return;
        }

        // 将处理也提出让线程池执行
        Set<MethodDescriptor> listseners = container.get(eventType);
        if (listseners != null) {
            for (MethodDescriptor md : listseners) {
                try {
                    md.invoke(poll);
                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                }
            }
        }
    }

}
