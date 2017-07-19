/*
 * Copyright 2017-2025 msun.com All right reserved. This software is the confidential and proprietary information of
 * msun.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with msun.com.
 */
package com.ms.commons.concurrent;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author zxc Apr 12, 2013 11:56:54 PM
 */
public class ConcurrentHashSet<E> extends MapBackedSet<E> {

    private static final long serialVersionUID = 8518578988740277828L;

    public ConcurrentHashSet() {
        super(new ConcurrentHashMap<E, Boolean>());
    }

    public ConcurrentHashSet(Collection<E> c) {
        super(new ConcurrentHashMap<E, Boolean>(), c);
    }

    @Override
    public boolean add(E o) {
        Boolean answer = ((ConcurrentMap<E, Boolean>) map).putIfAbsent(o, Boolean.TRUE);
        return answer == null;
    }
}
