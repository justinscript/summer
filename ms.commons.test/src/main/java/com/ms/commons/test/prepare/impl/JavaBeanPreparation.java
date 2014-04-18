/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.test.prepare.impl;

import java.util.List;

import com.ms.commons.test.annotation.napi.Optional;
import com.ms.commons.test.annotation.napi.Required;
import com.ms.commons.test.annotation.napi.SupplyBy;
import com.ms.commons.test.memorydb.MemoryDatabase;
import com.ms.commons.test.prepare.Preparation;
import com.ms.commons.test.prepare.PrepareUtil;

/**
 * @author zxc Apr 14, 2013 12:22:11 AM
 */
public class JavaBeanPreparation implements Preparation {

    @Optional(supply = SupplyBy.Framework)
    private MemoryDatabase database;
    @Required
    private String         table;
    @Optional(supply = SupplyBy.None)
    int                    limit = Integer.MAX_VALUE;

    public JavaBeanPreparation database(MemoryDatabase database) {
        this.database = database;
        return this;
    }

    public JavaBeanPreparation table(String table) {
        this.table = table;
        return this;
    }

    public JavaBeanPreparation limit(int limit) {
        this.limit = limit;
        return this;
    }

    public <T> T prepare(Class<T> clazz) {
        return prepareList(clazz).get(0);
    }

    public <T> List<T> prepareList(Class<T> clazz) {
        return PrepareUtil.prepareObjectList(database, clazz, table, limit);
    }
}
