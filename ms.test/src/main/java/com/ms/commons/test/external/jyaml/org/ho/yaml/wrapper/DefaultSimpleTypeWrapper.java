/*
 * Copyright 2017-2025 msun.com All right reserved. This software is the confidential and proprietary information of
 * msun.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with msun.com.
 */
package com.ms.commons.test.external.jyaml.org.ho.yaml.wrapper;

/**
 * @author zxc Apr 14, 2013 12:30:48 AM
 */
@SuppressWarnings("rawtypes")
public class DefaultSimpleTypeWrapper extends AbstractWrapper implements SimpleObjectWrapper {

    public DefaultSimpleTypeWrapper(Class type) {
        super(type);
    }

    public Class expectedArgType() {
        return type;
    }

    public Object getOutputValue() {
        return getObject();
    }
}
