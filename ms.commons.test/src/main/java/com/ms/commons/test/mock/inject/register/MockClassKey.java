/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.test.mock.inject.register;

import com.ms.commons.test.common.comparator.CompareUtil;

/**
 * @author zxc Apr 14, 2013 12:13:33 AM
 */
public class MockClassKey {

    private Object clazz;

    private Object method;

    public MockClassKey(Object clazz, Object method) {
        this.clazz = clazz;
        this.method = method;
    }

    public Object getClazz() {
        return clazz;
    }

    public Object getMethod() {
        return method;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        if (clazz != null) {
            hash = 31 * hash + clazz.hashCode();
        }
        if (method != null) {
            hash = 31 * hash + method.hashCode();
        }

        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if ((object == null) || (object.getClass() != MockClassKey.class)) {
            return false;
        }
        MockClassKey another = (MockClassKey) object;
        if (!CompareUtil.isObjectEquals(clazz, another.clazz)) {
            return false;
        }
        if (!CompareUtil.isObjectEquals(method, another.method)) {
            return false;
        }
        return true;
    }
}
