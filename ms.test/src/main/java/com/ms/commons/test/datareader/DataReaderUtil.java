/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.test.datareader;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.ms.commons.test.datareader.exception.DataReaderNotFoundException;
import com.ms.commons.test.datareader.impl.ExcelReader;
import com.ms.commons.test.datareader.impl.ExcelVerticalReader;
import com.ms.commons.test.datareader.impl.JsonReader;
import com.ms.commons.test.datareader.impl.TreeXmlReader;
import com.ms.commons.test.datareader.impl.WikiReader;
import com.ms.commons.test.datareader.impl.XmlReader;
import com.ms.commons.test.datareader.impl.YamlReader;

/**
 * @author zxc Apr 13, 2013 11:33:58 PM
 */
public class DataReaderUtil {

    static Logger                                  log = Logger.getLogger(DataReaderUtil.class);

    protected static final Map<String, DataReader> map = new HashMap<String, DataReader>();

    static {
        // default register excel reader
        register(DataReaderType.Excel, new ExcelReader());
        register(DataReaderType.ExcelVertical, new ExcelVerticalReader());
        register(DataReaderType.Xml, new XmlReader());
        register(DataReaderType.Json, new JsonReader());
        register(DataReaderType.Wiki, new WikiReader());
        register(DataReaderType.TreeXml, new TreeXmlReader());
        register(DataReaderType.Yaml, new YamlReader());
    }

    public static void register(DataReaderType type, DataReader dataReader) {
        register(type.getType(), dataReader);
    }

    public static void register(String type, DataReader dataReader) {
        synchronized (map) {
            try {
                dataReader.init();
                map.put(type, dataReader);
                log.info("Data reader `" + dataReader.getClass().getName() + "` registered.");
            } catch (Throwable t) {
                log.error("Data reader `" + dataReader.getClass().getName() + "` register failed.", t);
            }
        }
    }

    public static String getDefaultExt(DataReaderType type) {
        return getDataReader(type.getType()).defaultExt();
    }

    public static Object readData(DataReaderType type, String resourceName) {
        return readData(type.getType(), resourceName);
    }

    public static String getDefaultExt(String type) {
        return getDataReader(type).defaultExt();
    }

    public static Object readData(String type, String resourceName) {
        return getDataReader(type).read(resourceName);
    }

    protected static DataReader getDataReader(String type) {
        DataReader dataReader = null;
        synchronized (map) {
            dataReader = map.get(type);
        }
        if (dataReader == null) {
            log.error("Data reader of type `" + type + "` cannot be found.");
            throw new DataReaderNotFoundException("Data reader of type `" + type + "` cannot be found.");
        }
        return dataReader;
    }
}
