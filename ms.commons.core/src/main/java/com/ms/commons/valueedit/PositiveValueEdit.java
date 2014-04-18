/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.valueedit;

/**
 * 正数编辑
 * 
 * @author zxc Apr 12, 2013 2:26:15 PM
 */
public class PositiveValueEdit extends AbstractValueEdit {

    @Override
    boolean needChangedValue(String propertyName, Object propertyValue) {
        if (propertyValue == null) {
            return true;
        }
        if (propertyValue instanceof Integer || propertyValue instanceof Long) {
            if (((Number) propertyValue).intValue() < 0) {
                return true;
            }
        } else if (propertyValue instanceof Float || propertyValue instanceof Double) {
            if (((Number) propertyValue).floatValue() < 0f) {
                return true;
            }
        }
        return false;
    }

    public PositiveValueEdit(String name, Object value) {
        super(name, value);
    }

    public PositiveValueEdit(String from, String to, Object defaultValue) {
        super(from, to, defaultValue);
    }

    public PositiveValueEdit(String from, String to) {
        super(from, to);
    }
}
