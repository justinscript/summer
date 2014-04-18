/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.test.common.dbencoding;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zxc Apr 13, 2013 11:22:35 PM
 */
public abstract class AbstractDbEncodingInfo implements DbEncodingInfo {

    protected static final String SPLIT_BLANK = " +";
    protected static final String SPLIT_CHARS = "/\\,.;:";

    protected List<DbField>       dbFieldList = new ArrayList<DbField>();

    public List<DbField> getDbFieldList() {
        return dbFieldList;
    }

    protected DbField splitTableField(String tableAndField, String chars) {
        char[] cs = chars.toCharArray();
        for (char c : cs) {
            int i = tableAndField.indexOf(c);
            if (i > 0) {
                return new DbField(tableAndField.substring(0, i), tableAndField.substring(i + 1));
            }
        }
        throw new IllegalArgumentException("cannot find table and field in: " + tableAndField);
    }
}
