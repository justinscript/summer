/*
 * Copyright 2017-2025 msun.com All right reserved. This software is the confidential and proprietary information of
 * msun.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with msun.com.
 */
package com.ms.commons.summer.web.multipart;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.springframework.web.multipart.MultipartFile;

/**
 * @author zxc Apr 12, 2013 4:17:22 PM
 */
public class MultipartFileAdapter implements MultipartFile {

    private MultipartFile multipartFile;

    public MultipartFileAdapter(MultipartFile multipartFile) {
        this.multipartFile = multipartFile;
    }

    public byte[] getBytes() throws IOException {

        return multipartFile.getBytes();
    }

    public String getContentType() {

        return multipartFile.getContentType();
    }

    public InputStream getInputStream() throws IOException {

        return multipartFile.getInputStream();
    }

    public String getName() {

        return multipartFile.getName();
    }

    public String getOriginalFilename() {

        return multipartFile.getOriginalFilename();
    }

    public long getSize() {

        return multipartFile.getSize();
    }

    public boolean isEmpty() {

        return multipartFile.isEmpty();
    }

    public void transferTo(File dest) throws IOException, IllegalStateException {
        multipartFile.transferTo(dest);
    }
}
