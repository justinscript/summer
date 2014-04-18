/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.app.web.commons.tools;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.HashMap;

import org.apache.commons.lang.math.NumberUtils;

/**
 * 简单货币格式工具
 * 
 * @author zxc Apr 12, 2013 10:59:09 PM
 */
public class CurrencyFormattor {

    private static ThreadLocal<HashMap<String, NumberFormat>> formatHolder = new ThreadLocal<HashMap<String, NumberFormat>>();
    private static final String                               CURRENCY     = "#0.00";
    // 带一位小数的格式
    private static final String                               NUMBER       = "#0.0";
    private static final String                               PERCENT      = "#.##%";

    private static final Integer                              SCALE        = 2;

    /**
     * 将元转化为分
     * 
     * @param yuan 输入金额元(类似 10.00或者10)
     * @return
     */
    public static int convert2fen(String yuan) {
        return (int) (NumberUtils.toFloat(yuan) * 100);
    }

    /**
     * 将“分”格式化为“元”的样式
     * 
     * <pre>
     * format(null) 0.00
     * format(－1) 0.00
     * format(0) 0.00
     * format(1) 1.00
     * format(1000000) 10000.00
     * </pre>
     * 
     * @param price 输入的“分”的价格
     * @return 元的价格
     */
    public static String format(Integer fen) {
        double yuan = 0;
        if (isNotNull(fen)) {
            yuan = fen / 100d;
        }
        NumberFormat numberInstance = getFormat(CURRENCY);
        return numberInstance.format(yuan);
    }

    /**
     * 将数字转换为指定的格式
     * 
     * @param data 除数
     * @param divide 被除数
     * @return 返回带两位小数的格式
     */

    public static String format(Integer data, Double divide) {
        double result = 0;
        result = data / divide;
        NumberFormat numberInstance = getFormat(CURRENCY);
        String strValue = numberInstance.format(result);
        // 去掉小数点后面的0
        if (strValue != null && strValue.length() > 0) {
            strValue = strValue.replaceAll("(\\.0+|0+)$", "");
        }
        return strValue;
    }

    /**
     * 保留1为小数的格式
     * 
     * @param data
     * @param divide
     * @return
     */
    public static String formatWith1Dot(Integer data, Double divide) {
        double result = 0;
        result = data / divide;
        NumberFormat numberInstance = getFormat(NUMBER);
        String strValue = numberInstance.format(result);
        // 去掉小数点后面的0
        if (strValue != null && strValue.length() > 0) {
            strValue = strValue.replaceAll("(\\.0+|0+)$", "");
        }
        return strValue;
    }

    /**
     * 保留2为小数的格式
     * 
     * @param data
     * @param divide
     * @return
     */
    public static Double formatWith2Dot(Integer data, Double divide) {
        double result = 0;
        result = data / divide;
        Long tmp = Math.round(result * 100);
        result = (tmp / 100d);
        return result;
    }

    /**
     * 将“分”格式化为“元”的样式<br>
     * 负数时,显示""
     * 
     * @param fen
     * @return
     */
    public static String formatShowEmpty(Integer fen) {
        double yuan = 0;
        if (isNotNull(fen)) {
            yuan = fen / 100d;
            NumberFormat numberInstance = getFormat(CURRENCY);
            return numberInstance.format(yuan);
        }
        return "";
    }

    /**
     * 将“分”格式化为“元”的样式,不包含.00部分<br>
     * 负数时,显示""
     * 
     * @param fen
     * @return
     */
    public static String formatFen(Integer fen) {
        int yuan = 0;
        if (isNotNull(fen)) {
            yuan = fen / 100;
            return "" + yuan;
        }
        return "";
    }

    /**
     * 输入元，得到分
     * 
     * @param fen
     * @return
     */
    public static int yuanTofen(String yuan) {
        try {
            float fen = Float.parseFloat(yuan);
            if (fen > 0) {
                return (int) (fen * 100 + 0.001f);
            }
        } catch (Exception e) {
        }
        return 0;
    }

    /**
     * 输入元，得到分
     * 
     * @param fen
     * @return
     */
    public static int yuanTofen(Float yuan) {
        if (yuan != null && yuan > 0) {
            // +0.001是为了消除 java浮点计算小数问题
            return (int) (yuan.floatValue() * 100 + 0.001f);
        }
        return 0;
    }

    /**
     * 输入元，得到分
     * 
     * @param fen
     * @return
     */
    public static int yuanTofen(Integer yuan) {
        if (yuan != null && yuan > 0) {
            return (int) (yuan * 100);
        }
        return 0;
    }

    private static boolean isNotNull(Integer fen) {
        return fen != null;
    }

    /**
     * 格式化百分比
     * 
     * @param number
     * @return
     */
    public static String formatPercent(float number) {
        NumberFormat format = getFormat(PERCENT);
        return format.format(number);
    }

    public static float toFloat(String number) {
        try {
            return Float.parseFloat(number);
        } catch (Exception e) {
            return 0;
        }
    }

    private static NumberFormat getFormat(String key) {
        HashMap<String, NumberFormat> map = formatHolder.get();
        if (map == null) {
            map = new HashMap<String, NumberFormat>(3);
            formatHolder.set(map);// 保存回去
        }
        NumberFormat format = map.get(key);
        if (format == null) {
            format = new DecimalFormat(key);
            map.put(key, format);
            formatHolder.set(map);// 保存回去
        }
        return format;
    }

    /**
     * 价格从分转化为元
     */
    public static Integer formatPrice(Integer price) {
        if (price == null) {
            return null;
        }
        return price.intValue() / 100;
    }

    /**
     * "###.####"-->"#.00" floatPrice-->formatPrice(四舍五入)
     */
    public static String float2formatPrice(Float price) {
        if (price == null) {
            return null;
        }

        BigDecimal b = new BigDecimal(price);
        float f = b.setScale(SCALE, BigDecimal.ROUND_HALF_UP).floatValue();
        DecimalFormat fnum = new DecimalFormat(CURRENCY);

        return fnum.format(f);
    }

    /**
     * 将一个整数除以100返回一个浮点型数据 用于将淘宝评分的转换
     */
    public static Float formatScore(Integer score) {
        if (score == null) {
            return null;
        }
        return score.floatValue() / 100;
    }

    /**
     * 把字符串转化成整数
     * 
     * @param str
     * @return
     */
    public static Integer strToInt(String str) {
        try {
            Float num = Float.parseFloat(str);
            return num.intValue();
        } catch (Exception e) {
            return null;
        }
    }
}
