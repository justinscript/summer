/*
 * Copyright 2017-2025 msun.com All right reserved. This software is the confidential and proprietary information of
 * msun.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with msun.com.
 */
package com.ms.commons.valueedit;

import java.util.Map;

/**
 * @author zxc Apr 12, 2013 2:26:15 PM
 */
public abstract class AbstractValueEdit implements ValueEditable {

    private String from;
    private String to;
    private Object defaultValue;

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void edit(Object raw, Map name2Values) {
        if (raw == null) {
            // FIXME 是否判断下对象有该属性呢？对于NULL对像怎么判断他的class呢？
            name2Values.put(to, defaultValue);
        } else {
            // 只有在对像拥有该属性的时候采取覆盖
            if (name2Values.containsKey(from)) {
                Object _value = name2Values.get(from);
                if (needChangedValue(from, _value)) {
                    _value = defaultValue;
                }
                name2Values.remove(from);
                name2Values.put(to, _value);
            }
        }
    }

    abstract boolean needChangedValue(String propertyName, Object propertyValue);

    public AbstractValueEdit(String from, String to, Object defaultValue) {
        this.setFrom(from);
        this.setTo(to);
        this.setDefaultValue(defaultValue);
    }

    public AbstractValueEdit(String from, String to) {
        this(from, to, null);
    }

    public AbstractValueEdit(String name, Object value) {
        this(name, name, value);
    }

    @Override
    public int hashCode() {
        return (getFrom() + "^_^" + getTo()).hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof ValueEditable)) {
            return false;
        }
        AbstractValueEdit sp = (AbstractValueEdit) obj;
        if (getFrom().equals(sp.getFrom()) && getTo().equals(sp.getTo())) {
            return true;
        }
        return false;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public Object getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(Object defaultValue) {
        this.defaultValue = defaultValue;
    }

}
