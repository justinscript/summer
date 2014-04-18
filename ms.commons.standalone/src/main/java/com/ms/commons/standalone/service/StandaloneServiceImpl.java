/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.standalone.service;

import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.quartz.Job;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleTrigger;
import org.quartz.Trigger;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;

import com.ms.commons.log.LoggerFactoryWrapper;
import com.ms.commons.standalone.cons.Cons;
import com.ms.commons.standalone.cons.CronJobStatus;
import com.ms.commons.standalone.pojo.CronJob;
import com.ms.commons.standalone.pojo.StandaloneJob;
import com.ms.commons.standalone.utils.Shell;

/**
 * @author zxc Apr 12, 2013 8:59:38 PM
 */
public class StandaloneServiceImpl implements StandaloneService {

    private static final Logger logger              = LoggerFactoryWrapper.getLogger(StandaloneServiceImpl.class);
    private static final String DEPLOY_SCRIPT       = "/msun/deploy/bin/deploy_standalone.sh";
    private static String       DEPLOY_SCRIPT_INNER = "";

    private AtomicBoolean       isRunning           = new AtomicBoolean(false);
    private String              baseStandalonePath  = null;
    private Scheduler           scheduler           = null;

    public String getBaseStandalonePath() {
        if (StringUtils.isEmpty(baseStandalonePath)) {
            baseStandalonePath = System.getProperty("user.home") + File.separator + Cons.DEFAULT_STANDALONE_DIR;
        } else if (!baseStandalonePath.startsWith("/")) {
            baseStandalonePath = System.getProperty("user.home") + File.separator + baseStandalonePath;
        }
        DEPLOY_SCRIPT_INNER = baseStandalonePath + "/bin/deploy_standalone.sh";
        return baseStandalonePath;
    }

    public void setBaseStandalonePath(String baseStandalonePath) {
        this.baseStandalonePath = baseStandalonePath;
    }

    private Map<String, CronJob> cronJobMap = new ConcurrentHashMap<String, CronJob>();

    @Override
    public synchronized boolean start() {
        if (isRunning.compareAndSet(false, true)) {
            load();
            try {
                scheduler = StdSchedulerFactory.getDefaultScheduler();
                ensureDirExists();
                scheduleAllJobs();
            } catch (SchedulerException e) {
                String errorMsg = "StandaloneServiceImpl.start() StdSchedulerFactory.getDefaultScheduler() failed";
                logger.error(errorMsg, e);
                throw new RuntimeException(errorMsg, e);
            }
            return true;
        }
        return false;
    }

    @Override
    public synchronized boolean stop() {
        if (isRunning.compareAndSet(true, false)) {
            save();
            try {
                stopAllJobs();
            } catch (SchedulerException e) {
                String errorMsg = "StandaloneServiceImpl.stop() stopAllJobs failed";
                logger.error(errorMsg, e);
                throw new RuntimeException(errorMsg, e);
            }
            return true;
        }
        return false;
    }

    @Override
    public synchronized boolean restart() {
        this.stop();
        return this.start();
    }

    @Override
    public synchronized boolean isRunning() {
        return isRunning.get();
    }

    @Override
    public boolean run(CronJob cronJob) throws SchedulerException {
        String triggerName = "run_once_job_trigger" + "_" + cronJob.getIdentity();
        Date startAtDate = DateUtils.addSeconds(new Date(), 5);
        String jobName = getJobName(cronJob.getIdentity());
        SimpleTrigger trigger = (SimpleTrigger) newTrigger().withIdentity(triggerName, Cons.GROUP_NAME).startAt(startAtDate).forJob(jobName,
                                                                                                                                    Cons.GROUP_NAME).build();
        if (cronJob.isStandalone()) {
            innerScheduleStandaloneJob(cronJob, trigger);
        } else {
            innerScheduleWithInJvmJob(cronJob, trigger);
        }
        return true;
    }

    // all logs manage
    @Override
    public String tailLog(String identity) {
        if (cronJobMap.containsKey(identity)) {
            return showLog(String.format("%s/logs/%s.log", baseStandalonePath,
                                         cronJobMap.get(identity).getFullClassName()), true);
        }
        return "";
    }

    @Override
    public String showLog(String logFilePath, boolean isTail) {
        if (StringUtils.isEmpty(logFilePath)) {
            return "";
        }
        if (!logFilePath.matches("^[a-zA-Z0-9-_\\./:]+$")) {
            return "";
        }
        // if (isTail) {
        // return Shell.exec("tail -c 4096 " + logFilePath);
        // }
        // return Shell.exec("head -c 4096 " + logFilePath);
        return getHeadLogFile(logFilePath, isTail, 524288);
    }

    @Override
    public String grepLog(String logFilePath, String pattern) {
        if (StringUtils.isEmpty(logFilePath) || StringUtils.isEmpty(pattern)) {
            return "";
        }
        if (!logFilePath.matches("^[a-zA-Z0-9-_\\./:]+$") || !pattern.matches("^[a-zA-Z0-9-_\\./: ]+$")) {
            return "";
        }
        // return Shell.exec(String.format("tail -c 16384 %s | grep -i -C 5 '%s'", logFilePath, pattern));
        return grepLogFile(logFilePath, pattern, 1048576);
    }

    private String getHeadLogFile(String logFilePath, boolean isTail, int lenth) {
        String content = null;
        File logFile = new File(logFilePath);
        FileInputStream fiStream = null;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            fiStream = new FileInputStream(logFile);
            if (isTail && logFile.length() > lenth) {
                fiStream.skip(logFile.length() - lenth);
            }
            byte[] buffer = new byte[lenth];
            long count = 0;
            int n = 0;
            while (-1 != (n = fiStream.read(buffer))) {
                baos.write(buffer, 0, n);
                count += n;
                if (count >= lenth) {
                    break;
                }
            }
            byte[] result = ArrayUtils.subarray(baos.toByteArray(), 0, lenth);
            content = new String(result, "utf-8");
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            return "" + e.getMessage();
        } finally {
            IOUtils.closeQuietly(fiStream);
            IOUtils.closeQuietly(baos);
        }
        return content;
    }

    private String grepLogFile(String logFilePath, String pattern, int lenth) {
        String content = getHeadLogFile(logFilePath, false, lenth);
        String[] lines = content.split("\n");
        int[] marks = new int[lines.length];
        Pattern p = Pattern.compile(String.format(".*%s.*", pattern), Pattern.CASE_INSENSITIVE);
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i];
            if (p.matcher(line).matches()) {
                for (int j = i - 5; j <= i + 5; j++) {
                    if (j >= 0 && j < marks.length) {
                        marks[j] = 1;
                    }
                }
            }
        }
        String matchLings = "";
        for (int i = 0; i < marks.length; i++) {
            if (marks[i] == 1) {
                matchLings += lines[i];
            }
        }
        return matchLings;
    }

    @Override
    public List<String> listAllLogs() {
        File logDir = new File(String.format("%s/logs", baseStandalonePath));
        List<String> allLogs = new ArrayList<String>();
        long currentTimeMillis = System.currentTimeMillis();
        if (logDir.exists()) {
            for (File logFile : logDir.listFiles()) {
                if (logFile.isFile() && logFile.canRead()
                    && currentTimeMillis - logFile.lastModified() < TimeUnit.DAYS.toMillis(7)) {
                    allLogs.add(logFile.getName());
                }
            }
        }
        Collections.sort(allLogs);
        return allLogs;
    }

    @Override
    public String ps() {
        return Shell.exec("/bin/ps aux|/bin/grep com.ms.commons.standalone.");
    }

    @Override
    public boolean clearOldLogs(String identity) {
        return false;
    }

    @Override
    public Date scheduleJob(JobDetail jobDetail, Trigger trigger) throws SchedulerException {
        return scheduler.scheduleJob(jobDetail, trigger);
    }

    @Override
    public boolean checkExists(JobKey jobKey) throws SchedulerException {
        return scheduler.checkExists(jobKey);
    }

    @Override
    public boolean updateCronJob(CronJob cronJob) throws SchedulerException {
        this.removeCronJob(cronJob.getIdentity());
        this.addCronJob(cronJob);
        save();
        return true;
    }

    @Override
    public boolean addCronJob(CronJob cronJob) throws SchedulerException {
        if (cronJobMap.containsKey(cronJob.getIdentity())) {
            return false;
        }
        if (StringUtils.isNotEmpty(cronJob.getCronExpression())
            && !StringUtils.equals(cronJob.getCronExpression(), "-")) {
            scheduleJob(cronJob);
        }
        cronJobMap.put(cronJob.getIdentity(), cronJob);
        save();
        return true;
    }

    @Override
    public boolean startCronJob(String identity) throws SchedulerException {
        if (cronJobMap.containsKey(identity) && cronJobMap.get(identity).getStatus().equals(CronJobStatus.STOP)) {
            scheduleJob(cronJobMap.get(identity));
        }
        save();
        return true;
    }

    @Override
    public boolean stopCronJob(String identity) throws SchedulerException {
        deleteJob(identity);
        if (cronJobMap.containsKey(identity)) {
            cronJobMap.get(identity).setStatus(CronJobStatus.STOP);
            if (cronJobMap.get(identity).isStandalone()) {
                stopStandaloneJob(cronJobMap.get(identity));
            }
        }
        save();
        return true;
    }

    @Override
    public boolean removeCronJob(String identity) throws SchedulerException {
        stopCronJob(identity);
        cronJobMap.remove(identity);
        save();
        return true;
    }

    @Override
    public List<CronJob> listCronJobs() {
        List<CronJob> cronJobList = new ArrayList<CronJob>();
        for (CronJob cronJob : cronJobMap.values()) {
            cronJob.setStatus(getCronJobStatus(cronJob.getIdentity()));
            cronJobList.add(cronJob);
        }
        Collections.sort(cronJobList, new Comparator<CronJob>() {

            @Override
            public int compare(CronJob o1, CronJob o2) {
                return o1.getIdentity().compareTo(o2.getIdentity());
            }

        });
        return cronJobList;
    }

    @Override
    public CronJob getCronJob(String identity) {
        return cronJobMap.get(identity);
    }

    @Override
    public boolean load() {
        return readCronJobMapFromXml();
    }

    @Override
    public boolean save() {
        return writeCronJobMapFromXml();
    }

    @Override
    public void deploy() {
        synchronized (this) {
            new Thread(new Runnable() {

                @Override
                public void run() {
                    String deployScript = String.format("/bin/bash %s %s", DEPLOY_SCRIPT, baseStandalonePath);
                    if (new File(DEPLOY_SCRIPT_INNER).exists()) {
                        deployScript = String.format("/bin/bash %s %s", DEPLOY_SCRIPT_INNER, baseStandalonePath);
                    }
                    if (new File(DEPLOY_SCRIPT_INNER).exists() || new File(DEPLOY_SCRIPT).exists()) {
                        String deployResult = Shell.exec(deployScript);
                        String deployResultFile = String.format("%s/deploy.result", baseStandalonePath);
                        FileOutputStream fileOutputStream = null;
                        try {
                            fileOutputStream = new FileOutputStream(deployResultFile);
                            IOUtils.write(deployResult, fileOutputStream);
                        } catch (IOException e) {
                            logger.error("deploy failed", e);
                        } finally {
                            IOUtils.closeQuietly(fileOutputStream);
                        }
                    }
                }
            }).start();
        }
    }

    @Override
    public String getDeployResult() {
        String deployResultPath = String.format("%s/deploy.result", baseStandalonePath);
        File deployResultFile = new File(deployResultPath);
        if (!deployResultFile.exists() || !deployResultFile.canRead()) {
            return StringUtils.EMPTY;
        }
        try {
            return IOUtils.toString(new FileInputStream(deployResultFile), "utf-8");
        } catch (IOException e) {
            logger.error("deploy failed", e);
        }
        return StringUtils.EMPTY;
    }

    // ============== private method ==============
    private void deleteJob(String identity) throws SchedulerException {
        try {
            scheduler.deleteJob(getJobKey(identity));
        } catch (SchedulerException e) {
            logger.error("deleteJob failed", e);
            throw e;
        }
    }

    private CronJobStatus getCronJobStatus(String identity) {
        try {
            Boolean isExists = scheduler.checkExists(getJobKey(identity));
            if (isExists) {
                return CronJobStatus.NORMAL;
            }
        } catch (SchedulerException e) {
            logger.error("scheduler.checkExists error", e);
        }
        return CronJobStatus.STOP;
    }

    private JobKey getJobKey(String identity) {
        return new JobKey(getJobName(identity), Cons.GROUP_NAME);
    }

    /**
     * <pre>
     * 目录树:
     * - standalone.xml
     * - job1
     * -- bin.sh
     * -- logs
     * -- 201101011111.log
     * - job2
     * ...
     * </pre>
     */
    private void ensureDirExists() {
        File baseStandalonePathFile = new File(baseStandalonePath);
        if (!baseStandalonePathFile.exists()) {
            baseStandalonePathFile.mkdirs();
        }
        File logDir = new File(baseStandalonePath + File.separator + "logs");
        if (!logDir.exists()) {
            logDir.mkdirs();
        }
    }

    private void stopAllJobs() throws SchedulerException {
        scheduler.shutdown(true);
        save();
    }

    private void scheduleAllJobs() throws SchedulerException {
        for (CronJob cronJob : cronJobMap.values()) {
            if (!cronJob.getStatus().equals(CronJobStatus.NORMAL)) {
                continue;
            }
            scheduleJob(cronJob);

        }
        scheduler.startDelayed(30);
    }

    private void scheduleJob(CronJob cronJob) throws SchedulerException {
        if (getCronJobStatus(cronJob.getIdentity()).equals(CronJobStatus.NORMAL)) {
            scheduler.resumeJob(getJobKey(cronJob.getIdentity()));
            return;
        }
        boolean scheduleSuccess = false;
        if (cronJob.isStandalone()) {
            if (scheduleStandaloneJob(cronJob)) {
                scheduleSuccess = true;
            }
        } else {
            if (scheduleWithInJvmJob(cronJob)) {
                scheduleSuccess = true;
            }
        }
        if (scheduleSuccess) {
            if (cronJobMap.containsKey(cronJob.getIdentity())) {
                cronJobMap.get(cronJob.getIdentity()).setStatus(CronJobStatus.NORMAL);
            } else {
                cronJobMap.put(cronJob.getIdentity(), cronJob);
            }
            return;
        }
        cronJobMap.get(cronJob.getIdentity()).setStatus(CronJobStatus.STOP);
    }

    private boolean scheduleStandaloneJob(CronJob cronJob) throws SchedulerException {
        String jobName = getJobName(cronJob.getIdentity());
        Trigger trigger = newTrigger().withIdentity(getTriggerName(cronJob.getIdentity())).withSchedule(cronSchedule(cronJob.getCronExpression())).forJob(jobName,
                                                                                                                                                          Cons.GROUP_NAME).build();
        return innerScheduleStandaloneJob(cronJob, trigger);
    }

    private boolean innerScheduleStandaloneJob(CronJob cronJob, Trigger trigger) throws SchedulerException {
        String jobName = getJobName(cronJob.getIdentity());
        JobDetail jobDetail = newJob(StandaloneJob.class).withIdentity(jobName, Cons.GROUP_NAME).build();
        jobDetail.getJobDataMap().put("baseStandalonePath", baseStandalonePath);
        jobDetail.getJobDataMap().put("cronJob", cronJob);
        scheduler.scheduleJob(jobDetail, trigger);
        return true;
    }

    private void stopStandaloneJob(CronJob cronJob) {
        String stopFlagFilePath = String.format("%s/conf/%s.stop.flag", baseStandalonePath, cronJob.getFullClassName());
        File stopFlagFile = new File(stopFlagFilePath);
        if (!stopFlagFile.exists()) {
            try {
                stopFlagFile.createNewFile();
            } catch (IOException e) {
                logger.error("stopStandaloneJob(): createNewFile " + stopFlagFilePath + " failed", e);
                throw new RuntimeException(e);
            }
        }

    }

    private boolean scheduleWithInJvmJob(CronJob cronJob) throws SchedulerException {
        String jobName = getJobName(cronJob.getIdentity());
        Trigger trigger = newTrigger().withIdentity(getTriggerName(cronJob.getIdentity())).withSchedule(cronSchedule(cronJob.getCronExpression())).forJob(jobName,
                                                                                                                                                          Cons.GROUP_NAME).build();
        return innerScheduleWithInJvmJob(cronJob, trigger);
    }

    private boolean innerScheduleWithInJvmJob(CronJob cronJob, Trigger trigger) throws SchedulerException {
        try {
            @SuppressWarnings("unchecked")
            Class<? extends Job> clazz = (Class<? extends Job>) Class.forName(cronJob.getFullClassName());
            String jobName = getJobName(cronJob.getIdentity());
            JobDetail job = newJob(clazz).withIdentity(jobName, Cons.GROUP_NAME).build();

            scheduler.scheduleJob(job, trigger);
            return true;
        } catch (ClassNotFoundException e) {
            logger.error("scheduleAllJobs(): wrong class name for schedule job", e);
        }
        return false;
    }

    private String getTriggerName(String identity) {
        return "trigger_" + identity;
    }

    private String getJobName(String identity) {
        return "job_" + identity;
    }

    private boolean readCronJobMapFromXml() {
        String configPath = baseStandalonePath + File.separator + Cons.STANDALONE_CONFIG;
        File configFile = new File(configPath);
        Map<String, CronJob> cronJobMapFromXml = new ConcurrentHashMap<String, CronJob>();
        if (configFile.exists()) {
            try {
                String configContent = IOUtils.toString(new FileInputStream(configFile), "utf-8").trim();
                SAXBuilder builder = new SAXBuilder();
                Document document = builder.build(new StringReader(configContent));
                Element rootNode = document.getRootElement();
                @SuppressWarnings("unchecked")
                List<Element> cronJobElementList = rootNode.getChildren("cronJob");
                for (Element element : cronJobElementList) {
                    CronJob cronJob = new CronJob();
                    cronJob.setIdentity(element.getChildText("identity"));
                    cronJob.setFullClassName(element.getChildText("fullClassName"));
                    cronJob.setJvmParameter(element.getChildText("jvmParameter"));
                    cronJob.setCronExpression(element.getChildText("cronExpression"));
                    cronJob.setStatus(CronJobStatus.valueOf(element.getChildText("status")));
                    cronJob.setStandalone(Boolean.valueOf(element.getChildText("isStandalone")));
                    cronJobMapFromXml.put(cronJob.getIdentity(), cronJob);
                }
                this.cronJobMap = cronJobMapFromXml;
                return true;
            } catch (Exception e) {
                logger.error("StandaloneServiceImpl.readCronJobMapFromXml() failed", e);
            }
        }
        return false;
    }

    private boolean writeCronJobMapFromXml() {
        String configPath = baseStandalonePath + File.separator + Cons.STANDALONE_CONFIG;
        List<Element> cronJobList = new ArrayList<Element>();
        for (CronJob cronJob : listCronJobs()) {
            Element identity = new Element("identity");
            identity.setText(cronJob.getIdentity());
            Element fullClassName = new Element("fullClassName");
            fullClassName.setText(cronJob.getFullClassName());
            Element jvmParameter = new Element("jvmParameter");
            jvmParameter.setText(cronJob.getJvmParameter());
            Element cronExpression = new Element("cronExpression");
            cronExpression.setText(cronJob.getCronExpression());
            Element status = new Element("status");
            status.setText(cronJob.getStatus().name());
            Element isStandalone = new Element("isStandalone");
            isStandalone.setText(String.valueOf(cronJob.isStandalone()));
            Element cronJobElement = new Element("cronJob");
            cronJobElement.addContent(identity);
            cronJobElement.addContent(fullClassName);
            cronJobElement.addContent(jvmParameter);
            cronJobElement.addContent(cronExpression);
            cronJobElement.addContent(status);
            cronJobElement.addContent(isStandalone);
            cronJobList.add(cronJobElement);
        }
        File configPathTmpFile = new File(configPath + ".tmp");
        Element root = new Element("standalone");
        root.addContent(cronJobList);
        XMLOutputter xMLOutputter = new XMLOutputter();
        xMLOutputter.setFormat(Format.getPrettyFormat());
        Document doc = new Document(root);
        StringWriter stringWriter = new StringWriter();
        try {
            xMLOutputter.output(doc, stringWriter);
            IOUtils.write(stringWriter.toString(), new FileOutputStream(configPathTmpFile), "utf-8");
            File configPathFile = new File(configPath);
            if (configPathFile.exists()) {
                configPathFile.delete();
            }
            FileUtils.moveFile(configPathTmpFile, configPathFile);
            return true;
        } catch (Exception e) {
            logger.error("StandaloneServiceImpl.writeCronJobMapFromXml() failed", e);
        }
        return false;
    }
}
