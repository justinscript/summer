/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.udas.retry;

import java.util.Set;

import com.ms.commons.udas.impl.UdasObj;

/**
 * @author zxc Apr 12, 2013 6:37:42 PM
 */
public class RetryUtils {

    public static boolean equals(RetryObject source, RetryObject target) {
        if (source == null && target == null) {
            return true;
        }
        boolean flag = true;
        if (source != null && target != null) {
            flag &= (source.getCreatTime() == target.getCreatTime());
            flag &= (source.getRetryCount() == target.getRetryCount());
            flag &= (source.getValue().equals(target.getValue()));
            return flag;
        }
        return false;
    }

    public static boolean equals(Set<String> source, Set<String> target) {
        if (source == null && target == null) {
            return true;
        }

        if (source.size() != target.size()) {
            return false;
        }

        boolean flag = true;
        if (!source.isEmpty() && !target.isEmpty()) {
            for (String value : source) {
                flag &= target.contains(value);
            }
        }

        return flag;
    }

    public static boolean equals(UdasObj source, UdasObj target) {
        if (source == null && target == null) {
            return true;
        }
        boolean flag = true;
        if (source != null && target != null) {
            flag &= (source.getCreatTime() == target.getCreatTime());
            flag &= (source.getValue().equals(target.getValue()));
            return flag;
        }
        return false;
    }
}
