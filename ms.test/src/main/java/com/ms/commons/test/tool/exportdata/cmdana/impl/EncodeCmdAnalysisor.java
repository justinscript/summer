/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.test.tool.exportdata.cmdana.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.ms.commons.test.tool.exportdata.ConsoleCmd;
import com.ms.commons.test.tool.exportdata.cmd.EncodeCmd;
import com.ms.commons.test.tool.exportdata.cmd.EncodeType;
import com.ms.commons.test.tool.exportdata.cmd.encodecmd.EncodeOperation;
import com.ms.commons.test.tool.exportdata.cmd.encodecmd.TableFields;
import com.ms.commons.test.tool.exportdata.cmdana.CmdAnalysisor;

/**
 * @author zxc Apr 14, 2013 12:17:37 AM
 */
public class EncodeCmdAnalysisor extends CmdAnalysisor {

    @Override
    public List<String> exampleCommandList() {
        return Arrays.asList("encode show", "encode add|remove gbk|utf8 product/detail");
    }

    @Override
    public List<String> analysisCommandList() {
        return Arrays.asList("encode", "en");
    }

    @Override
    public ConsoleCmd analysisConsoleCmd(String fullCmd, String command) {
        String c2 = command.trim();
        EncodeCmd ec = new EncodeCmd();
        String cc = null;
        if (c2.startsWith("add ")) {
            cc = c2.substring("add ".length());
            ec.setEncodeOperation(EncodeOperation.Add);
        } else if (c2.startsWith("remove ")) {
            cc = c2.substring("remove ".length());
            ec.setEncodeOperation(EncodeOperation.Remove);
        } else if (c2.startsWith("delete ")) {
            cc = c2.substring("delete ".length());
            ec.setEncodeOperation(EncodeOperation.Remove);
        } else if (c2.trim().equals("show")) {
            ec.setEncodeOperation(EncodeOperation.Show);
        } else {
            throw new RuntimeException("Unknow encode type in command: " + fullCmd);
        }

        if (cc != null) {
            String ccc;
            cc = cc.trim();
            if (cc.startsWith("gbk ")) {
                ccc = cc.substring("gbk ".length());
                ec.setEncodeType(EncodeType.GBK);
            } else if (cc.startsWith("utf8 ")) {
                ccc = cc.substring("utf8 ".length());
                ec.setEncodeType(EncodeType.UTF8);
            } else if (cc.startsWith("utf-8 ")) {
                ccc = cc.substring("utf-8 ".length());
                ec.setEncodeType(EncodeType.UTF8);
            } else {
                throw new RuntimeException("Unknow encode type in command: " + fullCmd);
            }
            ec.setTableFieldList(convertToTableFields(ccc));
        }

        return ec;
    }

    private List<TableFields> convertToTableFields(String tableFields) {
        String trimedTableFields = tableFields.trim();
        List<TableFields> tableFieldList = new ArrayList<TableFields>();
        if (trimedTableFields.length() > 0) {
            String[] tfs = trimedTableFields.split("\\s");
            for (String tf : tfs) {
                int ios = tf.indexOf('/');
                String table = tf.substring(0, ios);
                String fields = tf.substring(ios + 1);
                TableFields tfields = new TableFields();
                tfields.setTable(table.trim().toLowerCase());
                tfields.setFields(new ArrayList<String>(Arrays.asList(fields.replace(" ", "").toLowerCase().split(","))));
                tableFieldList.add(tfields);
            }
        }
        return tableFieldList;
    }
}
