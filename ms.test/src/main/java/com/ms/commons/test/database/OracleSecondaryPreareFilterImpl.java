/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.test.database;

/**
 * @author zxc Apr 13, 2013 11:37:50 PM
 */
public class OracleSecondaryPreareFilterImpl implements SecondaryPreareFilter {

    public boolean accept(String table, StringBuilder newTableName) {
        if (table.trim().toLowerCase().startsWith("oracle_")) {
            newTableName.append(table.trim().substring("oracle_".length()));
            return true;
        }
        return false;
    }
}
