/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.log;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ms.commons.cons.SystemEnum;
import com.ms.commons.request.RequestInfo;
import com.ms.commons.utilities.CoreUtilities;

/**
 * 处理异常的日志
 * 
 * @author zxc Apr 12, 2013 1:32:21 PM
 */
public class ExceptionLoggerHandler {

    private static Logger logger = LoggerFactory.getLogger(ExceptionLoggerHandler.class);
    private static String IP_APP = null;
    static {
        IP_APP = CoreUtilities.getIPAddress() + "," + CoreUtilities.getHostName();

        String tmpSystemAppName = System.getProperty(SystemEnum.JVM_WEB_APP_NAME.getValue());
        if (tmpSystemAppName != null) {
            IP_APP += "," + tmpSystemAppName;
        }
    }

    public void record(String level, String msg, Throwable t) {
        logger.error(IP_APP + msg + " RequestInfo:[" + RequestInfo.get() + "]", t);
    }

}
