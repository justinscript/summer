/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.summer.web.servlet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import com.ms.commons.log.ExpandLogger;
import com.ms.commons.log.LoggerFactoryWrapper;

/**
 * @author zxc Apr 12, 2013 4:26:00 PM
 */
@SuppressWarnings("unchecked")
public class WebResultRequestWrapper extends HttpServletRequestWrapper {

    private static final ExpandLogger logger = LoggerFactoryWrapper.getLogger(WebResultRequestWrapper.class);
    protected HttpServletRequest      request;
    protected Map<String, String>     parameters;
    private String                    requestURI;

    public WebResultRequestWrapper(HttpServletRequest request) {
        super(request);
        this.request = request;
        this.parameters = new HashMap<String, String>(1);
    }

    public String getParameter(String name) {
        String parameter = this.parameters.get(name);
        if (parameter == null) parameter = this.request.getParameter(name);
        return parameter;
    }

    public String[] getParameterValues(String name) {
        List<String> params = new ArrayList<String>();
        String parameter = this.parameters.get(name);
        if (parameter != null) params.add(parameter);

        String[] parameterValues = this.request.getParameterValues(name);

        if (parameterValues != null) {
            for (String pv : parameterValues)
                params.add(pv);
        }

        if (params.size() == 0) {
            return null;
        }

        return params.toArray(new String[] {});
    }

    @SuppressWarnings("rawtypes")
    public Map getParameterMap() {
        Map result = new HashMap();
        Map map = super.getParameterMap();
        for (Object key : map.keySet()) {
            result.put(key, this.getParameter(key.toString()));
        }
        for (String key : this.parameters.keySet()) {
            result.put(key, this.parameters.get(key));
        }

        return result;
    }

    public WebResultRequestWrapper setParameter(String name, String value) {
        if (super.getParameter(name) == null) {
            this.parameters.put(name, value);
        } else {
            if (logger.isDebugEnabled()) {
                logger.warn("parameter \"" + name + "\" is alread exist in current request.");
            }
        }
        return this;
    }

    public WebResultRequestWrapper removeParameter(String name) {
        this.parameters.remove(name);
        return this;
    }

    public void resetRequestURI() {
        this.requestURI = null;
    }

    public Map<String, String> setRequestURI(String requestURI) {
        if (requestURI == null) {
            this.requestURI = null;
            return null;
        }
        Map<String, String> parameters = new HashMap<String, String>();
        int index = requestURI.indexOf("?");
        if (index != -1) {
            String params = requestURI.substring(index + 1);
            requestURI = requestURI.substring(0, index);
            if (!params.trim().equals("")) {
                String[] paramPairs = params.split("&");
                String[] pairs;
                for (String pair : paramPairs) {
                    pairs = pair.split("=");
                    if (pairs.length > 1) {
                        this.setParameter(pairs[0], pairs[1]);
                        parameters.put(pairs[0], pairs[1]);
                    }
                }
            }
        }
        this.requestURI = requestURI;

        return parameters;
    }

    public String getRequestURI() {
        if (this.requestURI != null) return this.requestURI;
        else return this.request.getRequestURI();
    }

    public StringBuffer getRequestURL() {
        return new StringBuffer(this.getRequestURI());
    }
}
