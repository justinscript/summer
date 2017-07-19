/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.scombiz.solr.utils;

import java.util.concurrent.atomic.AtomicLong;

/**
 * CounterMonitor 是一个简单的计数器监控，定义最近时间内计数超过设定值，然后提供检测接口
 * 
 * @author zxc Apr 12, 2013 9:33:35 PM
 */
public class CounterMonitor {

    private AtomicLong timeMillis          = new AtomicLong(-1);
    private AtomicLong triggeredTimeMillis = new AtomicLong(-1);
    private AtomicLong nowCount            = new AtomicLong(0);
    private long       count;
    private long       timeMillisWithIn;

    public boolean isExceedWithInForOnce() {
        adjust();
        if (nowCount.get() > count && triggeredTimeMillis.get() != timeMillis.get()) {
            triggeredTimeMillis.set(timeMillis.get());
            return true;
        }
        return false;
    }

    public boolean isExceed() {
        adjust();
        return nowCount.get() > count;
    }

    public void pulse() {
        adjust();
        nowCount.addAndGet(1);
    }

    private void adjust() {
        long now = System.currentTimeMillis();
        long lastTimeMillis = now - now % timeMillisWithIn;
        if (lastTimeMillis != timeMillis.get()) {
            timeMillis.set(lastTimeMillis);
            nowCount.set(0);
        }
    }

    public CounterMonitor(long count, long timeMillisWithIn) {
        this.timeMillis = new AtomicLong(System.currentTimeMillis());
        this.count = count;
        this.timeMillisWithIn = timeMillisWithIn;
    }

    public AtomicLong getTimeMillis() {
        return timeMillis;
    }

    public void setTimeMillis(AtomicLong timeMillis) {
        this.timeMillis = timeMillis;
    }

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }

    public long getTimeMillisWithIn() {
        return timeMillisWithIn;
    }

    public void setTimeMillisWithIn(long timeMillisWithIn) {
        this.timeMillisWithIn = timeMillisWithIn;
    }
}
