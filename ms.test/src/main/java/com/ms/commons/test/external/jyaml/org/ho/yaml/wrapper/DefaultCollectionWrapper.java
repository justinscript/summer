/*
 * Copyright 2017-2025 msun.com All right reserved. This software is the confidential and proprietary information of
 * msun.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with msun.com.
 */
package com.ms.commons.test.external.jyaml.org.ho.yaml.wrapper;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * @author zxc Apr 14, 2013 12:31:07 AM
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public class DefaultCollectionWrapper extends AbstractWrapper implements CollectionWrapper {

    public DefaultCollectionWrapper(Class type) {
        super(type);
    }

    public Collection getCollection() {
        return (Collection) getObject();
    }

    public void add(Object object) {
        getCollection().add(object);
    }

    public void add(int index, Object object) {
        ((List) getCollection()).add(index, object);
    }

    public boolean isTyped() {
        return false;
    }

    public Class componentType() {
        return null;
    }

    public int size() {
        return getCollection().size();
    }

    public boolean isOrdered() {
        return List.class.isAssignableFrom(getType());
    }

    public Iterator iterator() {
        return getCollection().iterator();
    }
}
