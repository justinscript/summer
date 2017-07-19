/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.fasttext.segment;

import java.util.Comparator;

/**
 * @author zxc Apr 12, 2013 3:18:51 PM
 */
public class CharArrayComparator<T> implements Comparator<T> {

    public int compare(T o1, T o2) {
        char[] a = ((InternalElement) o1).sequence;
        char[] b = ((InternalElement) o2).sequence;
        int loop = a.length > b.length ? b.length : a.length;
        for (int i = 0; i < loop; i++) {
            int c = a[i] - b[i];
            if (c != 0) {
                return c;
            }
        }
        return a.length - b.length;
    }
}
