/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.summer.web.servlet.result.mime;

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.http.HttpServletResponse;

/**
 * 文件下载的WebResult
 * 
 * @author zxc Apr 12, 2013 4:48:16 PM
 */
public class Download extends MimeResultAdapter {

    private String fileName;

    public String getFileName() {
        if (this.fileName == null) return "noname";
        else return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Download() {
        this.setCharacterEncoding("UTF-8");
    }

    public Download(String fileName) {
        this.setCharacterEncoding("UTF-8");
        this.fileName = fileName;
    }

    public final boolean onResponse(HttpServletResponse response) {
        String contentType = this.getContentType();
        if (contentType == null || contentType.toString().trim().length() == 0) {
            this.setContentType("application/octet-stream;charset=" + this.getCharacterEncoding());
        }
        response.setContentType(this.getContentType());
        response.setHeader("Pragma", "private");
        response.setHeader("Cache-Control", "private, must-revalidate");
        response.addHeader("Content-Disposition", "attachment; filename=\"" + this.getFileName() + "\"");

        try {
            this.onDownload(response.getOutputStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return true;
    }

    /**
     * 执行用户的下载操作
     * 
     * @param outputStream
     */
    protected void onDownload(OutputStream outputStream) throws IOException {
    }
}
