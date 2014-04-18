/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.udas.impl.objinfo;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

/**
 * @author zxc Apr 12, 2013 5:34:07 PM
 */
public class UdasHashSet<E> extends HashSet<E> implements UdasObjectInfo {

    private static final long serialVersionUID = 1129788408908676510L;

    public UdasHashSet() {
        super();
    }

    public UdasHashSet(Collection<? extends E> c) {
        super(c);
    }

    public UdasHashSet(int initialCapacity, float loadFactor) {
        super(initialCapacity, loadFactor);
    }

    public UdasHashSet(int initialCapacity) {
        super(initialCapacity);
    }

    @Override
    public String toSimpleString() {
        Iterator<E> i = iterator();
        if (!i.hasNext()) return "[]";
        StringBuilder sb = new StringBuilder();
        sb.append('[');
        for (;;) {
            E e = i.next();
            if (e == this) {
                sb.append("(this Collection)");
            } else {
                if (e instanceof UdasObjectInfo) {
                    sb.append(((UdasObjectInfo) e).toSimpleString());
                } else {
                    sb.append(e);
                }
            }
            if (!i.hasNext()) {
                return sb.append(']').toString();
            }
            sb.append(", ");
        }
    }
}
