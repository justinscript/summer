/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.test.assertion.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import junit.framework.Assert;

import com.ms.commons.test.assertion.Assertion;
import com.ms.commons.test.common.ParamResult;

/**
 * @author zxc Apr 13, 2013 11:13:26 PM
 */
public class LoopAssertion implements Assertion {

    private List<ParamResult> list = new ArrayList<ParamResult>();

    public LoopAssertion() {
    }

    public LoopAssertion(List<ParamResult> valueList) {
        this.list.addAll(list);
    }

    public LoopAssertion add(ParamResult... values) {
        this.list.addAll(Arrays.asList(values));
        return this;
    }

    public LoopAssertion addOne(ParamResult value) {
        this.list.add(value);
        return this;
    }

    public LoopAssertion addAll(List<ParamResult> valueList) {
        this.list.addAll(valueList);
        return this;
    }

    public void doAssert(AssertCallBack assertCallBack) {
        for (int i = 0; i < list.size(); i++) {
            ParamResult r = list.get(i);
            Object result = assertCallBack.call(r.getParams());

            Assert.assertEquals("Asserting index:" + i, r.getResult(), result);
        }
    }
}
