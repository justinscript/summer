/*
 * Copyright 2017-2025 msun.com All right reserved. This software is the confidential and proprietary information of
 * msun.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with msun.com.
 */
package com.ms.commons.test.external.jyaml.org.ho.yaml.wrapper;

import com.ms.commons.test.external.jyaml.org.ho.yaml.ReflectionUtil;
import com.ms.commons.test.external.jyaml.org.ho.yaml.exception.YamlException;

/**
 * @author zxc Apr 14, 2013 12:28:05 AM
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public class OneArgConstructorTypeWrapper extends DefaultSimpleTypeWrapper implements WrapperFactory {

    protected String argType;

    public OneArgConstructorTypeWrapper() {
        super(null);
    }

    public OneArgConstructorTypeWrapper(Class type) {
        super(type);
    }

    public OneArgConstructorTypeWrapper(Class type, String argType) {
        this(type);
        this.argType = argType;
    }

    @Override
    public Class expectedArgType() {
        return ReflectionUtil.classForName(argType);
    }

    @Override
    public void setObject(Object obj) {
        if (obj == null) super.setObject(null);
        else if (obj.getClass() == getType()) super.setObject(obj);
        else try {
            obj = type.getConstructor(new Class[] { expectedArgType() }).newInstance(new Object[] { obj });
            super.setObject(obj);
        } catch (Exception e) {
            throw new YamlException(e);
        }
    }

    public String getArgType() {
        return argType;
    }

    public void setArgType(String argType) {
        this.argType = argType;
    }

    public void setType(Class type) {
        this.type = type;
    }

    public ObjectWrapper makeWrapper() {
        ObjectWrapper ret = new OneArgConstructorTypeWrapper(getType(), argType);
        ret.setYamlConfig(config);
        return ret;
    }
}
