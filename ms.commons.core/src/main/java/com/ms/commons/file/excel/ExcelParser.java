/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.file.excel;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

/**
 * @author zxc Jul 8, 2013 5:00:00 PM
 */
public class ExcelParser {

    HSSFSheet                   m_sheet;
    int                         m_iNbRows;
    int                         m_iCurrentRow = 0;
    private static final String JAVA_TOSTRING = "EEE MMM dd HH:mm:ss zzz yyyy";

    public ExcelParser(HSSFSheet sheet) {
        m_sheet = sheet;
        m_iNbRows = sheet.getPhysicalNumberOfRows();
    }

    @SuppressWarnings({ "deprecation", "unused" })
    public String[] splitLine() throws Exception {
        if (m_iCurrentRow == m_iNbRows) return null;

        HSSFRow row = m_sheet.getRow(m_iCurrentRow);
        if (row == null) {
            return null;
        } else {
            int cellIndex = 0;
            int noOfCells = row.getPhysicalNumberOfCells();
            short firstCellNum = row.getFirstCellNum();
            short lastCellNum = row.getLastCellNum();
            String[] values = new String[lastCellNum];

            if (firstCellNum >= 0 && lastCellNum >= 0) {
                for (short iCurrent = firstCellNum; iCurrent < lastCellNum; iCurrent++) {
                    HSSFCell cell = (HSSFCell) row.getCell(iCurrent);
                    if (cell == null) {
                        values[iCurrent] = StringUtils.EMPTY;
                        cellIndex++;
                        continue;
                    } else {
                        switch (cell.getCellType()) {

                            case HSSFCell.CELL_TYPE_NUMERIC:
                                double value = cell.getNumericCellValue();
                                if (HSSFDateUtil.isCellDateFormatted(cell)) {
                                    if (HSSFDateUtil.isValidExcelDate(value)) {
                                        Date date = HSSFDateUtil.getJavaDate(value);
                                        SimpleDateFormat dateFormat = new SimpleDateFormat(JAVA_TOSTRING);
                                        values[iCurrent] = dateFormat.format(date);
                                    } else {
                                        throw new Exception("Invalid Date value found at row number " + row.getRowNum()
                                                            + " and column number " + cell.getCellNum());
                                    }
                                } else {
                                    values[iCurrent] = value + StringUtils.EMPTY;
                                }
                                break;

                            case HSSFCell.CELL_TYPE_STRING:
                                values[iCurrent] = cell.getStringCellValue();
                                break;

                            case HSSFCell.CELL_TYPE_BLANK:
                                values[iCurrent] = null;
                                break;

                            default:
                                values[iCurrent] = null;
                        }
                    }
                }
            }
            m_iCurrentRow++;
            return values;
        }
    }

    public static void main(String args[]) {
        HSSFWorkbook workBook = null;
        File file = new File("/home/zxc/back_word/dump_word/33_2013_07_07.xls");
        InputStream excelDocumentStream = null;
        try {
            excelDocumentStream = new FileInputStream(file);
            POIFSFileSystem fsPOI = new POIFSFileSystem(new BufferedInputStream(excelDocumentStream));
            workBook = new HSSFWorkbook(fsPOI);
            ExcelParser parser = new ExcelParser(workBook.getSheetAt(0));
            String[] res;
            while ((res = parser.splitLine()) != null) {
                if (res.length == 7) {
                    String resStr = StringUtils.join(res);
                    if (resStr.contains("关键词")) {
                        continue;
                    }
                }
                for (int i = 0; i < res.length; i++) {
                    System.out.println("Token Found [" + res[i] + "]");
                }
            }
            excelDocumentStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
