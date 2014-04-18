/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.summer.web.servlet.mvc;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ms.commons.summer.web.annotations.ViewEnum;
import com.ms.commons.summer.web.servlet.result.WebResult;

/**
 * @author zxc Apr 12, 2013 4:14:53 PM
 */
public abstract class ComponentMethodController {

    private String                nameSpace;
    protected HttpServletRequest  request;
    protected HttpServletResponse response;

    /**
     * @param request the request to set
     */
    public void setRequest(HttpServletRequest request) {
        this.request = request;
    }

    /**
     * @param response the response to set
     */
    public void setResponse(HttpServletResponse response) {
        this.response = response;
    }

    public String getNameSpace() {
        return nameSpace;
    }

    public void setNameSpace(String nameSpace) {
        this.nameSpace = nameSpace;
    }

    /**
     * web方式(同步)请求时,默认检查token方法
     * 
     * @param model
     * @param content
     * @return
     * @throws Exception
     */
    public WebResult webTokenCheck(Map<String, Object> model, String content, ViewEnum viewType) throws Exception {
        return null;
    }

    /**
     * ajax方式(异步)请求时,默认检查token方法
     * 
     * @param model
     * @param content
     * @return
     * @throws Exception
     */
    public WebResult ajaxTokenCheck(Map<String, Object> model, String content) throws Exception {
        return null;
    }
}
