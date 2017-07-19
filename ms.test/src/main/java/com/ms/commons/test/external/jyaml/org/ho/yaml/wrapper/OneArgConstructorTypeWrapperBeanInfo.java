/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.test.external.jyaml.org.ho.yaml.wrapper;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.beans.SimpleBeanInfo;

/**
 * @author zxc Apr 14, 2013 12:27:53 AM
 */
public class OneArgConstructorTypeWrapperBeanInfo extends SimpleBeanInfo {

    public PropertyDescriptor[] getPropertyDescriptors() {
        try {
            PropertyDescriptor typePD = new PropertyDescriptor("type", OneArgConstructorTypeWrapper.class);
            PropertyDescriptor argTypePD = new PropertyDescriptor("argType", OneArgConstructorTypeWrapper.class);
            PropertyDescriptor rv[] = { typePD, argTypePD };
            return rv;
        } catch (IntrospectionException e) {
            throw new Error(e.toString());
        }
    }
}
