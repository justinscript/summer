/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.test.datareader;

import com.ms.commons.test.memorydb.Data;

/**
 * @author zxc Apr 13, 2013 11:34:33 PM
 */
public abstract class AbstractDataReader implements DataReader {

    public void init() {
        // init here
    }

    public void destory() {
        // destory here
    }

    public Data read(String resourceName) {
        return internalRead(resourceName);
    }

    abstract protected Data internalRead(String resourceName);
}
