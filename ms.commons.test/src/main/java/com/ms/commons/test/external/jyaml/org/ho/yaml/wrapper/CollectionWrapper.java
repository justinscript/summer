/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.test.external.jyaml.org.ho.yaml.wrapper;

/**
 * @author zxc Apr 14, 2013 12:32:08 AM
 */
@SuppressWarnings("rawtypes")
public interface CollectionWrapper extends ObjectWrapper, Iterable {

    public void add(Object object);

    /**
     * assumes that isOrdered() returns true
     * 
     * @param index
     * @param object
     */
    public void add(int index, Object object);

    public boolean isTyped();

    public Class componentType();

    public int size();

    public boolean isOrdered();
}
