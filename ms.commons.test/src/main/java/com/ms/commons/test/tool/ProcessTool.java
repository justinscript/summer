/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.test.tool;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import com.ms.commons.test.common.ExceptionUtil;

/**
 * process tool
 * 
 * @author zxc Apr 13, 2013 11:42:35 PM
 */
public class ProcessTool {

    private List<String> command;
    private OutputStream output;

    public ProcessTool(List<String> command, OutputStream output) {
        this.command = command;
        this.output = output;
    }

    public int runCommand() {
        ProcessBuilder pb = new ProcessBuilder(command);
        try {
            Process antxEclipseProcess = pb.start();

            final InputStream is = antxEclipseProcess.getInputStream();
            (new Thread() {

                public void run() {
                    int b;
                    try {
                        while ((b = is.read()) != -1) {
                            output.write(b);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();

            int result = antxEclipseProcess.waitFor();

            Thread.sleep(100);
            output.flush();
            return result;
        } catch (Exception e) {
            throw ExceptionUtil.wrapToRuntimeException(e);
        }
    }
}
