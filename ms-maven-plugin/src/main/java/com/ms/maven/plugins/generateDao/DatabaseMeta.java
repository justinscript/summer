package com.ms.maven.plugins.generateDao;

import java.sql.*;
import java.util.*;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.maven.plugin.logging.Log;

/**
 * @author zxc Jul 1, 2013 6:36:00 PM
 */
public class DatabaseMeta {

    private String                           url;
    private String                           user;
    private String                           password;

    static {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Class.forName("oracle.jdbc.driver.OracleDriver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private Log                              log;
    private List<String>                     tables;
    private List<TableMeta>                  tableMetaList;
    private Map<String, TableMeta>           tableMetaMap;
    private Map<String, List<ColumnMeta>>    tableColumnMetaMap;

    private Map<String, String>              basePrepareWikiMap;
    private Map<String, Map<String, String>> tableMethodResultWikiMap;

    private Boolean                          isMysql  = false;
    private Boolean                          isOracle = false;
    // 是否为简单模式-- service不声明接口为简单模式
    private Boolean                          isSimple = false;

    public void setIsSimple(Boolean isSimple) {
        this.isSimple = isSimple;
    }

    private static final String[] keepColumns       = new String[] { "ID", "GMT_CREATE", "GMT_MODIFIED", "STATUS" };
    private static final String[] updateKeepColumns = new String[] { "ID", "GMT_CREATE", "GMT_MODIFIED" };

    public DatabaseMeta(String url, String user, String password, String tables, Log log) {
        this.url = url;
        this.user = user;
        this.password = password;
        this.log = log;
        this.tables = new ArrayList<String>();
        isMysql = this.url.startsWith("jdbc:mysql");
        isOracle = this.url.startsWith("jdbc:oracle");
        for (String table : tables.split(",")) {
            table = table.trim();
            if (StringUtils.isNotBlank(table)) {
                this.tables.add(isOracle ? table.toUpperCase() : table);
            }
        }
        Collections.sort(this.tables);
        initDatabaseMetaData();
    }

    public List<String> getTables() {
        return tables;
    }

    public List<TableMeta> getTableMetaList() {
        return tableMetaList;
    }

    public Map<String, TableMeta> getTableMetaMap() {
        return tableMetaMap;
    }

    public Map<String, List<ColumnMeta>> getTableColumnMetaMap() {
        return tableColumnMetaMap;
    }

    public String getResultWiki(String table, String testMethod) {
        if (tableMethodResultWikiMap.containsKey(table) && tableMethodResultWikiMap.get(table).containsKey(testMethod)) {
            return StringUtils.defaultString(tableMethodResultWikiMap.get(table).get(testMethod));
        }
        return getEmptyBasePrepareWiki(table);
    }

    public String getEmptyBasePrepareWiki(String table) {
        String emptyBasePrepareWiki = StringUtils.defaultString(basePrepareWikiMap.get(table));
        String[] emptyBasePrepareWikiLines = emptyBasePrepareWiki.split("\n");
        if (emptyBasePrepareWikiLines.length >= 2) {
            return String.format("%s\n%s\n", emptyBasePrepareWikiLines[0], emptyBasePrepareWikiLines[1]);
        }
        return StringUtils.EMPTY;
    }

    public String getBasePrepareWiki(String table) {
        return StringUtils.defaultString(basePrepareWikiMap.get(table));
    }

    public Boolean getIsMysql() {
        return isMysql;
    }

    public Boolean getIsOracle() {
        return isOracle;
    }

    public Boolean getIsSimple() {
        return isSimple;
    }

    private synchronized void initDatabaseMetaData() {
        if (tables != null && tableColumnMetaMap != null) {
            return;
        }
        tableMetaList = new ArrayList<TableMeta>();
        tableMetaMap = new HashMap<String, TableMeta>();
        tableColumnMetaMap = new HashMap<String, List<ColumnMeta>>();
        basePrepareWikiMap = new HashMap<String, String>();
        tableMethodResultWikiMap = new HashMap<String, Map<String, String>>();

        log.info("DatabaseMeta.initDatabaseMetaData():");
        log.info(String.format("-->>try to connect database using url: %s, user: %s, password: %s", url, user, password));
        Connection connection = null;
        Statement statement = null;
        ResultSet columnResultSet = null;
        try {
            connection = DriverManager.getConnection(url, user, password);
            for (String tableName : tables) {
                statement = connection.createStatement();
                statement.execute("select * from " + tableName + " where 1=2");
                columnResultSet = statement.getResultSet();
                ResultSetMetaData resultSetMetaData = columnResultSet.getMetaData();
                int columnCount = resultSetMetaData.getColumnCount();
                List<ColumnMeta> columnMetaList = new ArrayList<ColumnMeta>();
                for (int i = 1; i <= columnCount; i++) {
                    ColumnMeta columnMeta = new ColumnMeta(tableName, resultSetMetaData.getColumnName(i).toUpperCase(),
                                                           resultSetMetaData.getColumnTypeName(i).toUpperCase(),
                                                           resultSetMetaData.getColumnDisplaySize(i),
                                                           resultSetMetaData.getPrecision(i));
                    columnMetaList.add(columnMeta);
                }
                columnMetaList = columnSort(columnMetaList);
                tableColumnMetaMap.put(tableName, columnMetaList);
            }
        } catch (SQLException e) {
            log.error("DatabaseMeta throw SQLException: ", e);
            e.printStackTrace();
        } finally {
            closeQuietly(columnResultSet, log);
            closeQuietly(statement, log);
            closeQuietly(connection, log);
        }
        initTableMetaMap();
        // initBasePrepareWikiMap();
        // initTableMethodResultWikiMap();
    }

    private List<ColumnMeta> columnSort(List<ColumnMeta> columnMetaList) {
        Map<String, ColumnMeta> keepColumnMetaMap = new HashMap<String, ColumnMeta>();
        Iterator<ColumnMeta> iterator = columnMetaList.iterator();
        while (iterator.hasNext()) {
            ColumnMeta columnMeta = iterator.next();
            if (ArrayUtils.contains(keepColumns, columnMeta.getColumnName())) {
                keepColumnMetaMap.put(columnMeta.getColumnName(), columnMeta);
                iterator.remove();
            }
        }
        List<ColumnMeta> sortedColumnMetaList = new ArrayList<ColumnMeta>();
        for (String keepColumn : keepColumns) {
            if (keepColumnMetaMap.containsKey(keepColumn)) {
                sortedColumnMetaList.add(keepColumnMetaMap.get(keepColumn));
            }
        }
        Collections.sort(columnMetaList);
        sortedColumnMetaList.addAll(columnMetaList);
        return sortedColumnMetaList;
    }

    private void initTableMetaMap() {
        for (String table : tables) {
            String sTable = table.toLowerCase();
            if (table.matches("^.{1}_.+")) {
                sTable = table.substring(2);
            }
            String doClassName = "";
            // uppercase
            boolean needUppercase = true;
            for (char c : sTable.toCharArray()) {
                if (c == '_') {
                    needUppercase = true;
                    continue;
                }
                if (needUppercase) {
                    c = String.valueOf(c).toUpperCase().charAt(0);
                }
                doClassName += c;
                needUppercase = false;
            }
            if (StringUtils.isNotBlank(doClassName)) {
                TableMeta tableMeta = new TableMeta(table, doClassName, lowerCaseFirst(doClassName),
                                                    getPrimaryKeyJavaType(table), isHasStatus(table));
                tableMetaList.add(tableMeta);
                tableMetaMap.put(table, tableMeta);
            }
        }

    }

    // private void initBasePrepareWikiMap() {
    // for (String table : tables) {
    // StringBuilder sb = new StringBuilder();
    // sb.append(String.format("|%s|\n", table));
    // for (ColumnMeta columnMeta : tableColumnMetaMap.get(table)) {
    // sb.append(String.format("|%s", columnMeta.getColumnName()));
    // appendMoreBlank(sb, columnMeta, "");
    // }
    // sb.append("|\n");
    // for (int i = 0; i < 10; i++) {
    // for (ColumnMeta columnMeta : tableColumnMetaMap.get(table)) {
    // String randomValue = columnMeta.generateRandomValue();
    // sb.append(String.format("|%s", randomValue));
    // appendMoreBlank(sb, columnMeta, randomValue);
    // }
    // sb.append("|\n");
    // }
    // basePrepareWikiMap.put(table, sb.toString());
    // }
    // }

    private void appendMoreBlank(StringBuilder sb, ColumnMeta columnMeta, String randomValue) {
        int length = Math.max(columnMeta.getColumnName().length(), randomValue.length());
        int leftLength = columnMeta.getColumnName().length();
        if (randomValue.length() > 0) {
            leftLength = randomValue.length();
        }
        if (randomValue.length() < columnMeta.getColumnName().length() && columnMeta.getJavaType().equals("Date")
            && columnMeta.getColumnName().length() <= 8) {
            length += 8;
        }
        int blankNum = (length % 8 == 0 ? length / 8 : (length / 8 + 1)) * 8 - leftLength;
        for (int j = 0; j < blankNum; j++) {
            sb.append(" ");
        }
    }

    private void initTableMethodResultWikiMap() {
        for (String table : tables) {
            Map<String, String> methodResultWiki = new HashMap<String, String>();
            tableMethodResultWikiMap.put(table, methodResultWiki);
            methodResultWiki.put("insert", getAddResultWiki(table));
            methodResultWiki.put("update", getUpdateResultWiki(table));
        }
    }

    private String getAddResultWiki(String table) {
        StringBuilder sb = new StringBuilder();
        if (basePrepareWikiMap.containsKey(table)) {
            String[] basePrepareWikilines = basePrepareWikiMap.get(table).split("\n");
            sb.append(basePrepareWikilines[0] + "\n");
            sb.append(ignoreGmtCreateAndModified(basePrepareWikilines[1], 0, 3) + "\n");
            sb.append(ignoreGmtCreateAndModified(basePrepareWikilines[2], 0, 3) + "\n");
        }
        return sb.toString();
    }

    private String getUpdateResultWiki(String table) {
        StringBuilder sb = new StringBuilder();
        if (basePrepareWikiMap.containsKey(table)) {
            String[] basePrepareWikilines = basePrepareWikiMap.get(table).split("\n");
            sb.append(basePrepareWikilines[0] + "\n");
            sb.append(ignoreGmtCreateAndModified(basePrepareWikilines[1], 0, 3) + "\n");
            for (ColumnMeta columnMeta : tableColumnMetaMap.get(table)) {
                if (!ArrayUtils.contains(updateKeepColumns, columnMeta.getColumnName())) {
                    String randomValue = columnMeta.getRandomModifyValue();
                    sb.append(String.format("|%s", randomValue));
                    appendMoreBlank(sb, columnMeta, randomValue);
                }
            }
            sb.append("|\n");
        }
        return sb.toString();
    }

    private String ignoreGmtCreateAndModified(String basePrepareWikiline, int firstIndex, int lastIndex) {
        int beginIndex = 0;
        int endIndex = 0;
        int j = 0;
        for (int i = 0; i < basePrepareWikiline.length(); i++) {
            if (basePrepareWikiline.charAt(i) == '|') {
                if (j == firstIndex) {
                    beginIndex = i;
                } else if (j == lastIndex) {
                    endIndex = i;
                }
                j++;
            }
        }

        return basePrepareWikiline.substring(0, beginIndex + 1) + basePrepareWikiline.substring(endIndex + 1);
    }

    private boolean isHasStatus(String table) {
        if (tableColumnMetaMap.containsKey(table)) {
            for (ColumnMeta columnMeta : tableColumnMetaMap.get(table)) {
                if (columnMeta.getColumnName().equals("STATUS") && columnMeta.getJdbcType().equals("DECIMAL")) {
                    return true;
                }
            }
        }
        return false;
    }

    private String getPrimaryKeyJavaType(String table) {
        if (tableColumnMetaMap.containsKey(table)) {
            for (ColumnMeta columnMeta : tableColumnMetaMap.get(table)) {
                if (columnMeta.getColumnName().equals("ID")) {
                    return columnMeta.getJavaType();
                }
            }
        }
        return "Integer";
    }

    private String lowerCaseFirst(String doClassName) {
        return doClassName.substring(0, 1).toLowerCase() + doClassName.substring(1);
    }

    private void closeQuietly(Connection connection, Log log) {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                log.error("closeQuietly for Connection or Statement or ResultSet failed", e);
                e.printStackTrace();
            }
        }
    }

    private void closeQuietly(Statement statement, Log log) {
        if (statement != null) {
            try {
                statement.close();
            } catch (SQLException e) {
                log.error("closeQuietly for Connection or Statement or ResultSet failed", e);
                e.printStackTrace();
            }
        }
    }

    private void closeQuietly(ResultSet resultSet, Log log) {
        if (resultSet != null) {
            try {
                resultSet.close();
            } catch (SQLException e) {
                log.error("closeQuietly for Connection or Statement or ResultSet failed", e);
                e.printStackTrace();
            }
        }
    }

}
