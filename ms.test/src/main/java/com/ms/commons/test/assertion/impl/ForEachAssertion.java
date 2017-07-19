/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.test.assertion.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import junit.framework.Assert;

import com.ms.commons.test.assertion.Assertion;

/**
 * @author zxc Apr 13, 2013 11:13:44 PM
 */
public class ForEachAssertion<T> implements Assertion {

    private AtomicInteger index = new AtomicInteger(0);
    private List<T>       list  = new ArrayList<T>();

    public ForEachAssertion() {
    }

    public ForEachAssertion(List<T> valueList) {
        this.list.addAll(list);
    }

    public ForEachAssertion<T> add(T... values) {
        this.list.addAll(Arrays.asList(values));
        return this;
    }

    public ForEachAssertion<T> addOne(T value) {
        this.list.add(value);
        return this;
    }

    public ForEachAssertion<T> addAll(List<T> valueList) {
        this.list.addAll(valueList);
        return this;
    }

    public void doAssert(T value) {
        int ind = index.getAndIncrement();
        Assert.assertEquals("Compare index:" + ind, list.get(ind), value);
    }
}
