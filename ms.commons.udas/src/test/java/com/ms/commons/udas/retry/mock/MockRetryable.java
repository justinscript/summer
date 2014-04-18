/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.udas.retry.mock;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ms.commons.udas.retry.RetryObject;
import com.ms.commons.udas.retry.Retryable;

/**
 * @author zxc Apr 12, 2013 6:38:59 PM
 */
public class MockRetryable implements Retryable {

    private Map<String, RetryObject> container = new HashMap<String, RetryObject>();
    private static final Logger      logger    = LoggerFactory.getLogger(Retryable.class);
    private RuntimeException         needExceptionAfterRetry;
    private RuntimeException         exceptionBeforeRetry;

    @Override
    public void retry(String key, RetryObject retryObject) {
        logger.debug("UDAS接收到一个重试数据: key [" + key + "] value [" + retryObject + " ] 开始保存....");
        if (exceptionBeforeRetry != null) {
            logger.debug("但是要模拟异常,所以开始报异常了...");
            throw exceptionBeforeRetry;
        }

        logger.debug("UDAS保存前的集合： " + container);
        container.put(key, retryObject);
        logger.debug("UDAS保存过后的集合： " + container);
        logger.debug("---保存结束---");
        if (needExceptionAfterRetry != null) {
            logger.debug("保存完成后，还需要扔出一个异常来！");
            throw needExceptionAfterRetry;
        }
    }

    @Override
    public boolean needRetry(String key, RetryObject retryObject) {
        logger.debug("UDAS的needRetry方法被调用,Mock对象，该方法总是返回True！");
        return true;
    }

    public RetryObject get(String key) {
        return container.get(key);
    }

    public void clear() {
        logger.debug("UDAS的数据被清空...");
        container.clear();
    }

    @Override
    public String toString() {
        return container.toString();
    }
}
