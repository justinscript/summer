/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.test.common;

import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringUtils;

/**
 * @author zxc Apr 13, 2013 11:19:12 PM
 */
public class OutputUtil {

    private static long         started;

    private static Method       inmethod;

    private static NumberFormat format = new DecimalFormat("#,##0");

    private static List<String> cache  = new ArrayList<String>();

    synchronized public static void enter(Method method) {
        inmethod = method;
        started = System.currentTimeMillis();
        cache.add("-+-- enter method " + method.getDeclaringClass().getName() + "#" + method.getName());
    }

    synchronized public static void exit() {
        if (inmethod != null) {
            long cost = System.currentTimeMillis() - started;
            cache.add(" `-- exit method " + inmethod.getName() + " cost " + format.format(cost) + " ms");
            cache.add("");
            inmethod = null;
        }
    }

    synchronized public static void append(String line) {
        cache.add(" |   " + line);
    }

    public static void append(Object object) {
        append((object == null) ? "null" : object.toString());
    }

    public static void append(Object... objects) {
        append((objects == null) ? "null" : Arrays.asList(objects).toString());
    }

    public static void appendLines(String lines) {
        if (lines == null) {
            append("null");
        }
        String[] spliedLines = lines.split("(\r\n)|(\r)|(\n)");
        for (String line : spliedLines) {
            append(line);
        }
    }

    synchronized public void clear() {
        exit();
        cache = new ArrayList<String>();
    }

    synchronized public static String dump() {
        return StringUtils.join(cache, "\n");
    }

    public static void stdErr() {
        System.err.println(dump());
    }

    public static void stdOut() {
        System.out.println(dump());
    }
}
