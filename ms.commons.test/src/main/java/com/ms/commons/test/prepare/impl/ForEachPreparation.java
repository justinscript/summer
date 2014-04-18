/*
 * Copyright 2011-2016 ZXC.com All right import java.util.ArrayList; import java.util.Arrays; import java.util.List;
 * import java.util.concurrent.atomic.AtomicInteger; import com.ms.commons.test.prepare.Preparation; ll use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.test.prepare.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import com.ms.commons.test.prepare.Preparation;

/**
 * @author zxc Apr 14, 2013 12:22:19 AM
 */
public class ForEachPreparation<T> implements Preparation {

    private AtomicInteger index = new AtomicInteger(0);
    private List<T>       list  = new ArrayList<T>();

    public ForEachPreparation() {
    }

    public ForEachPreparation(List<T> valueList) {
        this.list.addAll(list);
    }

    public ForEachPreparation<T> add(T... values) {
        this.list.addAll(Arrays.asList(values));
        return this;
    }

    public ForEachPreparation<T> addOne(T value) {
        this.list.add(value);
        return this;
    }

    public ForEachPreparation<T> addAll(List<T> valueList) {
        this.list.addAll(valueList);
        return this;
    }

    public T prepare() {
        return list.get(index.getAndIncrement());
    }

    // //////////////////////////////////////////////////////////
    public T prepareNoIncrement() {
        return list.get(index.get());
    }

    public int getCount() {
        return list.size();
    }

    public boolean hasMore() {
        return (list.size() > index.get());
    }

    public void doIncrement() {
        index.incrementAndGet();
    }
}
