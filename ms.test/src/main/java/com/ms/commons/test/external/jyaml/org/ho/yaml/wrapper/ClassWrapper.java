/*
 * Copyright 2017-2025 msun.com All right reserved. This software is the confidential and proprietary information of
 * msun.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with msun.com.
 */
package com.ms.commons.test.external.jyaml.org.ho.yaml.wrapper;

import com.ms.commons.test.external.jyaml.org.ho.yaml.ReflectionUtil;
import com.ms.commons.test.external.jyaml.org.ho.yaml.exception.YamlException;

/**
 * @author zxc Apr 14, 2013 12:32:20 AM
 */
@SuppressWarnings("rawtypes")
public class ClassWrapper extends OneArgConstructorTypeWrapper {

    public ClassWrapper(Class type) {
        super(type);
    }

    @Override
    public void setObject(Object obj) {
        if (obj == null) super.setObject(null);
        else if (obj.getClass() == getType()) super.setObject(obj);
        else try {
            obj = Class.forName((String) obj);
            super.setObject(obj);
        } catch (Exception e) {
            throw new YamlException(e);
        }
    }

    public Object getOutputValue() {
        return ReflectionUtil.className((Class) getObject());
    }
}
