/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.test.tool.util;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

/**
 * @author zxc Apr 14, 2013 12:18:29 AM
 */
public class StrUtil {

    public static List<String> splitStringToList(String str, char splitC) {
        List<String> list = new ArrayList<String>();
        if (str != null) {
            for (String s : StringUtils.split(str, splitC)) {
                String ts = s.trim();
                if (ts.length() > 0) {
                    list.add(ts);
                }
            }
        }
        return list;
    }
}
