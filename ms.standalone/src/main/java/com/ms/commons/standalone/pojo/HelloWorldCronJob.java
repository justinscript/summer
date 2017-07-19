/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.standalone.pojo;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;

import com.ms.commons.log.LoggerFactoryWrapper;

/**
 * @author zxc Apr 12, 2013 9:00:56 PM
 */
public class HelloWorldCronJob implements Job {

    private static final Logger logger = LoggerFactoryWrapper.getLogger(HelloWorldCronJob.class);

    @Override
    public void execute(JobExecutionContext arg0) throws JobExecutionException {
        logger.error("HelloWorldCronJob is running");
    }
}
