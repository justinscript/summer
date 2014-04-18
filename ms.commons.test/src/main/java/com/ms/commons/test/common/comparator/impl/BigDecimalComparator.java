/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.test.common.comparator.impl;

import java.math.BigDecimal;

import com.ms.commons.test.common.comparator.Comparator;

/**
 * @author zxc Apr 13, 2013 11:05:40 PM
 */
public class BigDecimalComparator implements Comparator<BigDecimal> {

    public boolean compare(BigDecimal o1, BigDecimal o2) {
        BigDecimal r = o1.add(o2.negate());
        return (r.abs().compareTo(BigDecimal.valueOf(0.00000000001)) <= 0);
    }
}
