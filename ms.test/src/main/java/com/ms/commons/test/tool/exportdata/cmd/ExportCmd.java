/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.test.tool.exportdata.cmd;

import java.util.List;

import com.ms.commons.test.datawriter.DataWriterType;
import com.ms.commons.test.tool.exportdata.ConsoleCmd;

/**
 * @author zxc Apr 14, 2013 12:17:45 AM
 */
public class ExportCmd extends ConsoleCmd {

    private DataWriterType type;
    private List<String>   sqlList;

    public DataWriterType getType() {
        return type;
    }

    public void setType(DataWriterType type) {
        this.type = type;
    }

    public List<String> getSqlList() {
        return sqlList;
    }

    public void setSqlList(List<String> sqlList) {
        this.sqlList = sqlList;
    }
}
