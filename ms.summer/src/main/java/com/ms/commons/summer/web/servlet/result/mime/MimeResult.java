/*
 * Copyright 2017-2025 msun.com All right reserved. This software is the confidential and proprietary information of
 * msun.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with msun.com.
 */
package com.ms.commons.summer.web.servlet.result.mime;

import com.ms.commons.summer.web.servlet.result.WebResult;

/**
 * @author zxc Apr 12, 2013 4:46:45 PM
 */
public interface MimeResult extends WebResult {

    /**
     * 设置要写回客户端的数据体，这个接口会在2.0版中删除，请直接使用onResponse代替
     * 
     * @param content
     */
    @Deprecated
    public void setContent(byte[] content);

    /**
     * 获取要写加给客户端的数据体， 这个接口会在2.0版中删除，请直接使用onResponse代替
     * 
     * @return
     */
    @Deprecated
    public byte[] getContent();
}
