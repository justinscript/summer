/*
 * Copyright 2017-2025 msun.com All right reserved. This software is the confidential and proprietary information of
 * msun.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with msun.com.
 */
package com.ms.commons.test.common.convert;

import java.util.HashMap;
import java.util.Map;

import com.ms.commons.test.common.convert.impl.DateConverter;
import com.ms.commons.test.common.convert.impl.DoubleConverter;
import com.ms.commons.test.common.convert.impl.FloatConvert;
import com.ms.commons.test.common.convert.impl.IntegerConverter;
import com.ms.commons.test.common.convert.impl.LongConverter;
import com.ms.commons.test.common.convert.impl.StringConverter;
import com.ms.commons.test.common.convert.impl.TimestampConverter;
import com.ms.commons.test.database.type.FieldType;

/**
 * @author zxc Apr 13, 2013 11:23:30 PM
 */
public class DataBaseTypeConvertUtil {

    private static final Map<String, Converter<?>> convertMap = new HashMap<String, Converter<?>>();
    static {
        convertMap.put("VARCHAR", new StringConverter());
        convertMap.put("VARCHAR2", new StringConverter());
        convertMap.put("CHAR", new StringConverter());
        convertMap.put("DATE", new DateConverter());
        convertMap.put("NUMBER", new DoubleConverter());
        convertMap.put("DOUBLE", new DoubleConverter());
        convertMap.put("FLOAT", new FloatConvert());
        convertMap.put("TIMESTAMP", new TimestampConverter());
        convertMap.put("INT", new IntegerConverter());
        convertMap.put("INTEGER", new IntegerConverter());
        convertMap.put("DATETIME", new TimestampConverter());
        convertMap.put("BIGINT", new LongConverter());
        convertMap.put("INT UNSIGNED", new LongConverter());
    }

    public static void register(String type, Converter<?> converter) {
        synchronized (convertMap) {
            convertMap.put(type, converter);
        }
    }

    public static Object convert(FieldType type, Object value) {
        String fieldType = type.getType().toUpperCase();
        if ("NUMBER".equals(fieldType) && type.isFloat()) {
            fieldType = "DOUBLE";
        }
        Converter<?> converter;
        synchronized (convertMap) {
            converter = convertMap.get(fieldType);
        }
        if (converter == null) {
        }
        return converter.convert(value);
    }
}
