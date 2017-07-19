/*
 * Copyright 2017-2025 msun.com All right reserved. This software is the confidential and proprietary information of
 * msun.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with msun.com.
 */
package com.ms.commons.test.external.jyaml.org.ho.yaml.wrapper;

import java.util.Map;
import java.util.Set;

/**
 * @author zxc Apr 14, 2013 12:30:57 AM
 */
@SuppressWarnings("rawtypes")
public class DefaultMapWrapper extends AbstractWrapper implements MapWrapper {

    public DefaultMapWrapper(Class type) {
        super(type);
    }

    protected Map getMap() {
        return (Map) getObject();
    }

    public boolean containsKey(Object key) {
        return getMap().containsKey(key);
    }

    public Object get(Object key) {
        return getMap().get(key);
    }

    @SuppressWarnings("unchecked")
    public void put(Object key, Object value) {
        getMap().put(key, value);
    }

    public Class getExpectedType(Object key) {
        return null;
    }

    public Set keys() {
        return getMap().keySet();
    }
}
