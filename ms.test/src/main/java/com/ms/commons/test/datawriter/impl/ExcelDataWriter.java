/*
 * Copyright 2017-2025 msun.com All right reserved. This software is the confidential and proprietary information of
 * msun.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with msun.com.
 */
package com.ms.commons.test.datawriter.impl;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.ms.commons.test.datawriter.DataWriter;
import com.ms.commons.test.memorydb.MemoryDatabase;
import com.ms.commons.test.memorydb.MemoryField;
import com.ms.commons.test.memorydb.MemoryRow;
import com.ms.commons.test.memorydb.MemoryTable;

/**
 * @author zxc Apr 13, 2013 11:33:50 PM
 */
public class ExcelDataWriter implements DataWriter {

    public void write(MemoryDatabase memoryDatabase, OutputStream outputStream, String encode) {
        HSSFWorkbook book = new HSSFWorkbook();
        for (MemoryTable table : memoryDatabase.getTableList()) {
            writeTable(book, table);
        }
        try {
            book.write(outputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void writeTable(HSSFWorkbook book, MemoryTable table) {
        HSSFSheet sheet = book.createSheet(table.getName());
        if (table.getRowCount() > 0) {
            int rownum = 0;
            List<String> fields = new ArrayList<String>();
            for (MemoryField field : table.getRow(0).getFieldList()) {
                fields.add(field.getName());
            }
            writeRow(sheet.createRow(rownum++), fields);

            for (MemoryRow row : table.getRowList()) {
                List<String> values = new ArrayList<String>();
                for (MemoryField field : row.getFieldList()) {
                    values.add(field.getStringValue());
                }
                writeRow(sheet.createRow(rownum++), values);
            }
        }
    }

    private void writeRow(HSSFRow row, List<String> list) {
        short column = 0;
        for (String item : list) {
            HSSFCell cell = row.createCell(column++);
            cell.setCellValue(new HSSFRichTextString((item == null) ? "" : item));
        }
    }
}
