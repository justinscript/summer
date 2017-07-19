/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.summer.web.servlet;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

/**
 * @author zxc Apr 12, 2013 4:49:00 PM
 */
public class RedirectResponseWrapper extends HttpServletResponseWrapper {

    private String redirectLocation;

    public RedirectResponseWrapper(HttpServletResponse response) {
        super(response);
    }

    public void sendRedirect(String location) throws IOException {
        this.redirectLocation = location;
    }

    public String getRedirectLocation() {
        return redirectLocation;
    }
}
