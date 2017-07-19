/*
 * Copyright 2017-2025 msun.com All right reserved. This software is the confidential and proprietary information of
 * msun.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with msun.com.
 */
package com.ms.commons.test.database.type;

import java.io.Serializable;

/**
 * @author zxc Apr 13, 2013 11:39:11 PM
 */
public class FieldType implements Serializable {

    private static final long serialVersionUID = -6800141893541113577L;
    private String            name;
    private String            type;
    private int               size;
    private int               scale;
    private boolean           isFloat;

    public FieldType(String name, String type, int size, int scale, boolean isFloat) {
        this.name = name;
        this.type = type;
        this.size = size;
        this.scale = scale;
        this.isFloat = isFloat;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getScale() {
        return scale;
    }

    public void setScale(int scale) {
        this.scale = scale;
    }

    public boolean isFloat() {
        return isFloat;
    }

    public void setFloat(boolean isFloat) {
        this.isFloat = isFloat;
    }

    @Override
    public String toString() {
        return "FieldType [name=" + name + ", type=" + type + "]";
    }
}
