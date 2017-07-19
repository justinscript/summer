/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.summer.web.servlet.result.mime;

import java.io.IOException;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

/**
 * 把指定的字符串内容输出response中
 * 
 * @author zxc Apr 12, 2013 4:45:59 PM
 */
public class NormalWebResult extends MimeResultAdapter {

    private String content;

    public NormalWebResult(String content) {
        super();
        this.content = content;
        this.setContentType("text/html");
        this.setCharacterEncoding("UTF-8");
    }

    public final boolean onResponse(HttpServletResponse response) {
        try {
            this.onSerialize(response.getOutputStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return true;
    }

    /**
     * 执行用户对Json的串行化操作
     * 
     * @param outputStream
     */
    protected void onSerialize(ServletOutputStream outputStream) throws IOException {
        if (content != null && content.trim().length() != 0) {
            outputStream.write(content.getBytes());
        }
    }
}
