/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.summer.web.servlet.result;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import com.ms.commons.summer.web.servlet.URITools;

/**
 * @author zxc Apr 12, 2013 4:45:46 PM
 */
public class AbstractWebResult implements WebResult {

    private String              path;
    private String              contentType;
    private String              charset;
    private String              nameSpace;
    private Map<String, String> parameters = new HashMap<String, String>(0);

    public AbstractWebResult(String path) {
        this(null, path);
    }

    public AbstractWebResult(String nameSpace, String path) {
        this.nameSpace = nameSpace;
        this.path = path;
    }

    /**
     * @return the path
     */
    public String getPath() {
        return URITools.buildURIPath(this.path, null, this.parameters);
    }

    public String getView() {
        return this.path;
    }

    public WebResult addParam(String name, String value) {
        URITools.addParam(this.parameters, name, value);
        return this;
    }

    public Map<String, String> getParameters() {
        return this.parameters;
    }

    /**
     * @return the contentType
     */
    public String getContentType() {
        return contentType;
    }

    /**
     * @param contentType the contentType to set
     */
    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object obj) {
        if (obj instanceof AbstractWebResult) {
            return this.path.equals(((AbstractWebResult) obj).path);
        } else {
            return false;
        }
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    public int hashCode() {
        return WebResult.class.hashCode() + this.path.hashCode();
    }

    /*
     * (non-Javadoc)
     * @see com.alibaba.roma.web.servlet.WebResult#setCharacterEncoding(java.lang .String)
     */
    public void setCharacterEncoding(String charset) {
        this.charset = charset;
    }

    /*
     * (non-Javadoc)
     * @see com.alibaba.roma.web.servlet.WebResult#getCharacterEncoding()
     */
    public String getCharacterEncoding() {
        return this.charset;
    }

    public String getNameSpace() {
        return nameSpace;
    }

    public void setNameSpace(String nameSpace) {
        this.nameSpace = nameSpace;
    }

    /*
     * (non-Javadoc)
     * @see com.alibaba.roma.web.servlet.result.WebResult#onResponse(javax.servlet .http.HttpServletResponse)
     */
    public boolean onResponse(HttpServletResponse response) {
        return false;
    }
}
