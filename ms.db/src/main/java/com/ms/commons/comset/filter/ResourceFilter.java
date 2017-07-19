/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.comset.filter;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import com.ms.commons.comset.filter.info.LeafInfo;
import com.ms.commons.utilities.EncodeUtils;

/**
 * @author zxc Apr 12, 2013 5:24:34 PM
 */
public class ResourceFilter implements Filter {

    public void init(FilterConfig arg0) throws ServletException {
        final Runnable updateThread = new Runnable() {

            public void run() {
                ResourceTools.clear(); // 30分钟清除一次数据
            }
        };
        final ScheduledExecutorService updateScheduler = Executors.newScheduledThreadPool(1);
        int period = 30;
        updateScheduler.scheduleAtFixedRate(updateThread, 1, period, TimeUnit.MINUTES);
    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain arg2) throws IOException,
                                                                                            ServletException {
        String uri = ((HttpServletRequest) request).getRequestURI();
        uri = EncodeUtils.decode(uri);
        if (!ResourceTools.isTrace() || !uri.endsWith(".htm")) {
            arg2.doFilter(request, response);
            return;
        }
        ThreadContextCache.clean();
        BaseThreadLocal.clean();
        long threadId = Thread.currentThread().getId();
        try {
            LeafInfo rootInfo = ResourceTools.getRootLeafInfo(threadId);
            rootInfo.setName(uri);
            // System.out.println(uri+" id="+threadId);
            long start = System.currentTimeMillis();
            arg2.doFilter(request, response);
            // 统计执行时间的问题（有异常的请求就不统计了)
            long period = (System.currentTimeMillis() - start);
            rootInfo.addRunTime(1, period);
            ResourceTools.complete(rootInfo);
            // System.out.println("URL="+uri+" time="+period+" hashcode="+rootInfo.hashCode()+
            // " count="+rootInfo.getCount()+" allTime="+rootInfo.getPeriod()+"  aver="+rootInfo.getAvg());

        } finally {
            // 最后整理该线程的数据
            // ResourceTools.complete(threadId,uri);
            // 执行一次清空操作
            ThreadContextCache.clean();
            BaseThreadLocal.clean();

        }

    }

    public void destroy() {
    }

    public void setCheck(boolean check) {
        this.check = check;
    }

    @SuppressWarnings("unused")
    private boolean check;
}
