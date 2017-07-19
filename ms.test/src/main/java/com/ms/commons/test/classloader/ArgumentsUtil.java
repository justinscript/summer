/*
 * Copyright 2017-2025 msun.com All right reserved. This software is the confidential and proprietary information of
 * msun.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with msun.com.
 */
package com.ms.commons.test.classloader;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zxc Apr 13, 2013 11:07:35 PM
 */
public class ArgumentsUtil {

    public static String[] processArgs(String[] args) {
        if (args == null) {
            return null;
        }

        List<String> argList = new ArrayList<String>();
        for (String arg : args) {
            if (arg.startsWith("-D")) {
                int indexOfEQ = arg.indexOf('=');
                if (indexOfEQ > 2) {
                    String k = arg.substring(2, indexOfEQ);
                    String v = arg.substring(indexOfEQ + 1);
                    System.err.println("Add property: key=\"" + k + "\", value=\"" + v + "\"");
                    System.setProperty(k, v);
                    continue;
                }
            }
            argList.add(arg);
        }
        return argList.toArray(new String[0]);
    }
}
