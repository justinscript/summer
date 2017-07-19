/*
 * Copyright 2017-2025 msun.com All right reserved. This software is the confidential and proprietary information of
 * msun.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with msun.com.
 */
package com.ms.commons.lang;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * @author zxc Apr 12, 2013 1:31:03 PM
 */
public class ArrayUtils {

    public static <T extends Object> String[] convert(T[] array) {
        if (Argument.isEmptyArray(array)) {
            return null;
        }
        String[] result = new String[array.length];
        for (int i = 0, j = result.length; i < j; i++) {
            result[i] = array[i].toString();
        }
        return result;
    }

    public static String[] removeBlankElement(String[] array) {
        if (Argument.isEmptyArray(array)) {
            return null;
        }
        List<String> list = new ArrayList<String>(Arrays.asList(array));
        Iterator<String> iterator = list.iterator();
        while (iterator.hasNext()) {
            if (Argument.isBlank(iterator.next())) {
                iterator.remove();
            }
        }
        if (list.isEmpty()) {
            return null;
        } else {
            return list.toArray(new String[0]);
        }
    }

    @SuppressWarnings("unchecked")
    public static <E> E[] removeNullElement(E[] array) {
        if (Argument.isEmptyArray(array)) {
            return null;
        }

        int notNullValueCount = array.length;
        for (int i = 0, j = array.length; i < j; i++) {
            if (array[i] == null) {
                notNullValueCount--;
            }
        }
        if (notNullValueCount == 0) {
            return null;
        }
        E[] newInstance = (E[]) Array.newInstance(array.getClass().getComponentType(), notNullValueCount);

        for (int i = 0, j = 0; i < array.length; i++) {
            if (array[i] != null) {
                newInstance[j++] = array[i];
            }
        }
        return newInstance;
    }
}
