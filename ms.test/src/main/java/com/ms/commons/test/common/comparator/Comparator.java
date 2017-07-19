/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.test.common.comparator;

/**
 * @author zxc Apr 13, 2013 11:26:38 PM
 */
public interface Comparator<T> {

    boolean compare(T o1, T o2);
}
