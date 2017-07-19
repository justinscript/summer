/*
 * Copyright 2017-2025 msun.com All right reserved. This software is the confidential and proprietary information of
 * msun.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with msun.com.
 */
package com.ms.commons.test.external.jyaml.org.ho.yaml.wrapper;

import java.awt.Point;

/**
 * @author zxc Apr 14, 2013 12:27:42 AM
 */
public class PointWrapper extends DelayedCreationBeanWrapper {

    @SuppressWarnings("rawtypes")
    public PointWrapper(Class type) {
        super(type);
    }

    public String[] getPropertyNames() {
        return new String[] { "x", "y" };
    }

    @Override
    protected Object createObject() {
        return new Point(((Number) values.get("x")).intValue(), ((Number) values.get("y")).intValue());
    }

    @Override
    public Object createPrototype() {
        return new Point();
    }
}
