/*
 * Copyright 2017-2025 msun.com All right reserved. This software is the confidential and proprietary information of
 * msun.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with msun.com.
 */
package com.ms.commons.test.external.jyaml.org.ho.util;

import java.util.HashMap;
import java.util.Map;

/**
 * BiDirectionalMap provides a getReverse() method in addition to the HashMap to get a reverse mapping.
 * 
 * @param <K> the type of the keys
 * @param <V> the type of the values
 * @author zxc Apr 13, 2013 11:40:36 PM
 */
public class BiDirectionalMap<K, V> extends HashMap<K, V> {

    private static final long serialVersionUID = -4743484926693308477L;
    HashMap<V, K>             reverse          = new HashMap<V, K>();

    @Override
    public V put(K key, V value) {
        reverse.put(value, key);
        return super.put(key, value);
    }

    @Override
    public V remove(Object key) {
        V v = super.remove(key);
        reverse.remove(v);
        return v;
    }

    public Map<V, K> getReverse() {
        return reverse;
    }
}
