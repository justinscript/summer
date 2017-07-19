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
import java.util.List;

import com.ms.commons.test.datareader.exception.ResourceNotFoundException;
import com.ms.commons.test.memorydb.MemoryDatabase;
import com.ms.commons.test.memorydb.MemoryField;
import com.ms.commons.test.memorydb.MemoryRow;
import com.ms.commons.test.memorydb.MemoryTable;

import fit.Parse;
import fit.exception.FitParseException;
import fitlibrary.table.Cell;
import fitlibrary.table.Row;
import fitlibrary.table.Table;
import fitlibrary.table.Tables;
import fitlibrary.utility.ParseUtility;
import fitnesse.wiki.WikiPage;
import fitnesse.wikitext.WidgetBuilder;
import fitnesse.wikitext.widgets.AliasLinkWidget;
import fitnesse.wikitext.widgets.AnchorDeclarationWidget;
import fitnesse.wikitext.widgets.AnchorMarkerWidget;
import fitnesse.wikitext.widgets.BoldWidget;
import fitnesse.wikitext.widgets.CenterWidget;
import fitnesse.wikitext.widgets.EmailWidget;
import fitnesse.wikitext.widgets.EvaluatorWidget;
import fitnesse.wikitext.widgets.HashWidget;
import fitnesse.wikitext.widgets.HeaderWidget;
import fitnesse.wikitext.widgets.HruleWidget;
import fitnesse.wikitext.widgets.ImageWidget;
import fitnesse.wikitext.widgets.ItalicWidget;
import fitnesse.wikitext.widgets.LinkWidget;
import fitnesse.wikitext.widgets.ListWidget;
import fitnesse.wikitext.widgets.LiteralWidget;
import fitnesse.wikitext.widgets.NoteWidget;
import fitnesse.wikitext.widgets.ParentWidget;
import fitnesse.wikitext.widgets.PlainTextTableWidget;
import fitnesse.wikitext.widgets.PreformattedWidget;
import fitnesse.wikitext.widgets.StandardTableWidget;
import fitnesse.wikitext.widgets.StrikeWidget;
import fitnesse.wikitext.widgets.StyleWidget;
import fitnesse.wikitext.widgets.VariableDefinitionWidget;
import fitnesse.wikitext.widgets.VariableWidget;
import fitnesse.wikitext.widgets.WidgetRoot;

/**
 * @author zxc Apr 13, 2013 11:35:11 PM
 */
public class WikiReaderUtil extends BaseReaderUtil {

    public static MemoryDatabase readWiki(String file) {
        MemoryDatabase result = new MemoryDatabase();
        String wikiStringData = readWikiDataFromFile(getAbsolutedPath(file));
        result.setTableList(parseWiki2Bean(wikiStringData));
        return result;
    }

    protected static List<MemoryTable> parseWiki2Bean(String wikiStringData) {
        List<MemoryTable> tableList = new ArrayList<MemoryTable>();

        // table array
        Tables tables = WikiParser.parse(wikiStringData);
        if (tables.size() == 0) {
            throw new RuntimeException("There's no data defined in wiki string: " + wikiStringData);
        }

        // itrate table array
        for (int i = 0; i < tables.size(); i++) {
            Table table = tables.table(i);

            Row firstRow = table.row(0);
            String tableName = firstRow.cell(0).text();

            MemoryTable memoryTable = new MemoryTable(tableName);
            memoryTable.setRowList(getRowList(table));
            tableList.add(memoryTable);
        }

        return tableList;
    }

    private static List<MemoryRow> getRowList(Table table) {
        List<MemoryRow> rowList = new ArrayList<MemoryRow>();

        // first row is table name.
        if (table.size() == 1) {
            return rowList;
        }

        // second row is field title.
        Row titleRow = table.row(1);
        List<String> fieldTitle = new ArrayList<String>();
        for (int i = 0; i < titleRow.size(); i++) {
            Cell title = titleRow.cell(i);
            fieldTitle.add(title.text());
        }

        // iterate row array extends first row & second row
        for (int i = 2; i < table.size(); i++) {
            Row row = table.row(i);

            MemoryRow memoryRow = new MemoryRow(getFieldList(row, fieldTitle));
            rowList.add(memoryRow);
        }

        return rowList;
    }

    private static List<MemoryField> getFieldList(Row row, List<String> fieldTitle) {
        List<MemoryField> fieldList = new ArrayList<MemoryField>();

        if (row.size() != fieldTitle.size()) {
            throw new RuntimeException("Field size must match title size, title: " + fieldTitle + ", but field is: "
                                       + row);
        }

        for (int i = 0; i < row.size(); i++) {
            Cell cell = row.cell(i);
            String title = fieldTitle.get(i);
            MemoryField field = new MemoryField(title, null, cell.text());
            fieldList.add(field);
        }

        return fieldList;
    }

    protected static String readWikiDataFromFile(String fileFullPath) {
        StringBuffer data = new StringBuffer();
        FileReader fileReader = null;
        BufferedReader bufferedReader = null;
        try {
            fileReader = new FileReader(fileFullPath);
            bufferedReader = new BufferedReader(fileReader);
            String aLine;
            while ((aLine = bufferedReader.readLine()) != null) {
                data.append(aLine).append("\n");
            }
        } catch (FileNotFoundException e) {
            throw new ResourceNotFoundException("Wiki file '" + fileFullPath + "' not found.", e);
        } catch (IOException e) {
            throw new RuntimeException("Error occured while read data from wiki file.", e);
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

    public static class WikiParser {

        public static Tables parse(String content) {
            String htmlContent = null;
            try {
                ParentWidget root = new WidgetRoot(content, (WikiPage) null, htmlWidgetBuilder);
                htmlContent = root.render();
            } catch (Exception e) {
                throw new RuntimeException("Invalid wiki string:" + content, e);
            }

            Parse parse = null;
            try {
                parse = new Parse(ParseUtility.tabulize(htmlContent));
            } catch (FitParseException e) {
                throw new RuntimeException("Invalid wiki string:" + content, e);
            }
            Tables tables = new Tables(parse);
            return tables;
        }

        static WidgetBuilder htmlWidgetBuilder = new WidgetBuilder() {

                                                   {

                                                       // this.addWidgetClass(CommentWidget.class);
                                                       this.addWidgetClass(LiteralWidget.class);
                                                       // this.addWidgetClass(WikiWordWidget.class);
                                                       this.addWidgetClass(BoldWidget.class);
                                                       this.addWidgetClass(ItalicWidget.class);
                                                       this.addWidgetClass(PreformattedWidget.class);
                                                       this.addWidgetClass(HruleWidget.class);
                                                       this.addWidgetClass(HeaderWidget.class);
                                                       this.addWidgetClass(CenterWidget.class);
                                                       this.addWidgetClass(NoteWidget.class);
                                                       this.addWidgetClass(StyleWidget.ParenFormat.class);
                                                       this.addWidgetClass(StyleWidget.BraceFormat.class);
                                                       this.addWidgetClass(StyleWidget.BracketFormat.class);
                                                       // this.addWidgetClass(TableWidget.class);
                                                       this.addWidgetClass(StandardTableWidget.class);
                                                       this.addWidgetClass(PlainTextTableWidget.class);
                                                       this.addWidgetClass(ListWidget.class);
                                                       // this.addWidgetClass(ClasspathWidget.class);
                                                       this.addWidgetClass(ImageWidget.class);
                                                       this.addWidgetClass(LinkWidget.class);
                                                       // this.addWidgetClass(TOCWidget.class);
                                                       this.addWidgetClass(AliasLinkWidget.class);
                                                       // this.addWidgetClass(VirtualWikiWidget.class);
                                                       this.addWidgetClass(StrikeWidget.class);
                                                       // this.addWidgetClass(LastModifiedWidget.class);
                                                       // this.addWidgetClass(TodayWidget.class);
                                                       // this.addWidgetClass(XRefWidget.class);
                                                       // this.addWidgetClass(MetaWidget.class);
                                                       this.addWidgetClass(EmailWidget.class);
                                                       this.addWidgetClass(AnchorDeclarationWidget.class);
                                                       this.addWidgetClass(AnchorMarkerWidget.class);
                                                       // this.addWidgetClass(CollapsableWidget.class);
                                                       // this.addWidgetClass(IncludeWidget.class);
                                                       this.addWidgetClass(VariableDefinitionWidget.class);
                                                       this.addWidgetClass(EvaluatorWidget.class);
                                                       this.addWidgetClass(VariableWidget.class);
                                                       this.addWidgetClass(HashWidget.class);
                                                   }
                                               };
    }
}
