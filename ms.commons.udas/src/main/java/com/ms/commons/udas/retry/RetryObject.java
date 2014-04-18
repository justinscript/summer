/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.udas.retry;

import java.io.Serializable;

import com.ms.commons.udas.impl.UdasObj;

/**
 * 重试对象
 * 
 * @author zxc Apr 12, 2013 5:30:41 PM
 */
public class RetryObject extends UdasObj {

    public RetryObject(long createTime, Serializable value) {
        super(createTime, value);
    }

    private static final long serialVersionUID = 1L;
    private int               retryCount       = 1;

    public int getRetryCount() {
        return retryCount;
    }

    public void setRetryCount(int retryCount) {
        this.retryCount = retryCount;
    }

    /**
     * 再现有基础上将重试次数次数加1
     */
    public void incrRetryCount() {
        this.retryCount = retryCount + 1;
    }

    @Override
    public String toString() {
        return "RetryCount: " + retryCount + " CreateTime: " + getCreatTime() + " Value: " + getValue();
    }
}
