/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.test.annotation;

import java.lang.reflect.Method;

import org.apache.commons.lang.StringUtils;

import com.ms.commons.test.database.SecondaryPreareFilter;
import com.ms.commons.test.datareader.DataReaderType;

/**
 * Prepare PrepareInfo Annotation
 * 
 * @see Prepare
 * @see PrepareInfo
 * @author zxc Apr 13, 2013 11:02:27 PM
 */
public class PrepareAnnotationTool {

    private Method      method;
    private Prepare     prepare;
    private NoPrepare   noPrepare;
    private PrepareInfo prepareInfo;

    public PrepareAnnotationTool(Method method) {
        this.method = method;
        this.prepare = method.getAnnotation(Prepare.class);
        this.noPrepare = method.getAnnotation(NoPrepare.class);
        this.prepareInfo = method.getDeclaringClass().getAnnotation(PrepareInfo.class);
    }

    public boolean isPrepareEnable() {
        return (isPrepareOrInfoPresent() && !isNoPreparePresent());
    }

    public boolean isNoPreparePresent() {
        return (noPrepare != null);
    }

    public boolean isPreparePresent() {
        return (prepare != null);
    }

    public boolean isPrepareInfoPresent() {
        return (prepareInfo != null);
    }

    public boolean isPrepareAndInfoPresent() {
        return (isPreparePresent() && isPrepareInfoPresent());
    }

    public boolean isPrepareOrInfoPresent() {
        return (isPreparePresent() || isPrepareInfoPresent());
    }

    public String methodName() {
        if (isPreparePresent() && !StringUtils.isBlank(prepare.methodName())) {
            return prepare.methodName();
        }
        return method.getName();
    }

    public DataReaderType type() {
        if (isPreparePresent()) {
            if (prepare.type() != DataReaderType.None) {
                return prepare.type();
            }
        }
        if (isPrepareInfoPresent()) {
            if (prepareInfo.type() != DataReaderType.None) {
                return prepareInfo.type();
            }
        }
        return DataReaderType.Excel;
    }

    public boolean autoImport() {
        return (isPreparePresent()) ? prepare.autoImport() : false;
    }

    public String importTables() {
        return (isPreparePresent()) ? prepare.importTables() : "";
    }

    public boolean autoClear() {
        return (isPreparePresent()) ? prepare.autoClear() : false;
    }

    public boolean forceClear() {
        return (isPreparePresent()) ? prepare.forceClear() : false;
    }

    public boolean newThreadTransactionImport() {
        return (isPreparePresent()) ? prepare.newThreadTransactionImport() : false;
    }

    public String cnStringEncoding() {
        if (isPreparePresent()) {
            if (!StringUtils.isBlank(prepare.cnStringEncoding())) {
                return prepare.cnStringEncoding();
            }
        }
        if (isPrepareInfoPresent()) {
            if (!StringUtils.isBlank(prepareInfo.cnStringEncoding())) {
                return prepareInfo.cnStringEncoding();
            }
        }
        return StringUtils.EMPTY;
    }

    public String utf8StringEncoding() {
        if (isPreparePresent()) {
            if (!StringUtils.isBlank(prepare.utf8StringEncoding())) {
                return prepare.utf8StringEncoding();
            }
        }
        if (isPrepareInfoPresent()) {
            if (!StringUtils.isBlank(prepareInfo.utf8StringEncoding())) {
                return prepareInfo.utf8StringEncoding();
            }
        }
        return StringUtils.EMPTY;
    }

    public boolean autoClearExistsData() {
        return (isPreparePresent()) ? prepare.autoClearExistsData() : false;
    }

    public String primaryKey() {
        if (isPreparePresent()) {
            if (!StringUtils.isBlank(prepare.primaryKey())) {
                return prepare.primaryKey();
            }
        }
        if (isPrepareInfoPresent()) {
            if (!StringUtils.isBlank(prepareInfo.primaryKey())) {
                return prepareInfo.primaryKey();
            }
        }
        return StringUtils.EMPTY;
    }

    public boolean autoClearImportDataOnFinish() {
        return (isPreparePresent()) ? prepare.autoClearImportDataOnFinish() : false;
    }

    public boolean autoFormatFieldName() {
        return (isPreparePresent()) ? prepare.autoFormatFieldName() : true;
    }

    public boolean autoImportCommonData() {
        return (isPreparePresent()) ? prepare.autoImportCommonData() : true;
    }

    public Class<? extends SecondaryPreareFilter> secondaryPrepareFilter() {
        return (isPreparePresent()) ? prepare.secondaryPrepareFilter() : SecondaryPreareFilter.class;
    }
}
