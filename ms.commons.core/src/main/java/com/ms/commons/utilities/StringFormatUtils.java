/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.utilities;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

import com.ms.commons.lang.Assert;

/**
 * 字符串处理工具类
 * 
 * @author zxc Mar 1, 2013 5:18:34 PM
 */
public class StringFormatUtils {

    private static final char[] array = new char[] { ',', ' ', '(', ')' };

    public static String matcherRegex(String str, String regex) {
        return matcherRegex(str, regex, true);
    }

    public static String matcherRegex(String s, String regex, boolean needTrim) {
        if (StringUtils.isBlank(s)) {
            return StringUtils.EMPTY;
        }
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(s);
        return needTrim ? m.replaceAll(StringUtils.EMPTY).trim() : m.replaceAll(StringUtils.EMPTY);
    }

    public static boolean matchsRegex(String str, String regex) {
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(str);
        return m.matches();
    }

    public static boolean containsRegex(String str, String regex) {
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(str);
        return m.find();
    }

    /**
     * 将数组中被source包含的返回出去。
     */
    public static Set<String> containsAny(String source, String[] testArray) {
        Assert.assertNotNull(source);
        Assert.assertNotNull(testArray);
        Set<String> result = new HashSet<String>(testArray.length);
        for (String testWord : testArray) {
            if (source.contains(testWord)) {
                result.add(testWord);
            }
        }
        return result;
    }

    /**
     * 将字符串中的字母和数字连词取出来，单独处理。
     */
    public static Set<String> matchLetterAndDigit(String source) {
        Set<String> result = new HashSet<String>();
        char[] charByte = new char[source.length()];
        int offset = 0;
        for (int i = 0, j = source.length(); i < j; i++) {
            char charAt = source.charAt(i);
            if (isMatch(charAt)) {// 单字节
                charByte[offset] = charAt;
                offset++;
            } else {
                if (offset > 0) {
                    char[] copyOfRange = Arrays.copyOfRange(charByte, 0, offset);
                    if (copyOfRange.length > 1) {
                        result.add(new String(copyOfRange));
                    }
                    offset = 0;
                }
            }
            if (i == (j - 1) && offset > 0) {
                char[] copyOfRange = Arrays.copyOfRange(charByte, 0, offset);
                if (copyOfRange.length > 1) {
                    result.add(new String(copyOfRange));
                }
            }
        }
        return result;
    }

    private static boolean isMatch(char charAt) {
        String binaryString = Integer.toBinaryString(charAt);
        if (binaryString.length() > 8) {
            return false;
        } else {
            return !ArrayUtils.contains(array, charAt);
        }
    }

    public static boolean isContainsRegex(String str, String regex) {
        if (StringUtils.isBlank(str) || StringUtils.isBlank(regex)) {
            return false;
        }
        return Pattern.compile(regex).matcher(str).find();
    }

    // 返回字符串的字数，精确到double
    public static float getWordSize(String o) {
        int l = o.length();
        o = StringFormatUtils.matcherRegex(o, "[^\\x00-\\xff]", false);// 除去所有的双字节字符
        return (float) (o.length() * 0.5) + l - o.length();
    }

    // 英文算一个字符长度，其它所有2个字符长度
    public static int getEnWordSize(String o) {
        int l = o.length();
        o = StringFormatUtils.matcherRegex(o, "[^\\x00-\\xff]", false);// 除去所有的双字节字符
        return 2 * l - o.length();
    }

    public static void main(String[] args) {
        // String word = "!@#3232";
        // if (Pattern.compile("(?i)[a-z]").matcher(word).find()) {
        // System.out.println("有字母");
        // } else if (Pattern.compile("(?i)[0-9]").matcher(word).find()) {
        // System.out.println("有数字");
        // }
        // System.out.println(matchLetterAndDigit("60D- 55Z中 "));
        // System.out.println(matchLetterAndDigit("60D-55Z中 "));
        // System.out.println(matchLetterAndDigit("60D,55Z中 "));
        // System.out.println(matchLetterAndDigit("60D#55Z中 "));
        // System.out.println(matchLetterAndDigit("棉T恤"));

        System.out.println(getEnWordSize("12中甜美一一一一夏季吊带雪纺抹胸裙沙滩裙连衣"));
    }
}
