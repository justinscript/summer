/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.app.web.commons.pagecache;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Serializable;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

/**
 * @author zxc Apr 12, 2013 10:43:33 PM
 */
public class PageCacheResponseWrapper extends HttpServletResponseWrapper implements Serializable {

    private static final long   serialVersionUID = -7326065050408462198L;

    private int                 statusCode       = SC_OK;
    private int                 contentLength;
    private String              contentType;
    private ServletOutputStream outstr;
    private PrintWriter         writer;

    public PageCacheResponseWrapper(final HttpServletResponse response, final OutputStream outstr) {
        super(response);
        this.outstr = new FilterServletOutputStream(outstr);
    }

    public ServletOutputStream getOutputStream() {
        return outstr;
    }

    public void setStatus(final int code) {
        statusCode = code;
        super.setStatus(code);
    }

    /**
     * Send the error. If the response is not ok, most of the logic is bypassed and the error is sent raw Also, the
     * content is not cached.
     * 
     * @param i the status code
     * @param string the error message
     * @throws IOException
     */
    public void sendError(int i, String string) throws IOException {
        statusCode = i;
        super.sendError(i, string);
    }

    /**
     * Send the error. If the response is not ok, most of the logic is bypassed and the error is sent raw Also, the
     * content is not cached.
     * 
     * @param i the status code
     * @throws IOException
     */
    public void sendError(int i) throws IOException {
        statusCode = i;
        super.sendError(i);
    }

    /**
     * Send the redirect. If the response is not ok, most of the logic is bypassed and the error is sent raw. Also, the
     * content is not cached.
     * 
     * @param string the URL to redirect to
     * @throws IOException
     */
    public void sendRedirect(String string) throws IOException {
        statusCode = HttpServletResponse.SC_MOVED_TEMPORARILY;
        super.sendRedirect(string);
    }

    public void setStatus(final int code, final String msg) {
        statusCode = code;
        super.setStatus(code);
    }

    public int getStatus() {
        return statusCode;
    }

    public void setContentLength(final int length) {
        this.contentLength = length;
        super.setContentLength(length);
    }

    public int getContentLength() {
        return contentLength;
    }

    public void setContentType(final String type) {
        this.contentType = type;
        super.setContentType(type);
    }

    public String getContentType() {
        return contentType;
    }

    public PrintWriter getWriter() throws IOException {
        if (writer == null) {
            writer = new PrintWriter(new OutputStreamWriter(outstr, getCharacterEncoding()), true);
        }
        return writer;
    }

    public void flushBuffer() throws IOException {
        flush();
        super.flushBuffer();
    }

    public void reset() {
        super.reset();
        statusCode = SC_OK;
        contentType = null;
        contentLength = 0;
    }

    public void resetBuffer() {
        super.resetBuffer();
    }

    public void flush() throws IOException {
        if (writer != null) {
            writer.flush();
        }
        outstr.flush();
    }

    public String encodeRedirectUrl(String s) {
        return super.encodeRedirectURL(s);
    }

    public String encodeUrl(String s) {
        return super.encodeURL(s);
    }
}
