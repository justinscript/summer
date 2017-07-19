/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.test.common.dbencoding;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author zxc Apr 13, 2013 11:22:03 PM
 */
public class DbEncodingUtil {

    private static final Object                      LOCK                   = new Object();
    private static final Map<DbField, DbEncoding<?>> dbFieldEncodingMap     = new HashMap<DbField, DbEncoding<?>>();
    private static final Map<DbField, DbEncoding<?>> infoDbFieldEncodingMap = new HashMap<DbField, DbEncoding<?>>();

    public static final void clearAll() {
        synchronized (LOCK) {
            dbFieldEncodingMap.clear();
            infoDbFieldEncodingMap.clear();
        }
    }

    public static final void clearInfo() {
        synchronized (LOCK) {
            infoDbFieldEncodingMap.clear();
        }
    }

    public static final void register(DbField dbField, DbEncoding<?> dbEncoding) {
        synchronized (LOCK) {
            dbFieldEncodingMap.put(dbField, dbEncoding);
        }
    }

    public static final void registerInfo(DbField dbField, DbEncoding<?> dbEncoding) {
        synchronized (LOCK) {
            infoDbFieldEncodingMap.put(dbField, dbEncoding);
        }
    }

    public static final void applyInfo(DbEncodingInfo handle) {
        for (DbField f : handle.getDbFieldList()) {
            registerInfo(f, handle.getDbEncoding());
        }
    }

    public static final void applyInfos(DbEncodingInfo[] handles) {
        for (DbEncodingInfo h : handles) {
            applyInfo(h);
        }
    }

    public static final void applyInfoList(List<DbEncodingInfo> handles) {
        for (DbEncodingInfo h : handles) {
            applyInfo(h);
        }
    }

    public static final Object encode(String table, String field, Object value) {
        DbEncoding<?> dbEncoding = getDbEncoding(table, field);
        if (dbEncoding == null) {
            return value;
        }
        return dbEncoding.encode(value);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static final Object decode(String table, String field, Object value) {
        DbEncoding<Object> dbEncoding = (DbEncoding) getDbEncoding(table, field);
        if (dbEncoding == null) {
            return value;
        }
        return dbEncoding.decode(value);
    }

    private static final DbEncoding<?> getDbEncoding(String table, String field) {
        DbField dbField = new DbField(table, field);
        DbEncoding<?> dbEncoding;
        synchronized (LOCK) {
            dbEncoding = infoDbFieldEncodingMap.get(dbField);
            if (dbEncoding == null) {
                dbEncoding = dbFieldEncodingMap.get(dbField);
            }
        }
        return dbEncoding;
    }
}
