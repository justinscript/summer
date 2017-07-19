/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.concurrent;

import java.io.Serializable;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

/**
 * @author zxc Apr 12, 2013 11:59:12 PM
 */
public class MapBackedSet<E> extends AbstractSet<E> implements Serializable {

    private static final long       serialVersionUID = -8347878570391674042L;

    protected final Map<E, Boolean> map;

    public MapBackedSet(Map<E, Boolean> map) {
        this.map = map;
    }

    public MapBackedSet(Map<E, Boolean> map, Collection<E> c) {
        this.map = map;
        addAll(c);
    }

    @Override
    public int size() {
        return map.size();
    }

    @Override
    public boolean contains(Object o) {
        return map.containsKey(o);
    }

    @Override
    public Iterator<E> iterator() {
        return map.keySet().iterator();
    }

    @Override
    public boolean add(E o) {
        return map.put(o, Boolean.TRUE) == null;
    }

    @Override
    public boolean remove(Object o) {
        return map.remove(o) != null;
    }

    @Override
    public void clear() {
        map.clear();
    }
}
