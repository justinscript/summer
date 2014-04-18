/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.standalone.utils;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;

import com.ms.commons.log.LoggerFactoryWrapper;

/**
 * <pre>
 *  在java里面调用shell不是一个好主意，尽量减少此方法的调用，
 *  此外要非常小心返回结果太大，比如直接cat一个2G的日志，这样会把内存耗光， 
 *  总之，如果能使用java实现，不要执行shell调用，谢谢你了。
 *  另外，要注意shell注入的安全危险，这个非常重要！！！
 * </pre>
 * 
 * @author zxc Apr 12, 2013 8:58:38 PM
 */
public class Shell {

    private static final Logger logger = LoggerFactoryWrapper.getLogger(Shell.class);

    public static String exec(String cmd) {
        Process process = null;
        String[] cmds = { "/bin/bash", "-c", cmd, };
        try {
            process = new ProcessBuilder(cmds).redirectErrorStream(true).start();
            byte[] buffer = IOUtils.toByteArray(process.getInputStream());
            process.waitFor();
            return new String(buffer, "utf-8");
        } catch (Exception e) {
            logger.error("runtime.exec cmd: " + cmd + " failed", e);
        } finally {
            if (process != null) {
                process.destroy();
            }
        }
        return "";
    }

    public static void main(String[] argv) {
        System.out.println(exec("ls /home"));
    }
}
