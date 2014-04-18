/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.app.web.commons.pagecache;

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.ServletOutputStream;

/**
 * @author zxc Apr 12, 2013 10:46:04 PM
 */
public class FilterServletOutputStream extends ServletOutputStream {

    private OutputStream stream;

    public FilterServletOutputStream(final OutputStream stream) {
        this.stream = stream;
    }

    public void write(final int b) throws IOException {
        stream.write(b);
    }

    public void write(final byte[] b) throws IOException {
        stream.write(b);
    }

    public void write(final byte[] b, final int off, final int len) throws IOException {
        stream.write(b, off, len);
    }
}
