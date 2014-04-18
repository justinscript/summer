/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.test.constants;

import java.io.File;

import com.ms.commons.test.common.FileUtil;
import com.ms.commons.test.common.StringUtil;

/**
 * @author zxc Apr 13, 2013 11:17:28 PM
 */
public class IntlTestGlobalConstants {

    public static final String USER_DIR                           = System.getProperty("user.dir");
    public static final String USER_HOME                          = System.getProperty("user.home");

    public static final String LINE_SEPARATOR                     = System.getProperty("line.separator");

    public static final String SEP                                = File.separator;
    public static final String USER_HOME_ANTX_PROPERTIES          = USER_HOME + SEP + "antx.properties";
    public static final String USER_HOME_TESTCASE_ANTX_PROPERTIES = USER_HOME + SEP + "testcase_antx.properties";
    public static final String USER_DIR_ANTX_PROPERTIES           = USER_DIR + SEP + "antx.properties";
    public static final String TESTCASE_TEMP_DIR_BASE             = USER_HOME + SEP + "testcase_temp_dir";
    public static final String TESTCASE_DTD_DIR                   = TESTCASE_TEMP_DIR_BASE + SEP + "dtd";
    public static final String TESTCASE_TEMP_DIR                  = TESTCASE_TEMP_DIR_BASE
                                                                    + SEP
                                                                    + StringUtil.replaceNoWordChars(USER_DIR.replace(USER_HOME,
                                                                                                                     ""));
    public static final String TESTCASE_USER_TEMP_DIR             = TESTCASE_TEMP_DIR + SEP + "user_temp_dir";
    static {
        FileUtil.clearAndMakeDirs(TESTCASE_USER_TEMP_DIR);
    }

    public static final String TESTCASE_CLASSPATH                 = USER_DIR + File.separator + ".classpath";

    public static final String TESTCASE_RUN_CLASS_GROUPING        = "testcase.run.class.grouping";
    public static final String TESTCASE_RUN_CLASS_NO_GROUP        = /* NOT USE */"testcase.run.class.no.group";
    public static final String TESTCASE_RUN_GROUPING              = "testcase.run.grouping";
    public static final String TESTCASE_RUN_NO_GROUP              = "testcase.run.no.group";

    public static final String TESTCASE_LAZY_INIT                 = "testcase.lazy.init";

    public static final String TESTCASE_DUMP_MYSQL                = "testcase.dump.mysql";

    public static final String ANTX_FILE_KEY                      = "testcase.antx.file";
}
