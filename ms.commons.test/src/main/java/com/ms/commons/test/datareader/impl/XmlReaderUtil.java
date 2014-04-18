/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.test.datareader.impl;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.ms.commons.test.datareader.exception.ResourceNotFoundException;
import com.ms.commons.test.memorydb.MemoryDatabase;
import com.ms.commons.test.memorydb.MemoryField;
import com.ms.commons.test.memorydb.MemoryFieldType;
import com.ms.commons.test.memorydb.MemoryRow;
import com.ms.commons.test.memorydb.MemoryTable;

/**
 * @author zxc Apr 13, 2013 11:34:52 PM
 */
public class XmlReaderUtil extends BaseReaderUtil {

    static final Logger           log               = Logger.getLogger(XmlReaderUtil.class);
    static final SimpleDateFormat shortFormat       = new SimpleDateFormat("yyyy-MM-dd");
    static final SimpleDateFormat longFormat        = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    static final int              SHORT_DATE_LENGTH = 10;
    static final int              LONG_DATE_LENGTH  = 19;

    @SuppressWarnings("unchecked")
    public static MemoryDatabase readDocument(String file) {
        try {
            SAXReader reader = new SAXReader();
            Document doc = reader.read(new BufferedInputStream(new FileInputStream(getAbsolutedPath(file))));
            List<Element> elements = doc.getRootElement().elements("table");
            List<MemoryTable> tableList = new ArrayList<MemoryTable>();
            for (Element tableE : elements) {
                tableList.add(readTable(tableE));
            }

            MemoryDatabase database = new MemoryDatabase();
            database.setTableList(tableList);
            return database;
        } catch (FileNotFoundException e) {
            throw new ResourceNotFoundException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    protected static MemoryTable readTable(Element tableE) {
        String name = tableE.attributeValue("name");
        MemoryTable table = new MemoryTable(name);

        List<Element> elements = tableE.elements("row");
        List<MemoryRow> rowList = new ArrayList<MemoryRow>();
        for (Element rowE : elements) {
            rowList.add(readRow(rowE));
        }
        table.setRowList(rowList);
        return table;
    }

    @SuppressWarnings("unchecked")
    protected static MemoryRow readRow(Element rowE) {
        if (rowE == null) return null;
        List<Element> elements = rowE.elements("field");
        if (elements.isEmpty()) return null;

        List<MemoryField> fieldList = new ArrayList<MemoryField>(elements.size());
        for (Element fieldE : elements) {
            fieldList.add(readField(fieldE));
        }
        return new MemoryRow(fieldList);
    }

    protected static MemoryField readField(Element fieldE) {
        String name = fieldE.attributeValue("name");
        MemoryFieldType type = MemoryFieldType.make(fieldE.attributeValue("type"));
        Object value = null;
        switch (type) {
            case Date:
                String dateStr = fieldE.getStringValue();
                if (StringUtils.isBlank(dateStr)) break;
                try {
                    if (dateStr.length() == SHORT_DATE_LENGTH) {
                        value = shortFormat.parse(dateStr);
                    } else if (dateStr.length() == LONG_DATE_LENGTH) {
                        value = longFormat.parse(dateStr);
                    }
                } catch (ParseException e) {
                    if (log.isDebugEnabled()) log.debug("");
                }
                break;
            case Number:
                String number = fieldE.getStringValue();
                if (NumberUtils.isNumber(number)) {
                    value = NumberUtils.createInteger(number);
                }
                break;
            default:
                value = fieldE.getStringValue();
                break;
        }
        return new MemoryField(name, type, value);
    }

    public static void main(String[] args) {
        MemoryDatabase db = readDocument("/home/nick/1949.framework-test/data.xml");
        System.out.println(db.getTableList().get(0).getRowList().get(0).getFieldList().get(2).getValue());
    }

}
