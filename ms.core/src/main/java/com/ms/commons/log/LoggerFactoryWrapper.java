/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.log;

import java.util.HashMap;

import org.slf4j.LoggerFactory;

/**
 * 日志工厂类，对LoggerFactory的包装
 * 
 * @author zxc Apr 12, 2013 1:32:21 PM
 */
public class LoggerFactoryWrapper {

    // 处理sql日志
    private static SqlLoggerHandler                sqlhandler       = new SqlLoggerHandler();
    // 处理异常日志
    private static ExceptionLoggerHandler          exceptionhandler = new ExceptionLoggerHandler();

    private static HashMap<Class<?>, ExpandLogger> loggers          = new HashMap<Class<?>, ExpandLogger>();

    @SuppressWarnings("rawtypes")
    public static ExpandLogger getLogger(Class clazz) {
        ExpandLogger expandLogger = loggers.get(clazz);
        if (expandLogger == null) {
            expandLogger = new ExpandLogger(LoggerFactory.getLogger(clazz), sqlhandler, exceptionhandler);
            loggers.put(clazz, expandLogger);
        }
        return expandLogger;
    }

}
