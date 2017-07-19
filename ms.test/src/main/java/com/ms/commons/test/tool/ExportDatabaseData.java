/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.test.tool;

import java.io.File;
import java.io.FileOutputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import javax.sql.DataSource;

import net.sf.jsqlparser.parser.CCJSqlParserManager;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.StatementVisitor;
import net.sf.jsqlparser.statement.create.table.CreateTable;
import net.sf.jsqlparser.statement.delete.Delete;
import net.sf.jsqlparser.statement.drop.Drop;
import net.sf.jsqlparser.statement.insert.Insert;
import net.sf.jsqlparser.statement.replace.Replace;
import net.sf.jsqlparser.statement.select.FromItemVisitor;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.select.SelectVisitor;
import net.sf.jsqlparser.statement.select.SubJoin;
import net.sf.jsqlparser.statement.select.SubSelect;
import net.sf.jsqlparser.statement.select.Union;
import net.sf.jsqlparser.statement.truncate.Truncate;
import net.sf.jsqlparser.statement.update.Update;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.mutable.MutableObject;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;

import com.ms.commons.test.common.ExceptionUtil;
import com.ms.commons.test.common.convert.impl.database.UnCnStringTypeConverter;
import com.ms.commons.test.common.convert.impl.database.UnUTF8StringTypeConverter;
import com.ms.commons.test.datawriter.DataWriter;
import com.ms.commons.test.datawriter.DataWriterType;
import com.ms.commons.test.datawriter.DataWriterUtil;
import com.ms.commons.test.memorydb.MemoryDatabase;
import com.ms.commons.test.memorydb.MemoryField;
import com.ms.commons.test.memorydb.MemoryFieldType;
import com.ms.commons.test.memorydb.MemoryRow;
import com.ms.commons.test.memorydb.MemoryTable;
import com.ms.commons.test.tool.exportdata.ConsoleCmd;
import com.ms.commons.test.tool.exportdata.ConsoleCmdAnaylisysUtil;
import com.ms.commons.test.tool.exportdata.DatabaseConfigItem;
import com.ms.commons.test.tool.exportdata.DatabasePropertiesLoader;
import com.ms.commons.test.tool.exportdata.EncodeMapping;
import com.ms.commons.test.tool.exportdata.cmd.EncodeCmd;
import com.ms.commons.test.tool.exportdata.cmd.EncodeType;
import com.ms.commons.test.tool.exportdata.cmd.ExportCmd;
import com.ms.commons.test.tool.exportdata.cmd.encodecmd.EncodeOperation;
import com.ms.commons.test.tool.exportdata.cmd.encodecmd.TableFields;
import com.ms.commons.test.tool.exportdata.cmdana.CmdAnalysisor;
import com.ms.commons.test.tool.exportdata.exception.MessageException;
import com.ms.commons.test.tool.util.ConsoleUtil;

/**
 * @author zxc Apr 13, 2013 11:43:14 PM
 */
public class ExportDatabaseData {

    private static EncodeMapping encodeMapping = new EncodeMapping();
    static {
        encodeMapping.tryRead();
    }
    private static AtomicInteger GLOBAL_COUNT  = new AtomicInteger();

    public static void main(String[] args) {
        printWelcomeMessage();

        try {
            List<DatabaseConfigItem> confItems = DatabasePropertiesLoader.getDatabaseConfigItems();
            DatabaseConfigItem item = selectDatabaseConfigItem(confItems);
            if (item != null) {
                JdbcTemplate jdbcTemplate = getJdbcTemplate(item);

                if (jdbcTemplate != null) {
                    doRun(jdbcTemplate);
                }
            }
        } catch (Exception e) {
            if (e instanceof MessageException) {
                System.out.println(e.getMessage());
            } else {
                System.out.println("Unknow error: " + e.getMessage());
            }
        }

        System.out.println("Ended!");
    }

    private static void doRun(JdbcTemplate jdbcTemplate) {
        while (true) {
            try {
                System.out.print("SQL>");
                String line = ConsoleUtil.readLine();

                if ((line == null) || (StringUtils.isBlank(line))) {
                    continue;
                }
                if (Arrays.asList("exit", "quit").contains(line.trim().toLowerCase())) {
                    return;
                }
                ConsoleCmd cc = ConsoleCmdAnaylisysUtil.anaConsoleCmd(line);
                if (cc != null) {
                    if (cc instanceof ExportCmd) {
                        dealExportCmd(jdbcTemplate, (ExportCmd) cc);
                    } else if (cc instanceof EncodeCmd) {
                        dealEncodeCmd((EncodeCmd) cc);
                    }
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
                System.out.println("    Command sample:");
                for (CmdAnalysisor cmdAnalysisor : ConsoleCmdAnaylisysUtil.cmdAnalysisorList) {
                    for (String exampleCommand : cmdAnalysisor.exampleCommandList()) {
                        System.out.println("                   " + exampleCommand);
                    }
                }
            }
        }
    }

    private static void dealEncodeCmd(EncodeCmd ec) throws Exception {
        System.out.println("Command: " + ec);
        if (ec.getEncodeOperation() == EncodeOperation.Show) {
            System.out.println("GBK encoding:");
            for (TableFields tf : encodeMapping.getGbkMap().values()) {
                System.out.println("             " + tf);
            }
            System.out.println("UTF-8 encoding:");
            for (TableFields tf : encodeMapping.getUtf8Map().values()) {
                System.out.println("             " + tf);
            }
        } else if (ec.getEncodeOperation() == EncodeOperation.Add) {
            if (ec.getEncodeType() == EncodeType.GBK) {
                removeFields(encodeMapping.getUtf8Map(), ec.getTableFieldList());
                addFields(encodeMapping.getGbkMap(), ec.getTableFieldList());
            } else if (ec.getEncodeType() == EncodeType.UTF8) {
                removeFields(encodeMapping.getGbkMap(), ec.getTableFieldList());
                addFields(encodeMapping.getUtf8Map(), ec.getTableFieldList());
            }
            encodeMapping.tryWrite();
        } else if (ec.getEncodeOperation() == EncodeOperation.Remove) {
            if (ec.getEncodeType() == EncodeType.GBK) {
                removeFields(encodeMapping.getGbkMap(), ec.getTableFieldList());
            } else if (ec.getEncodeType() == EncodeType.UTF8) {
                removeFields(encodeMapping.getUtf8Map(), ec.getTableFieldList());
            }
            encodeMapping.tryWrite();
        }
    }

    private static void addFields(Map<String, TableFields> map, List<TableFields> tableFieldList) {
        for (TableFields tfs : tableFieldList) {
            if (map.containsKey(tfs.getTable())) {
                map.get(tfs.getTable()).getFields().addAll(tfs.getFields());
            } else {
                map.put(tfs.getTable(), tfs);
            }
        }
    }

    private static void removeFields(Map<String, TableFields> map, List<TableFields> tableFieldList) {
        for (TableFields tfs : tableFieldList) {
            if (map.containsKey(tfs.getTable())) {
                map.get(tfs.getTable()).getFields().removeAll(tfs.getFields());
            }
        }
    }

    private static void dealExportCmd(JdbcTemplate jdbcTemplate, ExportCmd ec) throws Exception {
        System.out.println("Exporting data, type: " + ec.getType());

        File exportFile = getGeneFileName(ec.getType());

        MemoryDatabase memoryDatabase = querySqlListToMemoryDatabase(jdbcTemplate, ec.getSqlList());

        DataWriter dataWriter = DataWriterUtil.getDataWriter(ec.getType());
        if (dataWriter == null) {
            throw new MessageException("Cannot find data writer for: " + ec.getType());
        }
        FileOutputStream fos = new FileOutputStream(exportFile);
        try {
            dataWriter.write(memoryDatabase, fos, "UTF-8");
        } finally {
            fos.flush();
            fos.close();
        }

        System.out.println("Generated data to file: " + exportFile);
    }

    private static MemoryDatabase querySqlListToMemoryDatabase(JdbcTemplate jdbcTemplate, List<String> sqlList) {
        Map<String, Integer> tableMap = new HashMap<String, Integer>();
        List<MemoryTable> tableList = new ArrayList<MemoryTable>();
        for (String sql : sqlList) {
            tableList.add(querySqlToMemoryTable(jdbcTemplate, sql, tableMap));
        }

        MemoryDatabase mdb = new MemoryDatabase();
        mdb.setTableList(tableList);
        return mdb;
    }

    private static MemoryTable querySqlToMemoryTable(JdbcTemplate jdbcTemplate, String sql,
                                                     Map<String, Integer> tableMap) {
        System.out.println("Executing: " + sql);

        List<MemoryRow> rowList = new ArrayList<MemoryRow>();

        MutableObject refTableName = new MutableObject();
        MemoryTable mt = new MemoryTable(getSqlTableName(sql, tableMap, refTableName));

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> resultList = jdbcTemplate.queryForList(sql);
        for (Map<String, Object> result : resultList) {
            List<MemoryField> fieldList = new ArrayList<MemoryField>();
            for (String field : result.keySet()) {
                fieldList.add(new MemoryField(field, MemoryFieldType.Unknow, translate(refTableName, field,
                                                                                       result.get(field))));
            }
            rowList.add(new MemoryRow(fieldList));
        }
        mt.setRowList(rowList);
        return mt;
    }

    private static Object translate(MutableObject refTableName, String field, Object object) {
        if ((refTableName == null) || (refTableName.getValue() == null)) {
            return object;
        }
        if (object == null) {
            return object;
        }
        String tb = refTableName.getValue().toString().toLowerCase().trim();
        String fi = field.toLowerCase().trim();
        {
            TableFields tf = encodeMapping.getGbkMap().get(tb);
            if ((tf != null) && (tf.getFields().contains(fi))) {
                return new UnCnStringTypeConverter().convert(object.toString());
            }
        }
        {
            TableFields tf = encodeMapping.getUtf8Map().get(tb);
            if ((tf != null) && (tf.getFields().contains(fi))) {
                return new UnUTF8StringTypeConverter().convert(object.toString());
            }
        }
        return object;
    }

    private static String getSqlTableName(String sql, final Map<String, Integer> tableMap,
                                          final MutableObject refTableName) {
        CCJSqlParserManager sqlPaerserManager = new CCJSqlParserManager();
        try {
            Statement statement = sqlPaerserManager.parse(new StringReader(sql));
            final MutableObject refTable = new MutableObject(null);

            statement.accept(new StatementVisitor() {

                public void visit(CreateTable createTable) {
                    throw new MessageException("Cannot parser sql for: " + createTable.getClass().getSimpleName());
                }

                public void visit(Truncate truncate) {
                    throw new MessageException("Cannot parser sql for: " + truncate.getClass().getSimpleName());
                }

                public void visit(Drop drop) {
                    throw new MessageException("Cannot parser sql for: " + drop.getClass().getSimpleName());
                }

                public void visit(Replace replace) {
                    throw new MessageException("Cannot parser sql for: " + replace.getClass().getSimpleName());
                }

                public void visit(Insert insert) {
                    throw new MessageException("Cannot parser sql for: " + insert.getClass().getSimpleName());
                }

                public void visit(Update update) {
                    throw new MessageException("Cannot parser sql for: " + update.getClass().getSimpleName());
                }

                public void visit(Delete delete) {
                    throw new MessageException("Cannot parser sql for: " + delete.getClass().getSimpleName());
                }

                public void visit(Select select) {
                    select.getSelectBody().accept(new SelectVisitor() {

                        public void visit(Union union) {
                            refTable.setValue("table_" + GLOBAL_COUNT.getAndIncrement());
                        }

                        public void visit(PlainSelect plainSelect) {
                            plainSelect.getFromItem().accept(new FromItemVisitor() {

                                public void visit(SubJoin subjoin) {
                                    refTable.setValue("table_" + GLOBAL_COUNT.getAndIncrement());
                                }

                                public void visit(SubSelect subSelect) {
                                    refTable.setValue("table_" + GLOBAL_COUNT.getAndIncrement());
                                }

                                public void visit(Table tableName) {
                                    String tn = tableName.getName().trim().toLowerCase();
                                    refTableName.setValue(tn);
                                    Integer tnCount = tableMap.get(tn);
                                    if (tnCount == null) {
                                        tableMap.put(tn, Integer.valueOf(1));
                                        refTable.setValue(tn);
                                    } else {
                                        tnCount = Integer.valueOf(tnCount.intValue() + 1);
                                        tableMap.put(tn, tnCount);
                                        refTable.setValue(tn + "_" + tnCount.intValue());
                                    }
                                }
                            });
                        }
                    });
                }
            });

            if (refTable.getValue() == null) {
                throw new MessageException("Canot parser sql and get table name.");
            }
            return (String) refTable.getValue();
        } catch (Exception e) {
            throw ExceptionUtil.wrapToRuntimeException(e);
        }
    }

    private static void printWelcomeMessage() {
        System.out.println("-----------------------------------------------------");
        System.out.println("| Export data test tool(s).                         |");
        System.out.println("|                                                   |");
        System.out.println("|           Unit Test Team @Alibaba B2B Internation |");
        System.out.println("|           Access our web page: http://goo.gl/8GaW |");
        System.out.println("-----------------------------------------------------");
        System.out.println("");
    }

    private static File getGeneFileName(DataWriterType type) {
        String ext = type.getExt();
        return new File(System.getProperty("user.home") + "/Desktop/" + "exported_data." + ext);
    }

    private static JdbcTemplate getJdbcTemplate(DatabaseConfigItem item) {
        try {
            Class.forName(item.getDriver());
            DataSource dataSource = new SingleConnectionDataSource(item.getUrl(), item.getUsername(),
                                                                   item.getPassword(), true);
            return new JdbcTemplate(dataSource);
        } catch (Exception e) {
            System.out.println("Error when get jdbc template: " + e.getMessage());
            return null;
        }
    }

    private static DatabaseConfigItem selectDatabaseConfigItem(List<DatabaseConfigItem> confItems) {
        if (confItems.size() == 1) {
            System.out.print("Selected: " + confItems.get(0));
            return confItems.get(0);
        }
        while (true) {
            System.out.println("0 : Exit export data tool");
            for (int i = 0; i < confItems.size(); i++) {
                System.out.println((i + 1) + " : " + confItems.get(i));
            }
            System.out.print("Please select: ");
            try {
                String selection = ConsoleUtil.readLine();
                int sel = Integer.parseInt(selection);
                if ((sel >= 0) && (sel <= confItems.size())) {
                    if (sel == 0) {
                        return null;
                    }
                    return confItems.get(sel - 1);
                }
            } catch (Exception e) {
                System.out.println("Input syntax error: " + e.getMessage());
            }
        }
    }
}
