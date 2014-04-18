/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.db.ibatis;

import java.lang.reflect.Method;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import com.ms.commons.comset.filter.RecordEnum;
import com.ms.commons.comset.filter.ResourceTools;
import com.ms.commons.log.ExpandLogger;
import com.ms.commons.log.LoggerFactoryWrapper;

/**
 * @author zxc Apr 12, 2013 5:23:15 PM
 */
public class SqlMapClientInterceptor implements MethodInterceptor {

    private ExpandLogger logger   = LoggerFactoryWrapper.getLogger(SqlMapClientInterceptor.class);

    private Object       object;

    private Enhancer     enhancer = new Enhancer();

    /**
     * @param o
     * @param method
     * @param args
     * @param proxy
     * @return
     * @throws Throwable
     */
    public Object intercept(Object o, Method method, Object[] args, MethodProxy proxy) throws Throwable {
        Object result = null;

        long startTime = System.currentTimeMillis();
        //
        result = proxy.invoke(object, args);

        String statementName = "Unknown";
        if (args.length > 0 && args[0] instanceof String) {
            statementName = (String) args[0];
        }
        long useTime = System.currentTimeMillis() - startTime;
        // System.out.println("statementName=" + statementName + " time=" + useTime);
        ResourceTools.recordRunTime(RecordEnum.DB, statementName, useTime);

        try {
            logSql(statementName, args, useTime);
        } catch (Exception e) {
            logger.error("", e);
        }

        return result;
    }

    /**
     * @param clz
     * @param object
     * @return
     */
    public Object proxy(Object object) {
        this.object = object;
        enhancer.setSuperclass(object.getClass());
        enhancer.setCallback(this);
        return enhancer.create();
    }

    private boolean isRequireSyslog() {
        // if (SYSLOG.isSyslogOn()) {
        // if (SYSLOG.isEnableClass(SqlLogObject.class)) {
        // return true;
        // } else {
        // return false;
        // }
        // } else {
        // return false;
        // }
        return true;
    }

    private void logSql(String statement, Object[] args, long time) {
        if (!isRequireSyslog()) return;
        if (args == null || args.length <= 0) {
            return;
        }
        if (args[0] != null && args[0].getClass() == String.class) {
            if (args.length == 1) {
                // 没有参数
                logger.recordSQL(statement, null, time);
            } else {
                // 参数是从第二个开始的，第一个是statement
                Object[] dest = new Object[args.length - 1];
                System.arraycopy(args, 1, dest, 0, args.length - 1);
                logger.recordSQL(statement, dest, time);
            }
        }
    }
}
