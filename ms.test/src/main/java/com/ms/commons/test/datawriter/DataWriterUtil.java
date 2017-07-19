/*
 * Copyright 2017-2025 msun.com All right reserved. This software is the confidential and proprietary information of
 * msun.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with msun.com.
 */
package com.ms.commons.test.datawriter;

import java.util.HashMap;
import java.util.Map;

import com.ms.commons.test.datawriter.impl.ExcelDataWriter;
import com.ms.commons.test.datawriter.impl.JsonDataWriter;
import com.ms.commons.test.datawriter.impl.WikiDataWriter;
import com.ms.commons.test.datawriter.impl.XmlDataWriter;

/**
 * @author zxc Apr 13, 2013 11:32:46 PM
 */
public class DataWriterUtil {

    protected static final Map<DataWriterType, DataWriter> map = new HashMap<DataWriterType, DataWriter>();

    static {
        register(DataWriterType.Excel, new ExcelDataWriter());
        register(DataWriterType.Xml, new XmlDataWriter());
        register(DataWriterType.Json, new JsonDataWriter());
        register(DataWriterType.Wiki, new WikiDataWriter());
    }

    synchronized public static void register(DataWriterType type, DataWriter dataWriter) {
        map.put(type, dataWriter);
    }

    synchronized public static DataWriter getDataWriter(DataWriterType type) {
        return map.get(type);
    }
}
