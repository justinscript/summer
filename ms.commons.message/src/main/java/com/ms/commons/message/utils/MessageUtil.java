/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.message.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

/**
 * @author zxc Apr 13, 2014 10:40:10 PM
 */
public class MessageUtil {

    /**
     * 删除数字中为空的元素
     * 
     * @param array
     * @return
     */
    public static String[] removeEmptyElement(String[] array) {
        if (array == null) {
            return new String[0];
        }
        int removeCount = 0;
        for (int i = 0; i < array.length; i++) {
            if (StringUtils.isEmpty(array[i])) {
                removeCount += 1;
            }
        }
        if (removeCount > 0) {
            String[] newArray = new String[array.length - removeCount];
            int index = 0;
            for (int i = 0; i < array.length; i++) {
                if (StringUtils.isNotEmpty(array[i])) {
                    newArray[index] = array[i];
                    index += 1;
                }
            }
            return newArray;
        }
        return array;
    }

    /**
     * @param dbString 原始String
     * @param len 期望拆分的长度
     * @return 返回差分后的String数组
     */
    public static String[] split(String dbString, Integer len) {
        if (dbString == null) {
            return new String[0];
        }
        // 数据库可存储的字符总长度
        int num = dbString.length() / len;
        int last = num;
        if (num * len != dbString.length()) {
            num += 1;
        }
        String[] content = new String[num];
        int start = 0;
        for (int i = 0; i < num; i++) {
            if (i != last) {
                content[i] = dbString.substring(start, start + len);
            } else {
                content[i] = dbString.substring(start, dbString.length());
            }
            start += len;
        }
        return content;
    }

    /**
     * 判断是否为有效的手机号码
     * 
     * @param mobilePhone
     * @return
     */
    public static boolean isValidateMobileNumber(String mobilePhone) {
        Pattern pattern = Pattern.compile("^((13[0-9])|(14[7])|(15[^4,\\D])|(18[0-9]))\\d{8}$");
        Matcher matcher = pattern.matcher(mobilePhone);
        if (matcher.matches()) {
            return true;
        }
        return false;
    }

    /**
     * 将信息接收人组成一个字符串，进行群发
     * 
     * @param to
     * @return
     */
    public static String buildReceiver(String[] to) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0, last = to.length - 1; i < to.length; i++) {
            sb.append(to[i]);
            if (i != last) {
                sb.append(';');
            }
        }
        return sb.toString();
    }
}
