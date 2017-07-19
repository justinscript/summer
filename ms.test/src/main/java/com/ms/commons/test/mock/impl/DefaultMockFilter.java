/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.test.mock.impl;

import com.ms.commons.test.mock.inject.MockFilter;

/**
 * @author zxc Apr 14, 2013 12:14:38 AM
 */
public class DefaultMockFilter implements MockFilter {

    public static final String DEFAULT_ALIBABA_PACKAGE = "com.ms";

    public boolean canMock(Class<?> clazz) {
        String clazzName = clazz.getName();
        if (clazzName.startsWith(DEFAULT_ALIBABA_PACKAGE)) {
            return true;
        }
        return false;
    }
}
