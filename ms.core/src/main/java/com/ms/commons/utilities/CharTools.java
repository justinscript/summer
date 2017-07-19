/*
 * Copyright 2017-2025 msun.com All right reserved. This software is the confidential and proprietary information of
 * msun.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with msun.com.
 */
package com.ms.commons.utilities;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.springframework.util.Assert;

/**
 * @author zxc Apr 12, 2013 1:37:11 PM
 */
public class CharTools {

    private static final Pattern unicode_url_pattern = Pattern.compile("&#(\\d+);");

    /**
     * 类似 T&#24676; 表述 “T恤” 网页中的 unicode 转化成为 10进制方式
     * 
     * @param str
     * @return
     */
    public static String unicodeUrlDecode(String str) {
        if (StringUtils.isBlank(str)) {
            return str;
        }
        StringBuilder sb = new StringBuilder();
        Matcher matcher = unicode_url_pattern.matcher(str);
        int preEnd = 0;
        while (matcher.find()) {
            String group = matcher.group(1);
            int start = matcher.start();
            sb.append(str.substring(preEnd, start));
            sb.append(fromCharCode(Integer.parseInt(group)));
            preEnd = matcher.end();
        }
        sb.append(str.substring(preEnd, str.length()));
        return sb.toString();
    }

    /**
     * 字符串解析成unicode
     * 
     * @param str
     * @return
     */
    public static String getUncode(String str) {

        if (str == null) {
            return "";
        }
        String hs = "";

        try {
            byte b[] = str.getBytes("UTF-16");
            for (int n = 0; n < b.length; n++) {
                str = (java.lang.Integer.toHexString(b[n] & 0XFF));
                if (str.length() == 1) {
                    hs = hs + "0" + str;
                } else hs = hs + str;
                if (n < b.length - 1) {
                    hs = hs + "";
                }
            }
            // 去除第一个标记字符
            str = hs.toUpperCase().substring(4);
            char[] chs = str.toCharArray();
            str = "";
            for (int i = 0; i < chs.length; i = i + 4) {
                str += "\\u" + chs[i] + chs[i + 1] + chs[i + 2] + chs[i + 3];
            }
            return str;
        } catch (Exception e) {
            System.out.print(e.getMessage());
        }
        return str;
    }

    /**
     * 返回str字符串在UTF8字符中占有多少字节<br>
     * 使用场景：目前数据库oracle保存中文是UTF格式，所以可以使用这个判断
     * 
     * @param str
     * @return
     */
    public static long getLength4UTF8(String str) {
        if (str == null || str.length() == 0) {
            return 0;
        }
        char[] chars = str.toCharArray();
        long length = 0;
        for (int i = 0; i < chars.length; i++) {
            byte[] bytes;
            try {
                bytes = ("" + chars[i]).getBytes("UTF-8");
                length += bytes.length;
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return length;
    }

    /**
     * 根据text字符，在UTF8编码格式下，拆分字符串，每个字符串的总字节不能超过maxLen<br>
     * 使用场景：把中文存入Oracle，一个中文占有3个字节。所以不能根据String.length()来判断长度。需要使用该方法按照字节数拆分<br>
     * 
     * @param text 被拆分的字符串
     * @param maxLen 最大字节长度 (至少大于等于3,不然一个中文字符都包含不了)
     * @return
     */
    public static List<String> splitText4UTF8(String text, int maxLen) {
        Assert.isTrue(maxLen >= 3); // 至少大于等于3,不然一个中文字符都包含不了
        List<String> list = new ArrayList<String>();
        if (text == null || text.length() == 0) {
            return list;
        }
        char[] chars = text.toCharArray();
        StringBuilder sb = new StringBuilder(maxLen);
        int currentLength = 0;
        for (int i = 0; i < chars.length; i++) {
            byte[] bytes;
            try {
                bytes = ("" + chars[i]).getBytes("UTF-8");
                if (currentLength + bytes.length <= maxLen) {
                    sb.append(chars[i]);
                    currentLength += bytes.length;
                } else {
                    list.add(sb.toString());
                    sb = new StringBuilder(maxLen);
                    sb.append(chars[i]);
                    currentLength = bytes.length;
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        if (sb.length() > 0) {
            list.add(sb.toString());
        }
        return list;
    }

    /**
     * 检查text字符有多少字节（以GBK为编码），同时检查是否有XML不认识的字符
     * 
     * @param text
     * @param maxLen
     * @return CheckResult
     */
    public static CheckResult checkXmlGbkChar(String text, int maxLen) {
        if (text == null || text.length() == 0 || maxLen == 0) {
            return new CheckResult(false, -1, -1, "Text is null or MaxLen is zero");
        }
        StringBuilder sb = new StringBuilder();
        boolean success = true;
        char[] chars = text.toCharArray();
        int byteLength = 0;
        for (int i = 0; i < chars.length; i++) {
            byte[] bytes;
            try {
                bytes = ("" + chars[i]).getBytes("GBK");
                int temp = bytes[0] & 0xff;
                if (bytes.length == 1 && temp < 32) // 32是空格，0－31的字符是不可见字符，均认为非法
                {
                    sb.append("非法字符char(" + temp + ")");
                    success = false;
                }
                byteLength += bytes.length;
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        if (byteLength > maxLen) {
            success = false;
        }
        return new CheckResult(success, text.length(), byteLength, sb.toString());
    }

    public static class CheckResult {

        public CheckResult(boolean success, int textLength, int bytesLength, String message) {
            this.success = success;
            this.textLength = textLength;
            this.bytesLength = bytesLength;
            this.message = message;
        }

        public int     bytesLength; // 字节长度（GBK编码）
        public int     textLength; // 字符长度
        public String  message;    // 检查出错后给出提示
        public boolean success;    // 检查是否通过
    }

    private static String fromCharCode(int... codePoints) {
        return new String(codePoints, 0, codePoints.length);
    }
}
