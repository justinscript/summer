/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.test.datareader.impl;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;

import com.ms.commons.test.datareader.exception.ResourceNotFoundException;
import com.ms.commons.test.memorydb.MemoryDatabase;
import com.ms.commons.test.memorydb.MemoryField;
import com.ms.commons.test.memorydb.MemoryRow;
import com.ms.commons.test.memorydb.MemoryTable;

/**
 * @author zxc Apr 13, 2013 11:36:09 PM
 */
public class JsonReaderUtil extends BaseReaderUtil {

    public static MemoryDatabase readJson(String file) {
        MemoryDatabase result = new MemoryDatabase();
        String jsonStringData = readJsonDataFromFile(getAbsolutedPath(file));
        result.setTableList(parseJson2Bean(jsonStringData));
        return result;
    }

    @SuppressWarnings("rawtypes")
    protected static List<MemoryTable> parseJson2Bean(String jsonStringData) {
        List<MemoryTable> tableList = new ArrayList<MemoryTable>();

        // table array
        JSONObject tablesJsonObject = null;
        try {
            tablesJsonObject = JSONObject.fromObject(jsonStringData);
        } catch (JSONException e) {
            throw new RuntimeException("Invaild json string: " + jsonStringData, e);
        }

        // itrate table array
        for (Iterator iterator = tablesJsonObject.keySet().iterator(); iterator.hasNext();) {
            String tableName = (String) iterator.next();
            JSONArray rowsJsonObject = null;
            try {
                rowsJsonObject = tablesJsonObject.getJSONArray(tableName);
            } catch (JSONException e) {
                throw new RuntimeException("Row must be a array in a table: " + tablesJsonObject, e);
            }

            MemoryTable table = new MemoryTable(tableName);
            table.setRowList(getRowList(rowsJsonObject));
            tableList.add(table);
        }

        return tableList;
    }

    private static List<MemoryRow> getRowList(JSONArray rowsJsonObject) {
        List<MemoryRow> rowList = new ArrayList<MemoryRow>();

        if (rowsJsonObject.size() == 0) {
            return rowList;
        }

        // first row is field title.
        JSONArray titleJsonObject;
        try {
            titleJsonObject = rowsJsonObject.getJSONArray(0);
        } catch (JSONException e) {
            throw new RuntimeException("A row must be a array: " + rowsJsonObject, e);
        }
        List<String> fieldTitle = new ArrayList<String>();
        for (int i = 0; i < titleJsonObject.size(); i++) {
            Object title = titleJsonObject.get(i);
            fieldTitle.add(title.toString());
        }

        // iterate row array extends first row
        for (int i = 1; i < rowsJsonObject.size(); i++) {
            JSONArray rowJsonObject;
            try {
                rowJsonObject = rowsJsonObject.getJSONArray(i);
            } catch (JSONException e) {
                throw new RuntimeException("A row must be a array: " + rowsJsonObject, e);
            }

            MemoryRow row = new MemoryRow(getFieldList(rowJsonObject, fieldTitle));
            rowList.add(row);
        }

        return rowList;
    }

    private static List<MemoryField> getFieldList(JSONArray rowJsonObject, List<String> fieldTitle) {
        List<MemoryField> fieldList = new ArrayList<MemoryField>();

        if (rowJsonObject.size() != fieldTitle.size()) {
            throw new RuntimeException("Field size must match title size, title: " + fieldTitle + ", but field is: "
                                       + rowJsonObject);
        }

        for (int i = 0; i < rowJsonObject.size(); i++) {
            Object fieldJsonObject = rowJsonObject.get(i);
            String title = fieldTitle.get(i);
            MemoryField field = new MemoryField(title, null, fieldJsonObject);
            fieldList.add(field);
        }

        return fieldList;
    }

    protected static String readJsonDataFromFile(String fileFullPath) {
        StringBuffer data = new StringBuffer();
        FileReader fileReader = null;
        BufferedReader bufferedReader = null;
        try {
            fileReader = new FileReader(fileFullPath);
            bufferedReader = new BufferedReader(fileReader);
            String aLine;
            while ((aLine = bufferedReader.readLine()) != null) {
                data.append(aLine);
            }
        } catch (FileNotFoundException e) {
            throw new ResourceNotFoundException("Json file '" + fileFullPath + "' not found.", e);
        } catch (IOException e) {
            throw new RuntimeException("Error occured while read data from json file.", e);
        } finally {
            try {
                if (fileReader != null) {
                    fileReader.close();
                }
                if (bufferedReader != null) {
                    bufferedReader.close();
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return data.toString();
    }
}
