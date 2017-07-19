/*
 * Copyright 2017-2025 msun.com All right reserved. This software is the confidential and proprietary information of
 * msun.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with msun.com.
 */
package com.ms.commons.summer.web.handler;

import javax.servlet.http.HttpServletRequest;

/**
 * @author zxc Apr 12, 2013 4:10:30 PM
 */
public class WidgetHandlerMapping extends ComponentMethodHandlerMapping {

    public Object lookupHandler(String urlPath, HttpServletRequest request) throws Exception {
        Object o = super.lookupHandler(urlPath, request);
        if (o != null) {
            return o;
        }
        o = getDefaultHandler();
        if (o == null) {
            return null;
        }
        return buildPathExposingHandler(o, urlPath);
    }
}
