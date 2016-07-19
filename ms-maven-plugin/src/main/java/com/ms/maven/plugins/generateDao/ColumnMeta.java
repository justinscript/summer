package com.ms.maven.plugins.generateDao;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;

/**
 * @author zxc Jul 1, 2013 6:36:00 PM
 */
public class ColumnMeta implements Comparable<ColumnMeta> {

    private String table;
    private String columnName;
    private String columnTypeName;
    private int    columnDisplaySize;
    private int    precision;

    private String javaType;
    private String jdbcType;
    private String javaName;
    private String getMethod;
    private String setMethod;

    private String randomModifyValue;
    private String randomModifyValueJavaCode;

    public ColumnMeta(String table, String columnName, String columnTypeName, int columnDisplaySize, int precision) {
        this.table = table;
        this.columnName = columnName;
        this.columnTypeName = columnTypeName;
        this.columnDisplaySize = columnDisplaySize;
        this.precision = precision;
        this.setJavaJdbcType(columnTypeName, columnDisplaySize);
        this.javaName = ColumnMeta.generateJavaNamePattern(columnName.toLowerCase());
        this.getMethod = ColumnMeta.generateJavaNamePattern("get_" + columnName.toLowerCase());
        this.setMethod = ColumnMeta.generateJavaNamePattern("set_" + columnName.toLowerCase());
        this.setRandomModifyValueAndCode();
    }

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public String getColumnTypeName() {
        return columnTypeName;
    }

    public void setColumnTypeName(String columnTypeName) {
        this.columnTypeName = columnTypeName;
    }

    public int getColumnDisplaySize() {
        return columnDisplaySize;
    }

    public void setColumnDisplaySize(int columnDisplaySize) {
        this.columnDisplaySize = columnDisplaySize;
    }

    public int getPrecision() {
        return precision;
    }

    public void setPrecision(int precision) {
        this.precision = precision;
    }

    public String getJavaType() {
        return javaType;
    }

    public void setJavaType(String javaType) {
        this.javaType = javaType;
    }

    public String getJdbcType() {
        return jdbcType;
    }

    public void setJdbcType(String jdbcType) {
        this.jdbcType = jdbcType;
    }

    public String getJavaName() {
        return javaName;
    }

    public void setJavaName(String javaName) {
        this.javaName = javaName;
    }

    public String getGetMethod() {
        return getMethod;
    }

    public void setGetMethod(String getMethod) {
        this.getMethod = getMethod;
    }

    public String getSetMethod() {
        return setMethod;
    }

    public void setSetMethod(String setMethod) {
        this.setMethod = setMethod;
    }

    public String getRandomModifyValue() {
        return randomModifyValue;
    }

    public void setRandomModifyValue(String randomModifyValue) {
        this.randomModifyValue = randomModifyValue;
    }

    public String getRandomModifyValueJavaCode() {
        return randomModifyValueJavaCode;
    }

    public void setRandomModifyValueJavaCode(String randomModifyValueJavaCode) {
        this.randomModifyValueJavaCode = randomModifyValueJavaCode;
    }

    // oracle: TIMESTAMP VARCHAR2 NUMBER FLOAT DOUBLE
    // mysql: DATETIME VARCHAR BIGINT INT MEDIUMINT SMALLINT TINYINT FLOAT DOUBLE
    private void setJavaJdbcType(String columnTypeName, int columnDisplaySize) {
        if (StringUtils.equals(columnTypeName, "TIMESTAMP") || StringUtils.equals(columnTypeName, "DATETIME")) {
            this.javaType = "Date";
            this.jdbcType = "TIMESTAMP";
        } else if (StringUtils.equals(columnTypeName, "VARCHAR2") || StringUtils.equals(columnTypeName, "VARCHAR")) {
            this.javaType = "String";
            this.jdbcType = "VARCHAR";
        } else if (StringUtils.equals(columnTypeName, "FLOAT")) {
            this.javaType = "Float";
            this.jdbcType = "DECIMAL";
        } else if (StringUtils.equals(columnTypeName, "DOUBLE")) {
            this.javaType = "Double";
            this.jdbcType = "DECIMAL";
        } else if (StringUtils.equals(columnTypeName, "BIGINT")) {
            this.javaType = "Long";
            this.jdbcType = "DECIMAL";
        } else if (StringUtils.equals(columnTypeName, "INT") || StringUtils.equals(columnTypeName, "MEDIUMINT")
                   || StringUtils.equals(columnTypeName, "SMALLINT") || StringUtils.equals(columnTypeName, "UNSIGNED")
                   || StringUtils.equals(columnTypeName, "TINYINT")) {
            this.javaType = "Integer";
            this.jdbcType = "DECIMAL";
        }
        if (StringUtils.equals(columnTypeName, "NUMBER")) {
            this.javaType = columnDisplaySize >= 32 ? "Long" : "Integer";
            this.jdbcType = "DECIMAL";
        }
    }

    private void setRandomModifyValueAndCode() {
        if (StringUtils.equals(this.javaType, "Date")) {
            Date tomorrow = DateUtils.addDays(new Date(), 2);
            this.randomModifyValue = new SimpleDateFormat("yyyy/MM/dd").format(tomorrow);
            this.randomModifyValueJavaCode = getTomorrow(this.randomModifyValue);
        } else if (StringUtils.equals(this.javaType, "String")) {
            this.randomModifyValue = generateRandomValue();
            this.randomModifyValueJavaCode = String.format("\"%s\"", this.randomModifyValue);
        } else if (StringUtils.equals(this.javaType, "Float")) {
            this.randomModifyValue = generateRandomValue();
            this.randomModifyValueJavaCode = String.format("%sf", this.randomModifyValue);
        } else if (StringUtils.equals(this.javaType, "Double")) {
            this.randomModifyValue = generateRandomValue();
            this.randomModifyValueJavaCode = String.format("%sd", this.randomModifyValue);
        } else if (StringUtils.equals(this.javaType, "Long")) {
            this.randomModifyValue = generateRandomValue();
            this.randomModifyValueJavaCode = String.format("%sl", this.randomModifyValue);
        } else if (StringUtils.equals(this.javaType, "Integer")) {
            this.randomModifyValue = generateRandomValue();
            this.randomModifyValueJavaCode = String.format("%s", this.randomModifyValue);
        }
    }

    public String generateRandomValue() {
        if (this.columnName.equals("STATUS") && this.jdbcType.equals("DECIMAL")) {
            return "0";
        }
        int count = Math.min(4, columnDisplaySize - 1);
        if (count <= 0) {
            count = 1;
        }
        if (StringUtils.equals(jdbcType, "TIMESTAMP")) {
            return getToday();
        } else if (StringUtils.equals(jdbcType, "VARCHAR")) {
            return RandomStringUtils.random(count, "abcdefghijklmnopqrstuvwxyz");
        } else if (StringUtils.equals(jdbcType, "DECIMAL")) {
            if (0 < precision && precision < 8) {
                return "1";
            }
            if (8 <= precision && precision < 14) {
                count = 3;
            }
            // ID column must long enough to keep random
            if (this.columnName.equals("ID")) {
                count = 6;
            }
            return RandomStringUtils.random(count, "123456789");
        }
        return "abcd";
    }

    private String getTomorrow(String randomModifyValue) {
        return String.format("new SimpleDateFormat(\"yyyy/MM/dd\").parse(\"%s\")", randomModifyValue);
    }

    private String getToday() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        return sdf.format(new Date());
    }

    private static String generateJavaNamePattern(String ss) {
        String javaNamePattern = "";
        // uppercase
        boolean needUppercase = false;
        for (char c : ss.toCharArray()) {
            if (c == '_') {
                needUppercase = true;
                continue;
            }
            if (needUppercase) {
                c = String.valueOf(c).toUpperCase().charAt(0);
            }
            javaNamePattern += c;
            needUppercase = false;
        }
        return javaNamePattern;
    }

    @Override
    public int compareTo(ColumnMeta o1) {
        return this.getColumnName().compareTo(o1.getColumnName());
    }

}
