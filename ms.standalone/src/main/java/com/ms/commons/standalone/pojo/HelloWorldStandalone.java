/*
 * Copyright 2017-2025 msun.com All right reserved. This software is the confidential and proprietary information of
 * msun.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with msun.com.
 */
package com.ms.commons.standalone.pojo;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;

import com.ms.commons.log.LoggerFactoryWrapper;
import com.ms.commons.standalone.job.AbstractJob;

/**
 * @author zxc Apr 12, 2013 9:00:45 PM
 */
public class HelloWorldStandalone extends AbstractJob {

    private static final Logger logger = LoggerFactoryWrapper.getLogger(HelloWorldStandalone.class);

    @Override
    public void execute(JobExecutionContext arg0) throws JobExecutionException {
        logger.error("HelloWorldStandalone is running");
    }
}
