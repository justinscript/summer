/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.test.external.jyaml.org.ho.yaml.wrapper;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.ms.commons.test.external.jyaml.org.ho.yaml.exception.PropertyAccessException;

/**
 * @author zxc Apr 14, 2013 12:30:37 AM
 */
@SuppressWarnings("rawtypes")
public abstract class DelayedCreationBeanWrapper extends DefaultBeanWrapper {

    protected Map<String, Object> values = new HashMap<String, Object>();

    protected HashSet<String>     keys;

    public DelayedCreationBeanWrapper(Class type) {
        super(type);
        keys = new HashSet<String>(Arrays.asList(getPropertyNames()));
    }

    @Override
    public void setProperty(String name, Object value) throws PropertyAccessException {
        values.put(name, value);
    }

    @Override
    public Object getProperty(String name) throws PropertyAccessException {
        if (values.containsKey(name)) return values.get(name);
        else return super.getProperty(name);
    }

    @Override
    public Set keys() {
        return keys;
    }

    public abstract String[] getPropertyNames();
}
