/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.app.web.commons.pagecache;

import java.io.Serializable;

/**
 * 缓存的整个页面的信息
 * 
 * @author zxc Apr 12, 2013 10:43:09 PM
 */
public class PageInfo implements Serializable {

    private static final long serialVersionUID = 1910617482881498092L;
    private String            contentType;
    private byte[]            body;
    private int               statusCode;
    private long              createTime;

    public PageInfo(String contentType, byte[] body, int statusCode) {
        super();
        this.contentType = contentType;
        this.body = body;
        this.statusCode = statusCode;
        createTime = System.currentTimeMillis();
    }

    public String getContentType() {
        return contentType;
    }

    public byte[] getBody() {
        return body;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public long getCreateTime() {
        return createTime;
    }

    /**
     * 数据是否是空的
     * 
     * @return
     */
    public boolean isEmpty() {
        return body == null || body.length == 0;
    }
}
