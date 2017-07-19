/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.udas.retry;

/**
 * 可以重试的任务,当{@link RetryService}执行重试时回调该接口中的方法。 它的作用是就是回调，当任务被重试时被回调。
 * 
 * <pre>
 * RetryService大致的回调逻辑是:
 *  RetryService.onRetry(){
 *      if(needRetry(key,value)){
 *          retry(key,value)
 *       }
 *  }
 * 
 * </pre>
 * 
 * @author zxc Apr 12, 2013 5:30:57 PM
 */
public interface Retryable {

    /**
     * 重试方法。 当之前一条失败的任务再次被执行时执行该方法。
     * 
     * <pre>
     * 实现该方法被调用场景：一条任务执行出错了，任务被{@link RetryService #addRetryTask(String, RetryObject)}记录下来了，
     * 然后再某个时刻，该任务需要回到上次执行的上下文再次执行。此时回调用该方法
     * </pre>
     * 
     * @param key 重试的任务的Key
     * @param retryObject 一条需要重试的任务
     */
    void retry(String key, RetryObject retryObject);

    /**
     * 更具当前上下文，判断一个重试任务是否需要被真正重试执行。 </pre>
     * 
     * @param key 重试的任务的Key
     * @param retryObject 一条需要重试的任务
     * @return true，那么该<code>RetryObject</code>会被传给 {@link Retryable#retry(String, RetryObject)} 执行
     */
    boolean needRetry(String key, RetryObject retryObject);
}
