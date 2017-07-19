/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.test.memorydb;

import java.io.Serializable;

/**
 * @author zxc Apr 13, 2013 11:44:23 PM
 */
public enum MemoryFieldType implements Serializable {
    Null, String, Number, Date, Error, Unknow;

    public static MemoryFieldType make(String value) {
        for (MemoryFieldType type : values()) {
            if (type.name().equals(value)) return type;
        }
        throw new IllegalArgumentException("no type matched with " + value);
    }
}
