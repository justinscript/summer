/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.test.tool.exportdata.cmd.encodecmd;

import java.util.List;

/**
 * @author zxc Apr 14, 2013 12:18:13 AM
 */
public class TableFields {

    private String       table;
    private List<String> fields;

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public List<String> getFields() {
        return fields;
    }

    public void setFields(List<String> fields) {
        this.fields = fields;
    }

    @Override
    public String toString() {
        return "TableFields [table=" + table + ", fields=" + fields + "]";
    }
}
