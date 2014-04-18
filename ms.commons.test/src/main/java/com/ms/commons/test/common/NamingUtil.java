/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.test.common;

import java.lang.reflect.Method;

import com.ms.commons.test.annotation.PrepareAnnotationTool;
import com.ms.commons.test.cache.BuiltInCacheKey;
import com.ms.commons.test.cache.ThreadContextCache;

/**
 * @author zxc Apr 13, 2013 11:19:23 PM
 */
public class NamingUtil {

    /**
     * like: abc_def => abcDef
     */
    public static String dbNameToJavaName(String dbName) {
        Method method = ThreadContextCache.get(Method.class, BuiltInCacheKey.Method);
        final PrepareAnnotationTool prepareAnnotation = new PrepareAnnotationTool(method);
        if (prepareAnnotation != null) {
            boolean autoFormatFieldName = prepareAnnotation.autoFormatFieldName();
            if (autoFormatFieldName == false) return dbName;
        }

        String middleName = dbName.replace("__", "_").toLowerCase();

        boolean prefixWidthUnderline = false;
        if (middleName.startsWith("_")) {
            prefixWidthUnderline = true;
            middleName = middleName.substring(1);
        }
        String[] middleNameArray = middleName.split("_");
        StringBuilder javaName = new StringBuilder(dbName.length() - middleNameArray.length + 1);
        for (int i = 0; i < middleNameArray.length; i++) {
            String sliceName = middleNameArray[i];
            if (i == 0) {
                javaName.append(sliceName);
            } else {
                javaName.append(sliceName.substring(0, 1).toUpperCase());
                javaName.append(sliceName.substring(1));
            }
        }
        if (prefixWidthUnderline) {
            javaName.insert(0, "_");
        }
        return javaName.toString();
    }
}
