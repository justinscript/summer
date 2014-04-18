/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.test.mock.inject;

import java.lang.reflect.Proxy;

/**
 * @author zxc Apr 14, 2013 12:12:55 AM
 */
public class ObjectKey {

    private Object object;

    public ObjectKey(Object object) {
        this.object = object;
    }

    @Override
    public int hashCode() {
        if (object == null) {
            return 0;
        }
        if (object instanceof Proxy) {
            return 0;
        }
        return object.hashCode();
    }

    @Override
    public boolean equals(Object object) {
        if (object == null) {
            return false;
        }

        if (object.getClass() != ObjectKey.class) {
            return false;
        }
        ObjectKey another = (ObjectKey) object;
        if (this.object == another.object) {
            return true;
        }
        if (this.object == null) {
            return false;
        }
        if (this.object instanceof Proxy) {
            return false;
        }
        return this.object.equals(another.object);
    }
}
