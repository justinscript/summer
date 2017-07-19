/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.lang;

/**
 * @author zxc Apr 12, 2013 2:34:08 PM
 */
public class NumberUtils {

    public static int toNonNegative(Integer value) {
        if (value == null || value < 0) {
            return 0;
        } else {
            return value;
        }
    }

    public static long toNonNegative(Long value) {
        if (value == null || value < 0) {
            return 0l;
        } else {
            return value;
        }
    }

}
