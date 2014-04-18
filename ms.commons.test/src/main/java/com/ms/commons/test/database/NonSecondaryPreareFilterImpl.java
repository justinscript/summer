/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.test.database;

/**
 * @author zxc Apr 13, 2013 11:37:57 PM
 */
public class NonSecondaryPreareFilterImpl implements SecondaryPreareFilter {

    public boolean accept(String table, StringBuilder newTableName) {
        return false;
    }
}
