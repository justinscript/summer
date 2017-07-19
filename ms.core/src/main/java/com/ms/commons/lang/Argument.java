/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.lang;

import java.util.Collection;

import org.apache.commons.lang.StringUtils;

/**
 * 对入参数进行判断
 * 
 * @author zxc Apr 12, 2013 1:29:40 PM
 */
public class Argument {

    public static boolean isPositive(Integer argument) {
        return argument != null && argument > 0;
    }

    public static boolean isPositive(Number argument) {
        if (argument == null) {
            return false;
        }
        return argument.floatValue() > 0f || argument.intValue() > 0;
    }

    public static boolean isNull(Object argument) {
        return argument == null;
    }

    public static boolean isBlank(String argument) {
        return StringUtils.isBlank(argument);
    }

    @SuppressWarnings("rawtypes")
    public static boolean isEmpty(Collection argument) {
        return isNull(argument) || argument.isEmpty();
    }

    public static boolean isNotNull(Object argument) {
        return argument != null;
    }

    /**
     * 判断一个集合部位空
     */
    @SuppressWarnings("rawtypes")
    public static boolean isNotEmpty(Collection argument) {
        return !isEmpty(argument);
    }

    /**
     * 判断一个数组不为空
     */
    public static boolean isNotEmptyArray(Object[] array) {
        return !isEmptyArray(array);
    }

    /**
     * 判断时一个空数组（null或者length为0）
     */
    public static boolean isEmptyArray(Object[] array) {
        return isNull(array) || array.length == 0;
    }

    public static boolean isNotBlank(String argument) {
        return StringUtils.isNotBlank(argument);
    }

    /**
     * 2个Integer是否相等 <br>
     * Two null references are considered to be equal
     * 
     * @param num1
     * @param num2
     * @return
     */
    public static boolean integerEqual(Integer num1, Integer num2) {
        return num1 == null ? num2 == null : num1.equals(num2);
    }

    public static void main(String[] args) {
        // String[] a = new String[0];
        // System.out.println("Expected False,Actually is" + isNotEmptyArray(a));
        // System.out.println(integerEqual(1, null));
        System.out.println("false:" + isPositive(-0.1));
        System.out.println("true:" + isPositive(0.1));
        System.out.println("true:" + isPositive(0.1f));
        System.out.println("false:" + isPositive(-0.1f));
        System.out.println("true:" + isPositive(1));
        System.out.println("false:" + isPositive(-1));
        System.out.println("true:" + isPositive(1l));
        System.out.println("false:" + isPositive(-1l));

    }
}
