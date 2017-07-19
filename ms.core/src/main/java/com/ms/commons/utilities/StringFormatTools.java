/*
 * Copyright 2017-2025 msun.com All right reserved. This software is the confidential and proprietary information of
 * msun.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with msun.com.
 */
package com.ms.commons.utilities;

// import org.joda.time.Duration;
// import org.joda.time.format.PeriodFormatter;
// import org.joda.time.format.PeriodFormatterBuilder;

/**
 * <pre>
 *  参考“http://blog.csdn.net/yohop/article/details/2534907”
 *  格式化表达式：%[零个或多个标志][最小字段宽度][精度][修改符]格式码
 *  标志:
 *      标志 - 含义 值在字段中做对齐，缺省情况下是右对齐。
 *  最小字段宽度:
 *      字段宽度是一个十进制整数，用于指定将出现在结果中的最小字符数。如果值的字符数少于字段宽度，就对它进行填充以增加长度。
 *  精度:
 *       以一个句点开头，后面跟一个可选的十进制数。
 *       对于e,E和f类型的转换，精度决定将出现在小数点之后的数字位数。例如.2小数点后精度为2
 *       当使用s类型的转换时，精度指定将被转换的最多的字符数。
 *       
 *  格式码:
 *       s,f,%(转义符号，类似字符串的"\")
 * </pre>
 * 
 * @author zxc Apr 12, 2013 1:32:21 PM
 */
public class StringFormatTools {

    // static PeriodFormatter formatter = new
    // PeriodFormatterBuilder().appendDays().appendSuffix("天").appendHours().appendSuffix("小时").appendMinutes().appendSuffix("分").appendSeconds().appendSuffix("秒").appendMillis3Digit().appendSuffix("毫秒").toFormatter();

    // public static String formatDuration(long durationMs) {
    // return formatDuration(durationMs, null);
    // }

    // public static String formatDuration(long durationMs, String prefix) {
    // Duration duration = new Duration(durationMs);
    // String print = formatter.print(duration.toPeriod());
    // return (prefix == null ? "" : prefix) + print + " (" + durationMs + ")ms";
    // }

    public static String formatFloat(Float value, int delimiter, String suffix) {
        float _value = value == null ? 0f : value;
        suffix = suffix == null ? "" : suffix;
        return String.format("%." + delimiter + "f" + suffix, _value);
    }

    public static String formatFloat(Number denominator, Number molecule) {
        if (denominator == null || molecule == null) {
            return null;
        }
        return formatFloat(denominator.floatValue() / molecule.floatValue(), 2, null);
    }

    public static String formatFloat(Float value) {
        return formatFloat(value, 2, null);
    }

    /**
     * 处理百分比
     */
    public static String formatPercent(Float value) {
        return formatFloat(value, 2, "%%");
    }

    public static String formatString(String value, boolean alignLeft, int minLength, int maxLength) {
        return String.format("%" + (alignLeft ? "-" : "") + minLength + "." + maxLength + "s", value);
    }

    public static void main(String[] args) {
        System.out.println(formatFloat(10, 3));
        System.out.println(formatFloat(10.000f, 2));
        System.out.println(formatPercent(10.000f));
        System.out.println(formatString("12345", true, 20, 20));
        System.out.println(formatString("12", true, 3, 4));
    }

}
