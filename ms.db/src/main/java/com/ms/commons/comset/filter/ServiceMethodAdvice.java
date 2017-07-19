/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.comset.filter;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

/**
 * @author zxc Apr 12, 2013 5:01:10 PM
 */
public class ServiceMethodAdvice implements MethodInterceptor {

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        long start = System.currentTimeMillis();
        Object invokeResult = invocation.proceed();
        long end = System.currentTimeMillis();
        String thisClassName = invocation.getThis().getClass().getSimpleName();
        if (thisClassName.indexOf("$$") == -1) {
            StringBuilder sb = new StringBuilder();
            sb.append(thisClassName).append(".").append(invocation.getMethod().getName()).append("()");
            ResourceTools.recordRunTime(RecordEnum.SERVICE, sb.toString(), end - start);
        }
        return invokeResult;
    }
}
