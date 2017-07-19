/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.standalone.job;

import java.io.File;
import java.io.IOException;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ms.commons.standalone.cons.Cons;
import com.ms.commons.standalone.cons.JobAction;

/**
 * @author zxc Apr 12, 2013 9:01:17 PM
 */
public class JobRunner {

    private static final Logger logger = LoggerFactory.getLogger(JobRunner.class);

    public static void main(String[] args) throws JobExecutionException {
        logger.error("__start of job, args is " + StringUtils.join(args, ", "));
        // try and catch everything
        try {
            checkArgs(args);
            JobAction jobAction = JobAction.valueOf(args[0].trim());
            String fullClassName = args[1].trim();
            String[] NewArgs = getNewArgs(args);

            AbstractJob job = getJobInstanceByClassName(fullClassName);
            job.setArgs(NewArgs);

            clearStandaloneStopFile();

            switch (jobAction) {
                case start:
                    job.execute(null);
                    break;
                case startNohup:
                    job.execute(null);
                    break;
                case startWithDebug:
                    job.execute(null);
                    break;
                case stop:
                    stopJob();
                    break;
                default:
                    throw new RuntimeException("jobAction must be start or stop");
            }
        } catch (Throwable t) {
            logger.error(t.getMessage(), t);
            t.printStackTrace();
        }
        logger.error("__end of job, args is " + StringUtils.join(args, ", "));
        System.exit(0);
    }

    private static void checkArgs(String[] args) {
        if (args == null || args.length < 2) {
            throw new RuntimeException("args must not null and args.length >= 2");
        }
        String action = args[0].trim();
        try {
            JobAction.valueOf(action);
        } catch (Exception e) {
            throw new RuntimeException("args[0] must be one of the com.ms.commons.standalone.cons.JobAction");
        }
    }

    private static String[] getNewArgs(String[] args) {
        String[] NewArgs = (String[]) ArrayUtils.subarray(args, 2, args.length + 1);
        return NewArgs;
    }

    private static AbstractJob getJobInstanceByClassName(String fullClassName) {
        try {
            Class<?> clazz = Class.forName(fullClassName);
            Object obj = clazz.newInstance();
            if (obj instanceof AbstractJob) {
                return (AbstractJob) obj;
            } else {
                String message = String.format("%s is not instanceof com.ms.commons.standalone.job.AbstractJob",
                                               fullClassName);
                throw new RuntimeException(message);
            }
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("can not find class name for: " + fullClassName, e);
        } catch (InstantiationException e) {
            throw new RuntimeException("can not newInstance() class name for: " + fullClassName, e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("IllegalAccessException class name for: " + fullClassName, e);
        }
    }

    private static void clearStandaloneStopFile() {
        String standaloneStopPath = System.getProperty(Cons.STANDALONE_STOP_FILE);
        File standaloneStopFile = new File(standaloneStopPath);
        if (standaloneStopFile.exists()) {
            standaloneStopFile.delete();
        }
    }

    private static void stopJob() {
        String standaloneStopPath = System.getProperty(Cons.STANDALONE_STOP_FILE);
        File standaloneStopFile = new File(standaloneStopPath);
        if (!standaloneStopFile.exists()) {
            try {
                standaloneStopFile.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException("create standalone stop file failed path: " + standaloneStopPath, e);
            }
        }
    }
}
