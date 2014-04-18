/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.scaner;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 定时扫描
 * 
 * @author zxc Apr 12, 2013 1:37:11 PM
 */
public class TaskManager {

    Callback           callback;
    Map<Integer, Task> contianer    = new ConcurrentHashMap<Integer, Task>();
    private long       deplaySecondsInSencondes;
    private long       fixRateInSenconds;
    private int        core_threads = 3;

    private void init() {
        ScheduledExecutorService newScheduledThreadPool = Executors.newScheduledThreadPool(core_threads);
        newScheduledThreadPool.scheduleAtFixedRate(new Runnable() {

            @Override
            public void run() {
                try {
                    for (Integer key : contianer.keySet()) {
                        Task task = contianer.get(key);
                        doTask(task, false);
                    }
                } catch (Throwable e) {
                    // logger
                }
            }

        }, deplaySecondsInSencondes, fixRateInSenconds, TimeUnit.SECONDS);
    }

    private void doTask(Task task, boolean sync) {
        if (sync || task.getScanedCount() > 1) {
            callback.doTask(task);
            contianer.remove(task.getId());
        } else {
            task.increaseScanedCount();
        }
    }

    public TaskManager(long fixRateInSenconds, long deplaySecondsInSencondes, Callback callback) {
        this.fixRateInSenconds = fixRateInSenconds;
        this.deplaySecondsInSencondes = deplaySecondsInSencondes;
        this.callback = callback;
        init();
    }

    public TaskManager add(int taskId, boolean isSync) {
        Task task = addNewTask(new Task(taskId, null));
        if (isSync) {
            doTask(task, true);
        }
        return this;
    }

    // public TaskManager add(Task task) {
    // addNewTask(task);
    // return this;
    // }

    private Task addNewTask(Task task) {
        Task result = task;
        if (contianer.containsKey(task.getId())) {
            Task old = contianer.get(task.getId());
            old.add(task);
            result = old;
        } else {
            contianer.put(task.getId(), task);
        }
        return result;
    }

    public interface Callback {

        public void doTask(Task counter);

    }

    public static class Task implements Serializable {

        /**
         */
        private static final long serialVersionUID = 4051051069962650723L;
        private Integer           id;
        private int               scanedCount;                            // 扫描计数
        private int               count;                                  // 任务计数
        private List<Object>      data;

        public Task(Integer id, Object data) {
            this.id = id;
            this.data = new ArrayList<Object>();
            if (data != null) {
                this.data.add(data);
            }
            this.count = 1;
        }

        public void increaseScanedCount() {
            this.scanedCount++;
        }

        public Integer getId() {
            return id;
        }

        public void increaseCount() {
            this.count++;
        }

        public void add(Task counter) {
            this.data.addAll(counter.getData());
            this.count += counter.getCount();
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }

        public List<Object> getData() {
            return this.data;
        }

        public int getScanedCount() {
            return scanedCount;
        }

        public void setScanedCount(int scanedCount) {
            this.scanedCount = scanedCount;
        }

    }
}
