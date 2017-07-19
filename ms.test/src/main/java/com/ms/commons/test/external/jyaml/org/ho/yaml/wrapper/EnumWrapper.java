/*
 * Copyright 2017-2025 msun.com All right reserved. This software is the confidential and proprietary information of
 * msun.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with msun.com.
 */
package com.ms.commons.test.external.jyaml.org.ho.yaml.wrapper;

import com.ms.commons.test.external.jyaml.org.ho.yaml.exception.YamlException;

/**
 * @author zxc Apr 14, 2013 12:28:26 AM
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public class EnumWrapper extends AbstractWrapper implements SimpleObjectWrapper {

    public EnumWrapper(Class type) {
        super(type);
    }

    public Class expectedArgType() {
        return String.class;
    }

    public Object getOutputValue() {
        try {
            return getType().getMethod("name", null).invoke(getObject(), null);
        } catch (Exception e) {
            throw new Error("Error getting enum value", e);
        }
    }

    @Override
    public void setObject(Object obj) {
        if (obj instanceof String) {
            try {
                super.setObject(getType().getMethod("valueOf", new Class[] { String.class }).invoke(getType(),
                                                                                                    new Object[] { obj }));
            } catch (Exception e) {
                throw new YamlException("Problem getting " + obj + " value of enum type " + type, e);
            }
        } else super.setObject(obj);
    }
}
