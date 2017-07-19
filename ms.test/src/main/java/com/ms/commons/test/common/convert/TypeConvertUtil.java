/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.test.common.convert;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.ms.commons.test.common.convert.exception.ConvertException;
import com.ms.commons.test.common.convert.impl.BigDecimalConverter;
import com.ms.commons.test.common.convert.impl.BooleanConverter;
import com.ms.commons.test.common.convert.impl.DateConverter;
import com.ms.commons.test.common.convert.impl.DoubleConverter;
import com.ms.commons.test.common.convert.impl.FloatConverter;
import com.ms.commons.test.common.convert.impl.IntegerConverter;
import com.ms.commons.test.common.convert.impl.LongConverter;
import com.ms.commons.test.common.convert.impl.StringConverter;
import com.ms.commons.test.common.convert.impl.TimestampConverter;

/**
 * @author zxc Apr 13, 2013 11:23:20 PM
 */
public class TypeConvertUtil {

    private static final Map<Class<?>, Converter<?>> convertMap = new HashMap<Class<?>, Converter<?>>();
    static {
        register(String.class, new StringConverter());
        register(int.class, new IntegerConverter());
        register(Integer.class, new IntegerConverter());
        register(long.class, new LongConverter());
        register(Long.class, new LongConverter());
        register(float.class, new FloatConverter());
        register(Float.class, new FloatConverter());
        register(double.class, new DoubleConverter());
        register(Double.class, new DoubleConverter());
        register(Date.class, new DateConverter());
        register(BigDecimal.class, new BigDecimalConverter());
        register(Timestamp.class, new TimestampConverter());
        register(boolean.class, new BooleanConverter());
        register(Boolean.class, new BooleanConverter());
        // register(Money.class, new MoneyConverter());
    }

    public static void register(Class<?> clazz, Converter<?> converter) {
        synchronized (convertMap) {
            convertMap.put(clazz, converter);
        }
    }

    public static Object convert(Class<?> clazz, Object value) {
        Converter<?> converter;
        synchronized (convertMap) {
            converter = convertMap.get(clazz);
        }
        if (converter == null) {
            throw new ConvertException("Converter for class `" + clazz.getName() + "` cannot be found.");
        }
        return converter.convert(value);
    }
}
