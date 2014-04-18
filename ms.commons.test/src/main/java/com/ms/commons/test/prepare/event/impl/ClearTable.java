/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.test.prepare.event.impl;

import java.util.ArrayList;
import java.util.List;

/**
 * 该类用于封装需要autoClearExistedData的表名和字段
 * 
 * @author zxc Apr 14, 2013 12:22:57 AM
 */
class ClearTable {

    String       tableName;
    // 一张表可以多个清理条件
    List<String> clearConditions = new ArrayList<String>();

    public ClearTable(String tableName) {
        this.tableName = tableName;
    }

    public void addClearCondition(String clearCondition) {
        this.clearConditions.add(clearCondition);
    }

    public List<String> getAllConditions() {
        return this.clearConditions;
    }
}
