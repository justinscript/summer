/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.test.memorydb;

import java.io.Serializable;

/**
 * @author zxc Apr 13, 2013 11:44:30 PM
 */
public class MemoryField implements Serializable {

    private static final long serialVersionUID = -5026133601777978042L;

    private MemoryFieldType   type;
    private String            name;
    private Object            value;

    public MemoryField(String name, MemoryFieldType type) {
        this.name = name;
        this.type = type;
    }

    public MemoryField(String name, MemoryFieldType type, Object value) {
        this.name = name;
        this.type = type;
        this.value = value;
    }

    public MemoryFieldType getType() {
        return type;
    }

    public void setType(MemoryFieldType type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public String getStringValue() {
        return (value == null) ? null : value.toString();
    }
}
