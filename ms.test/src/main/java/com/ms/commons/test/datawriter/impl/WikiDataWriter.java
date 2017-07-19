/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.test.datawriter.impl;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import com.ms.commons.test.common.ExceptionUtil;
import com.ms.commons.test.datawriter.DataWriter;
import com.ms.commons.test.memorydb.MemoryDatabase;
import com.ms.commons.test.memorydb.MemoryField;
import com.ms.commons.test.memorydb.MemoryRow;
import com.ms.commons.test.memorydb.MemoryTable;

/**
 * @author zxc Apr 13, 2013 11:33:28 PM
 */
public class WikiDataWriter implements DataWriter {

    public void write(MemoryDatabase memoryDatabase, OutputStream outputStream, String encode) {
        System.out.println("WARNING: we will remove '|' and '\\r\\n'(\\r or \\n)");
        PrintWriter pw = null;
        try {
            pw = new PrintWriter(new OutputStreamWriter(outputStream, encode));
            for (int i = 0; i < memoryDatabase.getTableCount(); i++) {
                if (i > 0) {
                    pw.println();
                }

                MemoryTable table = memoryDatabase.getTable(i);
                pw.println("|" + escapeForWiki(table.getName()) + "|");
                if (table.getRowCount() > 0) {
                    List<String> headList = getTableHeadList(table);
                    printRow(pw, headList);
                    for (MemoryRow row : table.getRowList()) {
                        printRow(pw, getTableRowList(row, headList));
                    }
                }
            }
            pw.flush();
        } catch (Exception e) {
            throw ExceptionUtil.wrapToRuntimeException(e);
        }
    }

    private static void printRow(PrintWriter pw, List<String> list) {
        pw.print("|");
        for (int i = 0; i < list.size(); i++) {
            pw.print(escapeForWiki(list.get(i)));
            pw.print("|");
        }
        pw.println();
    }

    private static List<String> getTableRowList(MemoryRow row, List<String> headList) {
        List<String> list = new ArrayList<String>();
        for (String fn : headList) {
            list.add(row.getField(fn).getStringValue());
        }
        return list;
    }

    private static List<String> getTableHeadList(MemoryTable table) {
        List<String> list = new ArrayList<String>();
        for (MemoryField field : table.getRow(0).getFieldList()) {
            list.add(field.getName());
        }
        return list;
    }

    private static String escapeForWiki(String str) {
        if (str == null) {
            return " ";
        }
        return " " + str.replace("|", "").replace("\r\n", "").replace("\r", "").replace("\n", "") + " ";
    }
}
