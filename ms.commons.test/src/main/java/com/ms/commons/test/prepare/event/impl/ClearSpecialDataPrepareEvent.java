/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.test.prepare.event.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.jdbc.core.JdbcTemplate;

import com.ms.commons.test.common.StringUtil;
import com.ms.commons.test.common.convert.DataBaseTypeConvertUtil;
import com.ms.commons.test.common.task.Task;
import com.ms.commons.test.common.task.TaskUtil;
import com.ms.commons.test.context.TestCaseRuntimeInfo;
import com.ms.commons.test.database.DatabaseReader;
import com.ms.commons.test.database.DatabaseWriter;
import com.ms.commons.test.database.JdbcTemplateAndTableName;
import com.ms.commons.test.database.type.FieldType;
import com.ms.commons.test.memorydb.MemoryField;
import com.ms.commons.test.memorydb.MemoryRow;
import com.ms.commons.test.memorydb.MemoryTable;
import com.ms.commons.test.prepare.PrepareUtil;
import com.ms.commons.test.prepare.event.PrepareEvent;

/**
 * 清理数据
 * 
 * @author zxc Apr 14, 2013 12:23:35 AM
 */
public class ClearSpecialDataPrepareEvent implements PrepareEvent {

    public static final String      __finish__task__list__ = "__finish__task__list__";

    /**
     * 需要清理的表
     */
    private Map<String, ClearTable> tablePrimaryKeyMap     = new HashMap<String, ClearTable>();

    /**
     * @param primaryKeys 例如 ORDERS/id,MEMBER_ID;ORDERS/MEMBER_ID
     */
    public ClearSpecialDataPrepareEvent(String primaryKeys) {
        if (StringUtils.isBlank(primaryKeys)) {
            return;
        }
        String trimedPKs = StringUtil.trimReplaceFullSpaceToHalf(primaryKeys).replaceAll("\\s+", " ");
        String[] tpks = trimedPKs.split(";");
        if ((tpks == null) || (tpks.length == 0)) {
            throw new RuntimeException("Primary keys cannot be null or empty: " + primaryKeys);
        }
        for (String tableAndPk : tpks) {
            String[] tAndPk = getTableNameAndCondtion(tableAndPk);
            String tableName = tAndPk[0].trim().toLowerCase();
            String clearCondition = tAndPk[1].trim().toLowerCase().replace(" ", "");
            ClearTable clearTable = tablePrimaryKeyMap.get(tableName);
            if (clearTable == null) {
                clearTable = new ClearTable(tableName);
                tablePrimaryKeyMap.put(tableName, clearTable);
            }
            clearTable.addClearCondition(clearCondition);
        }
    }

    public String[] getTableNameAndCondtion(String tableNameAndPks) {
        String[] tAndPk = tableNameAndPks.split("/");
        if ((tAndPk == null) || (tAndPk.length != 2)) {
            throw new RuntimeException("Primary keys should be: table/field " + tableNameAndPks);
        }
        String pk = tAndPk[1].trim().toLowerCase().replace(" ", "");
        if ((tAndPk[0].length() == 0) || (pk.length() == 0)) {
            throw new RuntimeException("Table or pk cannot be empty: " + tableNameAndPks);
        }
        return tAndPk;
    }

    public void onPrepareDatabase(PrepareEventContext context) {
    }

    public void onPrepareDatabaseFinish(PrepareEventContext context) {
    }

    public void onPrepareDatabaseTable(PrepareEventContext context) {
        if (context.isClearTable()) {
            // if clear table is true, the ignore this event
            return;
        }

        JdbcTemplate jdbcTemplate = context.getJdbcTemplate();
        String table = context.getCurrentTable();

        final JdbcTemplateAndTableName jdbcTemplateAndTableName = PrepareUtil.getJdbcTemplate(jdbcTemplate, table);
        // 1.取得需要清理的表
        final ClearTable pkConditions = tablePrimaryKeyMap.get(table.trim().toLowerCase());
        List<String> pkList;// 主键字段列表
        if (pkConditions != null) {
            for (String onePk : pkConditions.getAllConditions()) {
                pkList = Arrays.asList(onePk.split(","));
                clear(context, jdbcTemplateAndTableName, pkList);
            }
        } else {
            pkList = DatabaseReader.readTablePK(jdbcTemplateAndTableName.getJdbcTemplate(),
                                                jdbcTemplateAndTableName.getTableName());
            clear(context, jdbcTemplateAndTableName, pkList);
        }
    }

    private void clear(PrepareEventContext context, JdbcTemplateAndTableName jdbcTemplateAndTableName,
                       List<String> pkList) {
        if (pkList == null) {
            throw new RuntimeException("Cannot find pk in table:" + jdbcTemplateAndTableName.getTableName());
        }
        // 2.清理数据
        MemoryTable memoryTable = context.getDatabase().getTable(jdbcTemplateAndTableName.getTableName());
        clearData(memoryTable, jdbcTemplateAndTableName, pkList);
    }

    /**
     * 根据数据文件中的数据，来清理数据库
     * 
     * <pre>
     * 大致流程是：
     * 1.根据主键从数据文件中读取对应的值
     * 2.利用表名和字段的值，拼装SQL 执行。
     * </pre>
     * 
     * @param pkList 主键字段
     */
    @SuppressWarnings("unchecked")
    private void clearData(MemoryTable memoryTable, final JdbcTemplateAndTableName jdbcTemplateAndTableName,
                           final List<String> pkList) {
        List<Task> taskList = TaskUtil.createTaskList();
        List<MemoryRow> memberRowList = memoryTable.getRowList();
        // 从数据文件中读取逐渐主键字段的数值
        for (MemoryRow row : memberRowList) {
            final List<Object> pkValueList = new ArrayList<Object>();
            for (String pk : pkList) {
                Object pkValue = getPKValue(row, pk);
                FieldType fieldType = DatabaseReader.readTableField(jdbcTemplateAndTableName.getJdbcTemplate(),
                                                                    jdbcTemplateAndTableName.getTableName(), pk);
                Object convertedValue = DataBaseTypeConvertUtil.convert(fieldType, pkValue);

                pkValueList.add(convertedValue);
            }

            taskList.add(new Task() {

                public void finish() {
                    DatabaseWriter.deleteTableDataByPKs(jdbcTemplateAndTableName.getJdbcTemplate(),
                                                        jdbcTemplateAndTableName.getTableName(), pkList, pkValueList);
                }
            });
        }
        // 执行清理
        if (TestCaseRuntimeInfo.current().getPrepare().autoClearExistsData()) {
            TaskUtil.runTasks(taskList);
        }
        boolean isNewTransactionImport = TestCaseRuntimeInfo.current().getPrepare().newThreadTransactionImport();
        boolean isDefaultRollBack = TestCaseRuntimeInfo.current().getTestCaseInfo().defaultRollBack();
        boolean isWillNotDataDefaultRollBack = isNewTransactionImport || (!isDefaultRollBack);
        if (isWillNotDataDefaultRollBack && TestCaseRuntimeInfo.current().getPrepare().autoClearImportDataOnFinish()) {
            List<Task> storedTaskList = (List<Task>) TestCaseRuntimeInfo.current().getContext().get(__finish__task__list__);
            storedTaskList = (storedTaskList == null) ? new ArrayList<Task>() : storedTaskList;
            storedTaskList.addAll(taskList);
            TestCaseRuntimeInfo.current().getContext().put(__finish__task__list__, storedTaskList);
        }
    }

    private Object getPKValue(MemoryRow memoryRow, String pk) {
        List<MemoryField> memoryFieldList = memoryRow.getFieldList();
        for (MemoryField memoryField : memoryFieldList) {
            if (StringUtil.trimedIgnoreCaseEquals(memoryField.getName(), pk)) {
                return memoryField.getValue();
            }
        }
        throw new RuntimeException("Cannot find pk in row: " + pk);
    }

    public void onPrepareDatabaseTableFinish(PrepareEventContext context) {
    }
}
