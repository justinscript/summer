/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.test.tool.exportdata;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.NotImplementedException;
import org.apache.commons.lang.StringUtils;

import com.ms.commons.test.tool.exportdata.cmdana.CmdAnalysisor;
import com.ms.commons.test.tool.exportdata.cmdana.impl.EncodeCmdAnalysisor;
import com.ms.commons.test.tool.exportdata.cmdana.impl.ExportCmdAnalysisor;

/**
 * @author zxc Apr 14, 2013 12:16:58 AM
 */
public class ConsoleCmdAnaylisysUtil {

    public static List<CmdAnalysisor> cmdAnalysisorList = new ArrayList<CmdAnalysisor>();
    static {
        cmdAnalysisorList.add(new ExportCmdAnalysisor());
        cmdAnalysisorList.add(new EncodeCmdAnalysisor());
    }

    public static ConsoleCmd anaConsoleCmd(String cmd) {
        if (StringUtils.isBlank(cmd)) {
            throw new RuntimeException("Command cannot be empty!");
        }
        String trimedCmd = cmd.trim();
        for (CmdAnalysisor cmdAnalysisor : cmdAnalysisorList) {
            for (String pre : cmdAnalysisor.analysisCommandList()) {
                if (trimedCmd.startsWith(pre + " ")) {
                    return cmdAnalysisor.analysisConsoleCmd(cmd, trimedCmd.substring((pre + " ").length()));
                }
            }
        }
        if (trimedCmd.startsWith("convert ")) {
            throw new NotImplementedException("Not supported!");
        }
        throw new RuntimeException("Unknow command: " + cmd);
    }
}
