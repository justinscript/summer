/*
 * Copyright 2017-2025 msun.com All right reserved. This software is the confidential and proprietary information of
 * msun.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with msun.com.
 */
package com.ms.commons.test.common.comparator;

import java.math.BigDecimal;
import java.util.Hashtable;
import java.util.Map;

import com.ms.commons.test.common.comparator.impl.BigDecimalComparator;
import com.ms.commons.test.common.comparator.impl.DoubleComparator;
import com.ms.commons.test.common.comparator.impl.FloatComparator;

/**
 * @author zxc Apr 13, 2013 11:26:27 PM
 */
public class CompareUtil {

    private static final Map<Class<?>, Comparator<?>> comparatorMap = new Hashtable<Class<?>, Comparator<?>>();
    static {
        // register(UTF8String.class, new UTF8StringComparator());
        // register(ChineseString.class, new ChineseStringComparator());
        register(Float.class, new FloatComparator());
        register(Double.class, new DoubleComparator());
        register(BigDecimal.class, new BigDecimalComparator());
        // register(ActionTrace.class, new ActionTraceComparator());
    }

    public static <T> void register(Class<T> clazz, Comparator<T> comparator) {
        synchronized (comparatorMap) {
            comparatorMap.put(clazz, comparator);
        }
    }

    // MY GOD: why ChineseString not implements equals(Object) method
    @SuppressWarnings("unchecked")
    public static boolean isObjectEquals(Object obj0, Object obj1) {
        if (obj0 == null) {
            return (obj1 == null);
        } else {

            if (obj1 == null) {
                // obj0 is not null, but obj1 is null
                return false;
            }

            // the object class
            Class<?> clazz = obj0.getClass();

            if (clazz != obj1.getClass()) {
                // class not match
                return false;
            }

            if (obj0 == obj1) {
                // same object
                return true;
            }

            // other wise we user comparator to compare
            Comparator<?> comparator;
            synchronized (comparatorMap) {
                comparator = comparatorMap.get(clazz);
            }

            if (comparator != null) {
                return ((Comparator<Object>) comparator).compare(obj0, obj1);
            }

            return obj0.equals(obj1);
        }
    }
}
