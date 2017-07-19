/*
 * Copyright 2017-2025 msun.com All right reserved. This software is the confidential and proprietary information of
 * msun.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with msun.com.
 */
package com.ms.commons.test.treedb;

import java.util.List;

import com.ms.commons.test.memorydb.Data;

/**
 * @author zxc Apr 13, 2013 11:31:02 PM
 */
public class TreeDatabase implements Data {

    private static final long serialVersionUID = -6280166229307183599L;
    private List<TreeObject>  treeObjects;

    public List<TreeObject> getTreeObjects() {
        return treeObjects;
    }

    public void setTreeObjects(List<TreeObject> treeObjects) {
        this.treeObjects = treeObjects;
    }

    public TreeDatabase() {
    }

    public TreeObject getObject(String name) {
        for (TreeObject table : treeObjects) {
            if (table.getName().equalsIgnoreCase(name)) {
                return table;
            }
        }
        return null;
    }

    public TreeObject getObject(int index) {
        if (treeObjects != null && treeObjects.size() > 0) return treeObjects.get(index);
        return null;
    }

    public int getObjectCount() {
        return treeObjects.size();
    }
}
