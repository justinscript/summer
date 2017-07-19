/*
 * Copyright 2017-2025 msun.com All right reserved. This software is the confidential and proprietary information of
 * msun.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with msun.com.
 */
package com.ms.commons.test.datawriter.impl;

import java.io.OutputStream;

import org.apache.commons.lang.NotImplementedException;

import com.ms.commons.test.datawriter.DataWriter;
import com.ms.commons.test.memorydb.MemoryDatabase;

/**
 * @author zxc Apr 13, 2013 11:33:42 PM
 */
public class JsonDataWriter implements DataWriter {

    public void write(MemoryDatabase memoryDatabase, OutputStream outputStream, String encode) {
        throw new NotImplementedException("Not implemented!");
    }
}
