/*
 * Copyright 2017-2025 msun.com All right reserved. This software is the confidential and proprietary information of
 * msun.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with msun.com.
 */
package com.ms.commons.test.datawriter;

import java.io.OutputStream;

import com.ms.commons.test.memorydb.MemoryDatabase;

/**
 * @author zxc Apr 13, 2013 11:33:06 PM
 */
public interface DataWriter {

    void write(MemoryDatabase memoryDatabase, OutputStream outputStream, String encode);
}
