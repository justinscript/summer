/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.test.external.jyaml.org.ho.yaml.wrapper;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

import com.ms.commons.test.external.jyaml.org.ho.yaml.YamlConfig;
import com.ms.commons.test.external.jyaml.org.ho.yaml.exception.ObjectCreationException;

/**
 * @author zxc Apr 14, 2013 12:32:41 AM
 */
@SuppressWarnings("rawtypes")
public abstract class AbstractWrapper implements ObjectWrapper {

    protected Class                type;
    protected Object               object;
    protected List<CreateListener> listeners         = new ArrayList<CreateListener>();
    protected boolean              objectInitialized = false;
    protected YamlConfig           config;

    protected AbstractWrapper(Class type) {
        this.type = type;
    }

    protected void fireCreated() {
        for (CreateListener listener : listeners)
            listener.created(object);
    }

    @SuppressWarnings("unchecked")
    protected Object createObject() {
        try {
            if (config.isConstructorAccessibleForDecoding(type)) {
                Constructor constr = type.getDeclaredConstructor(null);
                constr.setAccessible(true);
                return constr.newInstance();
            } else throw new ObjectCreationException("Default constructor for " + type + " is not accessible.");
        } catch (Exception e) {
            throw new ObjectCreationException("Can't create object of type " + type + " using default constructor.", e);
        }
    }

    public void addCreateHandler(CreateListener listener) {
        if (object == null) listeners.add(listener);
        else listener.created(object);
    }

    public Class getType() {
        return type;
    }

    public void setObject(Object obj) {
        if (obj == null) {
            object = null;
            objectInitialized = true;
        } else {
            object = obj;
            objectInitialized = true;
            fireCreated();
        }

    }

    public Object getObject() {
        if (!objectInitialized) {
            setObject(createObject());
            return object;
        } else return object;
    }

    public Object createPrototype() {
        return createObject();
    }

    @Override
    public String toString() {
        return object == null ? "[" + getType() + "]" : "[" + object + "]";
    }

    public ObjectWrapper makeWrapper() {
        return null;
    }

    public void setYamlConfig(YamlConfig config) {
        this.config = config;
    }
}
