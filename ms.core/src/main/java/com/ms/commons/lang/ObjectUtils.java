/*
 * Copyright 2017-2025 msun.com All right reserved. This software is the confidential and proprietary information of
 * msun.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with msun.com.
 */
package com.ms.commons.lang;

import java.lang.reflect.Field;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;

import com.ms.commons.log.LoggerFactoryWrapper;

/**
 * @author zxc Apr 12, 2013 1:23:30 PM
 */
public class ObjectUtils {

    private static Logger log = LoggerFactoryWrapper.getLogger(ObjectUtils.class);

    /**
     * 把对象中的string数据类型进行一次trim操作
     * 
     * @param object
     * @throws Exception
     */
    public static void trim(Object object) {
        if (object == null) {
            return;
        }
        try {
            trimStringField(object, object.getClass());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    /**
     * 把对象中的string数据类型进行一次trim操作
     * 
     * @param object
     * @param parent 所有的父类是否需要trim
     * @throws Exception
     */
    public static void trim(Object object, boolean parentClass) {
        if (object == null) {
            return;
        }
        if (parentClass) {
            trim(object, null);
        } else {
            trim(object);
        }
    }

    /**
     * @param obj
     */
    public static void trim(Object obj, Class<?> stopClass) {
        if (obj == null) {
            return;
        }
        if (stopClass == null) {
            stopClass = Object.class;
        }
        Class<?> objClass = obj.getClass();
        boolean nextBreak = false;
        while (true) {
            try {
                trimStringField(obj, objClass);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                break;
            }
            if (nextBreak) {
                break;
            }
            objClass = objClass.getSuperclass();
            if (objClass == null || objClass == Object.class) {
                break;
            }
            if (objClass == stopClass) {
                nextBreak = true;
            }
        }
    }

    /**
     * 把对象中的string数据类型进行一次trim操作
     * 
     * @param object
     * @throws Exception
     */
    private static void trimStringField(Object object, Class<?> clazz) throws Exception {
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            if (field.getType() == String.class) {
                boolean isFoolback = false;
                if (field.isAccessible() == false) {
                    isFoolback = true;
                    field.setAccessible(true);
                }
                String value = (String) field.get(object);
                if (StringUtils.isNotEmpty(value)) {
                    value = value.trim();
                    field.set(object, value);
                }
                if (isFoolback) {
                    field.setAccessible(false);
                }
            }
        }
    }

}
