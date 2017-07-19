/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.test.memorydb;

import java.io.Serializable;
import java.util.List;

/**
 * @author zxc Apr 13, 2013 11:43:58 PM
 */
public class MemoryTable implements Serializable {

    private static final long serialVersionUID = -3710124556352144264L;
    private String            name;
    private List<MemoryRow>   rowList;

    public MemoryTable(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<MemoryRow> getRowList() {
        return rowList;
    }

    public void setRowList(List<MemoryRow> rowList) {
        this.rowList = rowList;
    }

    public int getRowCount() {
        return rowList.size();
    }

    public MemoryRow getRow(int index) {
        return rowList.get(index);
    }
}
