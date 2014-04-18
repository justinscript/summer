/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.log;

import org.slf4j.Logger;
import org.slf4j.Marker;

/**
 * 日志的包装类
 * 
 * @author zxc Apr 12, 2013 1:32:21 PM
 */
public class ExpandLogger implements Logger {

    private Logger                 logger;
    private boolean                usedebug;

    private SqlLoggerHandler       sqlhandler;
    private ExceptionLoggerHandler exceptionhandler;

    public ExpandLogger(Logger logger) {
        this.logger = logger;
    }

    public ExpandLogger(Logger logger, SqlLoggerHandler sqlhandler, ExceptionLoggerHandler exceptionhandler) {
        this.logger = logger;
        this.sqlhandler = sqlhandler;
        this.exceptionhandler = exceptionhandler;
    }

    public String getName() {
        return logger.getName();
    }

    public boolean isTraceEnabled() {
        return logger.isTraceEnabled();
    }

    public void trace(String msg) {
        logger.trace(msg);
    }

    public void trace(String format, Object arg) {
        logger.trace(format, arg);
    }

    public void trace(String format, Object arg1, Object arg2) {
        logger.trace(format, arg1, arg2);
    }

    public void trace(String format, Object[] argArray) {
        logger.trace(format, argArray);
    }

    public void trace(String msg, Throwable t) {
        if (exceptionhandler != null) {
            recordThrowable("trace", msg, t);
        } else {
            logger.trace(msg, t);
        }
    }

    public boolean isTraceEnabled(Marker marker) {
        return logger.isTraceEnabled(marker);
    }

    public void trace(Marker marker, String msg) {
        logger.trace(marker, msg);
    }

    public void trace(Marker marker, String format, Object arg) {
        logger.trace(marker, format, arg);
    }

    public void trace(Marker marker, String format, Object arg1, Object arg2) {
        logger.trace(marker, format, arg1, arg2);
    }

    public void trace(Marker marker, String format, Object[] argArray) {
        logger.trace(marker, format, argArray);
    }

    public void trace(Marker marker, String msg, Throwable t) {
        if (exceptionhandler != null) {
            recordThrowable("trace", msg, t);
        } else {
            logger.trace(marker, msg, t);
        }
    }

    public boolean isDebugEnabled() {
        return logger.isDebugEnabled() || isUsedebug();
    }

    public void debug(String msg) {
        logger.debug(msg);
    }

    public void debug(String format, Object arg) {
        logger.debug(format, arg);
    }

    public void debug(String format, Object arg1, Object arg2) {
        logger.debug(format, arg1, arg2);
    }

    public void debug(String format, Object[] argArray) {
        logger.debug(format, argArray);
    }

    public void debug(String msg, Throwable t) {
        if (exceptionhandler != null) {
            recordThrowable("debug", msg, t);
        } else {
            logger.debug(msg, t);
        }
    }

    public boolean isDebugEnabled(Marker marker) {
        return logger.isDebugEnabled(marker);
    }

    public void debug(Marker marker, String msg) {
        logger.debug(marker, msg);
    }

    public void debug(Marker marker, String format, Object arg) {
        logger.debug(marker, format, arg);
    }

    public void debug(Marker marker, String format, Object arg1, Object arg2) {
        logger.debug(marker, format, arg1, arg2);
    }

    public void debug(Marker marker, String format, Object[] argArray) {
        logger.debug(marker, format, argArray);
    }

    public void debug(Marker marker, String msg, Throwable t) {
        if (exceptionhandler != null) {
            recordThrowable("debug", msg, t);
        } else {
            logger.debug(marker, msg, t);
        }
    }

    public boolean isInfoEnabled() {
        return logger.isInfoEnabled();
    }

    public void info(String msg) {
        logger.info(msg);
    }

    public void info(String format, Object arg) {
        logger.info(format, arg);
    }

    public void info(String format, Object arg1, Object arg2) {
        logger.info(format, arg1, arg2);
    }

    public void info(String format, Object[] argArray) {
        logger.info(format, argArray);
    }

    public void info(String msg, Throwable t) {
        if (exceptionhandler != null) {
            recordThrowable("info", msg, t);
        } else {
            logger.info(msg, t);
        }
    }

    public boolean isInfoEnabled(Marker marker) {
        return logger.isInfoEnabled(marker);
    }

    public void info(Marker marker, String msg) {
        logger.info(marker, msg);
    }

    public void info(Marker marker, String format, Object arg) {
        logger.info(marker, format, arg);
    }

    public void info(Marker marker, String format, Object arg1, Object arg2) {
        logger.info(marker, format, arg1, arg2);
    }

    public void info(Marker marker, String format, Object[] argArray) {
        logger.info(marker, format, argArray);
    }

    public void info(Marker marker, String msg, Throwable t) {
        if (exceptionhandler != null) {
            recordThrowable("info", msg, t);
        } else {
            logger.info(marker, msg, t);
        }
    }

    public boolean isWarnEnabled() {
        return logger.isWarnEnabled();
    }

    public void warn(String msg) {
        logger.warn(msg);
    }

    public void warn(String format, Object arg) {
        logger.warn(format, arg);
    }

    public void warn(String format, Object[] argArray) {
        logger.warn(format, argArray);
    }

    public void warn(String format, Object arg1, Object arg2) {
        logger.warn(format, arg1, arg2);
    }

    public void warn(String msg, Throwable t) {
        if (exceptionhandler != null) {
            recordThrowable("warn", msg, t);
        } else {
            logger.warn(msg, t);
        }
    }

    public boolean isWarnEnabled(Marker marker) {
        return logger.isWarnEnabled(marker);
    }

    public void warn(Marker marker, String msg) {
        logger.warn(marker, msg);
    }

    public void warn(Marker marker, String format, Object arg) {
        logger.warn(marker, format, arg);
    }

    public void warn(Marker marker, String format, Object arg1, Object arg2) {
        logger.warn(marker, format, arg1, arg2);
    }

    public void warn(Marker marker, String format, Object[] argArray) {
        logger.warn(marker, format, argArray);
    }

    public void warn(Marker marker, String msg, Throwable t) {
        if (exceptionhandler != null) {
            recordThrowable("warn", msg, t);
        } else {
            logger.warn(marker, msg, t);
        }
    }

    public boolean isErrorEnabled() {
        return logger.isErrorEnabled();
    }

    public void error(String msg) {
        logger.error(msg);
    }

    public void error(String format, Object arg) {
        logger.error(format, arg);
    }

    public void error(String format, Object arg1, Object arg2) {
        logger.error(format, arg1, arg2);
    }

    public void error(String format, Object[] argArray) {
        logger.error(format, argArray);
    }

    public void error(String msg, Throwable t) {
        if (exceptionhandler != null) {
            recordThrowable("error", msg, t);
        } else {
            logger.info(msg, t);
        }
    }

    public boolean isErrorEnabled(Marker marker) {
        return logger.isErrorEnabled(marker);
    }

    public void error(Marker marker, String msg) {
        logger.error(marker, msg);
    }

    public void error(Marker marker, String format, Object arg) {
        logger.error(marker, format, arg);
    }

    public void error(Marker marker, String format, Object arg1, Object arg2) {
        logger.error(marker, format, arg1, arg2);
    }

    public void error(Marker marker, String format, Object[] argArray) {
        logger.error(marker, format, argArray);
    }

    public void error(Marker marker, String msg, Throwable t) {
        if (exceptionhandler != null) {
            recordThrowable("error", msg, t);
        } else {
            logger.error(marker, msg, t);
        }
    }

    public boolean isUsedebug() {
        return usedebug;
    }

    public void setUsedebug(boolean usedebug) {
        this.usedebug = usedebug;
    }

    /**
     * 记录sql的方法
     * 
     * @param statement
     * @param sql
     * @param time
     */
    public void recordSQL(String statement, Object[] args, long time) {
        if (sqlhandler != null) {
            sqlhandler.record(statement, args, time);
        }
    }

    /**
     * 记录异常的方法
     * 
     * @param level
     * @param message
     * @param t
     */
    public void recordThrowable(String level, String message, Throwable t) {
        if (exceptionhandler != null) {
            exceptionhandler.record(level, message, t);
        }
    }
}
