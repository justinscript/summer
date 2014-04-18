/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */package com.ms.commons.test.common.convert;

import java.lang.reflect.ParameterizedType;

/**
 * @author zxc Apr 13, 2013 11:23:51 PM
 */
public abstract class AbstractConverter<T> implements Converter<T> {

    @SuppressWarnings("unchecked")
    public final T convert(Object value) {
        if (value == null) {
            return null;
        } else {
            Class<?> classT = (Class<?>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
            if (value.getClass() == classT) {
                return (T) value;
            }
            try {
                return internalConvert(value);
            } catch (Exception e) {
                throw new RuntimeException("Error occured when convert `" + value + "` to type `" + classT.getName()
                                           + "`.", e);
            }
        }
    }

    abstract protected T internalConvert(Object value);
}
