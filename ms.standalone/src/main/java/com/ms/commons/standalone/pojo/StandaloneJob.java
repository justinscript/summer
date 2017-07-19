/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.standalone.pojo;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

import org.apache.commons.lang.StringUtils;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;

import com.ms.commons.log.LoggerFactoryWrapper;

/**
 * @author zxc Apr 12, 2013 9:00:08 PM
 */
public class StandaloneJob implements Job {

    private static final Logger logger = LoggerFactoryWrapper.getLogger(StandaloneJob.class);

    public StandaloneJob() {
    }

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        JobDataMap dataMap = context.getMergedJobDataMap();
        String baseStandalonePath = dataMap.getString("baseStandalonePath");
        CronJob cronJob = (CronJob) dataMap.get("cronJob");
        try {
            modifyDataSourceProperties(cronJob.getFullClassName(), cronJob.getIdentity(), baseStandalonePath);
        } catch (Exception e1) {
            logger.warn("运行job程序时，修改msun.datasource.properties发生错误【" + cronJob.getIdentity() + "】", e1);
        }

        String cmd = String.format("/bin/bash %s/bin/jobRunner.sh start %s", baseStandalonePath,
                                   cronJob.getFullClassName());
        Runtime runtime = Runtime.getRuntime();
        Process process = null;
        try {
            process = runtime.exec(cmd);
            process.waitFor();
        } catch (Exception e) {
            logger.error("runtime.exec cmd: " + cmd + " failed");
            throw new JobExecutionException(e);
        } finally {
            if (process != null) {
                process.destroy();
            }
        }
    }

    /**
     * fullClassName 修改conf下的配置文件，解决不能同时运行多个job问题
     */
    private synchronized void modifyDataSourceProperties(String fullClassName, String identity,
                                                         String baseStandalonePath) throws Exception {
        if (StringUtils.contains(fullClassName, "msun")) {
            String filePath = baseStandalonePath + "/conf/msun.datasource.properties";
            String tmpFile = filePath + ".tmp";
            File file = new File(filePath);
            File tmp = new File(tmpFile);
            tmp.createNewFile();
            if (file.exists()) {
                BufferedReader buffRead = new BufferedReader(new FileReader(file));
                BufferedWriter write = new BufferedWriter(new FileWriter(tmp));
                String content = null;
                while ((content = buffRead.readLine()) != null) {
                    if (StringUtils.contains(content, "nisa.client.appname")) {
                        content = "nisa.client.appname=" + identity;
                    }
                    write.write(content);
                    write.newLine();
                }
                write.close();
                buffRead.close();
            }
            tmp.renameTo(file);
        }
    }
}
