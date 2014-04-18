/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.summer.web.servlet.result.mime;

import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import com.ms.commons.summer.web.servlet.result.WebResult;

/**
 * MimeResult适配器
 * 
 * @author zxc Apr 12, 2013 4:46:10 PM
 */
public class MimeResultAdapter implements MimeResult {

    /**
     * 字节数组形式的内容
     */
    private byte[] content;
    /**
     * 内容类型
     */
    private String contentType;
    /**
     * 字符集
     */
    private String charset = "UTF-8";

    /*
     * (non-Javadoc)
     * @see com.alibaba.roma.web.servlet.result.mime.MimeResult#getContent()
     */
    public byte[] getContent() {
        return this.content;
    }

    /*
     * (non-Javadoc)
     * @see com.alibaba.roma.web.servlet.result.mime.MimeResult#setContent(byte[])
     */
    public void setContent(byte[] content) {
        this.content = content;
    }

    /*
     * (non-Javadoc)
     * @see com.alibaba.roma.web.servlet.result.WebResult#addParam(java.lang.String, java.lang.String)
     */
    public WebResult addParam(String name, String value) {
        throw new UnsupportedOperationException();
    }

    /*
     * (non-Javadoc)
     * @see com.alibaba.roma.web.servlet.result.WebResult#getCharacterEncoding()
     */
    public String getCharacterEncoding() {
        return this.charset;
    }

    /*
     * (non-Javadoc)
     * @see com.alibaba.roma.web.servlet.result.WebResult#getContentType()
     */
    public String getContentType() {
        return this.contentType;
    }

    /*
     * (non-Javadoc)
     * @see com.alibaba.roma.web.servlet.result.WebResult#getParameters()
     */
    public Map<String, String> getParameters() {
        throw new UnsupportedOperationException();
    }

    /*
     * (non-Javadoc)
     * @see com.alibaba.roma.web.servlet.result.WebResult#getView()
     */
    public String getView() {
        throw new UnsupportedOperationException();
    }

    /*
     * (non-Javadoc)
     * @see com.alibaba.roma.web.servlet.result.WebResult#setCharacterEncoding(java .lang.String)
     */
    public void setCharacterEncoding(String charset) {
        this.charset = charset;
    }

    /*
     * (non-Javadoc)
     * @see com.alibaba.roma.web.servlet.result.WebResult#setContentType(java.lang .String)
     */
    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getNameSpace() {
        throw new UnsupportedOperationException();
    }

    public void setNameSpace(String nameSpace) {
        throw new UnsupportedOperationException();
    }

    /*
     * (non-Javadoc)
     * @see com.alibaba.roma.web.servlet.result.mime.MimeResult#processResponse(javax .servlet.http.HttpServletResponse)
     */
    public boolean onResponse(HttpServletResponse response) {
        return false;
    }
}
