/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.test.common;

import java.util.Collections;
import java.util.List;

/**
 * @author zxc Apr 13, 2013 11:17:58 PM
 */
public class SortUtil {

    public static void sortList(List<?> list, final String sortField, final boolean asc) {
        Collections.sort(list, new java.util.Comparator<Object>() {

            @SuppressWarnings({ "unchecked", "rawtypes" })
            public int compare(Object o1, Object o2) {
                if (o1 == null) {
                    if (o2 == null) {
                        return 0;
                    } else {
                        return (asc ? -1 : 1);
                    }
                } else {
                    if (o2 == null) {
                        return (asc ? -1 : 1);
                    } else {
                        Object vo1 = ReflectUtil.getObject(o1, sortField);
                        Object vo2 = ReflectUtil.getObject(o2, sortField);
                        if ((vo1 instanceof Comparable<?>) && (vo2 instanceof Comparable<?>)) {
                            return ((Comparable) vo1).compareTo((Comparable) vo2) * (asc ? 1 : -1);
                        } else {
                            return vo1.hashCode() - vo2.hashCode() * (asc ? 1 : -1);
                        }
                    }
                }
            }
        });
    }
}
