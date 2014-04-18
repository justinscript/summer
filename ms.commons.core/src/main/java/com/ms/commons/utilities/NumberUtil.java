/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.utilities;

import java.math.BigDecimal;
import java.text.DecimalFormat;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;

/**
 * @author zxc Mar 1, 2013 5:15:17 PM
 */
public class NumberUtil {

    public static int convertToInt(Long price, int defaultValue) {
        if (price == null) {
            return defaultValue;
        }
        return parseInt(price.toString(), defaultValue);
    }

    public static float parseFloat(String data, float defaultValue) {
        if (StringUtils.isBlank(data)) {
            return defaultValue;
        }
        return NumberUtils.toFloat(data, defaultValue);
    }

    public static int parseInt(String qscore, int defaultValue) {
        if (StringUtils.isBlank(qscore)) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(qscore);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    public static long parseLong(String qscore, long defaultValue) {
        if (StringUtils.isBlank(qscore)) {
            return defaultValue;
        }
        try {
            return Long.parseLong(qscore);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    // 判断两个数字是否相等
    public static boolean isEqual(Number a, Number b) {
        return a == null ? b == null : a.equals(b);
    }

    /**
     * 四舍五入转化为字符串
     * 
     * @param number
     * @param precision 小数保留的位数
     * @return
     */
    public static String format2Str(double number, int precision) {
        String pattern = "0.";
        for (int i = 0; i < precision; i++) {
            pattern += "0";
        }
        DecimalFormat dg = new DecimalFormat(pattern); // 保留两位小数
        return dg.format(number);
    }

    /**
     * 四舍五入格式化double
     * 
     * @param number 原始数值
     * @param precision 保留的小数位数
     * @return
     */
    public static double format(double number, int precision) {
        int tmp = 1;
        for (int i = 0; i < precision; i++) {
            tmp *= 10;
        }
        int value = (int) Math.round(number * tmp);
        return (value * 1d) / tmp;
    }

    // 除法
    public static double div(double a, double b, int scale) {
        if (scale < 0) {
            throw new IllegalArgumentException("The scale must be a positive integer or zero");
        }
        BigDecimal b1 = new BigDecimal(Double.toString(a));
        BigDecimal b2 = new BigDecimal(Double.toString(b));
        return b1.divide(b2, scale, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    // 乘法
    public static double mul(double a, double b) {
        BigDecimal b1 = new BigDecimal(Double.toString(a));
        BigDecimal b2 = new BigDecimal(Double.toString(b));
        return b1.multiply(b2).doubleValue();
    }

    // 减法
    public static double sub(double a, double b) {
        BigDecimal b1 = new BigDecimal(Double.toString(a));
        BigDecimal b2 = new BigDecimal(Double.toString(b));
        return b1.subtract(b2).doubleValue();
    }

    public static void main(String[] args) {
        System.out.println(format2Str(0.1, 2));
        System.out.println(format2Str(0.11, 2));
        System.out.println(format2Str(0.115, 2));
        System.out.println(format2Str(0.114, 5));
        System.out.println(format2Str(0, 2));
    }

}
