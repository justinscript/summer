/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.test.external.jyaml.org.ho.yaml.wrapper;

import java.util.Collection;

/**
 * @author zxc Apr 14, 2013 12:30:15 AM
 */
@SuppressWarnings("rawtypes")
public interface MapWrapper extends ObjectWrapper {

    public Collection keys();

    public Object get(Object key);

    public void put(Object key, Object value);

    public Class getExpectedType(Object key);

    public boolean containsKey(Object key);
}
