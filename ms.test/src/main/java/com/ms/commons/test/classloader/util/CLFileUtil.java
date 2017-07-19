/*
 * Copyright 2017-2025 msun.com All right reserved. This software is the confidential and proprietary information of
 * msun.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with msun.com.
 */
package com.ms.commons.test.classloader.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.URL;

import org.apache.commons.io.IOUtils;

import com.ms.commons.test.common.FileUtil;

/**
 * @author zxc Apr 13, 2013 11:08:25 PM
 */
public class CLFileUtil {

    public static interface Processor {

        String process(String content);
    }

    public static Processor DEFAULT_PROCESSOR = new Processor() {

                                                  public String process(String content) {
                                                      return content == null ? "" : content;
                                                  }
                                              };

    public static void copyAndProcessFileContext(URL url, File newFile, Processor processor) {
        copyAndProcessFileContext(url, newFile, null, processor);
    }

    public static void copyAndProcessFileContext(URL url, File newFile, String encoding, Processor processor) {
        try {
            if (processor == null) {
                processor = DEFAULT_PROCESSOR;
            }

            if (!newFile.exists()) {
                String fileContext = readUrlToString(url, null);
                if (encoding == null) {
                    if (FileUtil.convertURLToFilePath(url).endsWith(".xml") && fileContext.contains("\"UTF-8\"")) {
                        encoding = "UTF-8";
                        fileContext = readUrlToString(url, "UTF-8");
                    }
                }

                (new File(getFilePath(newFile.getPath()))).mkdirs();

                Writer writer;
                if (encoding == null) {
                    writer = new OutputStreamWriter(new FileOutputStream(newFile));
                } else {
                    writer = new OutputStreamWriter(new FileOutputStream(newFile), encoding);
                }
                try {
                    writer.write(processor.process(fileContext));
                    writer.flush();
                } finally {
                    writer.close();
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String getFilePath(String file) {
        int lastSplit = Math.max(file.lastIndexOf('/'), file.lastIndexOf('\\'));
        return file.substring(0, lastSplit);
    }

    public static String readUrlToString(URL url, String encoding) throws IOException {
        if (url == null) {
            return null;
        }
        InputStream in = null;
        try {
            in = url.openStream();
            return IOUtils.toString(in, encoding);
        } finally {
            IOUtils.closeQuietly(in);
        }
    }
}
