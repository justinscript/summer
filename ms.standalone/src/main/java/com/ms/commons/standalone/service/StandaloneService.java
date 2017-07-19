/*
 * Copyright 2017-2025 msun.com All right reserved. This software is the confidential and proprietary information of
 * msun.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with msun.com.
 */
package com.ms.commons.standalone.service;

import java.util.Date;
import java.util.List;

import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.SchedulerException;
import org.quartz.Trigger;

import com.ms.commons.core.interfaces.StatusServices;
import com.ms.commons.standalone.pojo.CronJob;

/**
 * @author zxc Apr 12, 2013 8:59:56 PM
 */
public interface StandaloneService extends StatusServices {

    // run job now
    boolean run(CronJob cronJob) throws SchedulerException;

    // log
    String tailLog(String identity);

    String showLog(String logFilePath, boolean isTail);

    String grepLog(String logFilePath, String pattern);

    List<String> listAllLogs();

    String ps();

    boolean clearOldLogs(String identity);

    // cronJob modify
    boolean updateCronJob(CronJob cronJob) throws SchedulerException;

    boolean addCronJob(CronJob cronJob) throws SchedulerException;

    boolean startCronJob(String identity) throws SchedulerException;

    boolean stopCronJob(String identity) throws SchedulerException;

    boolean removeCronJob(String identity) throws SchedulerException;

    // read only method
    List<CronJob> listCronJobs();

    CronJob getCronJob(String identity);

    // load from xml and persistent to xml
    boolean load();

    boolean save();

    void deploy();

    String getDeployResult();

    // re cron all jobs, using start stop and restart method from StatusServices

    // 底层的 scheduleJob 调度，需要非常谨慎的调用，注意jobname的命名空间
    Date scheduleJob(JobDetail jobDetail, Trigger trigger) throws SchedulerException;

    boolean checkExists(JobKey jobKey) throws SchedulerException;
}
