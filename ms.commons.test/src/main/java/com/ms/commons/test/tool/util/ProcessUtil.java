/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.test.tool.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import com.ms.commons.test.common.ExceptionUtil;

/**
 * @author zxc Apr 14, 2013 12:18:37 AM
 */
public class ProcessUtil {

    public static String getProcessOutput(ProcessBuilder processBuilder) {
        try {
            Process p = processBuilder.start();
            final InputStream is = p.getInputStream();
            final ByteArrayOutputStream baos = new ByteArrayOutputStream();

            Thread it = new Thread() {

                public void run() {
                    int b;
                    try {
                        while ((b = is.read()) != -1) {
                            baos.write(b);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            };
            it.start();
            p.waitFor();

            Thread.sleep(500);
            if (it.isAlive()) {
                it.interrupt();
            }

            return new String(baos.toByteArray(), "UTF-8");
        } catch (Exception e) {
            throw ExceptionUtil.wrapToRuntimeException(e);
        }
    }
}
