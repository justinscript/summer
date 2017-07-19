/*
 * Copyright 2017-2025 msun.com All right reserved. This software is the confidential and proprietary information of
 * msun.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with msun.com.
 */
package com.ms.commons.test.external.jyaml.org.ho.yaml.wrapper;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Iterator;

import com.ms.commons.test.external.jyaml.org.ho.yaml.ReflectionUtil;
import com.ms.commons.test.external.jyaml.org.ho.yaml.exception.YamlException;

/**
 * @author zxc Apr 14, 2013 12:32:30 AM
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public class ArrayWrapper extends AbstractWrapper implements CollectionWrapper {

    ArrayList list = new ArrayList();

    public ArrayWrapper(Class type) {
        super(type);
        assert type.isArray();
    }

    @Override
    public Object createPrototype() {
        throw new UnsupportedOperationException("createPrototype not supported.");
    }

    public void add(Object object) {
        list.add(object);
    }

    public void add(int index, Object object) {
        list.add(index, object);
    }

    public boolean isTyped() {
        return true;
    }

    public Class componentType() {
        return type.getComponentType();
    }

    @Override
    protected Object createObject() {
        String componentTypeName = ReflectionUtil.arrayComponentName(ReflectionUtil.className(getType()));
        Class componentType = ReflectionUtil.classForName(componentTypeName);
        if (componentType == null) throw new YamlException("class " + componentTypeName + " cannot be resolved.");
        Object array = Array.newInstance(componentType, list.size());
        for (int i = 0; i < Array.getLength(array); i++)
            try {
                Array.set(array, i, list.get(i));
            } catch (IllegalArgumentException e) {
                throw new YamlException("Fail to set " + list.get(i) + " into array of type " + componentTypeName);
            }
        return array;
    }

    @Override
    protected void fireCreated() {
        list = toList(object);
        super.fireCreated();
    }

    ArrayList toList(Object array) {
        ArrayList ret = new ArrayList(Array.getLength(array));
        for (int i = 0; i < Array.getLength(array); i++)
            ret.add(Array.get(array, i));
        return ret;
    }

    public int size() {
        return list.size();
    }

    public boolean isOrdered() {
        return true;
    }

    public Iterator iterator() {
        return list.iterator();
    }
}
