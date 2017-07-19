/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.utilities;

import java.net.URLDecoder;
import java.net.URLEncoder;

/**
 * @author zxc Apr 12, 2013 1:36:31 PM
 */
public class EncodeUtils {

    public static final String ENCODE = "utf-8";

    public static String decode(String s) {
        try {
            return URLDecoder.decode(s, ENCODE);
        } catch (Exception e) {
            return s;
        }
    }

    public static String encode(String s) {
        try {
            return URLEncoder.encode(s, ENCODE);
        } catch (Exception e) {
            return s;
        }
    }
}
