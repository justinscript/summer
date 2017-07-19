/*
 * Copyright 2017-2025 msun.com All right reserved. This software is the confidential and proprietary information of
 * msun.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with msun.com.
 */
package com.ms.commons.summer.web.servlet;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import com.ms.commons.log.ExpandLogger;
import com.ms.commons.log.LoggerFactoryWrapper;

/**
 * @author zxc Apr 12, 2013 4:26:10 PM
 */
public class WebResponseWrapper extends HttpServletResponseWrapper {

    protected static ExpandLogger logger = LoggerFactoryWrapper.getLogger(WebResponseWrapper.class);
    /** The Writer we convey. */
    private StringWriter          sw;

    /** A buffer, alternatively, to accumulate bytes. */
    private ByteArrayOutputStream bos;

    /** 'True' if getWriter() was called; false otherwise. */
    private boolean               isWriterUsed;

    /** 'True if getOutputStream() was called; false otherwise. */
    private boolean               isStreamUsed;

    public WebResponseWrapper(HttpServletResponse response) {
        super(response);
    }

    public PrintWriter getWriter() {
        if (isStreamUsed) {
            logger.error("Unexpected internal error during import: Target servlet called getWriter(), then getOutputStream()");
            throw new IllegalStateException(
                                            "Unexpected internal error during import: Target servlet called getWriter(), then getOutputStream()");
        }
        isWriterUsed = true;
        if (sw == null) {
            sw = new StringWriter(2048);
        }
        return new PrintWriter(sw);
    }

    public ServletOutputStream getOutputStream() {
        if (isWriterUsed) {
            logger.error("Unexpected internal error during import: Target servlet called getOutputStream(), then getWriter()");
            throw new IllegalStateException(
                                            "Unexpected internal error during import: Target servlet called getOutputStream(), then getWriter()");
        }
        isStreamUsed = true;
        if (bos == null) {
            bos = new ByteArrayOutputStream();
        }
        ServletOutputStream sos = new ServletOutputStream() {

            public void write(int b) throws IOException {
                bos.write(b);
            }
        };
        return sos;
    }

    public boolean isStreamUseed() {
        return isStreamUsed;
    }

    public byte[] getByte() {
        if (isStreamUsed) {
            return bos.toByteArray();
        }
        return "".getBytes();
    }

    public String getString() throws UnsupportedEncodingException {
        if (isWriterUsed) {
            return sw.toString();
        } else if (isStreamUsed) {
            return bos.toString(this.getCharacterEncoding());
        } else {
            return ""; // target didn't write anything
        }
    }
}
