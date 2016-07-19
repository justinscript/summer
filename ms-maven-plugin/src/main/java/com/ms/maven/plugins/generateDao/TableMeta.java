package com.ms.maven.plugins.generateDao;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;

/**
 * @author zxc Jul 1, 2013 6:36:00 PM
 */
public class TableMeta {

    private String  table;
    private String  doClassName;
    private String  lcDoClassName;
    private String  primaryKeyJavaType;
    private String  primaryKeyResultclass;
    private String  serialVersionUID;
    private boolean hasStatus;

    public TableMeta(String table, String doClassName, String lcDoClassName, String primaryKeyJavaType,
                     boolean hasStatus) {
        super();
        this.table = table;
        this.doClassName = doClassName;
        this.lcDoClassName = lcDoClassName;
        this.primaryKeyJavaType = primaryKeyJavaType;
        if (StringUtils.equals(this.primaryKeyJavaType, "Integer")
            || StringUtils.equals(this.primaryKeyJavaType, "Int")) {
            this.primaryKeyResultclass = "java.lang.Integer";
        } else if (StringUtils.equals(this.primaryKeyJavaType, "Long")) {
            this.primaryKeyResultclass = "java.lang.Long";
        } else {
            this.primaryKeyResultclass = this.primaryKeyJavaType;
        }
        this.serialVersionUID = "10" + RandomStringUtils.random(17, "123456789");
        this.hasStatus = hasStatus;
    }

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public String getDoClassName() {
        return doClassName;
    }

    public void setDoClassName(String doClassName) {
        this.doClassName = doClassName;
    }

    public String getLcDoClassName() {
        return lcDoClassName;
    }

    public void setLcDoClassName(String lcDoClassName) {
        this.lcDoClassName = lcDoClassName;
    }

    public String getPrimaryKeyJavaType() {
        return primaryKeyJavaType;
    }

    public void setPrimaryKeyJavaType(String primaryKeyJavaType) {
        this.primaryKeyJavaType = primaryKeyJavaType;
    }

    public String getPrimaryKeyResultclass() {
        return primaryKeyResultclass;
    }

    public void setPrimaryKeyResultclass(String primaryKeyResultclass) {
        this.primaryKeyResultclass = primaryKeyResultclass;
    }

    public String getSerialVersionUID() {
        return serialVersionUID;
    }

    public void setSerialVersionUID(String serialVersionUID) {
        this.serialVersionUID = serialVersionUID;
    }

    // for ibatis
    public boolean getHasStatus() {
        return hasStatus;
    }

    public boolean isHasStatus() {
        return hasStatus;
    }

    public void setHasStatus(boolean hasStatus) {
        this.hasStatus = hasStatus;
    }

}
