/*
 * Copyright 2017-2025 msun.com All right reserved. This software is the confidential and proprietary information of
 * msun.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with msun.com.
 */
package com.ms.commons.test.tool;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.math.RandomUtils;

/**
 * @author zxc Apr 13, 2013 10:29:52 PM
 */
public class UnitTestTools {

    public static Integer nextInt() {
        return Math.abs(RandomUtils.nextInt());
    }

    /**
     * 随机生成5位数
     * 
     * @param len
     * @return
     */
    public static Integer nextInt(Integer len) {
        if (len == null) {
            len = 2;
        }
        int v = 0;
        int i = 0;
        while (i < len) {
            v *= 10;
            int x = RandomUtils.nextInt(10);
            while (x == 0) {
                x = RandomUtils.nextInt(10);
            }
            v += Math.abs(x);
            i++;
        }
        return Math.abs(v);
    }

    public static Long nextLong() {
        return Math.abs(RandomUtils.nextLong());
    }

    /**
     * 随机生成长整形数
     * 
     * @param len
     * @return
     */
    public static Long nextLong(Integer len) {
        return nextInt(len) * 1L;
    }

    public static String nextAlphabet() {
        return RandomStringUtils.randomAlphabetic(4);
    }

    /**
     * 随机生成字符串
     * 
     * @param count
     * @return
     */
    public static String nextAlphabet(Integer count) {
        return RandomStringUtils.randomAlphabetic(count);
    }

    public static void main(String[] args) {
        System.out.println(nextAlphabet(5));
    }

}
