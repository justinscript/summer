/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.udas.impl.objinfo;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

/**
 * @author zxc Apr 12, 2013 5:34:18 PM
 */
public class UdasHashMap<K, V> extends HashMap<K, V> implements UdasObjectInfo {

    private static final long serialVersionUID = -1820127040540396053L;

    public UdasHashMap() {
    }

    public UdasHashMap(int initialCapacity) {
        super(initialCapacity);
    }

    public UdasHashMap(int initialCapacity, float loadFactor) {
        super(initialCapacity, loadFactor);
    }

    public UdasHashMap(Map<? extends K, ? extends V> m) {
        super(m);
    }

    public String toSimpleString() {
        Iterator<Entry<K, V>> i = entrySet().iterator();
        if (!i.hasNext()) {
            return "{}";
        }
        StringBuilder sb = new StringBuilder();
        sb.append('{');
        for (;;) {
            Entry<K, V> e = i.next();
            K key = e.getKey();
            V value = e.getValue();
            sb.append(key == this ? "(this Map)" : key);
            sb.append('=');
            if (value instanceof UdasObjectInfo) {
                sb.append(value == this ? "(this Map)" : ((UdasObjectInfo) value).toSimpleString());
            } else {
                sb.append(value == this ? "(this Map)" : value);
            }
            sb.append("\n");
            if (!i.hasNext()) {
                return sb.append('}').toString();
            }
            sb.append(", ");
        }
    }
}
