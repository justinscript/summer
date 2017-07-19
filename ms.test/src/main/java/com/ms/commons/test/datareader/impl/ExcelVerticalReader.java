/*
 * Copyright 2017-2025 msun.com All right reserved. This software is the confidential and proprietary information of
 * msun.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with msun.com.
 */
package com.ms.commons.test.datareader.impl;

import com.ms.commons.test.datareader.AbstractDataReader;
import com.ms.commons.test.memorydb.MemoryDatabase;

/**
 * @author zxc Apr 13, 2013 11:36:36 PM
 */
public class ExcelVerticalReader extends AbstractDataReader {

    public static final String DEFAULT_EXT = "_v.xls";

    public String defaultExt() {
        return DEFAULT_EXT;
    }

    @Override
    protected MemoryDatabase internalRead(String resourceName) {
        return ExcelReadUtil.readExcel(resourceName, true);
    }
}
