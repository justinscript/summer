/*
 * Copyright 2017-2025 msun.com All right reserved. This software is the confidential and proprietary information of
 * msun.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with msun.com.
 */
package com.ms.commons.standalone.job;

import java.io.File;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.lang.StringUtils;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;

import com.ms.commons.standalone.cons.Cons;

/**
 * <pre>
 * AbstractJob 有两个地方需要关注
 * 1 String[] args 这是外界传递回来的参数，在脚本里面 ./jobRunner.sh start|stop com.ms.commons.standalone.msun.job.HelloWorldStandalone args0 args1
 *   这种情况下 args = [args0, args1]
 * 2 isNeedStop() 通过文件来标识是否退出，对于独立jvm的standalone，外界交互通过文件来通讯。
 *   **如果你在execute(JobExecutionContext arg0)里面不主动调用isNeedStop()，程序不能安全退出，只能等待到运行结束。**
 *   对于长时间运行的后台程序，需要添加安全退出机制，示例代码如下：
 *   public void execute(JobExecutionContext arg0) {
 *      for(int i = 0; i < total_page; i++) {
 *          if(isNeedStop()) {
 *              // do something and exit
 *          }
 *          List list = getListByPage(i);
 *          do_list(list);
 *      }
 *   }
 * </pre>
 * 
 * @author zxc Apr 12, 2013 9:01:32 PM
 */
public abstract class AbstractJob implements Job {

    private AtomicBoolean needStop = new AtomicBoolean(false);
    protected String[]    args     = null;

    public String[] getArgs() {
        return args;
    }

    public void setArgs(String[] args) {
        this.args = args;
    }

    public AbstractJob() {
    }

    public abstract void execute(JobExecutionContext arg0) throws JobExecutionException;

    public boolean isNeedStop() {
        if (needStop.get()) {
            return true;
        }
        String standaloneStopPath = System.getProperty(Cons.STANDALONE_STOP_FILE);
        if (StringUtils.isNotBlank(standaloneStopPath)) {
            File standaloneStopFile = new File(standaloneStopPath);
            if (standaloneStopFile.exists()) {
                standaloneStopFile.delete();
                needStop.set(true);
                return true;
            }
        }
        return false;
    }

    public void setStop() {
        needStop.set(true);
    }

    public boolean sleep(long time) {
        if (time > 0) {
            try {
                Thread.sleep(time);
                return true;
            } catch (InterruptedException e) {
            }
        }
        return false;
    }

    public static boolean isOnlyNum(String str) {
        return str.matches("\\d+");
    }

    /**
     * 用来打印消耗的时间
     * 
     * @param mLogger
     * @param level
     * @param msg
     * @param start
     * @return
     */
    public static Long printTimeCost(Logger mLogger, int level, String msg, Long start) {
        Long end;
        // --------------------------------------------------------
        // ++++++++++++++++++++性能跟踪调试语句 -- begin
        // --------------------------------------------------------

        end = System.currentTimeMillis();
        String indent = "";
        for (int i = 0; i < level; i++) {
            indent += '\t';
        }
        mLogger.error(indent + msg + (end - start) / 1000f + "秒");
        return end;
        // --------------------------------------------------------
        // ++++++++++++++++++++性能跟踪调试语句 -- end
        // --------------------------------------------------------
    }
}
