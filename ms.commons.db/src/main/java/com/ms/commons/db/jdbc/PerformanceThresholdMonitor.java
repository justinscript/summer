/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.db.jdbc;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import com.ms.commons.log.ExpandLogger;
import com.ms.commons.log.LoggerFactoryWrapper;

/**
 * @author zxc Apr 12, 2013 5:04:13 PM
 */
public class PerformanceThresholdMonitor implements MethodInterceptor {

    private ExpandLogger logger = LoggerFactoryWrapper.getLogger(PerformanceThresholdMonitor.class);

    private int          threshold;

    public PerformanceThresholdMonitor() {

    }

    /*
     * (non-Javadoc)
     * @see org.aopalliance.intercept.MethodInterceptor#invoke(org.aopalliance.intercept.MethodInvocation)
     */
    public Object invoke(MethodInvocation invocation) throws Throwable {
        long start = 0;
        long duration = 0;
        String method = invocation.getMethod().toString();

        try {
            start = System.currentTimeMillis();
            return invocation.proceed();
        } finally {
            duration = System.currentTimeMillis() - start;

            if (duration > threshold) {
                logger.info(method + " | " + duration);
            }

            // ServicePerfStatics.addDuration(invocation.getMethod().getName(),duration);
        }

    }

    public int getThreshold() {
        return threshold;
    }

    public void setThreshold(int threshold) {
        this.threshold = threshold;
    }
}
