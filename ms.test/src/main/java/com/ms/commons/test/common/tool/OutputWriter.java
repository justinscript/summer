/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.test.common.tool;

import java.io.Closeable;
import java.io.File;
import java.io.FileWriter;
import java.io.Flushable;
import java.io.IOException;
import java.io.Writer;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.ms.commons.test.common.ExceptionUtil;
import com.ms.commons.test.constants.IntlTestGlobalConstants;

/**
 * @author zxc Apr 13, 2013 11:20:50 PM
 */
public class OutputWriter implements Closeable, Flushable {

    private static final Queue<OutputWriter> outputWriters      = new ConcurrentLinkedQueue<OutputWriter>();
    static {
        Thread t = new Thread(new Runnable() {

            public void run() {
                while (true) {
                    flushAllWriters();
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        // ignore!
                    }
                }
            }
        });
        t.setName("OutputWriter_updater");
        t.start();
    }

    private boolean                          canBeFlush         = false;
    private int                              pendingMessageSize = 0;
    private Writer                           writer;

    protected OutputWriter(Writer writer) {
        this.writer = writer;
    }

    public static OutputWriter createWriter(String file) {
        return createWriter(new File(file));
    }

    public static OutputWriter createWriter(File file) {
        try {
            file.getParentFile().mkdirs();
            if (!file.exists()) file.createNewFile();
            return createWriter(new FileWriter(file, false));
        } catch (IOException e) {
            throw ExceptionUtil.wrapToRuntimeException(e);
        }
    }

    public static OutputWriter createWriter(Writer writer) {
        OutputWriter outputWriter = new OutputWriter(writer);
        outputWriters.offer(outputWriter);
        return outputWriter;
    }

    public static void flushAllWriters() {
        for (OutputWriter outputWriter : outputWriters.toArray(new OutputWriter[0])) {
            try {
                outputWriter.flush();
            } catch (Exception ex) {
                // oops, flush error.
                ex.printStackTrace();
            }
        }
    }

    public void writeLine(CharSequence message) {
        write(message + IntlTestGlobalConstants.LINE_SEPARATOR);
    }

    synchronized public void write(CharSequence message) {
        try {
            writer.write(message.toString());
            pendingMessageSize += message.length();
            canBeFlush = true;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        if (pendingMessageSize > (1024 * 4)) {
            try {
                flush();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    synchronized public void close() throws IOException {
        if (writer != null) {
            flush();
            writer.close();
        }
        outputWriters.remove(this);
    }

    synchronized public void flush() throws IOException {
        if (canBeFlush && writer != null) {
            writer.flush();
            pendingMessageSize = 0;
            canBeFlush = false;
        }
    }
}
