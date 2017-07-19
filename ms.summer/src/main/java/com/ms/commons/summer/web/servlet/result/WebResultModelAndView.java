/*
 * Copyright 2017-2025 msun.com All right reserved. This software is the confidential and proprietary information of
 * msun.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with msun.com.
 */
package com.ms.commons.summer.web.servlet.result;

import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;

/**
 * @author zxc Apr 12, 2013 4:44:26 PM
 */
public class WebResultModelAndView extends ModelAndView implements ResponseInterceptableModelAndView {

    public WebResultModelAndView() {
        super();
    }

    public WebResultModelAndView(String viewName) {
        super(viewName);
    }

    public WebResultModelAndView(View view) {
        super(view);
    }

    @SuppressWarnings("rawtypes")
    public WebResultModelAndView(String viewName, Map model) {
        super(viewName, model);
    }

    @SuppressWarnings("rawtypes")
    public WebResultModelAndView(View view, Map model) {
        super(view, model);
    }

    public WebResultModelAndView(String viewName, String modelName, Object modelObject) {
        super(viewName, modelName, modelObject);
    }

    public WebResultModelAndView(View view, String modelName, Object modelObject) {
        super(view, modelName, modelObject);
    }

    private ResponseInterceptableModelAndView webResult;

    /**
     * @param webResult the webResult to set
     */
    public void setWebResult(ResponseInterceptableModelAndView webResult) {
        this.webResult = webResult;
    }

    /*
     * (non-Javadoc)
     * @see com.alibaba.roma.web.servlet.result.ResponseInterceptableModelAndView#onResponse(javax
     * .servlet.http.HttpServletResponse)
     */
    public boolean onResponse(HttpServletResponse response) {
        if (this.webResult != null) {
            return this.webResult.onResponse(response);
        } else {
            return false;
        }
    }
}
