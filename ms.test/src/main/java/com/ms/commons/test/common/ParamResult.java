/*
 * Copyright 2017-2025 msun.com All right reserved. This software is the confidential and proprietary information of
 * msun.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with msun.com.
 */
package com.ms.commons.test.common;

import java.util.List;

/**
 * @author zxc Apr 13, 2013 11:19:04 PM
 */
public class ParamResult {

    private Object[] params;
    private Object   result;

    public ParamResult(Object[] params, Object result) {
        this.params = params;
        this.result = result;
    }

    public static ParamResult createOO(Object param, Object result) {
        return new ParamResult(new Object[] { param }, result);
    }

    public static ParamResult createLO(List<Object> paramList, Object result) {
        return new ParamResult(paramList.toArray(), result);
    }

    public Object[] getParams() {
        return params;
    }

    public Object getResult() {
        return result;
    }
}
