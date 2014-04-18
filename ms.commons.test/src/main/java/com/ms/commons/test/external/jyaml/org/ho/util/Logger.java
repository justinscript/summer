/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.test.external.jyaml.org.ho.util;

/**
 * @author zxc Apr 13, 2013 11:40:28 PM
 */
public class Logger {

    public enum Level {
        INFO, WARN, NONE
    };

    Level level = Level.WARN;

    public Logger() {
    }

    public Logger(Level level) {
        this.level = level;
    }

    public void info(Object message) {
        if (level == Level.INFO) System.out.println("INFO: " + message);
    }

    public void warn(Object message) {
        if (level != Level.NONE) System.err.println("WARNING: " + message);
    }
}
