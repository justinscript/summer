/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.log;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;

/**
 * @author zxc Apr 12, 2013 1:32:21 PM
 */
public class LoggerHelper {

    static String newline = System.getProperty("line.separator");

    private static String leftpad(int level, String msg) {
        String prefix = StringUtils.repeat("\t", level);
        String[] lines = StringUtils.split(msg, newline);
        for (int i = 0; i < lines.length; i++) {
            lines[i] = prefix + lines[i];
        }
        return StringUtils.join(lines, newline);
    }

    public static void error(Logger logger, int level, String msg) {
        logger.error(leftpad(level, msg));
    }

    public static void debug(Logger logger, int level, String msg) {
        logger.debug(leftpad(level, msg));
    }
}
