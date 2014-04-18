/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.test.memorydb;

import java.util.List;

/**
 * 基于内存的数据暂存结构，不能存用于放大量数据或用于检索
 * 
 * @author zxc Apr 13, 2013 11:44:40 PM
 */
public class MemoryDatabase implements Data {

    private static final long serialVersionUID = -185837905890975455L;
    private List<MemoryTable> tableList;

    public List<MemoryTable> getTableList() {
        return tableList;
    }

    public void setTableList(List<MemoryTable> tableList) {
        this.tableList = tableList;
    }

    public MemoryTable getTable(String name) {
        for (MemoryTable table : tableList) {
            if (table.getName().equalsIgnoreCase(name)) {
                return table;
            }
        }
        return null;
    }

    public int getTableCount() {
        return tableList.size();
    }

    public MemoryTable getTable(int index) {
        return tableList.get(index);
    }
}
