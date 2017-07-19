/*
 * Copyright 2017-2025 msun.com All right reserved. This software is the confidential and proprietary information of
 * msun.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with msun.com.
 */
package com.ms.commons.test.tool.exportdata.cmdana;

import java.util.List;

import com.ms.commons.test.tool.exportdata.ConsoleCmd;

/**
 * @author zxc Apr 14, 2013 12:17:21 AM
 */
public abstract class CmdAnalysisor {

    abstract public List<String> exampleCommandList();

    abstract public List<String> analysisCommandList();

    abstract public ConsoleCmd analysisConsoleCmd(String fullCmd, String command);
}
