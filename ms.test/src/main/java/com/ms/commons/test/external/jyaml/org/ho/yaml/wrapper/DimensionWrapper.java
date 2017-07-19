/*
 * Copyright 2017-2025 msun.com All right reserved. This software is the confidential and proprietary information of
 * msun.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with msun.com.
 */
package com.ms.commons.test.external.jyaml.org.ho.yaml.wrapper;

import java.awt.Dimension;

/**
 * @author zxc Apr 14, 2013 12:30:26 AM
 */
public class DimensionWrapper extends DelayedCreationBeanWrapper {

    @SuppressWarnings("rawtypes")
    public DimensionWrapper(Class type) {
        super(type);
    }

    public String[] getPropertyNames() {
        return new String[] { "width", "height" };
    }

    @Override
    protected Object createObject() {
        return new Dimension(((Number) values.get("width")).intValue(), ((Number) values.get("height")).intValue());
    }

    @Override
    public Object createPrototype() {
        return new Dimension();
    }
}
