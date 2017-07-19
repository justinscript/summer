/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.test.tool;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import com.ms.commons.test.common.ExceptionUtil;
import com.ms.commons.test.common.task.Task;
import com.ms.commons.test.tool.util.AutoDeleteProjectTaskUtil;
import com.ms.commons.test.tool.util.AutoImportProjectTaskUtil;

/**
 * @author zxc Apr 13, 2013 11:43:05 PM
 */
public class GenerateAntxEclipse {

    public static void main(String[] args) {
        String userDir = System.getProperty("user.dir");
        File project = new File(userDir + File.separator + "project.xml");
        if (!project.exists()) {
            System.err.println("File '" + project + "' not found!");
            System.exit(-1);
        }

        Task task = new Task() {

            public void finish() {
                runAntxEclipse();
            }
        };

        // auto delete
        task = AutoDeleteProjectTaskUtil.wrapAutoDeleteTask(project, task);

        // auto import
        task = AutoImportProjectTaskUtil.wrapAutoImportTask(project, task);

        task.finish();
    }

    private static void runAntxEclipse() {
        ProcessBuilder pb = new ProcessBuilder("antx", "eclipse");
        try {
            Process antxEclipseProcess = pb.start();

            final InputStream is = antxEclipseProcess.getInputStream();
            (new Thread() {

                public void run() {
                    int b;
                    try {
                        while ((b = is.read()) != -1) {
                            System.out.write(b);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();

            antxEclipseProcess.waitFor();
        } catch (Exception e) {
            throw ExceptionUtil.wrapToRuntimeException(e);
        }
    }
}
