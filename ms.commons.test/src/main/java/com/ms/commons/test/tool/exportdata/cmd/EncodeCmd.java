/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.test.tool.exportdata.cmd;

import java.util.List;

import com.ms.commons.test.tool.exportdata.ConsoleCmd;
import com.ms.commons.test.tool.exportdata.cmd.encodecmd.EncodeOperation;
import com.ms.commons.test.tool.exportdata.cmd.encodecmd.TableFields;

/**
 * @author zxc Apr 14, 2013 12:18:01 AM
 */
public class EncodeCmd extends ConsoleCmd {

    private EncodeOperation   encodeOperation;
    private EncodeType        encodeType;
    private List<TableFields> tableFieldList;

    public EncodeOperation getEncodeOperation() {
        return encodeOperation;
    }

    public void setEncodeOperation(EncodeOperation encodeOperation) {
        this.encodeOperation = encodeOperation;
    }

    public EncodeType getEncodeType() {
        return encodeType;
    }

    public void setEncodeType(EncodeType encodeType) {
        this.encodeType = encodeType;
    }

    public List<TableFields> getTableFieldList() {
        return tableFieldList;
    }

    public void setTableFieldList(List<TableFields> tableFieldList) {
        this.tableFieldList = tableFieldList;
    }

    @Override
    public String toString() {
        return "EncodeCmd [encodeOperation=" + encodeOperation + ", encodeType=" + encodeType + ", tableFieldList="
               + tableFieldList + "]";
    }
}
