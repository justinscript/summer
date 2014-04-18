/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.summer.web.handler;

import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.lang.StringUtils;
import org.springframework.validation.FieldError;

/**
 * @author zxc Apr 12, 2013 4:10:53 PM
 */
public class ObjectArrayDataBinder {

    private Class<?>      clazz;
    private String        objectName;
    private FieldError    fieldError;
    private BeanUtilsBean instance;

    public ObjectArrayDataBinder(Class<?> clazz, String objectName) {
        this.clazz = clazz;
        this.objectName = objectName;
    }

    @SuppressWarnings("unchecked")
    public <E> E[] bind(HttpServletRequest request) {
        Map<String, String[]> paramAndValues = new HashMap<String, String[]>();
        int size = initParamAndValues(request, objectName, paramAndValues);
        if (size <= 0) {
            return null;
        }
        E[] result = (E[]) createArray(clazz, size);
        initBeanUtilBean();
        for (Iterator<Entry<String, String[]>> it = paramAndValues.entrySet().iterator(); it.hasNext();) {
            Entry<String, String[]> entry = it.next();
            String name = entry.getKey();
            String[] values = entry.getValue();
            for (int i = 0; i < size; i++) {
                E object = result[i];
                setValue(object, name, values[i]);
            }
        }
        return result;
    }

    private void initBeanUtilBean() {
        SimpleDateFormat sdf1 = new SimpleDateFormat(SummerServletRequestDataBinder.FULL_DATE_FORMAT_PATTERN);
        SimpleDateFormat sdf2 = new SimpleDateFormat(SummerServletRequestDataBinder.SIMPLE_DATE_FORMAT_PATTERN);
        DateConver dateConver = new DateConver(sdf1, sdf2);
        instance = BeanUtilsBean.getInstance();
        instance.getConvertUtils().register(dateConver, Date.class);
    }

    @SuppressWarnings("unchecked")
    private int initParamAndValues(HttpServletRequest request, String name, Map<String, String[]> paramAndValues) {
        int size = -1;
        boolean checkPrefix = false;
        if (StringUtils.isNotEmpty(name)) {
            checkPrefix = true;
        }
        for (Enumeration<String> parameterNames = request.getParameterNames(); parameterNames.hasMoreElements();) {
            String parameterName = parameterNames.nextElement();
            if (checkPrefix && parameterName.startsWith(name + ".")) {
                String[] values = request.getParameterValues(parameterName);
                if (values != null && values.length > 0) {
                    paramAndValues.put(parameterName.substring(name.length() + 1), values);
                    if (size == -1) {
                        size = values.length;
                    } else if (size != values.length) {
                        fieldError = new FieldError(name, name, "数组长度不一致");
                        return -1;
                    }
                }
            }
        }
        return size;
    }

    /**
     * @return the fieldError
     */
    public FieldError getFieldError() {
        return fieldError;
    }

    @SuppressWarnings("unchecked")
    public static <E> E[] createArray(Class<E> clazz, int capacity) {
        E[] array = (E[]) Array.newInstance(clazz, capacity);
        for (int i = 0; i < capacity; i++) {
            try {
                array[i] = clazz.newInstance();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return array;
    }

    private void setValue(Object object, String name, String value) {
        try {
            instance.setProperty(object, name, value);
        } catch (Exception e) {
        }
    }

    public static void main(String[] args) {
        Class<?> clazz = String.class;
        String[] createArray = (String[]) createArray(clazz, 10);
        System.out.println(createArray.length);
    }
}
