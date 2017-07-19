/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.test.datareader.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;

import com.ms.commons.test.common.convert.TypeConvertUtil;
import com.ms.commons.test.datareader.AbstractDataReader;
import com.ms.commons.test.datareader.exception.ResourceNotFoundException;
import com.ms.commons.test.external.jyaml.org.ho.yaml.Yaml;
import com.ms.commons.test.external.jyaml.org.ho.yaml.YamlStream;
import com.ms.commons.test.memorydb.MemoryDatabase;
import com.ms.commons.test.memorydb.MemoryField;
import com.ms.commons.test.memorydb.MemoryFieldType;
import com.ms.commons.test.memorydb.MemoryRow;
import com.ms.commons.test.memorydb.MemoryTable;

/**
 * @author zxc Apr 13, 2013 11:34:42 PM
 */
public class YamlReader extends AbstractDataReader {

    public String defaultExt() {
        return ".yaml";
    }

    @Override
    @SuppressWarnings("unchecked")
    protected MemoryDatabase internalRead(String resourceName) {
        MemoryDatabase result = new MemoryDatabase();
        result.setTableList(new ArrayList<MemoryTable>());
        String absPath = BaseReaderUtil.getAbsolutedPath(resourceName);
        try {
            String yamlString = FileUtils.readFileToString(new File(absPath));
            YamlStream<?> ystream = Yaml.loadStream(yamlString);

            for (Object y : ystream) {
                if (!(y instanceof Map<?, ?>)) {
                    throw new RuntimeException("Yaml format error: " + absPath);
                }
                Map<String, Object> ym = (Map<String, Object>) y;
                String tableName = (String) TypeConvertUtil.convert(String.class, ym.get("name"));
                if (tableName == null) {
                    throw new RuntimeException("Table name is null.");
                }
                MemoryTable mt = new MemoryTable(tableName);
                mt.setRowList(new ArrayList<MemoryRow>());
                Object vss = ym.get("data");
                if (!(vss instanceof Object[][])) {
                    throw new RuntimeException("Data is not Object[][], but is:" + vss);
                }
                Object[][] oss = (Object[][]) vss;

                Object[] title = oss[0];

                List<String> titleList = new ArrayList<String>();
                for (int i = 0; i < title.length; i++) {
                    titleList.add((String) TypeConvertUtil.convert(String.class, title[i]));
                }

                for (int i = 1; i < oss.length; i++) {
                    Object[] row = oss[i];
                    if (title.length != row.length) {
                        throw new RuntimeException("Row " + i + " size is not same to tile, in table: " + tableName);
                    }
                    List<MemoryField> mfl = new ArrayList<MemoryField>();
                    for (int x = 0; x < row.length; x++) {
                        mfl.add(new MemoryField(titleList.get(x), MemoryFieldType.Unknow, row[x]));
                    }
                    MemoryRow mr = new MemoryRow(mfl);
                    mt.getRowList().add(mr);
                }

                result.getTableList().add(mt);
            }
        } catch (FileNotFoundException e) {
            throw new ResourceNotFoundException("Yaml file '" + absPath + "' not found.", e);
        } catch (Exception e) {
            throw new RuntimeException("Error occured while read data from yaml file: " + absPath, e);
        }
        return result;
    }
}
