/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.test.external.jyaml.org.ho.yaml.wrapper;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.ms.commons.test.external.jyaml.org.ho.yaml.ReflectionUtil;
import com.ms.commons.test.external.jyaml.org.ho.yaml.Utilities;
import com.ms.commons.test.external.jyaml.org.ho.yaml.exception.PropertyAccessException;

/**
 * @author zxc Apr 14, 2013 12:31:18 AM
 */
@SuppressWarnings("rawtypes")
public class DefaultBeanWrapper extends AbstractWrapper implements MapWrapper {

    public DefaultBeanWrapper(Class type) {
        super(type);
    }

    public boolean hasProperty(String name) {
        PropertyDescriptor prop = ReflectionUtil.getPropertyDescriptor(type, name);
        return config.isPropertyAccessibleForEncoding(prop);
    }

    public Object getProperty(String name) throws PropertyAccessException {
        Object obj = getObject();
        return getProperty(obj, name);
    }

    public Object getProperty(Object obj, String name) {
        try {
            PropertyDescriptor prop = ReflectionUtil.getPropertyDescriptor(type, name);
            if (config.isPropertyAccessibleForEncoding(prop)) {
                Method rm = prop.getReadMethod();
                rm.setAccessible(true);
                return rm.invoke(obj, null);
            }
        } catch (Exception e) {
        }
        try {
            Field field = type.getDeclaredField(name);
            if (config.isFieldAccessibleForEncoding(field)) {
                field.setAccessible(true);
                return field.get(obj);
            }
        } catch (Exception e) {
        }
        throw new PropertyAccessException("Can't get " + name + " property on type " + type + ".");
    }

    public void setProperty(String name, Object value) throws PropertyAccessException {
        try {
            PropertyDescriptor prop = ReflectionUtil.getPropertyDescriptor(type, name);
            if (config.isPropertyAccessibleForEncoding(prop)) {
                Method wm = prop.getWriteMethod();
                wm.setAccessible(true);
                wm.invoke(getObject(), new Object[] { value });
                return;
            }

        } catch (Exception e) {
        }
        try {
            Field field = type.getDeclaredField(name);
            if (config.isFieldAccessibleForDecoding(field)) {
                field.setAccessible(true);
                field.set(getObject(), value);
            }
            return;
        } catch (Exception e) {
        }
        // ignore this
    }

    public Class getPropertyType(String name) {
        if (ReflectionUtil.hasProperty(type, name)) return ReflectionUtil.getPropertyDescriptor(type, name).getPropertyType();
        else {
            try {
                Field field = type.getDeclaredField(name);
                if (ReflectionUtil.isMemberField(field)) {
                    field.setAccessible(true);
                    return field.getType();
                }
            } catch (Exception e) {
            }
            return null;
        }
    }

    /* =========== MapWrapper implementation =========================== */

    public boolean containsKey(Object key) {
        return hasProperty((String) key);
    }

    public Object get(Object key) {
        return getProperty((String) key);
    }

    public Class getExpectedType(Object key) {
        return getPropertyType((String) key);
    }

    public Collection keys() {
        Object prototype = createPrototype();
        Set<String> set = new HashSet<String>();
        for (PropertyDescriptor prop : ReflectionUtil.getProperties(getType())) {
            if (config.isPropertyAccessibleForEncoding(prop)) try {
                if (!Utilities.same(getProperty(getObject(), prop.getName()), getProperty(prototype, prop.getName()))) set.add(prop.getName());
            } catch (Exception e) {
            }
        }
        for (Field field : getType().getDeclaredFields())
            if (config.isFieldAccessibleForEncoding(field)) {
                field.setAccessible(true);
                try {
                    if (!Utilities.same(field.get(prototype), field.get(getObject()))) set.add(field.getName());
                } catch (Exception e) {
                }
            }

        // sort the keys alphabetically
        List<String> ret = new ArrayList<String>(set);
        Collections.sort(ret, new Comparator<String>() {

            public int compare(String o1, String o2) {
                return o1.compareTo(o2);
            }

        });
        return ret;
    }

    public void put(Object key, Object value) {
        setProperty((String) key, value);
    }
}
