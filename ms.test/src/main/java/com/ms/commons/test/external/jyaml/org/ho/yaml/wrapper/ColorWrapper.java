/*
 * Copyright 2017-2025 msun.com All right reserved. This software is the confidential and proprietary information of
 * msun.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with msun.com.
 */
package com.ms.commons.test.external.jyaml.org.ho.yaml.wrapper;

import java.awt.Color;

/**
 * @author zxc Apr 14, 2013 12:31:56 AM
 */
public class ColorWrapper extends DelayedCreationBeanWrapper {

    @SuppressWarnings("rawtypes")
    public ColorWrapper(Class type) {
        super(type);
    }

    @Override
    public String[] getPropertyNames() {
        return new String[] { "red", "green", "blue", "alpha" };
    }

    @Override
    protected Object createObject() {
        Color prototype = (Color) createPrototype();

        return new Color(
                         (values.containsKey("red") ? ((Number) values.get("red")).floatValue() : (float) prototype.getRed()) / 255,
                         (values.containsKey("green") ? ((Number) values.get("green")).floatValue() : (float) prototype.getGreen()) / 255,
                         (values.containsKey("blue") ? ((Number) values.get("blue")).floatValue() : (float) prototype.getBlue()) / 255,
                         (values.containsKey("alpha") ? ((Number) values.get("alpha")).floatValue() : (float) prototype.getAlpha()) / 255);
    }

    @Override
    public Object createPrototype() {
        return Color.BLACK;
    }
}
