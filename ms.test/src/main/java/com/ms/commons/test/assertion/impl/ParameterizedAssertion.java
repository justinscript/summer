/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.test.assertion.impl;

import java.lang.reflect.Method;

import org.apache.commons.lang.mutable.Mutable;
import org.apache.commons.lang.mutable.MutableObject;

import com.ms.commons.test.annotation.napi.Optional;
import com.ms.commons.test.annotation.napi.SupplyBy;
import com.ms.commons.test.assertion.Assertion;
import com.ms.commons.test.assertion.exception.AssertException;
import com.ms.commons.test.common.ReflectUtil;
import com.ms.commons.test.common.comparator.CompareUtil;
import com.ms.commons.test.common.convert.TypeConvertUtil;
import com.ms.commons.test.memorydb.MemoryDatabase;
import com.ms.commons.test.memorydb.MemoryField;
import com.ms.commons.test.memorydb.MemoryFieldType;
import com.ms.commons.test.memorydb.MemoryRow;
import com.ms.commons.test.memorydb.MemoryTable;

/**
 * @author zxc Apr 13, 2013 11:13:07 PM
 */
public class ParameterizedAssertion implements Assertion {

    @Optional(supply = SupplyBy.Framework)
    private MemoryDatabase database;
    @Optional(comment = "instance, clazz or method 任选其一即可")
    private Object         instance;
    @Optional(comment = "instance, clazz or method 任选其一即可")
    private Class<?>       clazz;
    @Optional(comment = "instance, clazz or method 任选其一即可, method 如果指定则不需要methodName")
    private Method         method;
    @Optional(comment = "默认为第一个table")
    private String         table;

    public ParameterizedAssertion database(MemoryDatabase database) {
        this.database = database;
        return this;
    }

    public ParameterizedAssertion table(String table) {
        this.table = table;
        return this;
    }

    public ParameterizedAssertion instance(Object instance) {
        this.instance = instance;
        return this;
    }

    public ParameterizedAssertion clazz(Class<?> clazz) {
        this.clazz = clazz;
        return this;
    }

    public ParameterizedAssertion method(Method method) {
        this.method = method;
        return this;
    }

    public void doAssert(String methodName) {
        if (table == null) {
            table = database.getTable(0).getName();
        }

        Object _instance = (instance instanceof Class<?>) ? null : instance;
        Class<?> _clazz = (clazz == null) ? ((instance instanceof Class<?>) ? (Class<?>) instance : ((instance == null) ? null : instance.getClass())) : clazz;
        Method _method = (method == null) ? ReflectUtil.getDeclaredMethod(_clazz, methodName) : method;

        MemoryTable memoryTable = database.getTable(table);

        for (MemoryRow memoryRow : memoryTable.getRowList()) {
            Mutable outParameters = new MutableObject();
            Object result = ReflectUtil.invokeMethodByMemoryRow(_instance, _method, memoryRow, outParameters);
            MemoryField field = memoryRow.getField(0);
            Object aspect = TypeConvertUtil.convert(_method.getReturnType(),
                                                    (field.getType() == MemoryFieldType.Null) ? null : field.getValue());

            if (!CompareUtil.isObjectEquals(aspect, result)) {
                throw new AssertException("Call parameterized assertion failed for: " + _clazz + "#" + methodName
                                          + " with parameters: " + outParameters.getValue() + " returns: " + result
                                          + " but aspects: " + aspect);
            }
        }
    }
}
