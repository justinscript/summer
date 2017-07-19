/*
 * Copyright 2017-2025 msun.com All right reserved. This software is the confidential and proprietary information of
 * msun.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with msun.com.
 */
package com.ms.commons.test.tool.exportdata.cmdana.impl;

import java.util.Arrays;
import java.util.List;

import com.ms.commons.test.datawriter.DataWriterType;
import com.ms.commons.test.tool.exportdata.ConsoleCmd;
import com.ms.commons.test.tool.exportdata.cmd.ExportCmd;
import com.ms.commons.test.tool.exportdata.cmdana.CmdAnalysisor;
import com.ms.commons.test.tool.util.StrUtil;

/**
 * @author zxc Apr 14, 2013 12:17:28 AM
 */
public class ExportCmdAnalysisor extends CmdAnalysisor {

    @Override
    public List<String> exampleCommandList() {
        return Arrays.asList("export excel|xml|json|wiki select * from table where id < 100");
    }

    @Override
    public List<String> analysisCommandList() {
        return Arrays.asList("export", "ex");
    }

    @Override
    public ConsoleCmd analysisConsoleCmd(String fullCmd, String command) {
        String c2 = command.trim();
        DataWriterType type;
        String sql;
        if (c2.startsWith("excel ")) {
            sql = c2.substring("excel ".length());
            type = DataWriterType.Excel;
        } else if (c2.startsWith("xml ")) {
            sql = c2.substring("xml ".length());
            type = DataWriterType.Xml;
        } else if (c2.startsWith("json ")) {
            sql = c2.substring("json ".length());
            type = DataWriterType.Json;
        } else if (c2.startsWith("wiki ")) {
            sql = c2.substring("wiki ".length());
            type = DataWriterType.Wiki;
        } else {
            throw new RuntimeException("Unknow export type in command: " + fullCmd);
        }

        List<String> sqlList = StrUtil.splitStringToList(sql, '|');
        if (sqlList.isEmpty()) {
            throw new RuntimeException("No sql input!");
        }

        ExportCmd ec = new ExportCmd();
        ec.setType(type);
        ec.setSqlList(sqlList);

        return ec;
    }
}
