/*
 * Copyright 2017-2025 msun.com All right reserved. This software is the confidential and proprietary information of
 * msun.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with msun.com.
 */
package com.ms.commons.test.datareader.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

import com.ms.commons.test.common.ExceptionUtil;
import com.ms.commons.test.datareader.exception.ResourceNotFoundException;
import com.ms.commons.test.memorydb.MemoryDatabase;
import com.ms.commons.test.memorydb.MemoryField;
import com.ms.commons.test.memorydb.MemoryFieldType;
import com.ms.commons.test.memorydb.MemoryRow;
import com.ms.commons.test.memorydb.MemoryTable;

/**
 * @author zxc Apr 13, 2013 11:36:49 PM
 */
public class ExcelReadUtil extends BaseReaderUtil {

    static Logger log = Logger.getLogger(ExcelReadUtil.class);

    public static MemoryDatabase readExcel(String fileName, boolean isVertical) {
        try {
            String absPath = getAbsolutedPath(fileName);
            File file = new File(absPath);
            absPath = getOriDataFile(absPath); // get Orient path
            if (log.isInfoEnabled()) log.info("Switch: Read file `" + absPath + "` to memory database.");
            // if (file.exists()) {
            // checkDataFile(absPath);// check data file if out of date
            // }
            FileInputStream input = new FileInputStream(file);
            POIFSFileSystem fs = new POIFSFileSystem(input);
            HSSFWorkbook wb = new HSSFWorkbook(fs);
            MemoryDatabase database = new MemoryDatabase();
            database.setTableList(readSheets(wb, isVertical));
            return database;
        } catch (FileNotFoundException e) {
            throw new ResourceNotFoundException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected static List<MemoryTable> readSheets(HSSFWorkbook wb) {
        return readSheets(wb, false);
    }

    private static List<MemoryTable> readSheets(HSSFWorkbook wb, boolean isVerticalStyle) {
        List<MemoryTable> tableList = new ArrayList<MemoryTable>();
        for (int i = 0; i < wb.getNumberOfSheets(); i++) {
            HSSFSheet sheet = wb.getSheetAt(i);
            String sheetName = wb.getSheetName(i);
            if (sheet.getPhysicalNumberOfRows() > 0) {
                if (isVerticalStyle) {
                    tableList.add(readSheetWithVerticalStyle(sheetName, sheet));
                } else {
                    tableList.add(readSheet(sheetName, sheet));
                }
            }
        }
        return tableList;
    }

    protected static MemoryTable readSheet(String name, HSSFSheet sheet) {
        HSSFRow headRow = sheet.getRow(0);
        int maxRows = sheet.getPhysicalNumberOfRows();
        String[] headFields = readSheetHead(headRow);

        MemoryTable table = new MemoryTable(name.trim());
        List<MemoryRow> rowList = new ArrayList<MemoryRow>();
        for (int i = 1; i < maxRows; i++) {
            HSSFRow row = sheet.getRow(i);
            MemoryRow memoryRow = readSheetRow(headFields, row);
            if (memoryRow == null) {
                break;
            }
            rowList.add(memoryRow);
        }
        table.setRowList(rowList);
        return table;
    }

    /**
     * A column means an item in table. With horizontal style, in contrast, a row represents an item in table.
     * 
     * @author Qiu Shuo
     */
    private static MemoryTable readSheetWithVerticalStyle(String name, HSSFSheet sheet) {
        MemoryTable table = new MemoryTable(name.trim());
        List<MemoryRow> itemList = new ArrayList<MemoryRow>();
        int maxRows = sheet.getPhysicalNumberOfRows();
        int maxItemNumPlusOne = 0;
        // get maxItemNumPlusOne
        {
            for (int i = 0; i < maxRows; i++) {
                HSSFRow row = sheet.getRow(i);
                int cur = row.getLastCellNum();
                maxItemNumPlusOne = (cur > maxItemNumPlusOne) ? cur : maxItemNumPlusOne;
            }
        }
        for (int i = 0; i < maxRows; i++) {
            HSSFRow row = sheet.getRow(i);
            HSSFCell columnNameCell = row.getCell((short) 0);
            String columnName = columnNameCell.getRichStringCellValue().getString();
            for (short j = 1; j < maxItemNumPlusOne; j++) {
                HSSFCell cell = row.getCell(j);
                MemoryField field = readCellValue(columnName, cell);
                while (itemList.size() <= j - 1) {
                    itemList.add(new MemoryRow(new ArrayList<MemoryField>()));
                }
                MemoryRow item = itemList.get(j - 1);
                item.getFieldList().add(field);
            }
        }
        table.setRowList(itemList);
        return table;
    }

    protected static MemoryRow readSheetRow(String[] headFields, HSSFRow row) {
        if (row == null) {
            return null;
        }

        boolean isAllFieldEmpty = true;
        List<MemoryField> fieldList = new ArrayList<MemoryField>();
        for (short i = 0; i < headFields.length; i++) {
            MemoryField field = readCellValue(headFields[i], row.getCell(i));
            if (field.getType() != MemoryFieldType.Null) {
                isAllFieldEmpty = false;
            }
            fieldList.add(field);
        }
        if (isAllFieldEmpty) {
            return null;
        } else {
            return new MemoryRow(fieldList);
        }
    }

    protected static String[] readSheetHead(HSSFRow headRow) {
        List<String> headList = new ArrayList<String>();
        int n = headRow.getPhysicalNumberOfCells();
        for (Integer i = 0; i < n; i++) {
            HSSFCell cell = headRow.getCell(i.shortValue());
            String head = readCellValue(null, cell).getStringValue();
            if (head == null || head.trim().length() == 0) {
                break;
            }
            headList.add(readCellValue(null, cell).getStringValue());
        }
        return headList.toArray(new String[] {});
    }

    /**
     * @param name column name of table in which this cell locates.
     */
    @SuppressWarnings("deprecation")
    protected static MemoryField readCellValue(String name, HSSFCell cell) {

        MemoryField field = null;
        if (cell == null) {
            field = new MemoryField(name, MemoryFieldType.Null);
        } else {
            switch (cell.getCellType()) {
                case HSSFCell.CELL_TYPE_NUMERIC:
                case HSSFCell.CELL_TYPE_FORMULA:
                    if (getCellValueForFormula(cell)) {
                        field = new MemoryField(name, MemoryFieldType.Date, cell.getDateCellValue().toString());
                    } else {
                        field = new MemoryField(name, MemoryFieldType.Number, cell.getNumericCellValue());
                    }
                    break;

                case HSSFCell.CELL_TYPE_STRING:
                    field = new MemoryField(name, MemoryFieldType.String, cell.getStringCellValue());
                    break;

                case HSSFCell.CELL_TYPE_BLANK:
                    field = new MemoryField(name, MemoryFieldType.Null);
                    break;
                default:
                    field = new MemoryField(name, MemoryFieldType.Unknow, cell.getStringCellValue());
                    break;
            }
        }

        return field;
    }

    private static boolean getCellValueForFormula(HSSFCell cell) {
        boolean isDateCell;
        if (isNewerPoi()) { // in poi 3.6
            try {
                Class<?> clazzDateUtil = Class.forName("org.apache.poi.ss.usermodel.DateUtil");
                Class<?> clazzCell = Class.forName("org.apache.poi.ss.usermodel.Cell");
                Method method = clazzDateUtil.getDeclaredMethod("isCellDateFormatted", new Class[] { clazzCell });
                method.setAccessible(true);
                isDateCell = ((Boolean) method.invoke(null, new Object[] { cell })).booleanValue();
            } catch (Exception e) {
                throw ExceptionUtil.wrapToRuntimeException(e);
            }
        } else {
            isDateCell = HSSFDateUtil.isCellDateFormatted(cell);
        }
        return isDateCell;
    }

    private static boolean isNewerPoi() {
        try {
            Class.forName("org.apache.poi.ss.usermodel.Cell");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }
}
