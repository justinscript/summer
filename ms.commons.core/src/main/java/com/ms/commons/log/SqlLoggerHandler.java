/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.log;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thoughtworks.xstream.XStream;
import com.ms.commons.cons.SystemEnum;
import com.ms.commons.utilities.CoreUtilities;

/**
 * 日志工厂类，对LoggerFactory的包装
 * 
 * @author zxc Apr 12, 2013 1:32:21 PM
 */
public class SqlLoggerHandler {

    // 默认的慢sql时间
    private static long   slowtime = 1;
    private static Logger logger   = LoggerFactory.getLogger(SqlLoggerHandler.class);
    private static String IP_APP   = null;
    private XStream       xStream  = new XStream();
    static {
        IP_APP = CoreUtilities.getIPAddress() + "," + CoreUtilities.getHostName();

        String tmpSystemAppName = System.getProperty(SystemEnum.JVM_WEB_APP_NAME.getValue());
        if (tmpSystemAppName != null) {
            IP_APP += "," + tmpSystemAppName;
        }
    }

    public void record(String statement, Object[] args, long time) {
        // 如果是慢sql则记录完整的sql信息
        if (isSlowly(time)) {
            String sqlArgs = (args == null ? "null" : xStream.toXML(args));
            sqlArgs = IP_APP + " " + sqlArgs;
            logger.warn(String.format("%s|%s|%s", statement, time, sqlArgs));
        } else {
            // 不是慢sql，只记录statement及时间
            logger.debug(String.format("%s|%s|null", statement, time));
        }
    }

    private boolean isSlowly(long time) {
        return time > slowtime;
    }

    /**
     * 设置慢SQL的时间
     * 
     * @param time
     */
    public static void setSlowtime(long time) {
        slowtime = time;
    }

}
