/*
 * Copyright 2017-2025 msun.com All right reserved. This software is the confidential and proprietary information of
 * msun.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with msun.com.
 */
package com.ms.commons.summer.web.view;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.servlet.view.InternalResourceView;

/**
 * @author zxc Apr 12, 2013 4:22:27 PM
 */
public class ForwardView extends InternalResourceView {

    public ForwardView(String forwardUrl) {
        super(forwardUrl);
    }

    /**
     * 覆盖父类方法,不需要把model中的所有属性放入request的setAttribute中
     */
    @SuppressWarnings("rawtypes")
    protected void exposeModelAsRequestAttributes(Map model, HttpServletRequest request) throws Exception {

    }
}
