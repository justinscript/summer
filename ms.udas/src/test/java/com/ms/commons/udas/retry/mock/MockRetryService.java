/*
 * Copyright 2017-2025 msun.com All right reserved. This software is the confidential and proprietary information of
 * msun.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with msun.com.
 */
package com.ms.commons.udas.retry.mock;

import com.ms.commons.udas.retry.RetryService;
import com.ms.commons.udas.retry.Retryable;

/**
 * @author zxc Apr 12, 2013 6:38:44 PM
 */
public class MockRetryService extends RetryService {

    public MockRetryService(Retryable retryable, String namespace) {
        super(retryable, namespace);
    }

    protected void createThreadPool() {
        // nothing to do
    }

    public void retryAllTasks() {
        super.retryAllTasks();
    }
}
