/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.test.tool.util;

import java.io.File;
import java.io.IOException;

/**
 * @author zxc Apr 14, 2013 12:19:25 AM
 */
public class AntxEnvUtil {

    private static String antxHome = null;

    public static String getAntxHome() {
        if (antxHome != null) {
            return antxHome;
        }

        ProcessBuilder pb = new ProcessBuilder("which", "antx");
        String antxPath = ProcessUtil.getProcessOutput(pb).trim();
        if (antxPath.length() == 0) {
            throw new RuntimeException("Cannot find antx!");
        }
        antxHome = antxPath;
        return antxPath;
    }

    public static String getAntxBinHome() throws IOException {
        return (new File(getAntxHome() + "/../")).getCanonicalPath();
    }

    public static String getProjectRepHome() throws IOException {
        return System.getProperty("user.home") + File.separator + ".antx/repository.project";
    }

    public static String getAntxRepHome() throws IOException {
        return (new File(getAntxHome() + "/../../../../repository")).getCanonicalPath();
    }
}
