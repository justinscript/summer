/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.udas.impl.objinfo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 * @author zxc Apr 12, 2013 5:33:50 PM
 */
public class UdasList<E> extends ArrayList<E> implements UdasObjectInfo {

    private static final long serialVersionUID = -4316943016402181087L;

    public UdasList() {
        super();
    }

    public UdasList(Collection<? extends E> c) {
        super(c);
    }

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
