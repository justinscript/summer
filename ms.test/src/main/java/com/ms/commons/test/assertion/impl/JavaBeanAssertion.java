/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.test.assertion.impl;

import java.util.List;

import com.ms.commons.test.annotation.napi.Optional;
import com.ms.commons.test.annotation.napi.Required;
import com.ms.commons.test.annotation.napi.SupplyBy;
import com.ms.commons.test.assertion.Assert;
import com.ms.commons.test.assertion.Assertion;
import com.ms.commons.test.memorydb.MemoryDatabase;

/**
 * @author zxc Apr 13, 2013 11:13:35 PM
 */
public class JavaBeanAssertion implements Assertion {

    @Optional(supply = SupplyBy.Framework)
    private MemoryDatabase database;
    @Required
    private String         table;
    @Optional(supply = SupplyBy.None)
    private String[]       columns;

    public JavaBeanAssertion database(MemoryDatabase database) {
        this.database = database;
        return this;
    }

    public JavaBeanAssertion table(String table) {
        this.table = table;
        return this;
    }

    public JavaBeanAssertion columns(String... columns) {
        this.columns = columns;
        return this;
    }

    public void doAssert(Object object) {
        Assert.assertResult(database, object, table, 0);
    }

    public void doAssertList(List<?> list) {
        Assert.assertResultList(database, list, table, columns);
    }
}
