/*
 * Copyright 2017-2025 msun.com All right reserved. This software is the confidential and proprietary information of
 * msun.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with msun.com.
 */
package com.ms.commons.test.common.dbencoding;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * @author zxc Apr 13, 2013 11:21:54 PM
 */
public class DbField {

    private String table;
    private String field;

    public DbField(String table, String field) {
        this.table = trimLower(table);
        this.field = trimLower(field);
    }

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = trimLower(table);
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = trimLower(field);
    }

    @Override
    public int hashCode() {
        HashCodeBuilder hcb = new HashCodeBuilder();
        hcb.append(table);
        hcb.append(field);
        return hcb.toHashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj.getClass() != this.getClass()) {
            return false;
        }

        DbField another = (DbField) obj;
        EqualsBuilder eb = new EqualsBuilder();
        eb.append(this.table, another.table);
        eb.append(this.field, another.field);
        return eb.isEquals();
    }

    private String trimLower(String f) {
        return (f == null) ? null : f.trim().toLowerCase();
    }
}
