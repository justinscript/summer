/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.fasttext.segment;

/**
 * @author zxc Apr 12, 2013 3:45:46 PM
 */
public class InternalElement implements Comparable<InternalElement> {

    public char          sequence[];
    public TermExtraInfo termExtraInfo;

    public InternalElement(char line[]) {
        this.sequence = line;
    }

    public InternalElement(char line[], TermExtraInfo node) {
        this.sequence = line;
        this.termExtraInfo = node;
    }

    public int compareTo(InternalElement o) {
        char[] a = this.sequence;
        char[] b = o.sequence;
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
