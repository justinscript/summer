/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.test.common;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.List;

/**
 * @author zxc Apr 13, 2013 11:20:11 PM
 */
public class CollectionUtil {

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static <T> List<T> distinceList(List<T> l) {
        return new ArrayList(new LinkedHashSet(l));
    }

    public static <T> List<T> joinList(List<T>... lists) {
        List<T> list = new ArrayList<T>();
        if (lists != null) {
            for (List<T> l : lists) {
                list.addAll(l);
            }
        }
        return list;
    }

    public static <T> Enumeration<T> toEnumeration(final Collection<T> collection) {
        return new Enumeration<T>() {

            Object[] array = collection.toArray();
            int      index = 0;

            public boolean hasMoreElements() {
                return (index < array.length);
            }

            @SuppressWarnings("unchecked")
            public T nextElement() {
                return (T) array[index++];
            }
        };
    }

    public static <T> List<T> joinEnumerationsToList(Enumeration<T>... enumerations) {
        ArrayList<T> list = new ArrayList<T>();
        for (int i = 0; i < enumerations.length; i++) {
            list.addAll(toCollection(ArrayList.class, enumerations[i]));
        }
        return list;
    }

    @SuppressWarnings("unchecked")
    public static <T extends Collection<K>, K> T toCollection(Class<?> clazz, Enumeration<K> enumeration) {
        if (clazz == null) {
            throw new RuntimeException("Class cannot be null.");
        }
        try {
            T collection = (T) clazz.newInstance();

            if (enumeration != null) {
                while (enumeration.hasMoreElements()) {
                    collection.add(enumeration.nextElement());
                }
            }

            return collection;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
