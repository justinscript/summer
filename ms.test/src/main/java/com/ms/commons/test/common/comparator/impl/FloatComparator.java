/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.test.common.comparator.impl;

import com.ms.commons.test.common.comparator.Comparator;

/**
 * @author zxc Apr 13, 2013 11:26:46 PM
 */
public class FloatComparator implements Comparator<Float> {

    public boolean compare(Float o1, Float o2) {
        return (Math.abs(o1.floatValue() - o2.floatValue()) <= 0.00000000001);
    }
}
