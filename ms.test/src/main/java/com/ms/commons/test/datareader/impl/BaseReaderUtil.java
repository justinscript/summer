/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.test.datareader.impl;

import java.io.File;
import java.net.URLDecoder;

import org.apache.log4j.Logger;

import com.ms.commons.test.common.FileUtil;
import com.ms.commons.test.runtime.RuntimeUtil;
import com.ms.commons.test.runtime.constant.RuntimeEnvironment;

/**
 * @author zxc Apr 13, 2013 11:37:08 PM
 */
public class BaseReaderUtil {

    static Logger           log      = Logger.getLogger(BaseReaderUtil.class);

    protected static String basePath = null;

    synchronized public static String getBasePath() {
        if ((basePath == null) || (basePath.length() == 0)) {
            throw new RuntimeException("Base path was not setted.");
        }
        return basePath.endsWith("/") ? basePath : (basePath + "/");
    }

    @SuppressWarnings("deprecation")
    synchronized public static void setBasePath(String path) {
        basePath = path;
        if ((path != null) && (path.contains("%"))) {
            basePath = URLDecoder.decode(path);
        }
    }

    public static String getAbsolutedPath(String file) {
        String absolutedPath = file.startsWith("/") ? file : (getBasePath() + file);
        if (log.isInfoEnabled()) log.info("Read file `" + absolutedPath + "` to memory database.");
        return absolutedPath;
    }

    protected static String getOriDataFile(String absPath) {
        if (RuntimeUtil.getRuntime().getEnvironment() != RuntimeEnvironment.Eclipse) {
            return absPath;
        }
        if ((absPath == null) || absPath.trim().length() == 0) {
            return absPath;
        }
        String orintPath = absPath.replace(RuntimeUtil.getRuntime().getOutputPath(), "src/java.test");
        if (orintPath.contains("src/java.test_test")) {
            orintPath = orintPath.replace("src/java.test_test", "src/java.test");
        }
        if (new File(orintPath).exists()) {
            return orintPath;
        } else {
            return absPath;
        }
    }

    protected static void checkDataFile(String absPath) {
        if (RuntimeUtil.getRuntime().getEnvironment() != RuntimeEnvironment.Eclipse) {
            return;
        }
        if ((absPath == null) || absPath.trim().length() == 0) {
            return;
        }

        String orintPath = absPath.replace(RuntimeUtil.getRuntime().getOutputPath(), "src/java.test");
        if (orintPath.contains("src/java.test_test")) {
            orintPath = orintPath.replace("src/java.test_test", "src/java.test");
        }

        File f1 = new File(absPath);
        File f2 = new File(orintPath);

        if (f1.exists() && (!f2.exists())) {
            log.warn("File " + f2.getAbsolutePath() + " not exists.");
            return;
        }

        if (!FileUtil.isFileSame(f1, f2)) {
            throw new RuntimeException("File " + f1.getName()
                                       + " has been change, please press F5 in order to refresh then excel file.");
        }
    }
}
