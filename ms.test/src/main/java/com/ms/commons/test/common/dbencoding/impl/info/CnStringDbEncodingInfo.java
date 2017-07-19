/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.test.common.dbencoding.impl.info;

import com.ms.commons.test.common.dbencoding.AbstractDbEncodingInfo;
import com.ms.commons.test.common.dbencoding.DbEncoding;
import com.ms.commons.test.common.dbencoding.impl.CnStringDbEncoding;

/**
 * @author zxc Apr 13, 2013 11:23:11 PM
 */
public class CnStringDbEncodingInfo extends AbstractDbEncodingInfo {

    public CnStringDbEncodingInfo(String tableAndFields) {
        if ((tableAndFields != null) && (tableAndFields.length() > 0)) {
            for (String tableAndField : tableAndFields.trim().split(SPLIT_BLANK)) {
                dbFieldList.add(splitTableField(tableAndField, SPLIT_CHARS));
            }
        }
    }

    public DbEncoding<?> getDbEncoding() {
        return new CnStringDbEncoding();
    }
}
