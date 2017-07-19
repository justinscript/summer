/*
 * Copyright 2017-2025 msun.com All right reserved. This software is the confidential and proprietary information of
 * msun.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with msun.com.
 */
package com.ms.commons.collection;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import com.ms.commons.lang.Argument;

/**
 * @author zxc Apr 12, 2013 2:34:08 PM
 */
public class Wrapper {

    /**
     * 对象数组变成HashSet,如果values是空的话，返回new HashSet<T>(0)
     */
    public static <T extends Object> java.util.HashSet<T> hashset(T... values) {
        if (Argument.isEmptyArray(values)) {
            return new HashSet<T>(0);
        }
        HashSet<T> result = new HashSet<T>(values.length);
        for (T value : values) {
            if (value != null) result.add(value);
        }
        return result;
    }

    /**
     * 对象数组变成List,如果values是空的话，返回new ArrayList<T>(0)
     */
    public static <T extends Object> java.util.List<T> collection(T... values) {
        if (Argument.isEmptyArray(values)) {
            return new ArrayList<T>(0);
        }
        List<T> result = new ArrayList<T>(values.length);
        for (T value : values) {
            if (value != null) result.add(value);
        }
        return result;
    }

    public static <T extends Object> T[] uniq(T[] values) {
        HashSet<T> hashset = hashset(values);
        return hashset.toArray(values);
    }
}
