/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.test.prepare.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.ms.commons.test.annotation.napi.Optional;
import com.ms.commons.test.annotation.napi.SupplyBy;
import com.ms.commons.test.common.ExceptionUtil;
import com.ms.commons.test.datareader.impl.BaseReaderUtil;
import com.ms.commons.test.external.jyaml.org.ho.yaml.Yaml;
import com.ms.commons.test.prepare.Preparation;

/**
 * @author zxc Apr 14, 2013 12:21:57 AM
 */
public class YamlPreparation implements Preparation {

    @Optional(supply = SupplyBy.Framework)
    private String relativePath;

    public YamlPreparation relativePath(String relativePath) {
        this.relativePath = relativePath;
        return this;
    }

    public Object prepare() {
        String absPath = BaseReaderUtil.getAbsolutedPath(relativePath);
        try {
            return Yaml.load(new File(absPath));
        } catch (Exception e) {
            throw ExceptionUtil.wrapToRuntimeException(e);
        }
    }

    public <T> T prepare(Class<T> clazz) {
        String absPath = BaseReaderUtil.getAbsolutedPath(relativePath);
        try {
            return Yaml.loadType(new File(absPath), clazz);
        } catch (Exception e) {
            throw ExceptionUtil.wrapToRuntimeException(e);
        }
    }

    public List<Object> prepareList() {
        String absPath = BaseReaderUtil.getAbsolutedPath(relativePath);
        try {
            List<Object> result = new ArrayList<Object>();
            for (Object obj : Yaml.loadStream(new File(absPath))) {
                result.add(obj);
            }
            return result;
        } catch (Exception e) {
            throw ExceptionUtil.wrapToRuntimeException(e);
        }
    }

    public <T> List<T> prepareList(Class<T> clazz) {
        String absPath = BaseReaderUtil.getAbsolutedPath(relativePath);
        try {
            List<T> result = new ArrayList<T>();
            for (T obj : Yaml.loadStreamOfType(new File(absPath), clazz)) {
                result.add(obj);
            }
            return result;
        } catch (Exception e) {
            throw ExceptionUtil.wrapToRuntimeException(e);
        }
    }
}
