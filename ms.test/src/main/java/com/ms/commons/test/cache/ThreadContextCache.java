/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.test.cache;

import java.util.HashMap;
import java.util.Map;

/**
 * @author zxc Apr 13, 2013 11:12:26 PM
 */
public class ThreadContextCache {

    protected static final ThreadLocal<Map<Object, Object>> resources = new ThreadLocal<Map<Object, Object>>() {

                                                                          protected Map<Object, Object> initialValue() {
                                                                              return new HashMap<Object, Object>();
                                                                          }

                                                                      };

    public static void clear() {
        resources.get().clear();
    }

    public static void put(Object key, Object value) {
        resources.get().put(key, value);
    }

    @SuppressWarnings("unchecked")
    public static <T> T get(Class<T> clazz, Object key) {
        return (T) resources.get().get(key);
    }

    public static Object get(Object key) {
        return resources.get().get(key);
    }
}
