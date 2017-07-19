/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.test.memorydb;

import java.io.Serializable;
import java.util.List;

/**
 * @author zxc Apr 13, 2013 11:44:09 PM
 */
public class MemoryRow implements Serializable {

    private static final long serialVersionUID = -6772125059353861701L;
    private List<MemoryField> fieldList;

    public MemoryRow(List<MemoryField> fieldList) {
        this.fieldList = fieldList;
    }

    public List<MemoryField> getFieldList() {
        return fieldList;
    }

    public void setFieldList(List<MemoryField> fieldList) {
        this.fieldList = fieldList;
    }

    public MemoryField getField(String name) {
        for (MemoryField field : fieldList) {
            if (field.getName().equalsIgnoreCase(name)) {
                return field;
            }
        }
        return null;
    }

    public int getFieldCount() {
        return fieldList.size();
    }

    public MemoryField getField(int index) {
        return fieldList.get(index);
    }
}
