/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.test.runtime;

import com.ms.commons.test.runtime.util.RuntimeBuilder;

/**
 * @author zxc Apr 13, 2013 11:43:30 PM
 */
public class RuntimeUtil {

    private static final Object     runtimeLock = new Object();
    volatile private static Runtime runtime;

    public static Runtime getRuntime() {
        if (runtime == null) {
            synchronized (runtimeLock) {
                if (runtime == null) {
                    runtime = RuntimeBuilder.build();
                }
            }
        }
        return runtime;
    }
}
