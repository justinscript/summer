/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.app.web.validation.validator;

/**
 * StringLength只能校验字符串长度,且min和max都是闭合区间
 * 
 * @author zxc Apr 12, 2013 11:17:39 PM
 */
public class StringLength extends AbstarctValidator {

    private int max;
    private int min;

    public boolean validate(Object value) {
        if (value instanceof String) {
            String strValue = (String) value;
            return strValue.length() >= min && strValue.length() <= max;
        } else {
            throw new RuntimeException("StringLength只能校验字符串长度");
        }

    }

    public int getMax() {
        return max;
    }

    public void setMax(int max) {
        this.max = max;
    }

    public int getMin() {
        return min;
    }

    public void setMin(int min) {
        this.min = min;
    }

    public static void main(String[] args) {
        StringLength sl = new StringLength();
        sl.setMax(2);
        sl.setMin(1);
        sl.setErrorMessage("test${max},${min},${displayName}");
        System.out.println(sl.getErrorMessage("Email"));
    }
}
