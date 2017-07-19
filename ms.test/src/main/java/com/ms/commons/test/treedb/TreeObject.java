/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.test.treedb;

import java.util.List;

/**
 * @author zxc Apr 13, 2013 11:30:52 PM
 */
public class TreeObject {

    private String  name;

    private List<?> treeObjectList;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<?> getTreeObjectList() {
        return treeObjectList;
    }

    public void setTreeObjectList(List<?> treeObjectList) {
        this.treeObjectList = treeObjectList;
    }

    public int getRowCount() {
        return treeObjectList.size();
    }

    public Object getRow(int index) {
        return treeObjectList.get(index);
    }
}
