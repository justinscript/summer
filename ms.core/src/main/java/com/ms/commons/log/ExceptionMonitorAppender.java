/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.spi.LoggingEvent;

/**
 * @author zxc Apr 12, 2013 1:32:21 PM
 */
public class ExceptionMonitorAppender extends AppenderSkeleton {

    private static AtomicBoolean               startThread        = new AtomicBoolean(false);
    private static LinkedBlockingDeque<String> logBlockingDeque   = new LinkedBlockingDeque<String>();
    private static String                      MAGIC_RANDOM_SPLIT = "--__EXCEPTION MONITOR APPENDER SPLIT__--";
    private String                             appName;
    private String                             remoteLogUrl;

    private String                             hostName;
    private Integer                            port;
    private String                             path;

    public ExceptionMonitorAppender() {
        if (startThread.compareAndSet(false, true)) {
            new Thread(new Runnable() {

                @Override
                public void run() {
                    System.out.println("running: " + remoteLogUrl);
                    try {
                        URL url = new URL(remoteLogUrl);
                        hostName = url.getHost();
                        port = url.getPort();
                        path = url.getPath();
                        System.out.println(remoteLogUrl + hostName + port + path);
                    } catch (MalformedURLException e) {
                        // System.out.println("MalformedURLException: " + e.getMessage() + e.getCause().getMessage());
                        // return;
                    }
                    while (true) {
                        System.out.println("while (true)");
                        try {
                            List<String> logList = new ArrayList<String>();
                            for (int i = 0; i < 100; i++) {
                                String log = logBlockingDeque.poll(3, TimeUnit.SECONDS);
                                if (log == null) {
                                    System.out.println("log == null");
                                    break;
                                }
                                System.out.println("log != null");
                                logList.add(log);
                            }
                            if (logList.size() > 0) {
                                StringBuilder sb = new StringBuilder();
                                for (String log : logList) {
                                    sb.append(log + MAGIC_RANDOM_SPLIT);
                                }
                                postLogMessageToRemote(sb.toString());
                            }
                        } catch (Throwable t) {
                            System.out.println(t.getMessage());
                            // ignore, nothing to do
                        }
                    }

                }

                @SuppressWarnings("unused")
                private void postLogMessageToRemote(String logs) {
                    if (!logs.equals("")) {
                        System.out.println(logs);
                        System.out.println(hostName + port + path);
                        Socket socket = null;
                        BufferedWriter wr = null;
                        BufferedReader rd = null;
                        try {
                            String postData = URLEncoder.encode("logs", "UTF-8") + "="
                                              + URLEncoder.encode(logs, "UTF-8");
                            socket = new Socket(hostName, port);
                            socket.setSoTimeout(3000);
                            wr = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF8"));
                            wr.write("POST " + path + " HTTP/1.0\r\n");
                            wr.write("Content-Length: " + postData.length() + "\r\n");
                            wr.write("Content-Type: application/x-www-form-urlencoded\r\n");
                            wr.write("\r\n");
                            wr.write(postData);
                            wr.flush();
                            rd = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                            String line = null;
                            while ((line = rd.readLine()) != null) {
                                // pass
                            }
                        } catch (Exception e) {
                            System.out.println(e.getMessage());
                            // pass
                        } finally {
                            closeQuietly(wr);
                            closeQuietly(rd);
                            try {
                                socket.close();
                            } catch (IOException e) {
                                // pass
                            }
                        }
                    }
                }

                private void closeQuietly(Closeable closeable) {
                    try {
                        closeable.close();
                    } catch (IOException e) {
                        // pass
                    }
                }
            }, "ExceptionMonitor-thread").start();
        }
    }

    @Override
    protected void append(LoggingEvent arg0) {
        if (arg0.getThrowableInformation() != null) {
            StringBuilder sb = new StringBuilder();
            sb.append(arg0.getTimeStamp() + "\n");
            sb.append(arg0.getLevel() + "\n");
            sb.append(arg0.getLoggerName() + "\n");
            if (arg0.getThrowableStrRep() != null) {
                for (String str : arg0.getThrowableStrRep()) {
                    sb.append(str);
                }
            }
            System.out.println("logBlockingDeque.offer: " + sb.toString());
            logBlockingDeque.offer(sb.toString());
            // to many log, ignore all
            if (logBlockingDeque.size() > 100000) {
                logBlockingDeque.clear();
            }
        }
    }

    @Override
    public void close() {
        return;
    }

    @Override
    public boolean requiresLayout() {
        return false;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getRemoteLogUrl() {
        return remoteLogUrl;
    }

    public void setRemoteLogUrl(String remoteLogUrl) {
        this.remoteLogUrl = remoteLogUrl;
    }
}
